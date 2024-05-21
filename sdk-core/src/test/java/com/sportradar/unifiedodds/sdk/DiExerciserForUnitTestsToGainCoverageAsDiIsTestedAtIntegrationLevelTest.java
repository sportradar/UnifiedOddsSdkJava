/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sportradar.unifiedodds.sdk.cfg.CustomConfigurationBuilder;
import java.util.Locale;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class DiExerciserForUnitTestsToGainCoverageAsDiIsTestedAtIntegrationLevelTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    @Before
    public void stubBookmakerAndProducers() {
        stubAnyBookmaker();
        stubAnyProducers();
    }

    @Test
    public void forArgumentlessConfigurationBuilderFactoryMethodAsItEffectivelyDoesDi() {
        Locale anyLanguage = Locale.ENGLISH;
        val config = againstWiremock(
            UofSdk
                .getUofConfigurationBuilder()
                .setAccessToken("any")
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
        )
            .build();

        assertThat(config).isNotNull();
    }

    @Test
    public void forConfigurationBuilderFactoryMethodWithUserSpecifiedFilesAsItEffectivelyDoesDi() {
        Locale anyLanguage = Locale.ENGLISH;

        val config = againstWiremock(
            UofSdk
                .getUofConfigurationBuilder("UFSdkConfiguration.properties", "any")
                .setAccessToken("any")
                .selectCustom()
                .setDefaultLanguage(anyLanguage)
        )
            .build();

        assertThat(config).isNotNull();
    }

    private CustomConfigurationBuilder againstWiremock(CustomConfigurationBuilder builder) {
        return builder.setApiHost("localhost").setApiPort(wireMockRule.port()).setApiUseSsl(false);
    }

    private void stubAnyBookmaker() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/users/whoami.xml"))
                .willReturn(
                    WireMock.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<bookmaker_details response_code=\"OK\" expire_at=\"2025-07-26T17:44:24Z\" " +
                        "bookmaker_id=\"1\" virtual_host=\"/virtualhost\"/>"
                    )
                )
        );
    }

    private void stubAnyProducers() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(
                    WireMock.ok(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<producers response_code=\"OK\">\n" +
                        "    <producer id=\"1\" name=\"LO\" description=\"Live Odds\" " +
                        "api_url=\"https://stgapi.betradar.com/v1/liveodds/\" active=\"true\" scope=\"live\" " +
                        "stateful_recovery_window_in_minutes=\"600\"/>\n" +
                        "</producers>"
                    )
                )
        );
    }
}
