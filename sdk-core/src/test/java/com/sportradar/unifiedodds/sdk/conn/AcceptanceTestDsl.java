/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import lombok.SneakyThrows;
import lombok.val;

@SuppressWarnings({ "VisibilityModifier", "LambdaBodyLength", "ClassFanOutComplexity" })
public class AcceptanceTestDsl {

    public final RabbitMqProducer rabbitProducer;
    public final RoutingKeys routingKeys;
    public final FeedMessageBuilder messages;
    public final WaiterForSingleMessage listinerWaitingFor;

    private AcceptanceTestDsl(
        RabbitMqProducer rabbitProducer,
        GlobalVariables globalVariables,
        MessagesInMemoryStorage messagesStorage
    ) {
        this.rabbitProducer = rabbitProducer;
        this.routingKeys = new RoutingKeys(globalVariables);
        this.messages = new FeedMessageBuilder(globalVariables);
        this.listinerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    }

    public static class Setup {

        private final Credentials sdkCredentials = Credentials.with(
            Constants.SDK_USERNAME,
            Constants.SDK_PASSWORD
        );
        private final VhostLocation vhostLocation = VhostLocation.at(
            RABBIT_BASE_URL,
            Constants.UF_VIRTUALHOST
        );
        private final ExchangeLocation exchangeLocation = ExchangeLocation.at(
            vhostLocation,
            Constants.UF_EXCHANGE
        );
        private final Credentials adminCredentials = Credentials.with(
            Constants.ADMIN_USERNAME,
            Constants.ADMIN_PASSWORD
        );
        private final ConnectionFactory factory = new ConnectionFactory();
        private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
        private GlobalVariables globalVariables;
        private ApiSimulator apiSimulator;
        private BaseUrl sportsApiBaseUrl;
        private Callable<RabbitMqProducer> startRabbitProducer;
        private Callable<UofSdk> startSdk;

        private Setup(WireMockExtension wiremock, GlobalVariables globalVariables) {
            this.globalVariables = globalVariables;
            this.apiSimulator = new ApiSimulator(wiremock.getRuntimeInfo().getWireMock());
            sportsApiBaseUrl = BaseUrl.of("localhost", wiremock.getRuntimeInfo().getHttpPort());
        }

        private Setup(WireMockRule wiremock, GlobalVariables globalVariables) {
            this.globalVariables = globalVariables;
            this.apiSimulator = new ApiSimulator(wiremock);
            sportsApiBaseUrl = BaseUrl.of("localhost", wiremock.port());
        }

        public static Setup context(
            Function<GlobalVariables, GlobalVariables> setGlobalVariables,
            WireMockRule wiremock
        ) {
            return new Setup(wiremock, setGlobalVariables.apply(new GlobalVariables()));
        }

        public static Setup context(
            Function<GlobalVariables, GlobalVariables> setGlobalVariables,
            WireMockExtension wiremock
        ) {
            return new Setup(wiremock, setGlobalVariables.apply(new GlobalVariables()));
        }

        public Setup stubApiBookmakerAndProducersAnd(Function<ApiSimulator, ApiSimulator> stubApi) {
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            stubApi.apply(apiSimulator);
            return this;
        }

        public Setup sdkWithFeed(Function<SdkSetup, SdkSetup> configureSdk) {
            this.startRabbitProducer =
                () ->
                    connectDeclaringExchange(
                        exchangeLocation,
                        adminCredentials,
                        factory,
                        new TimeUtilsImpl()
                    );
            this.startSdk =
                () ->
                    configureSdk
                        .apply(
                            SdkSetup
                                .with(
                                    sdkCredentials,
                                    RABBIT_BASE_URL,
                                    sportsApiBaseUrl,
                                    globalVariables.getNodeId()
                                )
                                .with(ListenerCollectingMessages.to(messagesStorage))
                                .with(ExceptionHandlingStrategy.Throw)
                        )
                        .with1Session()
                        .withOpenedFeed();
            return this;
        }

        @SneakyThrows
        public void runScenario(Scenario scenario) {
            try (val rabbitProducer = startRabbitProducer.call(); val sdk = startSdk.call()) {
                val dsl = new AcceptanceTestDsl(rabbitProducer, globalVariables, messagesStorage);
                scenario.run(sdk, dsl);
            }
        }
    }

    public static interface Scenario {
        public void run(UofSdk sdk, AcceptanceTestDsl dsl);
    }
}
