package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.http.client.domain.ChannelInfo;
import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.sportradar.uf.datamodel.UFSnapshotComplete;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.OddsFeed;
import com.sportradar.unifiedodds.sdk.OperationManager;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.*;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SdkConnectionTests {

    private RabbitProducer rabbitProducer;
    private TestFeed feed;
    private FeedMessageBuilder feedMessageBuilder;
    private OddsFeedConfiguration config;
    private SdkConnListener sdkListener;

    @Before
    public void setup() {
        // setup classes for simulating outside data sources (api access, ...)
        // setup connection to test rabbit server
        config = OddsFeed.getOddsFeedConfigurationBuilder()
                .setAccessToken("testuser")
                .selectCustom()
                .setMessagingUsername(Constants.SDK_USERNAME)
                .setMessagingPassword(Constants.SDK_PASSWORD)
                .setMessagingHost(Constants.RABBIT_IP)
                .useMessagingSsl(false)
                .setApiHost(Constants.RABBIT_IP)
                .setDefaultLocale(Locale.ENGLISH)
                .setMessagingVirtualHost(Constants.UF_VIRTUALHOST)
                .setMinIntervalBetweenRecoveryRequests(20)
                .build();

        sdkListener = new SdkConnListener();
        feed = new TestFeed(sdkListener, config, sdkListener);
        feedMessageBuilder = new FeedMessageBuilder(1);

        // establish connection to the test rabbit server for rabbit producer
        TestProducersProvider testProducersProvider = new TestProducersProvider();
        rabbitProducer = new RabbitProducer(testProducersProvider);
        assertNotNull(rabbitProducer.ManagementClient);
        assertTrue(rabbitProducer.ManagementClient.alivenessTest(Constants.UF_VIRTUALHOST));
        rabbitProducer.start();
        waitAndCheckTillTimeout(w -> rabbitProducer.getConnectionCount() == 1, "Only producer connection is available", 3000, 30000);
    }

    @After
    public void cleanup()
    {
        // cleanup of services initialized in setup method (the rest should be cleaned within specific Test method)
        rabbitProducer.ProducersAlive.clear();
        try {
            feed.close();
        }
        catch (Exception e) {
            Helper.writeToOutput("Error closing feed: " + e.getMessage());
        }
        rabbitProducer.stop();
    }

    @Test
    public void normalRunTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 0);
        assertTrue(sdkListener.CalledEvents.isEmpty());
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));
        List<Integer> disabledProducers = Arrays.asList( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo()); // at the start it is null (different from net version)
        assertTrue(producer.isFlaggedDown());

        feed.open();

        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId), null, 0);
        waitAndCheckTillTimeout(w -> checkProducerRecovery (producerId, false), "Producer recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        Producer finalProducer = producer;
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains(String.valueOf(finalProducer.getRecoveryInfo().getRequestId()))).count());

        // send 2 changeOdds and snapshotComplete
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());
//        assertEquals(0, producer.getRecoveryInfo().getRequestId()); // maybe it should reset RequestId

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(2, simpleMessageListener.FeedMessages.size());
    }
    
    @Test
    public void normalStartWithSetAfterTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 0);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        Map<Integer, Date> afters = new HashMap<>();
        afters.put(producerId, Helper.addToDate(Calendar.MINUTE, -10));
        afters.put(3, Helper.addToDate(Calendar.MINUTE, -10));
        setAfterTimestamp(afters, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 10000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains("after")).count());
        Producer finalProducer = producer;
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains(String.valueOf(finalProducer.getRecoveryInfo().getRequestId()))).count());

        // send changeOdds and snapshotComplete
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(1, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void multipleProducersWithAfterTest() throws InitException {
        // setup for producer 1 and 3
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        final int producerId1 = 1;
        final int producerId3 = 3;
        rabbitProducer.addProducersAlive(producerId1, 0);
        rabbitProducer.addProducersAlive(producerId3, 0);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/prematch/", 1, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        Map<Integer, Date> afters = new HashMap<>();
        afters.put(producerId1, Helper.addToDate(Calendar.MINUTE, -10));
        afters.put(producerId3, Helper.addToDate(Calendar.MINUTE, -10));
        setAfterTimestamp(afters, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer1 = feed.getProducerManager().getProducer(producerId1);
        assertNotNull(producer1);
        assertNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        Producer producer3 = feed.getProducerManager().getProducer(producerId3);
        assertNotNull(producer3);
        assertNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery (producerId1, false), "Producer 1 recovery info is not null", 1000, 10000);
        waitAndCheckTillTimeout(w -> checkProducerRecovery (producerId3, false), "Producer 3 recovery info is not null", 1000, 10000);
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);

        assertNotNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertNotNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);

        List<String> recoveryCalled1 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled1.size());
        assertEquals(1, recoveryCalled1.stream().filter(f->f.contains("after")).count());
        long finalProducerRequestId1 = producer1.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled1.stream().filter(f->f.contains(String.valueOf(finalProducerRequestId1))).count());

        List<String> recoveryCalled3 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/pre/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled3.size());
        assertEquals(1, recoveryCalled3.stream().filter(f->f.contains("after")).count());
        long finalProducerRequestId3 = producer3.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled3.stream().filter(f->f.contains(String.valueOf(finalProducerRequestId3))).count());

        // send changeOdds and snapshotComplete
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId3, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer3.getRecoveryInfo().getRequestId());
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId3, producer3.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId1, false), "Producer 1 is not down");
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId3, false), "Producer 3 is not down");
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);
        assertFalse(producer1.isFlaggedDown());
        assertFalse(producer3.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer Ctrl is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(3, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void multipleProducersMultiSessionTest() throws InitException {
        // setup for producer 1 and 3, with 3 sessions (Live, Pre and Virtuals)
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        final int producerId1 = 1;
        final int producerId3 = 3;
        rabbitProducer.addProducersAlive(producerId1, 0);
        rabbitProducer.addProducersAlive(producerId3, 0);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/prematch/", 1, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        Map<Integer, Date> afters = new HashMap<>();
        afters.put(producerId1, Helper.addToDate(Calendar.MINUTE, -10));
        afters.put(producerId3, Helper.addToDate(Calendar.MINUTE, -10));
        setAfterTimestamp(afters, feed);

        SimpleMessageListener liveSimpleMessageListener = new SimpleMessageListener("live", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(liveSimpleMessageListener).setMessageInterest(MessageInterest.LiveMessagesOnly).build();
        SimpleMessageListener prematchSimpleMessageListener = new SimpleMessageListener("prematch", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(prematchSimpleMessageListener).setMessageInterest(MessageInterest.PrematchMessagesOnly).build();
        SimpleMessageListener virtualSimpleMessageListener = new SimpleMessageListener("virtual", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(virtualSimpleMessageListener).setMessageInterest(MessageInterest.VirtualSports).build();

        Producer producer1 = feed.getProducerManager().getProducer(producerId1);
        assertNotNull(producer1);
        assertNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        Producer producer3 = feed.getProducerManager().getProducer(producerId3);
        assertNotNull(producer3);
        assertNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery (producerId1, false), "Producer 1 recovery info is not null", 1000, 10000);
        waitAndCheckTillTimeout(w -> checkProducerRecovery (producerId3, false), "Producer 3 recovery info is not null", 1000, 10000);
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);

        assertEquals(2, feed.TestHttpHelper.CalledUrls.size());
        assertNotNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertNotNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);

        List<String> recoveryCalled1 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled1.size());
        assertEquals(1, recoveryCalled1.stream().filter(f->f.contains("after")).count());
        final long producer1RecoveryRequestId1 = producer1.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled1.stream().filter(f->f.contains(String.valueOf(producer1RecoveryRequestId1))).count());

        List<String> recoveryCalled3 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/pre/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled3.size());
        assertEquals(1, recoveryCalled3.stream().filter(f->f.contains("after")).count());
        final long producer3RecoveryRequestId1 = producer3.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled3.stream().filter(f->f.contains(String.valueOf(producer3RecoveryRequestId1))).count());

        // send changeOdds and snapshotComplete
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId3, producer3.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer3.getRecoveryInfo().getRequestId());
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId3, producer3.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId1, false), "Producer 1 is not down");
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId3, false), "Producer 3 is not down");
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);
        assertFalse(producer1.isFlaggedDown());
        assertFalse(producer3.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(6, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer Ctrl is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(2, liveSimpleMessageListener.FeedMessages.size());
        assertEquals(1, prematchSimpleMessageListener.FeedMessages.size());
        assertEquals(0, virtualSimpleMessageListener.FeedMessages.size());
    }

    @Test
    public void recoveryUnsuccessfulRetryTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done (at first unsuccessful - testing if it retries)
        // wait till snapshotComplete arrives and check if all good
        //rabbitProducer.addProducersAlive(1);
        final int producerId = 1;
        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_BAD_GATEWAY));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        feed.open();

        // setup for recovery request and fails
        Helper.writeToOutput("Waiting for first recovery call");
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 10000);
        producer = feed.getProducerManager().getProducer(producerId);

        Helper.sleep(2000); // so all events are called

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        Helper.writeToOutput("Recovery 1 called with RequestId=" + producerRecoveryRequestId1);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList()); // failed one
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains("request_id=" + producerRecoveryRequestId1)).count());
        assertTrue(checkListContainsString(sdkListener.CalledEvents, "RequestId=" + producerRecoveryRequestId1, "After=0", 1));

        Helper.sleep((config.getMinIntervalBetweenRecoveryRequests()+5) * 1000L);

        // call second one and fail
        Helper.writeToOutput("Waiting for second recovery call");
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, producerRecoveryRequestId1, false), "Producer new recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);
        final long producerRecoveryRequestId2 = producer.getRecoveryInfo().getRequestId();
        Helper.writeToOutput("Recovery 2 called with RequestId=" + producerRecoveryRequestId2);
        assertNotEquals(producerRecoveryRequestId1, producerRecoveryRequestId2);

        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        writeStringList(feed.TestHttpHelper.CalledUrls, "Called urls: ");
        recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList()); // failed one
        assertEquals(2, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(c -> c.contains("after")).count());
        assertEquals(1, recoveryCalled.stream().filter(c->c.contains("request_id=" + producerRecoveryRequestId2)).collect(Collectors.toList()).size());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c->c.contains("RequestId=" + producerRecoveryRequestId2) && c.contains("After=0")).collect(Collectors.toList()).size());

        // send 2 changeOdds and snapshotComplete for old recovery request id - should not trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producerRecoveryRequestId1, null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producerRecoveryRequestId1, null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producerRecoveryRequestId1, null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer is not down", 1000, 10000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertTrue(producer.isFlaggedDown());

        Helper.sleep((config.getMinIntervalBetweenRecoveryRequests()+5) * 1000L);

        // now get the valid recovery and complete it
        feed.TestHttpHelper.PostResponses.clear();
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));
        Helper.writeToOutput("Waiting for third recovery call");
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, producerRecoveryRequestId2, false), "Producer recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);
        final long producerRecoveryRequestId3 = producer.getRecoveryInfo().getRequestId();
        assertTrue(producer.isFlaggedDown());

        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList()); // ok one
        assertEquals(3, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(c -> c.contains("after")).count());
        assertEquals(1, recoveryCalled.stream().filter(c->c.contains("request_id=" + producerRecoveryRequestId3)).collect(Collectors.toList()).size());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("RequestId=" + producerRecoveryRequestId3) && c.contains("After=0")).count());

        // send 2 changeOdds and snapshotComplete for new recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        Helper.sleep(1000);
        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(4, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(4, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void producerNoAlivesMakeRecoveryTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done (at first unsuccessful - testing if it retries)
        // wait till snapshotComplete arrives and check if all good
        //rabbitProducer.addProducersAlive(1);
        final int producerId = 1;
        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        feed.open();

        // check if any recovery is done (without alive message), because of missing alive messages, it will be interrupted
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 5000, 62000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains(String.valueOf(producerRecoveryRequestId1))).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("RequestId=" + producerRecoveryRequestId1)).count());

        // send 2 changeOdds and snapshotComplete for recovery request id - it was interrupted and new one will start
        rabbitProducer.addProducersAlive(producerId, 4000);
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(2, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void unsuccessfulRecoveryDoesNotRepeatToOftenTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done (at first unsuccessful - testing if it retries)
        // wait till snapshotComplete arrives and check if all good
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 3000);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_NOT_ACCEPTABLE));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 10000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains(String.valueOf(producerRecoveryRequestId1))).count());
        writeStringList(sdkListener.CalledEvents, "Attempted recoveries 1: ");
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("RequestId=" + producerRecoveryRequestId1)).collect(Collectors.toList()).size());

        Helper.sleep( config.getMinIntervalBetweenRecoveryRequests() * 1000L); // wait till new recovery request can be made

        //reset so it can be accepted
        feed.TestHttpHelper.PostResponses.clear();
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_ACCEPTED));

        // wait till new recovery is made
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, producerRecoveryRequestId1, false), "Producer waiting for new recovery", 1000, 60000);
        producer = feed.getProducerManager().getProducer(producerId);
        final long producerRecoveryRequestId2 = producer.getRecoveryInfo().getRequestId();

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(2, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        assertEquals(1, recoveryCalled.stream().filter(f->f.contains(String.valueOf(producerRecoveryRequestId1))).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated") && c.contains("failed")).collect(Collectors.toList()).size());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("RequestId=" + producerRecoveryRequestId2)).collect(Collectors.toList()).size());

        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "RequestId=" + producerRecoveryRequestId2, 1),
                                "Producer new recovery info is not null", 1000, 30000);

        writeStringList(sdkListener.CalledEvents, "Attempted recoveries 2: ");
        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down anymore");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(2, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void aliveSubscribedFalseTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        // when alive message with subscribed=0 comes, producer goes down
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 5000);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 10000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        assertTrue(checkListContainsString(sdkListener.CalledEvents, "Recovery initiated. RequestId=" + producerRecoveryRequestId1, "After=0", 1));

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(1, producer.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        final long producerRecoveryRequestId2 = producer.getRecoveryInfo().getRequestId();

        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId)); // to set new alive timestamp for next recovery
        Helper.sleep(2000);

        rabbitProducer.ProducersAlive.clear();

        // send alive with subscribed=0 to mark producer down
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId, null, false));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, true), "Producer 1 is down", 3000, 30000);
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).count());

        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId, null, false));
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());

        // reset alives and check all
        rabbitProducer.addProducersAlive(producerId, 5000);
