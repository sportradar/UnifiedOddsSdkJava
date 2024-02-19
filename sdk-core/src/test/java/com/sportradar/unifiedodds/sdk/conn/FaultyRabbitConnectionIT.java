/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.PeriodicAliveSender.periodicAliveSender;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.integrationtest.preconditions.PreconditionsForProxiedRabbitIntegrationTests.shouldMavenRunToxiproxyIntegrationTests;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit.ProxiedRabbit;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
public class FaultyRabbitConnectionIT {

    private static final int MILLIS_IN_SECOND = 1000;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private Credentials sdkCredentials = Credentials.with(Constants.SDK_USERNAME, Constants.SDK_PASSWORD);
    private VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private ExchangeLocation exchangeLocation = ExchangeLocation.at(vhostLocation, Constants.UF_EXCHANGE);
    private Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private ConnectionFactory factory = new ConnectionFactory();
    private RawMessagesInMemoryStorage rawMessagesStorage = new RawMessagesInMemoryStorage();
    private MessagesAssertions messagesAssertions = new MessagesAssertions(rawMessagesStorage);
    private ProxiedRabbit proxy;
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    public FaultyRabbitConnectionIT() throws Exception {}

    @Before
    public void setup() throws Exception {
        if (shouldMavenRunToxiproxyIntegrationTests()) {
            proxy = ProxiedRabbit.proxyRabbit();
        }
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMockRule.port());
    }

    @After
    public void tearDownProxy() throws Exception {
        if (shouldMavenRunToxiproxyIntegrationTests()) {
            proxy.close();
        }
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    public void messagesAreNotDuplicatedAfterNetworkOutage()
        throws InitException, IOException, TimeoutException, InterruptedException {
        assumeThat("see developerREADME", shouldMavenRunToxiproxyIntegrationTests(), equalTo(true));
        wireMockRule.stubFor(get(anyUrl()).willReturn(WireMock.ok()));
        stubBookmaker();
        enableOnlyLiveProducer();
        final int nodeId = 1;

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, PROXIED_RABBIT_BASE_URL, sportsApiBaseUrl, nodeId)
                .with(ListenerCollectingRawMessages.to(rawMessagesStorage))
                .with1Session()
                .withDefaultLanguage(Locale.ENGLISH)
                .withOpenedFeed();
            val periodicAlives = periodicAliveSender(rabbitProducer);
        ) {
            periodicAlives.startSendingToLiveProducer();
            assertThatSdkReceivesSystemAlivesForLiveProducer();

            goThroughOver3minuteNetworkOutage();

            waitEnoughTimeForSdkToRecoverConnectionAndChannels();
            assertThatSdkNotDuplicatesMessages();
        }
    }

    private void waitEnoughTimeForSdkToRecoverConnectionAndChannels() {
        sleepSeconds(30);
    }

    private void assertThatSdkReceivesSystemAlivesForLiveProducer() throws IOException {
        final int countBefore = rawMessagesStorage.findAlivesOf(MessageInterest.SystemAliveMessages).size();
        waitUntil(() -> {
            final int countAfter = rawMessagesStorage
                .findAlivesOf(MessageInterest.SystemAliveMessages)
                .size();
            assertThat(countAfter).isGreaterThan(countBefore);
        });
    }

    private void enableOnlyLiveProducer() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(
                    WireMock.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<producers response_code=\"OK\">\n" +
                        "    <producer id=\"1\" name=\"LO\" description=\"Live Odds\" api_url=\"https://stgapi.betradar.com/v1/liveodds/\" active=\"true\" scope=\"live\" stateful_recovery_window_in_minutes=\"600\"/>\n" +
                        "    <producer id=\"3\" name=\"Ctrl\" description=\"Betradar Ctrl\" api_url=\"https://stgapi.betradar.com/v1/pre/\" active=\"false\" scope=\"prematch\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"4\" name=\"BetPal\" description=\"BetPal\" api_url=\"https://stgapi.betradar.com/v1/betpal/\" active=\"false\" scope=\"live\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"5\" name=\"PremiumCricket\" description=\"Premium Cricket\" api_url=\"https://stgapi.betradar.com/v1/premium_cricket/\" active=\"false\" scope=\"live|prematch\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"6\" name=\"VF\" description=\"Virtual football\" api_url=\"https://stgapi.betradar.com/v1/vf/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"7\" name=\"WNS\" description=\"Numbers Betting\" api_url=\"https://stgapi.betradar.com/v1/wns/\" active=\"false\" scope=\"prematch\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"8\" name=\"VBL\" description=\"Virtual Basketball League\" api_url=\"https://stgapi.betradar.com/v1/vbl/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"9\" name=\"VTO\" description=\"Virtual Tennis Open\" api_url=\"https://stgapi.betradar.com/v1/vto/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"10\" name=\"VDR\" description=\"Virtual Dog Racing\" api_url=\"https://stgapi.betradar.com/v1/vdr/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"11\" name=\"VHC\" description=\"Virtual Horse Classics\" api_url=\"https://stgapi.betradar.com/v1/vhc/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"12\" name=\"VTI\" description=\"Virtual Tennis In-Play\" api_url=\"https://stgapi.betradar.com/v1/vti/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"14\" name=\"C-Odds\" description=\"Competition Odds\" api_url=\"https://stgapi.betradar.com/v1/codds/\" active=\"false\" scope=\"live\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"15\" name=\"VBI\" description=\"Virtual Baseball In-Play\" api_url=\"https://stgapi.betradar.com/v1/vbi/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "    <producer id=\"16\" name=\"PB\" description=\"Performance betting\" api_url=\"https://stgapi.betradar.com/v1/performance/\" active=\"false\" scope=\"live|prematch\" stateful_recovery_window_in_minutes=\"4320\"/>\n" +
                        "    <producer id=\"17\" name=\"VCI\" description=\"Virtual Cricket In-Play\" api_url=\"https://stgapi.betradar.com/v1/vci/\" active=\"false\" scope=\"virtual\" stateful_recovery_window_in_minutes=\"180\"/>\n" +
                        "</producers>"
                    )
                )
        );
    }

    private void assertThatSdkNotDuplicatesMessages() throws InterruptedException {
        letSdkToReceiveInflightMessages();
        messagesAssertions.assertThatSystemAlivesHaveNotDuplicates();
    }

    private static void letSdkToReceiveInflightMessages() throws InterruptedException {
        Thread.sleep(5);
    }

    private void waitUntil(VoidCallables.ThrowingRunnable condition) {
        int gapsBetweenRecheckingInSeconds = 1;
        int timeLapsedInSeconds = 0;
        int timeEnoughForSdkToReestablishConnectionInMillis = 20000;
        while (timeLapsedInSeconds <= timeEnoughForSdkToReestablishConnectionInMillis) {
            try {
                condition.run();
                return;
            } catch (AssertionError e) {
                //keep checking
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            sleepSeconds(gapsBetweenRecheckingInSeconds);
            timeLapsedInSeconds += gapsBetweenRecheckingInSeconds;
        }
        throw new AssertionError(
            "Condition is not fulfilled during time SDK is expected to re-establish connection"
        );
    }

    private void sleepSeconds(int amount) {
        try {
            Thread.sleep(amount * MILLIS_IN_SECOND);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void goThroughOver3minuteNetworkOutage() throws IOException {
        proxy.disable();
        sleepSeconds(3 * 60 + 30);
        proxy.enable();
    }

    private void stubBookmaker() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/users/whoami.xml"))
                .willReturn(
                    WireMock.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<bookmaker_details response_code=\"OK\" expire_at=\"2025-07-26T17:44:24Z\" bookmaker_id=\"1\" virtual_host=\"/virtualhost\"/>"
                    )
                )
        );
    }
}
