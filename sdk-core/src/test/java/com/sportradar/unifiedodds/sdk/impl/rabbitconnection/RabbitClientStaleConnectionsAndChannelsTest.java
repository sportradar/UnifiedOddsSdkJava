/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.build.preconditions.PreconditionsForRabbitRequiringUnitTests.shouldMavenRunTestsExercisingRabbitServer;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.simpleStub;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.time.TimeInterval.minutes;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.*;
import com.sportradar.unifiedodds.sdk.testutil.configuration.SdkInternalConfigurationStubs;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import lombok.val;
import org.junit.jupiter.api.*;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "MultipleStringLiterals" })
public class RabbitClientStaleConnectionsAndChannelsTest {

    private static final int CHANNEL_IDLE_TIMEOUT_IN_SECONDS_BEFORE_RECONNECTING = 180;
    private static final int LESS_THAN_CHANNEL_IDLE_TIMEOUT_IN_SECONDS_BEFORE_RECONNECTING =
        CHANNEL_IDLE_TIMEOUT_IN_SECONDS_BEFORE_RECONNECTING - 1;
    private final Instant fixedTime = Instant.ofEpochMilli(1664402400000L);
    private final int bookmakerId = 8736;
    private final int nodeId = 1234;

    private final Credentials sdkCredentials = Credentials.with(
        RabbitClientStaleConnectionsAndChannelsTest.class.getSimpleName(),
        Constants.SDK_PASSWORD
    );
    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(
        vhostLocation,
        Constants.UF_EXCHANGE
    );
    private final Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private final ConnectionFactory factory = new ConnectionFactory();
    private final RabbitMessagesInMemoryStorage rabbitMessagesStorage = new RabbitMessagesInMemoryStorage();
    private final WaiterForRabbitMessages waiterFor = new WaiterForRabbitMessages(rabbitMessagesStorage);
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitConnections connectionUtils = new RabbitConnections(rabbitMqClient);
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(fixedTime);

    RabbitClientStaleConnectionsAndChannelsTest() throws Exception {}

    @BeforeAll
    static void doesNotExecuteAnyTestsIfRabbitServerNotSetUp() throws Exception {
        assumeThat(shouldMavenRunTestsExercisingRabbitServer(), equalTo(true));
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    public void connectionWithoutMessagesFlowingIsNeverRestartedIfChannelsAreNeverInspected()
        throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext();
        ) {
            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withMessagingUsername(sdkCredentials.getUsername())
                .withMessagingPassword(sdkCredentials.getPassword())
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(RABBIT_BASE_URL.getHost())
                .withPort(RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();

            RabbitMqChannelImpl channel = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .with(config)
                .withRoutingKeys("-.-.-.alive.#")
                .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.-.-.-.-");
            waiterFor.theOnlyAliveMessage();

            connectionUtils
                .assertThatConnectionForUsername(sdkCredentials.getUsername())
                .hasSameQueueNamesBeforeAndAfter(
                    expected -> expected.channels(1).queues(1),
                    () -> {
                        time.tick(minutes(10));

                        rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.-.-.-.-");
                        waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));
                    }
                );

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings({ "VariableDeclarationUsageDistance", "LambdaBodyLength" })
    public void restartsAllChannelsAndConnectionWhenInspectionIdentifiesChannelUsingStaleConnection()
        throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext();
        ) {
            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withMessagingUsername(sdkCredentials.getUsername())
                .withMessagingPassword(sdkCredentials.getPassword())
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(RABBIT_BASE_URL.getHost())
                .withPort(RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();

            RabbitMqChannelImpl channel1 = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .with(config)
                .withRoutingKeys("-.-.-.alive.1")
                .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .buildOpened();

            RabbitMqChannelImpl channel2 = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .with(config)
                .withRoutingKeys("-.-.-.alive.2")
                .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.1");
            waiterFor.nthMessage(1, with -> with.routingKey("-.-.-.alive.1"));
            rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.2");
            waiterFor.nthMessage(1, with -> with.routingKey("-.-.-.alive.2"));

            connectionUtils
                .assertThatConnectionForUsername(sdkCredentials.getUsername())
                .hasSameQueueNamesBeforeAndAfter(
                    expected -> expected.queues(2).channels(2),
                    () -> {
                        channel1.checkStatus();
                        channel2.checkStatus();

                        time.tick(seconds(LESS_THAN_CHANNEL_IDLE_TIMEOUT_IN_SECONDS_BEFORE_RECONNECTING));

                        channel1.checkStatus();
                        channel2.checkStatus();

                        rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.1");
                        waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.1"));
                        //simulate that no messages are flowing through channel2
                        //during the idle timeout period before reconnecting

                    }
                )
                .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                    expected -> expected.queues(2).channels(2),
                    awaitUntil -> {
                        time.tick(seconds(CHANNEL_IDLE_TIMEOUT_IN_SECONDS_BEFORE_RECONNECTING));

                        channel2.checkStatus();

                        awaitUntil
                            .connectionRecreated()
                            .queuesRecreated(expected -> expected.queues(1).channels(1));

                        channel1.checkStatus();

                        awaitUntil.queuesRecreated(expected -> expected.queues(2).channels(2));

                        rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.1");
                        waiterFor.nthMessage(3, with -> with.routingKey("-.-.-.alive.1"));
                        rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.2");
                        waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.2"));
                    }
                );

            rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.1");
            waiterFor.nthMessage(4, with -> with.routingKey("-.-.-.alive.1"));
            rabbitProducer.send(aliveForProducer1(), "-.-.-.alive.2");
            waiterFor.nthMessage(3, with -> with.routingKey("-.-.-.alive.2"));

            channel1.close();
            channel2.close();
        }
    }

    private String aliveForProducer1() {
        return "<alive product=\"1\" timestamp=\"" + time.now() + "\" subscribed=\"1\"/>";
    }
}
