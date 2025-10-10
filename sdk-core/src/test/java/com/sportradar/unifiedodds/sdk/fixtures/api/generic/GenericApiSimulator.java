/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.fixtures.api.generic;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.sportradar.unifiedodds.sdk.conn.JaxbContexts;
import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.function.Consumer;
import lombok.val;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber" }
)
public class GenericApiSimulator {

    public static final String XML_DECLARATION =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    public static final String UNIFIED_XML_NAMESPACE = "http://schemas.sportradar.com/sportsapi/v1/unified";
    private final Consumer<MappingBuilder> stubRegistrar;

    public GenericApiSimulator(WireMockRule wireMockRule) {
        this.stubRegistrar = wireMockRule::stubFor;
    }

    public GenericApiSimulator(WireMock wireMock) {
        this.stubRegistrar = wireMock::register;
    }

    public void stubApiGetRequest(String url, HeaderEquality headerEquality, ApiStubDelay delay) {
        register(
            get(urlPathMatching(url))
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
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

    private void register(MappingBuilder mappingBuilder) {
        stubRegistrar.accept(mappingBuilder);
    }

    public void stubRequest(MappingBuilderFromMethod method, String url, HeaderEquality... headerEqualities) {
        val finalMapping = Arrays
            .stream(headerEqualities)
            .reduce(
                method.withUrl(url),
                (mapping, header) -> mapping.withHeader(header.name, header.valuePattern),
                (mapping1, mapping2) -> mapping2
            );

        register(finalMapping);
    }

    public void stubRequest(
        MappingBuilderFromMethod method,
        String url,
        HeaderEquality headerEquality,
        ApiStubDelay delay
    ) {
        register(
            method
                .withUrl(url)
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(aResponse().withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    public void stubFailure(MappingBuilderFromMethod method, String url, Fault fault) {
        register(method.withUrl(url).willReturn(aResponse().withFault(fault)));
    }

    public void stubBadRequest(MappingBuilderFromMethod method, String url, HeaderEquality headerEquality) {
        register(
            method
                .withUrl(url)
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(badRequest())
        );
    }

    public static enum MappingBuilderFromMethod {
        POST(url -> post(url)),
        PUT(url -> put(url)),
        DELETE(url -> delete(url));

        private final java.util.function.Function<String, MappingBuilder> wiremockMappingBuilder;

        MappingBuilderFromMethod(java.util.function.Function<String, MappingBuilder> wiremockMappingBuilder) {
            this.wiremockMappingBuilder = wiremockMappingBuilder;
        }

        public MappingBuilder withUrl(String url) {
            return wiremockMappingBuilder.apply(url);
        }
    }

    @SuppressWarnings("VisibilityModifier")
    public static class ApiStubDelay {

        public final Duration delay;

        private ApiStubDelay(Duration delay) {
            this.delay = delay;
        }

        public static ApiStubDelay toBeDelayedBy(long delay, TemporalUnit unit) {
            return new ApiStubDelay(Duration.of(delay, unit));
        }

        public static ApiStubDelay toBeDelayedBy(Duration delay) {
            return new ApiStubDelay(delay);
        }
    }

    @SuppressWarnings("VisibilityModifier")
    public static class HeaderEquality {

        public static final String UUID_REGEX =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
        public final String name;
        public final StringValuePattern valuePattern;

        public HeaderEquality(String name, StringValuePattern valuePattern) {
            this.name = name;
            this.valuePattern = valuePattern;
        }

        public static HeaderEquality requiringHeader(String name, String value) {
            return new HeaderEquality(name, equalTo(value));
        }

        public static HeaderEquality requiringAuthorizationHeader(String value) {
            return new HeaderEquality("Authorization", equalTo(value));
        }

        public static HeaderEquality requiringHeaderWithAnyValue(String name) {
            return new HeaderEquality(name, matching("^.+$"));
        }

        public static HeaderEquality requiringHeaderWithAnyUuidValue(String name) {
            return new HeaderEquality(name, matching(UUID_REGEX));
        }

        public static HeaderEquality requiringNoHeader(String name) {
            return new HeaderEquality(name, absent());
        }
    }
}
