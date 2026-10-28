/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.build.preconditions.PreconditionsForRabbitRequiringUnitTests.shouldMavenRunTestsExercisingRabbitServer;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.simpleStub;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.lang.String.valueOf;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.*;
import com.sportradar.unifiedodds.sdk.testutil.configuration.SdkInternalConfigurationStubs;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.ExchangeLocation;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import lombok.val;
import org.junit.jupiter.api.*;

@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "HiddenField",
        "IllegalCatch",
        "JavaNCSS",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "MultipleStringLiterals",
        "OverloadMethodsDeclarationOrder",
        "ParameterAssignment",
    }
)
class RabbitClientTest {

    private static final Instant FIXED_TIME = Instant.ofEpochMilli(1664402400000L);

    private final String commonIamToken = "abc123";
    private final int bookmakerId = 8736;
    private final int nodeId = 1234;
    private final Credentials sdkCredentials = Credentials.with(valueOf(bookmakerId), commonIamToken);
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
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(FIXED_TIME);

    RabbitClientTest() throws Exception {}

    @BeforeAll
    static void doesNotExecuteAnyTestsIfRabbitServerNotSetUp() throws Exception {
        assumeThat(shouldMavenRunTestsExercisingRabbitServer(), equalTo(true));
    }

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
    }

    @AfterEach
    void tearDown() throws Exception {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessage() throws Exception {
        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
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
            config.setAccessToken("test-token");

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

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    private String aliveForProducer1() {
        return "<alive product=\"1\" timestamp=\"" + time.now() + "\" subscribed=\"1\"/>";
    }
}
