/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings({ "ClassFanOutComplexity", "MultipleStringLiterals" })
public class ConfigureAccessTokenTest {

    private final String accessToken = "someAccessToken";
    private final Locale anyLanguage = Locale.FRENCH;
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
    private final TokenSetter tokenSetter = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @ParameterizedTest
    @MethodSource("allEnvironments")
    public void configureViaPropertiesFileIn(Environment environment) {
        propsFileContent.put("uf.sdk.accessToken", accessToken);
        propsFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
        propsFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromSdkProperties();

        assertThat(config.getAccessToken()).isEqualTo(accessToken);
    }

    @ParameterizedTest
    @MethodSource("allEnvironments")
    public void configureViaYmlFileIn(Environment environment) {
        yamlFileContent.put("uf.sdk.accessToken", accessToken);
        yamlFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
        yamlFileContent.put("uf.sdk.environment", environment.toString());

        val config = tokenSetter.buildConfigFromApplicationYml();

        assertThat(config.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    public void configureViaJavaApi() {
        UofConfiguration config = tokenSetter
            .setAccessToken(accessToken)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    public void absentClientAuthenticationBlockIndicatesThatSsoTokenCredentialIsUsed() {
        UofConfiguration config = tokenSetter
            .setAccessToken(accessToken)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getAccessToken()).isEqualTo(accessToken);
        assertThat(config.getClientAuthentication()).isNull();
    }

    @Nested
    class AbsentToken {

        @Test
        void configuringPropertiesFileWithoutTokenPropertyIsNotAllowed() {
            propsFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            propsFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromSdkProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uf.sdk.accessToken");
        }

        @Test
        void configuringPropertiesFileWithNullTokenPropertyValueIsNotAllowed() {
            propsFileContent.put("uf.sdk.accessToken", null);
            propsFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            propsFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromSdkProperties)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uf.sdk.accessToken");
        }

        @Test
        void configuringPropertiesFileWithEmptyTokenPropertyValueIsNotAllowed() {
            propsFileContent.put("uf.sdk.accessToken", "");
            propsFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            propsFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromSdkProperties)
                .isInstanceOf(java.security.InvalidParameterException.class)
                .hasMessageContaining("access token");
        }

        @Test
        void configuringYmlFileWithoutTokenPropertyIsNotAllowed() {
            yamlFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            yamlFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromApplicationYml)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sportradar.sdk.uf.accessToken");
        }

        @Test
        void configuringYmlFileWithNullTokenPropertyValueIsNotAllowed() {
            yamlFileContent.put("uf.sdk.accessToken", null);
            yamlFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            yamlFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromApplicationYml)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sportradar.sdk.uf.accessToken");
        }

        @Test
        void configuringYmlFileWithEmptyTokenPropertyValueIsNotAllowed() {
            yamlFileContent.put("uf.sdk.accessToken", "");
            yamlFileContent.put("uf.sdk.defaultLanguage", anyLanguage.toString());
            yamlFileContent.put("uf.sdk.environment", "GlobalIntegration");

            assertThatThrownBy(tokenSetter::buildConfigFromApplicationYml)
                .isInstanceOf(java.security.InvalidParameterException.class)
                .hasMessageContaining("access token");
        }

        @ParameterizedTest
        @NullAndEmptySource
        void absentNullTokenViaJavaApiIsNotAllowed(String noTokenValue) {
            assertThatThrownBy(() ->
                    tokenSetter
                        .setAccessToken(noTokenValue)
                        .selectCustom()
                        .setDefaultLanguage(anyLanguage)
                        .build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Access token");
        }
    }

    public static Object[][] allEnvironments() {
        return Stream.of(Environment.values()).map(e -> new Object[] { e }).toArray(Object[][]::new);
    }
}
