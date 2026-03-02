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
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.ConnectionContext;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.RabbitMqChannelImpl;
import com.sportradar.unifiedodds.sdk.testutil.configuration.SdkInternalConfigurationStubs;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.ExchangeLocation;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

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

    private static final Instant FIXED_TIME = Instant.ofEpochMilli(1664402400000L);

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

    RabbitClientCredentialsTest() throws Exception {}

    @AfterEach
    void tearDown() throws TimeoutException {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessageWhenUsingExplicitRabbitCredentials() throws Exception {
        assumeThat(shouldMavenRunTestsExercisingRabbitServer(), equalTo(true));

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
                .withAccessToken("test-token")
                .withSdkNodeId(nodeId)
                .withMessagingUsername(sdkCredentials.getUsername())
                .withMessagingPassword(sdkCredentials.getPassword())
                .withUseMessagingSsl(false)
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(RABBIT_BASE_URL.getHost())
                .withPort(RABBIT_BASE_URL.getPort())
                .withApiHost("anything.com")
                .build();

            try (ConnectionContext connectionContext = new ConnectionContext()) {
                RabbitMqChannelImpl channel = connectionContext
                    .channelBuilder()
                    .with(whoAmIReader)
                    .with(time)
                    .with(deprecatedConfiguration)
                    .withRoutingKeys("-.-.-.alive.#")
                    .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                    .withConsumerDescription("something")
                    .withSdkVersion("test-version")
                    .buildOpened();

                rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
                assertThatEventuallyOneMessageHasBeenReceived();

                channel.close();
            }
        }
    }

    @Timeout(value = 30, unit = SECONDS)
    @Test
    void receivesMessageWhenUsingLegacySsoCredentialsTranslatingToTokenAsUsername() throws Exception {
        String ssoToken = "someSsoToken";
        rabbitMqUserSetup.setupUser(
            Credentials.with(ssoToken, "inProdPasswordIsNotSetBecauseStandardRabbitServerRequiresPassword")
        );

        assumeThat("see developerREADME", shouldMavenRunTestsExercisingRabbitServer(), equalTo(true));

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
                .withAccessToken(ssoToken)
                .withSdkNodeId(nodeId)
                .withUseMessagingSsl(false)
                .withMessagingUsername(null)
                .withMessagingPassword("inProdPasswordIsNotSetBecauseStandardRabbitServerRequiresPassword")
                .withMessagingVirtualHost(UF_VIRTUALHOST)
                .withMessagingHost(RABBIT_BASE_URL.getHost())
                .withPort(RABBIT_BASE_URL.getPort())
                .withApiHost("anything.com")
                .build();

            RabbitMqChannelImpl channel = connectionContext
                .channelBuilder()
                .with(whoAmIReader)
                .with(time)
                .with(deprecatedConfiguration)
                .withRoutingKeys("-.-.-.alive.#")
                .with(ListenerCollectingRabbitMessages.to(rabbitMessagesStorage))
                .withConsumerDescription("something")
                .withSdkVersion("test-version")
                .buildOpened();

            rabbitProducer.send(aliveForProducer1(), RoutingKeys.alive());
            assertThatEventuallyOneMessageHasBeenReceived();

            channel.close();
        }
    }

    private void assertThatEventuallyOneMessageHasBeenReceived() throws IOException {
        await()
            .until(() -> rabbitMessagesStorage.findAlivesOf(MessageInterest.SystemAliveMessages).size() == 1);
    }

    private String aliveForProducer1() {
        return "<alive product=\"1\" timestamp=\"" + time.now() + "\" subscribed=\"1\"/>";
    }
}
