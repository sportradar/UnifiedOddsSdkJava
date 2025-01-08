/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.exactGoalsMarket;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.UnifiedOddsStatisticsJmxIT.SdkJmxAttribute.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.conn.RoutingKeys;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.rmi.registry.LocateRegistry;
import java.util.Locale;
import javax.management.*;
import javax.management.remote.*;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
class UnifiedOddsStatisticsJmxIT {

    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final ObjectName sdkStatisticsObject = new ObjectName(
        "com.sportradar.unifiedodds.sdk.impl:type=UnifiedOdds"
    );
    private JMXServiceURL jmxUrl;

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(WIRE_MOCK.getRuntimeInfo().getWireMock());
    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
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
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private final WaiterForSingleMessage listinerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    UnifiedOddsStatisticsJmxIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", WIRE_MOCK.getPort());

        val jmxPort = findFreePort();
        val mBeanServer = ManagementFactory.getPlatformMBeanServer();
        LocateRegistry.createRegistry(jmxPort);
        jmxUrl = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi");

        val connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, mBeanServer);
        connectorServer.start();
    }

    @Test
    public void messageStatisticsAreUpdatedUponMessageArrival() throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        com.sportradar.unifiedodds.sdk.conn.RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(exactGoalsMarketDescription(), aLanguage);
        apiSimulator.stubVariantListContaining(fivePlusVariantDescription(), aLanguage);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            assertThat(jmxAttribute(NumberOfMessagesReceived)).isEqualTo(0);

            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            rabbitProducer.send(
                messages.betCancel(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveBetCancel()
            );

            listinerWaitingFor.theOnlyOddsChange();
            listinerWaitingFor.theOnlyBetCancel();

            assertThat(jmxAttribute(NumberOfMessagesReceived)).isEqualTo(2);
            assertThat(jmxAttribute(NumberOfOddsChangesReceived)).isEqualTo(1);
            assertThat(jmxAttribute(NumberOfBetCancelsReceived)).isEqualTo(1);
        }
    }

    @SneakyThrows
    private Object jmxAttribute(SdkJmxAttribute attributeName) {
        try (JMXConnector connector = JMXConnectorFactory.connect(jmxUrl)) {
            MBeanServerConnection connection = connector.getMBeanServerConnection();
            return connection.getAttribute(sdkStatisticsObject, attributeName.name());
        }
    }

    private static int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    enum SdkJmxAttribute {
        NumberOfMessagesReceived,
        NumberOfOddsChangesReceived,
        NumberOfBetCancelsReceived,
    }
}
