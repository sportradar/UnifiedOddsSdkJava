/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.buildActiveProducer;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import lombok.val;

public class ApiSimulator {

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    private final WireMockRule wireMockRule;

    public ApiSimulator(WireMockRule wireMockRule) {
        this.wireMockRule = wireMockRule;
    }

    public void activateOnlyLiveProducer() {
        Producers producers = new Producers();
        producers.setResponseCode(ResponseCode.OK);
        producers.getProducer().add(buildActiveProducer(LIVE_ODDS));

        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(producers)))
        );
    }

    public void defineBookmaker() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/users/whoami.xml"))
                .willReturn(
                    WireMock.ok(
                        XML_DECLARATION +
                        "<bookmaker_details response_code=\"OK\" " +
                        "expire_at=\"2025-07-26T17:44:24Z\" " +
                        "bookmaker_id=\"1\" " +
                        "virtual_host=\"/virtualhost\"/>"
                    )
                )
        );
    }

    public void stubEmptyMarketList() {
        stub(new MarketDescriptions());
    }

    public void stubMarketListContaining(DescMarket market) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);
        stub(descriptions);
    }

    private void stub(MarketDescriptions descriptions) {
        wireMockRule.stubFor(
            get(urlPathMatching("/v1/descriptions/en/markets.xml.*"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(descriptions)))
        );
    }

    public void noMarketVariants() {
        wireMockRule.stubFor(
            get(urlPathEqualTo("/v1/descriptions/en/variants.xml"))
                .willReturn(
                    WireMock.ok(
                        XML_DECLARATION +
                        "  <variant_descriptions response_code=\"OK\">" +
                        "</variant_descriptions>"
                    )
                )
        );
    }
}
