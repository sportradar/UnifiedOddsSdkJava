/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.buildActiveProducer;
import static java.lang.String.format;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Charsets;
import com.sportradar.uf.custombet.datamodel.CapiResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.function.Consumer;
import javax.xml.bind.JAXB;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpStatus;

public class ApiSimulator {

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    private final Consumer<MappingBuilder> stubRegistrar;

    public ApiSimulator(WireMockRule wireMockRule) {
        this.stubRegistrar = wireMockRule::stubFor;
    }

    public ApiSimulator(WireMock wireMock) {
        this.stubRegistrar = wireMock::register;
    }

    public void activateOnlyLiveProducer() {
        Producers producers = new Producers();
        producers.setResponseCode(ResponseCode.OK);
        producers.getProducer().add(buildActiveProducer(LIVE_ODDS));

        register(
            get(urlPathEqualTo("/v1/descriptions/producers.xml"))
                .willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(producers)))
        );
    }

    public void defineBookmaker() {
        register(
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

    public void stubEmptyMarketList(Locale language) {
        stub(new MarketDescriptions(), format("/v1/descriptions/%s/markets.xml.*", language.toString()));
    }

    public void stubMarketListContaining(DescMarket market, Locale language) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);
        stub(descriptions, format("/v1/descriptions/%s/markets.xml.*", language.toString()));
    }

    public void stubVariantListContaining(DescVariant variantDescription, Locale language) {
        val descriptions = new VariantDescriptions();
        descriptions.getVariant().add(variantDescription);
        stub(descriptions, format("/v1/descriptions/%s/variants.xml.*", language.toString()));
    }

    public void stubEmptyVariantList(Locale language) {
        val descriptions = new VariantDescriptions();
        stub(descriptions, format("/v1/descriptions/%s/variants.xml.*", language.toString()));
    }

    public void stubSingleVariantMarket(DescMarket market, Locale language) {
        val descriptions = new MarketDescriptions();
        descriptions.getMarket().add(market);
        stub(
            descriptions,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s?.*",
                language,
                market.getId(),
                market.getVariant()
            )
        );
    }

    public void stubSingleVariantMarket(
        int httpStatus,
        int marketId,
        Identifiable variantId,
        Locale language
    ) {
        stub(
            httpStatus,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s.*",
                language.getLanguage(),
                marketId,
                variantId.id()
            )
        );
    }

    public void stubSingleVariantMarket(
        int httpStatus,
        int marketId,
        Identifiable variantId,
        Locale language,
        MarketDescriptions descriptions
    ) {
        stub(
            httpStatus,
            format(
                "/v1/descriptions/%s/markets/%s/variants/%s.*",
                language.getLanguage(),
                marketId,
                variantId.id()
            ),
            descriptions
        );
    }

    private void stub(int httpStatus, String path) {
        register(get(urlPathMatching(path)).willReturn(WireMock.status(httpStatus)));
    }

    private void stub(int httpStatus, String path, Object descriptions) {
        register(
            get(urlPathMatching(path))
                .willReturn(
                    WireMock
                        .aResponse()
                        .withStatus(httpStatus)
                        .withBody(JaxbContexts.SportsApi.marshall(descriptions))
                )
        );
    }

    private void stub(Object descriptions, String path) {
        register(
            get(urlPathMatching(path)).willReturn(WireMock.ok(JaxbContexts.SportsApi.marshall(descriptions)))
        );
    }

    public void noMarketVariants() {
        register(
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

    public void returnNotFoundForCustomBetAvailableSelections() {
        CapiResponse response = notFoundResponse(
            "no available selections for a path " + "/v1/custombet/[^/]+/available_selections"
        );
        register(
            get(urlPathMatching("/v1/custombet/[^/]+/available_selections"))
                .willReturn(
                    WireMock.aResponse().withBody(toXml(response)).withStatus(HttpStatus.SC_NOT_FOUND)
                )
        );
    }

    public void returnNotFoundForCustomBetCalculate() {
        CapiResponse response = notFoundResponse(
            "no available selections for a path " + "/v1/custombet/calculate"
        );
        register(
            post(urlPathMatching("/v1/custombet/calculate"))
                .willReturn(
                    WireMock.aResponse().withBody(toXml(response)).withStatus(HttpStatus.SC_NOT_FOUND)
                )
        );
    }

    private static CapiResponse notFoundResponse(String message) {
        CapiResponse capiNotFoundResponse = new CapiResponse();
        capiNotFoundResponse.setResponseCode("NOT_FOUND");
        capiNotFoundResponse.setMessage(message);
        return capiNotFoundResponse;
    }

    private void register(MappingBuilder mappingBuilder) {
        stubRegistrar.accept(mappingBuilder);
    }

    @SneakyThrows
    private String toXml(Object content) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            JAXB.marshal(content, bos);
            return new String(bos.toByteArray(), Charsets.UTF_8);
        }
    }
}
