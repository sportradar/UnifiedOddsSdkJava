/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.OAuth2Token;
import java.util.List;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.HttpStatus;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ClassDataAbstractionCoupling", "MultipleStringLiterals", "MagicNumber" }
)
public class CommonIamSimulator {

    private static final int ADDITIONAL_TIMES_TOKEN_IS_REQUESTED_DURING_SDK_CONFIGURATION = 2;
    private final Consumer<MappingBuilder> stubRegistrar;
    private final WireMock wireMock;

    public CommonIamSimulator(WireMock wireMock) {
        this.stubRegistrar = wireMock::register;
        this.wireMock = wireMock;
    }

    public void verifyTokenEndpointCalled2TimesDuringConfiguration(Runnable configurationOnly) {
        configurationOnly.run();

        wireMock.verifyThat(
            exactly(ADDITIONAL_TIMES_TOKEN_IS_REQUESTED_DURING_SDK_CONFIGURATION),
            postRequestedFor(urlPathEqualTo("/oauth/token"))
        );
        assertThat(ADDITIONAL_TIMES_TOKEN_IS_REQUESTED_DURING_SDK_CONFIGURATION).isEqualTo(2);
    }

    public void verifyTokenEndpointCalledOnce() {
        wireMock.verifyThat(exactly(1), postRequestedFor(urlPathEqualTo("/oauth/token")));
    }

    public void verifyTokenEndpointCalledTimes(int expected) {
        wireMock.verifyThat(exactly(expected), postRequestedFor(urlPathEqualTo("/oauth/token")));
    }

    public void verifyAfterSdkStartupTokenEndpointCalledOnce() {
        wireMock.verifyThat(
            exactly(1 + ADDITIONAL_TIMES_TOKEN_IS_REQUESTED_DURING_SDK_CONFIGURATION),
            postRequestedFor(urlPathEqualTo("/oauth/token"))
        );
    }

    @SneakyThrows
    public void stubTokenEndpoint(OAuth2Token token, ApiSimulator.BodyMatchCondition bodyCondition) {
        val tokenResponse = new ObjectMapper().writeValueAsString(token);

        register(
            post(urlPathEqualTo("/oauth/token"))
                .withRequestBody(matching(bodyCondition.pattern))
                .willReturn(ok(tokenResponse))
        );
    }

    @SneakyThrows
    public void stubTokenEndpoint(OAuth2Token token, ApiSimulator.HeaderEquality headerEquality) {
        val tokenResponse = new ObjectMapper().writeValueAsString(token);

        register(
            post(urlPathEqualTo("/oauth/token"))
                .withHeader(headerEquality.name, headerEquality.valuePattern)
                .willReturn(ok(tokenResponse))
        );
    }

    @SneakyThrows
    public void stubTokenEndpoint(OAuth2Token token, ApiSimulator.ApiStubDelay delay) {
        val tokenResponse = new ObjectMapper().writeValueAsString(token);

        register(
            post(urlPathEqualTo("/oauth/token"))
                .willReturn(ok(tokenResponse).withFixedDelay((int) delay.delay.toMillis()))
        );
    }

    @SneakyThrows
    public void stubTokenEndpoint(OAuth2Token token) {
        val tokenResponse = new ObjectMapper().writeValueAsString(token);

        register(post(urlPathEqualTo("/oauth/token")).willReturn(ok(tokenResponse)));
    }

    @SneakyThrows
    public void stubTokenEndpoint(OAuth2Token firstToken, OAuth2Token secondToken, OAuth2Token nextToken) {
        val firstTokenResponse = new ObjectMapper().writeValueAsString(firstToken);
        val secondTokenResponse = new ObjectMapper().writeValueAsString(secondToken);
        val nextTokenResponse = new ObjectMapper().writeValueAsString(nextToken);

        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .willReturn(ok(firstTokenResponse))
                .willSetStateTo("SECOND")
        );
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .whenScenarioStateIs("SECOND")
                .willReturn(ok(secondTokenResponse))
                .willSetStateTo("NEXT")
        );
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .whenScenarioStateIs("NEXT")
                .willReturn(ok(nextTokenResponse))
                .willSetStateTo("NEXT")
        );
    }

    @SneakyThrows
    public void stubTokenEndpoint(
        OAuth2Token firstToken,
        OAuth2Token secondToken,
        OAuth2Token thirdToken,
        OAuth2Token nextToken
    ) {
        val firstTokenResponse = new ObjectMapper().writeValueAsString(firstToken);
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .willReturn(ok(firstTokenResponse))
                .willSetStateTo("SECOND")
        );
        val secondTokenResponse = new ObjectMapper().writeValueAsString(secondToken);
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .whenScenarioStateIs("SECOND")
                .willReturn(ok(secondTokenResponse))
                .willSetStateTo("THIRD")
        );
        val thirdTokenResponse = new ObjectMapper().writeValueAsString(thirdToken);
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .whenScenarioStateIs("THIRD")
                .willReturn(ok(thirdTokenResponse))
                .willSetStateTo("NEXT")
        );
        val nextTokenResponse = new ObjectMapper().writeValueAsString(nextToken);
        register(
            post(urlPathEqualTo("/oauth/token"))
                .inScenario("tokens")
                .whenScenarioStateIs("NEXT")
                .willReturn(ok(nextTokenResponse))
                .willSetStateTo("NEXT")
        );
    }

    private void register(MappingBuilder mappingBuilder) {
        stubRegistrar.accept(mappingBuilder);
    }

    public String getLastRequestBody() {
        val allRequestBodies = getAllRequestBodies();
        return allRequestBodies.get(allRequestBodies.size() - 1);
    }

    public List<String> getAllRequestBodies() {
        return wireMock
            .find(postRequestedFor(urlEqualTo("/oauth/token")))
            .stream()
            .map(LoggedRequest::getBodyAsString)
            .collect(java.util.stream.Collectors.toList());
    }

    public void stubTokenEndpointWithInternalServerErrorResponse() {
        register(
            post(urlEqualTo("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                        .withBody("Internal Server Error")
                )
        );
    }

    public void stubTokenEndpointWithInvalidJson() {
        register(
            post(urlEqualTo("/oauth/token"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK).withBody("{'Invalid': \"JSON"))
        );
    }

    public void stubTokenEndpointWithoutExpiresIn() {
        register(
            post(urlEqualTo("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("{\"access_token\":\"token\",\"token_type\":\"Bearer\"}")
                )
        );
    }

    public void stubTokenEndpointWithoutTokenType() {
        register(
            post(urlEqualTo("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("{\"access_token\":\"token\",\"expires_in\":3600}")
                )
        );
    }

    public void stubTokenEndpointWithoutAccessToken() {
        register(
            post(urlEqualTo("/oauth/token"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.SC_OK)
                        .withBody("{\"expires_in\":3600,\"token_type\":\"Bearer\"}")
                )
        );
    }
}
