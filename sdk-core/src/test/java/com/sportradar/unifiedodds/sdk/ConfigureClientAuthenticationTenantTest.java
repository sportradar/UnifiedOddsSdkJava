/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings({ "ClassFanOutComplexity", "MultipleStringLiterals" })
class ConfigureClientAuthenticationTenantTest {

    private static final String ENVIRONMENT_PROPERTY = "uf.sdk.environment";
    private static final String ANY_TOKEN = "any";
    private static final String ANY_KEY_ID = "keyId";
    private static final String ANY_CLIENT_ID = "clientId";
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final UofClientAuthentication.PrivateKeyJwtData anyAuthentication = UofClientAuthentication
        .privateKeyJwt()
        .setSigningKeyId(ANY_KEY_ID)
        .setClientId(ANY_CLIENT_ID)
        .setPrivateKey(anyPrivateKey())
        .build();

    private final Locale anyLanguage = Locale.FRENCH;
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();

    private final TokenSetter builder = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @Test
    void setsClientAuthenticationTenant() {
        val config = builder
            .setClientAuthentication(anyAuthentication)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setClientAuthenticationTenant("tenant")
            .build();

        assertThat(config.getClientAuthentication().getTenant()).isEqualTo("tenant");
    }

    @Test
    void clientAuthenticationTenantIsNullIfNotConfiguredExplicitly() {
        val config = builder
            .setClientAuthentication(anyAuthentication)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getClientAuthentication().getTenant()).isNull();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = { " " })
    void setClientAuthenticationTenantExpectsNonEmptyValue(String incorrectTenant) {
        assertThatThrownBy(() ->
                builder
                    .setClientAuthentication(anyAuthentication)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationTenant(incorrectTenant)
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("tenant");
    }

    @Test
    void settingCustomAuthenticationTenantIsNotAllowedIfClientAuthenticationIsNotSet() {
        assertThatThrownBy(() ->
                builder
                    .setAccessToken(ANY_TOKEN)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setClientAuthenticationTenant("anyTenant")
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("tenant");
    }

    @Test
    void loadingCustomEnvironmentRepresentingPropertiesFileDoesNotChangePreviouslySetCustomTenant() {
        propsFileContent.put(ENVIRONMENT_PROPERTY, "Custom");

        val config = builder
            .setClientAuthentication(anyAuthentication)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setClientAuthenticationTenant("configuredTenant")
            .loadConfigFromSdkProperties()
            .build();

        assertThat(config.getClientAuthentication().getTenant()).isEqualTo("configuredTenant");
    }

    @Test
    void loadingCustomEnvironmentRepresentingYamlFileDoesNotChangePreviouslySetCustomTenant() {
        yamlFileContent.put(ENVIRONMENT_PROPERTY, "Custom");

        val config = builder
            .setClientAuthentication(anyAuthentication)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setClientAuthenticationTenant("configuredTenant")
            .loadConfigFromApplicationYml()
            .build();

        assertThat(config.getClientAuthentication().getTenant()).isEqualTo("configuredTenant");
    }

    private static PrivateKey anyPrivateKey() {
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA");
            final int keySize = 2048;
            keyGen.initialize(keySize);
            val keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy private key", e);
        }
    }
}