//        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, null, null)); // send non-recovery user message to avoid ProcessingQueueDelayViolation
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));

        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "Recovery initiated", 2), "Producer second recovery initiated", 3000, 60000);
        producer = feed.getProducerManager().getProducer(1);

        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertNotEquals(producerRecoveryRequestId2, producer.getRecoveryInfo().getRequestId());

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down (currently=" + producer.isFlaggedDown() + ")", 2000, 30000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(4, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated") && c.contains(String.valueOf(producerRecoveryRequestId1)) && c.contains("After=0")).count());
        // initial request
        final long producerRecoveryRequestId3 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId3) && !c.contains("After=0")).count()); // second
        // request
        assertEquals(4, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerDown")).count());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(4, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void connectionBreakTest() throws InitException {
        // setup for producer 1
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        // then connection drops and should reconnect
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 5000);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 0, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        OperationManager.setRabbitHeartbeat(20);
        OperationManager.setRabbitConnectionTimeout(10);

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);

        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(f->f.contains("after")).count());
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        assertEquals( 1, sdkListener.CalledEvents.stream().filter(f->f.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId1)).collect(Collectors.toList()).size());

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        final long producerRecoveryRequestId2 = producer.getRecoveryInfo().getRequestId();

        List<ConnectionInfo> connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        Optional<ConnectionInfo> sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        List<ChannelInfo> channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(2, channels.size());

        // close connection and wait to auto restart
        rabbitProducer.ManagementClient.updateUser(Constants.SDK_USERNAME, ("1" + Constants.SDK_PASSWORD).toCharArray(), Collections.singletonList("administrator")); // disable sdk rabbit connection user
        rabbitProducer.ManagementClient.closeConnection(sdkConnection.get().getName(), "test invoked");
        Helper.sleep(2000);
        connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(1, connections.size()); // producer connection only

        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "Producer LO is down", 2), "Producer down event called", 3000, 60000);
        writeStringList(sdkListener.CalledEvents, "SdkListener CalledEvents: ");
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).count()); // producers are marked down
        assertTrue(sdkListener.CalledEvents.stream().filter(c -> c.contains("Connection exception")).count() > 0); //Connection to the feed lost

        sdkListener.CalledEvents.clear();

        // reset user so sdk connection can be made
        rabbitProducer.ManagementClient.updateUser(Constants.SDK_USERNAME, Constants.SDK_PASSWORD.toCharArray(), Collections.singletonList("administrator"));

        // reset alives and check all
        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "Recovery initiated", 1), "Producer second recovery initiated",
                                3000, 60000);
        producer = feed.getProducerManager().getProducer(producerId);
        final long producerRecoveryRequestId3 = producer.getRecoveryInfo().getRequestId();

        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId3) && !c.contains("After=null")).count());
        assertNotEquals(producerRecoveryRequestId1, producer.getRecoveryInfo().getRequestId());

        Helper.sleep(5000); // so all connection and channels are set up

        // check for new connection is done and new alives arrive
        connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(2, channels.size());

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down (" + producer.isFlaggedDown() + ")", 2000, 30000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        writeStringList(sdkListener.CalledEvents, "Final sdkListener.CalledEvents: ");
        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(0, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated") && c.contains(String.valueOf(producerRecoveryRequestId1)) && c.contains("After=null")).count()); //
        // initial request
        final long producerRecoveryRequestId4 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId4) && !c.contains("After=null")).count()); // second request
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerUp")).count());
        assertEquals(0, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).count()); // because before cleared
        assertEquals(0, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerDown")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("onProducerStatusChange")).count());

        assertEquals(4, simpleMessageListener.FeedMessages.size());
    }

    @Test
    public void connectionBreakMultiSessionTest() throws InitException {
        // setup for producer 1, 3, 6 and 3 different session
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        // then connection drops and should reconnect
        final int producerId1 = 1;
        final int producerId3 = 3;
        final int producerId6 = 6;
        rabbitProducer.addProducersAlive(producerId1, 5000);
        rabbitProducer.addProducersAlive(producerId3, 6000);
        rabbitProducer.addProducersAlive(producerId6, 7000);
        // reset sdk connection user
        rabbitProducer.ManagementClient.updateUser(Constants.SDK_USERNAME, Constants.SDK_PASSWORD.toCharArray(), Collections.singletonList("administrator"));

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/prematch/", 1, HttpStatus.SC_ACCEPTED));
        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/vf/", 1, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener liveSimpleMessageListener = new SimpleMessageListener("live", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(liveSimpleMessageListener).setMessageInterest(MessageInterest.LiveMessagesOnly).build();

        SimpleMessageListener prematchSimpleMessageListener = new SimpleMessageListener("prematch", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(prematchSimpleMessageListener).setMessageInterest(MessageInterest.PrematchMessagesOnly).build();

        SimpleMessageListener virtualSimpleMessageListener = new SimpleMessageListener("virtual", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(virtualSimpleMessageListener).setMessageInterest(MessageInterest.VirtualSports).build();

        Producer producer1 = feed.getProducerManager().getProducer(producerId1);
        assertNotNull(producer1);
        assertNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        Producer producer3 = feed.getProducerManager().getProducer(producerId3);
        assertNotNull(producer3);
        assertNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());
        Producer producer6 = feed.getProducerManager().getProducer(producerId6);
        assertNotNull(producer6);
        assertNull(producer6.getRecoveryInfo());
        assertTrue(producer6.isFlaggedDown());

        OperationManager.setRabbitHeartbeat(20);
        OperationManager.setRabbitConnectionTimeout(10);

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId1, false), "Producer 1 recovery info is not null", 1000, 20000);
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId3, false), "Producer 3 recovery info is not null", 1000, 20000);
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId6, false), "Producer 6 recovery info is not null", 1000, 20000);
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);
        producer6 = feed.getProducerManager().getProducer(producerId6);

        assertEquals(3, feed.TestHttpHelper.CalledUrls.size());
        assertNotNull(producer1.getRecoveryInfo());
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertNotNull(producer3.getRecoveryInfo());
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);
        assertNotNull(producer6.getRecoveryInfo());
        assertTrue(producer6.isFlaggedDown());
        assertTrue(producer6.getRecoveryInfo().getRequestId() > 0);

        List<String> recoveryCalled1 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled1.size());
        final long producer1RecoveryRequestId1 = producer1.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled1.stream().filter(c -> c.contains("request_id=" + producer1RecoveryRequestId1) && !c.contains("after")).count());
        List<String> recoveryCalled3 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/pre/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled3.size());
        final long producer3RecoveryRequestId1 = producer3.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled3.stream().filter(c -> c.contains("request_id=" + producer3RecoveryRequestId1) && !c.contains("after")).count());
        List<String> recoveryCalled6 = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/vf/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled6.size());
        final long producer6RecoveryRequestId1 = producer6.getRecoveryInfo().getRequestId();
        assertEquals(1, recoveryCalled6.stream().filter(c -> c.contains("request_id=" + producer6RecoveryRequestId1) && !c.contains("after")).count());

        // send changeOdds and snapshotComplete
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId3, producer3.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId6, producer6.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer6.isFlaggedDown());
        assertTrue(producer6.getRecoveryInfo().getRequestId() > 0);
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer3.getRecoveryInfo().getRequestId());
        assertNotEquals(producer3.getRecoveryInfo().getRequestId(), producer6.getRecoveryInfo().getRequestId());
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer6.getRecoveryInfo().getRequestId());
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId3, producer3.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId6, producer6.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId1, false), "Producer 1 is not down");
        producer1 = feed.getProducerManager().getProducer(producerId1);
        assertFalse(producer1.isFlaggedDown());
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId3, false), "Producer 3 is not down");
        producer3 = feed.getProducerManager().getProducer(producerId3);
        assertFalse(producer3.isFlaggedDown());
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId6, false), "Producer 6 is not down");
        producer6 = feed.getProducerManager().getProducer(producerId6);
        assertFalse(producer6.isFlaggedDown());

        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).collect(Collectors.toList()).size());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).collect(Collectors.toList()).size());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer Ctrl is up")).collect(Collectors.toList()).size());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer VF is up")).collect(Collectors.toList()).size());

        List<ConnectionInfo> connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        Optional<ConnectionInfo> sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        List<ChannelInfo> channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(4, channels.size());

        // close connection and wait to auto restart
        rabbitProducer.ManagementClient.updateUser(Constants.SDK_USERNAME, ("1" + Constants.SDK_PASSWORD).toCharArray(), Collections.singletonList("administrator")); // disable sdk rabbit connection user
        rabbitProducer.ManagementClient.closeConnection(sdkConnection.get().getName(), "test invoked");
        Helper.sleep(2000);
        connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(1, connections.size()); // producer connection only

        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "Producer LO is down", 2), "Producer doown event called", 3000, 60000);
        Helper.sleep(2000); // so all events can be called
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).collect(Collectors.toList()).size()); // producers are marked down
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer Ctrl is down")).collect(Collectors.toList()).size());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer VF is down")).collect(Collectors.toList()).size());
        assertTrue(sdkListener.CalledEvents.stream().filter(c -> c.contains("Connection exception")).count() > 0); //Connection to the feed lost

        sdkListener.CalledEvents.clear();

        // reset user so sdk connection can be made
        rabbitProducer.ManagementClient.updateUser(Constants.SDK_USERNAME, Constants.SDK_PASSWORD.toCharArray(), Collections.singletonList("administrator"));

        // reset alives and check all
        waitAndCheckTillTimeout(w -> checkListContainsString(sdkListener.CalledEvents, "Recovery initiated", 3), "Producers second recovery not initiated", 3000, 60000);
        producer1 = feed.getProducerManager().getProducer(producerId1);
        producer3 = feed.getProducerManager().getProducer(producerId3);
        producer6 = feed.getProducerManager().getProducer(producerId6);
        final long producer1RecoveryRequestId2 = producer1.getRecoveryInfo().getRequestId();
        final long producer3RecoveryRequestId2 = producer3.getRecoveryInfo().getRequestId();
        final long producer6RecoveryRequestId2 = producer6.getRecoveryInfo().getRequestId();
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer1RecoveryRequestId2) && !c.contains("After=null")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer3RecoveryRequestId2) && !c.contains("After=null")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer6RecoveryRequestId2) && !c.contains("After=null")).count());

        // check for new connection is done and new alives arrive
        connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(4, channels.size());

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId3, producer3.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId6, producer6.getRecoveryInfo().getRequestId(), null));
        
        assertTrue(producer1.isFlaggedDown());
        assertTrue(producer1.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer3.isFlaggedDown());
        assertTrue(producer3.getRecoveryInfo().getRequestId() > 0);
        assertTrue(producer6.isFlaggedDown());
        assertTrue(producer6.getRecoveryInfo().getRequestId() > 0);
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer3.getRecoveryInfo().getRequestId());
        assertNotEquals(producer3.getRecoveryInfo().getRequestId(), producer6.getRecoveryInfo().getRequestId());
        assertNotEquals(producer1.getRecoveryInfo().getRequestId(), producer6.getRecoveryInfo().getRequestId());
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId1, producer1.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId3, producer3.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildSnapshotComplete(producerId6, producer6.getRecoveryInfo().getRequestId(), null));

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId1, false), "Producer 1 is not down");
        producer1 = feed.getProducerManager().getProducer(producerId1);
        assertFalse(producer1.isFlaggedDown());
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId3, false), "Producer 3 is not down");
        producer3 = feed.getProducerManager().getProducer(producerId3);
        assertFalse(producer3.isFlaggedDown());
        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId6, false), "Producer 6 is not down");
        producer6 = feed.getProducerManager().getProducer(producerId6);
        assertFalse(producer6.isFlaggedDown());

        Helper.sleep(1000);
        final long producer1RecoveryRequestId3 = producer1.getRecoveryInfo().getRequestId();
        final long producer3RecoveryRequestId3 = producer3.getRecoveryInfo().getRequestId();
        final long producer6RecoveryRequestId3 = producer6.getRecoveryInfo().getRequestId();
        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(4, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count()); // because we deleted previous called events
        assertEquals(9, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count()); // comes on all sessions
        assertEquals(0, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated") && c.contains(String.valueOf(producer1RecoveryRequestId1)) && c.contains("After=null")).count()); // initial request
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer1RecoveryRequestId3) && !c.contains("After=null")).count());
        // second request
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer3RecoveryRequestId3) && !c.contains("After=null")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producer6RecoveryRequestId3) && !c.contains("After=null")).count());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer Ctrl is up")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer VF is up")).count());

        assertEquals(4, liveSimpleMessageListener.FeedMessages.size());
        assertEquals(2, prematchSimpleMessageListener.FeedMessages.size());
        assertEquals(2, virtualSimpleMessageListener.FeedMessages.size());
    }

    @Test
    public void channelNoMessagesTest() throws InitException {
        // connection started but no message arrive via channel
        // open feed and check that recovery was done
        // wait till snapshotComplete arrives and check if all good
        // then connection drops and should reconnect
        final int producerId = 1;
        rabbitProducer.addProducersAlive(producerId, 5000);

        assertTrue(sdkListener.CalledEvents.isEmpty());

        feed.TestHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement("/liveodds/", 1, HttpStatus.SC_ACCEPTED));

        List<Integer> disabledProducers = Arrays.asList ( 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 );
        disableProducers(disabledProducers, feed);

        SimpleMessageListener simpleMessageListener = new SimpleMessageListener("all", feed, config.getDesiredLocales());
        feed.getSessionBuilder().setListener(simpleMessageListener).setMessageInterest(MessageInterest.AllMessages).build();

        Producer producer = feed.getProducerManager().getProducer(producerId);
        assertNotNull(producer);
        assertNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());

        OperationManager.setRabbitHeartbeat(20);
        OperationManager.setRabbitConnectionTimeout(10);

        feed.open();

        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);

        Helper.sleep(1000);
        assertNotNull(producer.getRecoveryInfo());
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        List<String> recoveryCalled = feed.TestHttpHelper.CalledUrls.stream().filter(f -> f.contains("/liveodds/recovery/")).collect(Collectors.toList());
        assertEquals(1, recoveryCalled.size());
        assertEquals(0, recoveryCalled.stream().filter(c -> c.contains("after")).count());
        final long producerRecoveryRequestId1 = producer.getRecoveryInfo().getRequestId();
        assertTrue(checkListContainsString(sdkListener.CalledEvents, "Recovery initiated. RequestId=" + producerRecoveryRequestId1, 1));

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildAlive(producerId));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        UFSnapshotComplete snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer is not down");
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());
        Helper.sleep(6000); // so timestamp for next recovery can be set
        // clear periodic alive messages
        rabbitProducer.ProducersAlive.clear();

        Helper.writeToOutput("First connection and channels checks");
        List<ConnectionInfo> connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        Optional<ConnectionInfo> sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        List<ChannelInfo> channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(2, channels.size());
        String channelId1 = channels.get(0).getName();
        String channelId2 = channels.get(1).getName();
        assertNotEquals(channelId1, channelId2);

        Helper.writeToOutput("Pause for channel timeout");
        Helper.sleep(Duration.ofSeconds(220).toMillis()); // timeout for creating new channel
