/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.build.preconditions.PreconditionsForRabbitRequiringUnitTests.shouldMavenRunTestsExercisingRabbitServer;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.simpleStub;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.lang.String.valueOf;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mockStatic;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofPrivateKeyJwtAuthenticationStub;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit.ProxiedRabbit;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures;
import com.sportradar.unifiedodds.sdk.internal.impl.RuntimeConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.*;
import com.sportradar.unifiedodds.sdk.testutil.configuration.SdkInternalConfigurationStubs;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import lombok.val;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

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
class RabbitClientCredentialsTest {

    private final Instant fixedTime = Instant.ofEpochMilli(1664402400000L);
    private final int bookmakerId = 8736;
    private final int nodeId = 1234;
    private final Credentials sdkCredentials = Credentials.with(
        RabbitClientCredentialsTest.class.getSimpleName(),
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
    private final RabbitConnections connections = new RabbitConnections(rabbitMqClient);
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(fixedTime);
    private ProxiedRabbit proxy;
    private final int maxChannelIdleTimeInSecondsBeforeReconnecting = 300;
    private final Class<SdkExceptionHandler> sdkExceptionHandler = SdkExceptionHandler.class;
    private final Class<SingleInstanceAmqpConnectionFactory> singleInstanceAmqpConnectionFactoryClass =
        SingleInstanceAmqpConnectionFactory.class;

    RabbitClientCredentialsTest() throws Exception {}

    @BeforeAll
    static void doesNotExecuteAnyTestsIfRabbitServerNotSetUp() throws Exception {
        assumeThat(shouldMavenRunTestsExercisingRabbitServer(), equalTo(true));
    }

    @BeforeEach
    void setup() throws Exception {
        proxy = ProxiedRabbit.proxyRabbit();
    }

    @AfterEach
    void tearDown() throws Exception {
        proxy.close();
        rabbitMqUserSetup.revertChangesMade();
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessageWhenUsingExplicitRabbitCredentials() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
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
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setAccessToken("test-token");

            try (ConnectionContext connectionContext = new ConnectionContext()) {
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
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessageWhenUsingLegacySsoCredentialsTranslatingToTokenAsUsername() throws Exception {
        String ssoToken = "someSsoToken";
        rabbitMqUserSetup.setupUser(
            Credentials.with(
                ssoToken,
                "inProdPasswordIsNotSetButStandardRabbitServerUsedInTestsRequiresPassword"
            )
        );

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
                .withUseMessagingSsl(false)
                .withMessagingUsername(null)
                .withMessagingPassword(
                    "inProdPasswordIsNotSetButStandardRabbitServerUsedInTestsRequiresPassword"
                )
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setAccessToken(ssoToken);

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

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    void legacySsoCredentialsIsNotLeakedOnConnectionErrors() throws Exception {
        String ssoToken = "someSsoToken";
        rabbitMqUserSetup.setupUser(
            Credentials.with(
                ssoToken,
                "inProdPasswordIsNotSetButStandardRabbitServerUsedInTestsRequiresPassword"
            )
        );

        val logsMock = LogsMock.createCapturingFor(singleInstanceAmqpConnectionFactoryClass);

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
                .withUseMessagingSsl(false)
                .withMessagingUsername(null)
                .withMessagingPassword("passwordDoesNotMatchToImitateConnectionFailure")
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setAccessToken(ssoToken);

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

            await()
                .untilAsserted(() ->
                    logsMock.verifyLoggedLineContaining(
                        "ACCESS_REFUSED - Login was refused using authentication mechanism PLAIN. For details see the broker logfile"
                    )
                );
            logsMock.verifyNotLoggedLineContaining(ssoToken);

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    void noCredentialIsLoggedOnCredentialRejectionFromRabbit() throws Exception {
        rabbitMqUserSetup.setupUser(Credentials.with("userNotTheOneToBeUsed", "somePassword"));

        val logsMock = LogsMock.createCapturingFor(singleInstanceAmqpConnectionFactoryClass);

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
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
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

            await().untilAsserted(() -> logsMock.verifyLoggedLineContaining("Error creating connection"));
            logsMock.verifyNotLoggedLineContaining(sdkCredentials.getUsername());
            logsMock.verifyNotLoggedLineContaining(sdkCredentials.getPassword());

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessageWhenUsingCommonIamCredentials() throws Exception {
        String commonIamToken = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamToken));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = providingBearerToken(commonIamToken);

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

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
                .with(tokenCache)
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void attemptingToReconnectOnChannelInspectionBeforeExpiryUsesOldToken() throws Exception {
        String commonIamTokenToBeSetAsPassword = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), "incorrectPassword"));
        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = providingBearerToken(commonIamTokenToBeSetAsPassword);

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

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
                .with(tokenCache)
                .buildOpened();

            rabbitMqUserSetup.updateUser(
                Credentials.with(valueOf(bookmakerId), commonIamTokenToBeSetAsPassword)
            );

            channel.checkStatus();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void attemptingToReconnectOnChannelInspectionAfterExpiryUsesNewToken() throws Exception {
        String commonIamTokenSetAsPassword = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassword));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = builder()
                .providingBearerToken("someOtherToken")
                .afterExpiryProviding(commonIamTokenSetAsPassword)
                .build();

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

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
                .with(tokenCache)
                .buildOpened();

            tokenCache.firstTokenExpires();

            channel.checkStatus();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void failureToRetrieveTokenIsFollowedByReconnectionAttemptsOnEachChannelInspectionUsingNewTokenEachTime()
        throws Exception {
        String commonIamTokenSetAsPassowrd = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassowrd));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = failingWithOAuth2TokenRetrievalExceptionFollowedByProvidingToken(
                commonIamTokenSetAsPassowrd
            );

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

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
                .with(tokenCache)
                .buildOpened();

            time.tick(seconds(30));

            channel.checkStatus();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void tokenExpiryAfterConnectionCreationDoesNotAffectExistingConnection() throws Exception {
        String commonIamTokenSetAsPassowrd = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassowrd));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = builder()
                .providingBearerToken(commonIamTokenSetAsPassowrd)
                .afterExpiryProviding("newTokenAfterExpiry")
                .build();

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

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
                .with(tokenCache)
                .buildOpened();

