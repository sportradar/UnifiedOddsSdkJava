/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.unifiedodds.sdk.internal.cfg.*;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("ClassFanOutComplexity")
public class ConfigurePrivateKeyJwtAuthenticationTest {

    public static final String ANY_CLIENT_ID = "irrelevantClientId";
    public static final PrivateKey ANY_PRIVATE_KEY = anyPrivateKey();
    public static final String ANY_KEY_ID = "irrelevantKeyId";
    public static final String ANY_TOKEN = "anyToken";
    private final Locale anyLanguage = Locale.FRENCH;
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
    private final TokenSetter tokenSetter = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @Test
    public void authenticationBuilderSetsSigningKeyIdAcrossConfigurations() {
        val signingKeyId = "uniqueKeyId";
        val authentication = UofClientAuthentication
            .privateKeyJwt()
            .setSigningKeyId(signingKeyId)
            .setClientId(ANY_CLIENT_ID)
            .setPrivateKey(ANY_PRIVATE_KEY)
            .build();

        val config = tokenSetter
            .setClientAuthentication(authentication)
            .setAccessToken(ANY_TOKEN)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getClientAuthentication().getSigningKeyId()).isEqualTo(signingKeyId);
    }

    @Test
    public void authenticationBuilderSetsClientIdAcrossConfigurations() {
        val expectedClientId = "uniqueClientId";
        val authentication = UofClientAuthentication
            .privateKeyJwt()
            .setSigningKeyId(ANY_KEY_ID)
            .setClientId(expectedClientId)
            .setPrivateKey(ANY_PRIVATE_KEY)
            .build();

        val config = tokenSetter
            .setClientAuthentication(authentication)
            .setAccessToken(ANY_TOKEN)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getClientAuthentication().getClientId()).isEqualTo(expectedClientId);
    }

    @Test
    public void authenticationBuilderSetsPrivateKeyAcrossConfigurations() {
        val expectedPrivateKey = anyPrivateKey();
        val authentication = UofClientAuthentication
            .privateKeyJwt()
            .setSigningKeyId(ANY_KEY_ID)
            .setClientId(ANY_CLIENT_ID)
            .setPrivateKey(expectedPrivateKey)
            .build();

        val config = tokenSetter
            .setClientAuthentication(authentication)
            .setAccessToken(ANY_TOKEN)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();

        assertThat(config.getClientAuthentication().getPrivateKey()).isSameAs(expectedPrivateKey);
    }

    @ParameterizedTest
    @ValueSource(strings = { "DSA", "DH" })
    public void onAttemptToSetNonRsaKeyExceptionIsThrown(String algorithm) {
        val expectedPrivateKey = privateKeyFor(algorithm);
        val builder = UofClientAuthentication
            .privateKeyJwt()
            .setSigningKeyId(ANY_KEY_ID)
            .setClientId(ANY_CLIENT_ID);

        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> builder.setPrivateKey(expectedPrivateKey).build())
            .withMessage(
                "Only RSA is supported as the algorithm for Private Key JWT authentication. Found: %s",
                algorithm
            );
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void setSigningKeyIdThrowsOnNull(String signingKeyId) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setSigningKeyId(signingKeyId)
                    .setClientId(ANY_CLIENT_ID)
                    .setPrivateKey(ANY_PRIVATE_KEY)
                    .build()
            );
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void setClientIdThrowsOnNull(String clientId) {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setSigningKeyId(ANY_KEY_ID)
                    .setClientId(clientId)
                    .setPrivateKey(ANY_PRIVATE_KEY)
                    .build()
            );
    }

    @Test
    public void setPrivateKeyThrowsOnNull() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setSigningKeyId(ANY_KEY_ID)
                    .setClientId(ANY_CLIENT_ID)
                    .setPrivateKey(null)
                    .build()
            );
    }

    @Test
    public void buildThrowsOnSigningKeyNotSet() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setClientId(ANY_KEY_ID)
                    .setPrivateKey(ANY_PRIVATE_KEY)
                    .build()
            );
    }

    @Test
    public void buildThrowsOnClientIdNotSet() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setSigningKeyId(ANY_KEY_ID)
                    .setPrivateKey(ANY_PRIVATE_KEY)
                    .build()
            );
    }

    @Test
    public void buildThrowsOnPrivateKeyNotSet() {
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(() ->
                UofClientAuthentication
                    .privateKeyJwt()
                    .setSigningKeyId(ANY_KEY_ID)
                    .setClientId(ANY_CLIENT_ID)
                    .build()
            );
    }

    private static PrivateKey anyPrivateKey() {
        return privateKeyFor("RSA");
    }

    private static PrivateKey privateKeyFor(String algorithm) {
        try {
            val keyGen = KeyPairGenerator.getInstance(algorithm);
            val keySize = 2048;
            keyGen.initialize(keySize);
            val keyPair = keyGen.generateKeyPair();
            return keyPair.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy private key", e);
        }
    }
}