//        rabbitProducer.addProducersAlive(producerId, 5000);
//        Helper.sleep(Duration.ofSeconds(30).toMillis());

        Helper.writeToOutput("Second connection and channels checks");
        // new channel should be made
        connections = rabbitProducer.ManagementClient.getConnections();
        assertEquals(2, connections.size()); // producer and sdk connection
        sdkConnection = connections.stream().filter(f -> f.getUser().equals(Constants.SDK_USERNAME)).findFirst();
        assertTrue(sdkConnection.isPresent());
        channels = rabbitProducer.ManagementClient.getChannels(sdkConnection.get().getName());
        assertEquals(2, channels.size());
        assertNotEquals(channelId1, channels.get(1).getName());
        assertNotEquals(channelId2, channels.get(1).getName());

        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is down")).count()); // producers are marked down
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Connection to the feed lost")).count()); //Connection to the feed lost

        // reset alives and wait for recovery
        rabbitProducer.addProducersAlive(producerId, 3000);
        waitAndCheckTillTimeout(w -> checkProducerRecovery(producerId, false), "Producer recovery info is not null", 1000, 20000);
        producer = feed.getProducerManager().getProducer(producerId);

        final long producerRecoveryRequestId2 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId2) && !c.contains("After=0")).count());
        assertNotEquals(producerRecoveryRequestId1, producer.getRecoveryInfo().getRequestId());

        // send 2 changeOdds and snapshotComplete for recovery request id - should not trigger producerUp because recovery was interrupted because of alive timeout
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down (" + producer.isFlaggedDown() + ")", 2000, 8000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertTrue(producer.isFlaggedDown()); // true because previous recovery was interrupted because of alive timeout

        // send 2 changeOdds and snapshotComplete for recovery request id - should trigger producerUp
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(null, producerId, producer.getRecoveryInfo().getRequestId(), null));
        rabbitProducer.send(feedMessageBuilder.buildOddsChange(-1L, producerId, producer.getRecoveryInfo().getRequestId(), null));
        assertTrue(producer.isFlaggedDown());
        assertTrue(producer.getRecoveryInfo().getRequestId() > 0);
        snapshotComplete = feedMessageBuilder.buildSnapshotComplete(producerId, producer.getRecoveryInfo().getRequestId(), null);
        rabbitProducer.send(snapshotComplete);

        waitAndCheckTillTimeout(w -> checkProducerFlaggedDown(producerId, false), "Producer 1 is not down (" + producer.isFlaggedDown() + ")", 2000, 30000);
        producer = feed.getProducerManager().getProducer(producerId);
        assertFalse(producer.isFlaggedDown());

        assertFalse(sdkListener.CalledEvents.isEmpty());
        assertEquals(6, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFOddsChange")).count());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Raw feed data") && c.contains("UFSnapshotComplete")).count());
        assertEquals(3, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated")).count());
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated") && c.contains(String.valueOf(producerRecoveryRequestId2)) && !c.contains("After=0")).count()); //
            // initial request
        assertEquals(2, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=") && !c.contains("After=0")).count());
        final long producerRecoveryRequestId3 = producer.getRecoveryInfo().getRequestId();
        assertEquals(1, sdkListener.CalledEvents.stream().filter(c -> c.contains("Recovery initiated. RequestId=" + producerRecoveryRequestId3) && !c.contains("After=0")).count()); //
            // second request
        assertEquals(4, sdkListener.CalledEvents.stream().filter(c -> c.contains("Producer LO is up")).count());

        assertEquals(6, simpleMessageListener.FeedMessages.size());
    }

    private void waitAndCheckTillTimeout(Predicate<Boolean> condition, String message) {
        waitAndCheckTillTimeout(condition, message, Duration.ofMillis(500), Duration.ofMillis(10000));
    }

    private void waitAndCheckTillTimeout(Predicate<Boolean> condition, String message, int checkPeriod, int timeout) {
        waitAndCheckTillTimeout(condition, message, Duration.ofMillis(checkPeriod), Duration.ofMillis(timeout));
    }

    private void waitAndCheckTillTimeout(Predicate<Boolean> condition, String message, Duration checkPeriod, Duration timeout) {
        if (!message.isEmpty())
        {
            message = String.format("'%s' ", message);
        }
        Date start = new Date();
        while (Helper.durationBetweenDatesInMs(start, new Date()) < timeout.toMillis())
        {
            try {
                if (condition.test(true)) {
                    return;
                }
                Helper.writeToOutput(String.format("Condition %snot true (yet) (%sms)", message, Helper.durationBetweenDatesInMs(start, new Date())));
            }
            catch(Exception ex) {
                Helper.writeToOutput(String.format("Condition %snot true yet (error) (%sms)", message, Helper.durationBetweenDatesInMs(start, new Date())));
            }
            Helper.sleep(checkPeriod.toMillis());
        }
        Helper.writeToOutput(String.format("Condition %snot true (final) (%sms)", message, Helper.durationBetweenDatesInMs(start, new Date())));
    }

    private void disableProducers(List<Integer> producerIds, OddsFeed feed) {
        if (producerIds.isEmpty()) {
            return;
        }

        for (Integer producerId : producerIds) {
            feed.getProducerManager().disableProducer(producerId);
        }
    }

    private void setAfterTimestamp(Map<Integer, Date> afters, OddsFeed feed)
    {
        if (afters.isEmpty()) {
            return;
        }

        for (Map.Entry<Integer, Date> after : afters.entrySet()) {
            feed.getProducerManager().setProducerRecoveryFromTimestamp(after.getKey(), after.getValue().getTime());
        }
    }

    /**
     * Checks if the producer RecoveryInfo is equal isNull
     * @param producerId the producer id to check
     * @param isNull check if the producer RecoveryInfo is null
     * @return true if getRecoveryInfo()==null, else false
     */
    private boolean checkProducerRecovery(int producerId, boolean isNull){
        Producer producer = feed.getProducerManager().getProducer(producerId);
        if(isNull){
            return producer.getRecoveryInfo() == null;
        }
        return producer.getRecoveryInfo() != null;
    }

    /**
     * Checks if the producer isFlaggedDown equal to input parameter isDown
     * @param producerId the producer id to check
     * @param isDown check if the producer isFlaggedDown is down
     * @return true if isFlaggedDown()==isDown, else false
     */
    private boolean checkProducerFlaggedDown(int producerId, boolean isDown){
        Producer producer = feed.getProducerManager().getProducer(producerId);
        if(isDown){
            return producer.isFlaggedDown();
        }
        return !producer.isFlaggedDown();
    }

    /**
     * Checks if the producer recoveryId equal to input parameter requestId
     * @param producerId the producer id to check
     * @param requestId check if the producer requestId is equal to this
     * @param findRequestId find the specified requestId, or anything but this
     * @return based on findRequest returns true if producer.getRecoveryInfo().getRequestId() == requestId, else false
     */
    private boolean checkProducerRecovery(int producerId, long requestId, boolean findRequestId){
        Producer producer = feed.getProducerManager().getProducer(producerId);
        if(findRequestId){
            return producer.getRecoveryInfo().getRequestId() == requestId;
        }
        return producer.getRecoveryInfo().getRequestId() != requestId;
    }

    /**
     * Checks if the list contains text correct amount of times
     * @param list to check
     * @param text to check
     * @param count amount to check
     * @return true if isFlaggedDown()==isDown, else false
     */
    private boolean checkListContainsString(List<String> list, String text, int count){
        if(list == null || list.isEmpty()){
            return count == 0;
        }
        boolean result = list.stream().filter(f -> f.contains(text)).count() == count;
        if(!result){
            writeStringList(list, "CheckListContainsString: ");
        }
        return result;
    }

    /**
     * Checks if the list contains text correct amount of times
     * @param list to check
     * @param firstText first text to check
     * @param secondText second text to check
     * @param count amount to check
     * @return true if isFlaggedDown()==isDown, else false
     */
    private boolean checkListContainsString(List<String> list, String firstText, String secondText, int count){
        if(list == null || list.isEmpty()){
            return count == 0;
        }
        boolean result = list.stream().filter(f -> f.contains(firstText) && f.contains(secondText)).count() == count;
        if(!result){
            writeStringList(list, "CheckListContainsString: ");
        }
        return result;
    }

    /**
     * Write the items from the list of strings
     * @param list to write
     * @param pretext each String in text pretext with this
     */
    private void writeStringList(List<String> list, String pretext){
        if(list == null || list.isEmpty()){
            Helper.writeToOutput(pretext + "empty list");
            return;
        }
        for(String item : list){
            Helper.writeToOutput(pretext + item);
        }
    }
}