            tokenCache.firstTokenExpires();

            channel.checkStatus();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.theOnlyAliveMessage();

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    void afterDisconnectInitiatedBySdkDueToProlongedPeriodOfNotReceivingMessagesUsesNewCommonIamTokenToReconnect()
        throws Exception {
        val commonIamTokenSetAsPassword = "abc123";
        val newTokenAfterExpiry = "newTokenAfterExpiry";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassword));

        val logsMock = LogsMock.createCapturingFor(singleInstanceAmqpConnectionFactoryClass);

        time.travelTo(fixedTime);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext();
        ) {
            val tokenCache = builder()
                .providingBearerToken(commonIamTokenSetAsPassword)
                .afterExpiryProviding(newTokenAfterExpiry)
                .build();
            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();

            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
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
                .with(tokenCache)
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            waiterFor.theOnlyAliveMessage();
            rabbitMqUserSetup.updateUser(Credentials.with(valueOf(bookmakerId), newTokenAfterExpiry));

            time.tick(seconds(maxChannelIdleTimeInSecondsBeforeReconnecting + 1));
            tokenCache.firstTokenExpires();

            connections
                .assertThatConnectionForUsername(valueOf(bookmakerId))
                .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                    expected -> expected.queues(1).channels(1),
                    awaitUntil -> {
                        channel.checkStatus();

                        awaitUntil.connectionRecreated();
                    }
                );

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));
            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    void afterDisconnectCausedByNetworkBlipUsesNewCommonIamTokenToReconnectWhenOldTokenExpires()
        throws Exception {
        val commonIamTokenSetAsPassword = "abc123";
        val newTokenAfterExpiry = "newTokenAfterExpiry";

        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassword));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext();
        ) {
            val tokenCache = builder()
                .providingBearerToken(commonIamTokenSetAsPassword)
                .afterExpiryProviding(newTokenAfterExpiry)
                .build();
            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();

            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
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
                .with(tokenCache)
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            waiterFor.theOnlyAliveMessage();

            rabbitMqUserSetup.updateUser(Credentials.with(valueOf(bookmakerId), newTokenAfterExpiry));
            tokenCache.firstTokenExpires();

            connections
                .assertThatConnectionForUsername(valueOf(bookmakerId))
                .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                    expected -> expected.queues(1).channels(1),
                    awaitUntil -> {
                        simulateAdHocDisconnectByIntermediaryNetworkInfrastructure();

                        awaitUntil.connectionRecreated();
                    }
                );

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));

            channel.close();
        }
    }

    private void simulateAdHocDisconnectByIntermediaryNetworkInfrastructure() throws IOException {
        proxy.disable();
        proxy.enable();
    }

    @Timeout(value = 60, unit = SECONDS)
    @Test
    @SuppressWarnings("VariableDeclarationUsageDistance")
    void rabbitAutoRecoveryUsesNewCommonIamTokenToReconnectAfterProlongedPacketLossWhenOldTokenIsExpired()
        throws Exception {
        val commonIamTokenSetAsPassword = "abc123";
        val newTokenAfterExpiry = "newTokenAfterExpiry";

        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamTokenSetAsPassword));

        val logsMock = LogsMock.createCapturingFor(sdkExceptionHandler);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext();
            MockedStatic<RuntimeConfiguration> runtimeConfiguration = mockStatic(RuntimeConfiguration.class);
        ) {
            val tokenCache = builder()
                .providingBearerToken(commonIamTokenSetAsPassword)
                .afterExpiryProviding(newTokenAfterExpiry)
                .build();
            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();

            runtimeConfiguration.when(RuntimeConfiguration::getRabbitHeartbeat).thenReturn(1);

            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
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
                .with(tokenCache)
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            waiterFor.theOnlyAliveMessage();
            rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), newTokenAfterExpiry));
            tokenCache.firstTokenExpires();

            connections
                .assertThatConnectionForUsername(valueOf(bookmakerId))
                .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                    expected -> expected.queues(1).channels(1),
                    awaitUntil -> {
                        goThroughNetworkOutageToTriggerRabbitAutoRecovering(logsMock);

                        awaitUntil.connectionRecreated();
                    }
                );

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));

            channel.close();
        }
    }

    @Nested
    class RabbitUsernameAndPasswordExplicitlySetWithClientAuthentication {

        @Timeout(value = 30, unit = SECONDS)
        @Test
        void receivesMessageUsingExplicitRabbitUsernameAndPassword() throws Exception {
            rabbitMqUserSetup.setupUser(sdkCredentials);

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                )
            ) {
                WhoAmIReader whoAmIReader = simpleStub()
                    .withBookmakerId(bookmakerId)
                    .withAnyMdcContextMap()
                    .withVirtualHost(Constants.UF_VIRTUALHOST)
                    .build();

                SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                    .simpleStub()
                    .withSdkNodeId(nodeId)
                    .withUseMessagingSsl(false)
                    .withMessagingVirtualHost(UF_VIRTUALHOST)
                    .withMessagingUsername(sdkCredentials.getUsername())
                    .withMessagingPassword(sdkCredentials.getPassword())
                    .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                    .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                    .withApiHost("anyhost.local")
                    .build();

                UofConfigurationStub config = new UofConfigurationStub();
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

                try (ConnectionContext connectionContext = new ConnectionContext()) {
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
        }

        @Timeout(value = 30, unit = SECONDS)
        @Test
        @SuppressWarnings("VariableDeclarationUsageDistance")
        void receivesMessageUsingExplicitRabbitUsernameAndPasswordAlsoAfterDisconnectCausedByNetworkBlip()
            throws Exception {
            rabbitMqUserSetup.setupUser(sdkCredentials);

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                );
                val connectionContext = new ConnectionContext()
            ) {
                val whoAmIReader = simpleStub()
                    .withBookmakerId(bookmakerId)
                    .withAnyMdcContextMap()
                    .withVirtualHost(Constants.UF_VIRTUALHOST)
                    .build();

                val deprecatedConfiguration = SdkInternalConfigurationStubs
                    .simpleStub()
                    .withSdkNodeId(nodeId)
                    .withUseMessagingSsl(false)
                    .withMessagingVirtualHost(UF_VIRTUALHOST)
                    .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                    .withMessagingUsername(sdkCredentials.getUsername())
                    .withMessagingPassword(sdkCredentials.getPassword())
                    .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                    .withApiHost("anyhost.local")
                    .build();

                val config = new UofConfigurationStub();

                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val channel = connectionContext
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

                connections
                    .assertThatConnectionForUsername(sdkCredentials.getUsername())
                    .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                        expected -> expected.queues(1).channels(1),
                        awaitUntil -> {
                            simulateAdHocDisconnectByIntermediaryNetworkInfrastructure();

                            awaitUntil.connectionRecreated();
                        }
                    );

                rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
                waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));

                channel.close();
            }
        }

        @Timeout(value = 30, unit = SECONDS)
        @Test
        void receivesMessageUsingOnlyExplicitRabbitUsernameAndCommonIamTokenAsPassword() throws Exception {
            val commonIamTokenSetAsPassword = "abc123";
            val overriddenUsername = "some-username";

            val tokenCache = providingBearerToken(commonIamTokenSetAsPassword);
            rabbitMqUserSetup.setupUser(Credentials.with(overriddenUsername, commonIamTokenSetAsPassword));

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                )
            ) {
                val whoAmIReader = simpleStub()
                    .withBookmakerId(bookmakerId)
                    .withAnyMdcContextMap()
                    .withVirtualHost(Constants.UF_VIRTUALHOST)
                    .build();

                val deprecatedConfiguration = SdkInternalConfigurationStubs
                    .simpleStub()
                    .withSdkNodeId(nodeId)
                    .withUseMessagingSsl(false)
                    .withMessagingVirtualHost(UF_VIRTUALHOST)
                    .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                    .withMessagingUsername(overriddenUsername)
                    .withMessagingPassword(null)
                    .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                    .withApiHost("anyhost.local")
                    .build();

                val config = new UofConfigurationStub();
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

                try (val connectionContext = new ConnectionContext()) {
                    val channel = connectionContext
                        .channelBuilder()
                        .with(whoAmIReader)
                        .with(time)
                        .with(deprecatedConfiguration)
                        .with(config)
                        .withRoutingKeys("-.-.-.alive.#")
                        .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                        .withConsumerDescription("something")
                        .withSdkVersion("test-version")
                        .with(tokenCache)
                        .buildOpened();

                    rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
                    waiterFor.theOnlyAliveMessage();

                    channel.close();
                }
            }
        }

        @Timeout(value = 30, unit = SECONDS)
        @Test
        @SuppressWarnings("VariableDeclarationUsageDistance")
        void receivesMessageUsingOnlyExplicitRabbitUsernameAfterDisconnectCausedByNetworkBlipUsesNewCommonIamTokenToReconnect()
            throws Exception {
            val immediatelyExpiringToken = "abc123";
            val newTokenAfterExpiry = "newTokenAfterExpiry";
            val overriddenUsername = "some-username-set-by-client";

            rabbitMqUserSetup.setupUser(Credentials.with(overriddenUsername, immediatelyExpiringToken));

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                );
                val connectionContext = new ConnectionContext()
            ) {
                val tokenCache = builder()
                    .providingBearerToken(immediatelyExpiringToken)
                    .afterExpiryProviding(newTokenAfterExpiry)
                    .build();
                val whoAmIReader = simpleStub()
                    .withBookmakerId(bookmakerId)
                    .withAnyMdcContextMap()
                    .withVirtualHost(Constants.UF_VIRTUALHOST)
                    .build();

                val deprecatedConfiguration = SdkInternalConfigurationStubs
                    .simpleStub()
                    .withSdkNodeId(nodeId)
                    .withUseMessagingSsl(false)
                    .withMessagingVirtualHost(UF_VIRTUALHOST)
                    .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                    .withMessagingUsername(overriddenUsername)
                    .withMessagingPassword(null)
                    .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                    .withApiHost("anyhost.local")
                    .build();

                val config = new UofConfigurationStub();

                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val channel = connectionContext
                    .channelBuilder()
                    .with(whoAmIReader)
                    .with(time)
                    .with(deprecatedConfiguration)
                    .with(config)
                    .withRoutingKeys("-.-.-.alive.#")
                    .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                    .withConsumerDescription("something")
                    .withSdkVersion("test-version")
                    .with(tokenCache)
                    .buildOpened();

                rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

                waiterFor.theOnlyAliveMessage();
                rabbitMqUserSetup.updateUser(Credentials.with(overriddenUsername, newTokenAfterExpiry));
                tokenCache.firstTokenExpires();

                connections
                    .assertThatConnectionForUsername(overriddenUsername)
                    .hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
                        expected -> expected.queues(1).channels(1),
                        awaitUntil -> {
                            simulateAdHocDisconnectByIntermediaryNetworkInfrastructure();

                            awaitUntil.connectionRecreated();
                        }
                    );

                rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
                waiterFor.nthMessage(2, with -> with.routingKey("-.-.-.alive.-.-.-.-"));

                channel.close();
            }
        }

        @Timeout(value = 30, unit = SECONDS)
        @Test
        void receivesMessageUsingOnlyExplicitRabbitPasswordAndBookmakerIsAsUsername() throws Exception {
            val overriddenPassword = "some-password";

            rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), overriddenPassword));

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                )
            ) {
                val whoAmIReader = simpleStub()
                    .withBookmakerId(bookmakerId)
                    .withAnyMdcContextMap()
                    .withVirtualHost(Constants.UF_VIRTUALHOST)
                    .build();

                val deprecatedConfiguration = SdkInternalConfigurationStubs
                    .simpleStub()
                    .withSdkNodeId(nodeId)
                    .withUseMessagingSsl(false)
                    .withMessagingVirtualHost(UF_VIRTUALHOST)
                    .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                    .withMessagingUsername(null)
                    .withMessagingPassword(overriddenPassword)
                    .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                    .withApiHost("anyhost.local")
                    .build();

                val config = new UofConfigurationStub();
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

                try (val connectionContext = new ConnectionContext()) {
                    val channel = connectionContext
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
        }
    }

    private void goThroughNetworkOutageToTriggerRabbitAutoRecovering(LogsMock logsMock) throws IOException {
        proxy.dropAllPackets();
        await()
            .atMost(25, SECONDS)
            .untilAsserted(() -> logsMock.verifyLoggedExceptionMessageContaining("Heartbeat missing"));
        proxy.passAllPackets();
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void exceptionHandlerIsRegisteredWhenUsingCommonIamCredentials() throws Exception {
        val logsMock = LogsMock.createCapturingFor(sdkExceptionHandler);

        String commonIamToken = "abc123";
        rabbitMqUserSetup.setupUser(Credentials.with(valueOf(bookmakerId), commonIamToken));

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            ConnectionContext connectionContext = new ConnectionContext()
        ) {
            val tokenCache = providingBearerToken(commonIamToken);

            WhoAmIReader whoAmIReader = simpleStub()
                .withBookmakerId(bookmakerId)
                .withAnyMdcContextMap()
                .withVirtualHost(Constants.UF_VIRTUALHOST)
                .build();

            SdkInternalConfiguration deprecatedConfiguration = SdkInternalConfigurationStubs
                .simpleStub()
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withMessagingUsername(null)
                .withMessagingPassword(null)
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            RabbitMqChannelImpl channel = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .with(config)
                .withRoutingKeys("-.-.-.alive.#")
                .with((routingKey, body, properties, receivedAt) -> {
                    throw new Error("Error instead of Exception to bypass a catch in production code");
                })
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .with(tokenCache)
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            await()
                .untilAsserted(() -> logsMock.verifyLoggedLineContaining("Consumer exception for channel"));

            channel.close();
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void exceptionHandlerIsRegisteredWhenUsingLegacySsoTokenCredentials() throws Exception {
        val logsMock = LogsMock.createCapturingFor(sdkExceptionHandler);

        String ssoToken = "someSsoToken";
        rabbitMqUserSetup.setupUser(
            Credentials.with(
                ssoToken,
                "inProdPasswordIsNotSetBecauseStandardRabbitServerUsedInTestsRequiresPassword"
            )
        );

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
                .withUseMessagingSsl(false)
                .withMessagingUsername(null)
                .withMessagingPassword(
                    "inProdPasswordIsNotSetBecauseStandardRabbitServerUsedInTestsRequiresPassword"
                )
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(PROXIED_RABBIT_BASE_URL.getHost())
                .withPort(PROXIED_RABBIT_BASE_URL.getPort())
                .withApiHost("anyhost.local")
                .build();

            UofConfigurationStub config = new UofConfigurationStub();
            config.setAccessToken(ssoToken);

            RabbitMqChannelImpl channel = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .with(config)
                .withRoutingKeys("-.-.-.alive.#")
                .with((routingKey, body, properties, receivedAt) -> {
                    throw new Error("Error instead of Exception to bypass a catch in production code");
                })
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());

            await()
                .untilAsserted(() -> logsMock.verifyLoggedLineContaining("Consumer exception for channel"));

            channel.close();
        }
    }

    private String aliveForProducer1() {
        return "<alive product=\"1\" timestamp=\"" + time.now() + "\" subscribed=\"1\"/>";
    }
}
