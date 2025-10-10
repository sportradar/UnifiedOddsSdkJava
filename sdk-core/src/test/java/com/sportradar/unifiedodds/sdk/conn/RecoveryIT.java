/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.requiringAuthorizationHeader;
import static com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.validCommonIamToken;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.util.Locale.ENGLISH;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings(
    { "ClassFanOutComplexity", "UnnecessaryParentheses", "ClassDataAbstractionCoupling", "MagicNumber" }
)
class RecoveryIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    @RegisterExtension
    private static WireMockExtension commonIamWireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final GlobalMessagesInMemoryStorage globalMessagesStorage = new GlobalMessagesInMemoryStorage();

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );

    private final CommonIamData commonIamData = CommonIamData.with(
        Constants.COMMON_IAM_CLIENT_ID,
        Constants.COMMON_IAM_KEY_ID,
        Constants.COMMON_IAM_PRIVATE_KEY
    );

    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
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

    private final WaiterForSingleMessage listenerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final WaitingGlobalMessagesReader globalMessagesReaderWaitingFor = new WaitingGlobalMessagesReader(
        globalMessagesStorage
    );

    private BaseUrl sportsApiBaseUrl;
    private BaseUrl commonIamApiBaseUrl;
    private CommonIamSimulator commonIamSimulator;

    RecoveryIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        commonIamApiBaseUrl = BaseUrl.of("localhost", commonIamWireMock.getPort());
        commonIamSimulator = new CommonIamSimulator(commonIamWireMock.getRuntimeInfo().getWireMock());
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    void tokenIsCachedAndReusedForSubsequentRecoveryApiCalls() throws Exception {
        val producerId = ProducerId.LIVE_ODDS;
        globalVariables.setProducer(producerId);

        val messages = new FeedMessageBuilder(globalVariables);
        val routingKeys = new RoutingKeys(globalVariables);

        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());
        apiSimulator.defineBookmaker(requiringAuthorizationHeader(validCommonIamToken().getHeaderValue()));
        apiSimulator.activateProducer(producerId);
        apiSimulator.stubRecovery(producerId);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(GlobalListenerCollectingMessages.to(globalMessagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed();
        ) {
            rabbitProducer.send(messages.alive(), routingKeys.alive());
            val recoveryInitiated = globalMessagesReaderWaitingFor.firstRecoveryInitiatedMessageEverReceived(
                producerId
            );
            awaitUntil(() -> apiSimulator.isRecoveryEverRequestedFor(producerId));
            rabbitProducer.send(
                messages.snapshotComplete(recoveryInitiated.getRequestId()),
                routingKeys.snapshotComplete()
            );
            globalMessagesReaderWaitingFor.firstProducerUpEverReceived(producerId);
        }
    }

    private void awaitUntil(Callable<Boolean> predicate) {
        int testSecondsForSlowMachines = 10;
        await().atMost(testSecondsForSlowMachines, TimeUnit.SECONDS).until(predicate);
    }
}
