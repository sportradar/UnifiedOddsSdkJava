package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.impl.Constants.ODDS_CHANGE_KEY;
import static org.mockito.Mockito.times;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "MagicNumber", "VisibilityModifier" })
public class ChannelMessageConsumerTest {

    final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );

    ChannelMessageConsumer chanMsgConsumer;

    @Before
    public void setup() {
        chanMsgConsumer = injector.getInstance(ChannelMessageConsumer.class);
    }

    @Test
    public void nullDataRaisesDeserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] nullData = null;

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, nullData, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.any(), Mockito.any());
    }

    @Test
    public void emptyDataRaisesDeserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] emptyData = new byte[0];

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, emptyData, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.any(), Mockito.any());
    }

    @Test(expected = IllegalStateException.class)
    public void throwsOnReceiveEventsBeforeReceiverIsOpened() throws Exception {
        byte[] data = oddsChangeBytes();

        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L);
    }

    @Test
    public void invokesOnMsgReceivedAfterReceiverIsOpened() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L);

        //Verify
        Mockito
            .verify(msgConsumer)
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test(expected = NullPointerException.class)
    public void throwsForNullRoutingKey() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        //Execute
        chanMsgConsumer.onMessageReceived(null, data, null, 0L);
    }

    @Test
    public void invokesOnMsgReceivedForUnparsableRoutingKey() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        //Execute
        chanMsgConsumer.onMessageReceived("routing_key", data, null, 0L);

        //Verify
        Mockito
            .verify(msgConsumer)
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test
    public void deserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = new byte[] { 1, 2, 3, 4 }; //this can't be deserialized

        //Execute
        chanMsgConsumer.onMessageReceived("routing_key", data, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.eq(data), Mockito.any());
    }

    @Test
    public void testUnmarshallerThreadSafe() throws Exception {
        final int totalThreads = 20;
        final int invocationsPerThread = 1000;
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);
        byte[] data = oddsChangeBytes();

        CountDownLatch finished = new CountDownLatch(totalThreads);
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

        List<Callable<Void>> callableTasks = new ArrayList<>();
        for (int i = 0; i < totalThreads; i++) {
            callableTasks.add(() -> {
                int count = 0;
                while (count < invocationsPerThread) {
                    chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L);
                    count++;
                }
                finished.countDown();
                return null;
            });
        }

        executor.invokeAll(callableTasks);
        finished.await();

        executor.shutdownNow();

        //Verify
        Mockito
            .verify(msgConsumer, times(invocationsPerThread * totalThreads))
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test
    public void getsConsumerDescription() {
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = new byte[] { 1, 2, 3, 4 }; //this can't be deserialized

        chanMsgConsumer.onMessageReceived("routing_key", data, null, 0L);

        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.eq(data), Mockito.any());
    }

    private byte[] oddsChangeBytes() throws Exception {
        return Files.readAllBytes(
            Paths.get(this.getClass().getClassLoader().getResource(Constants.ODDS_CHANGE_MSG_URI).toURI())
        );
    }
}
