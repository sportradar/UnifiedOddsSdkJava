/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.EnvironmentSelector;
import com.sportradar.unifiedodds.sdk.cfg.TokenSetter;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.StubSdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@SuppressWarnings({ "MultipleStringLiterals", "LineLength" })
public class ConfigureRabbitCredentialsTest {

    public static final String ACCESS_TOKEN = "access token";
    public static final String TOKEN_SOURCES =
        "com.sportradar.unifiedodds.sdk.ConfigureRabbitCredentialsTest#tokenSources";
    public static final String INVALID_CREDENTIAL_COMBINATIONS =
        "com.sportradar.unifiedodds.sdk.ConfigureRabbitCredentialsTest#invalidCredentialCombinations";
    public static final String MISSING_CREDENTIAL_COMBINATIONS =
        "com.sportradar.unifiedodds.sdk.ConfigureRabbitCredentialsTest#missingCredentialCombinations";
    private final boolean replayMode = true;
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

    @AfterEach
    void tearDown() {
        System.clearProperty("uf.accesstoken");
    }

    @Test
    void messagingCredentialsAreLoadedFromYamlFileWhenCommonIamIsConfigured() {
        val username = "yamlUser";
        val password = "yamlPass";

        yamlFileContent.put("uf.sdk.messagingUsername", username);
        yamlFileContent.put("uf.sdk.messagingPassword", password);

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromApplicationYml()
            .build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(username);
        assertThat(config.getRabbit().getPassword()).isEqualTo(password);
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfig.getMessagingPassword()).isEqualTo(password);
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfigForReplay.getMessagingPassword()).isEqualTo(password);
    }

    @Test
    void messagingCredentialsAreLoadedFromPropertiesFileWhenCommonIamIsConfigured() {
        val username = "yamlUser";
        val password = "yamlPass";

        propsFileContent.put("uf.sdk.messagingUsername", username);
        propsFileContent.put("uf.sdk.messagingPassword", password);

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromSdkProperties()
            .build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(username);
        assertThat(config.getRabbit().getPassword()).isEqualTo(password);
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfig.getMessagingPassword()).isEqualTo(password);
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfigForReplay.getMessagingPassword()).isEqualTo(password);
    }

    @ParameterizedTest(name = "Token loaded from {1}")
    @MethodSource(TOKEN_SOURCES)
    void explicitlyConfiguresBothUsernameAndPasswordWhenSsoTokenIsConfigured(
        Function<TokenSetter, EnvironmentSelector> tokenLoader,
        TokenSource tokenSource
    ) {
        configureAccessTokenFor(tokenSource);

        val username = "customUser";
        val password = "customPass";

        val config = tokenLoader
            .apply(tokenSetter)
            .selectCustom()
            .setMessagingCredentials(username, password)
            .setDefaultLanguage(anyLanguage)
            .build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(username);
        assertThat(config.getRabbit().getPassword()).isEqualTo(password);
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfig.getMessagingPassword()).isEqualTo(password);
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(username);
        assertThat(internalConfigForReplay.getMessagingPassword()).isEqualTo(password);
    }

    @ParameterizedTest(name = "Token loaded from {1}")
    @MethodSource(TOKEN_SOURCES)
    void notConfiguringUsernameAndPasswordExplicitlyWhenSsoTokenIsConfiguredSetsUsernameAsTokenAndPasswordAsNull(
        Function<TokenSetter, EnvironmentSelector> tokenLoader,
        TokenSource tokenSource
    ) {
        configureAccessTokenFor(tokenSource, ACCESS_TOKEN);

        val config = tokenLoader.apply(tokenSetter).selectCustom().setDefaultLanguage(anyLanguage).build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(INVALID_CREDENTIAL_COMBINATIONS)
    void configuringInvalidMessagingCredentialsInYamlFileWhenSsoTokenIsConfiguredThrowsException(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(yamlFileContent);

        assertThatThrownBy(() ->
                tokenSetter
                    .setAccessToken(ACCESS_TOKEN)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromApplicationYml()
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(MISSING_CREDENTIAL_COMBINATIONS)
    void emptyOrMissingMessagingCredentialsInYamlFileWhenClientAuthenticationIsConfiguredAreIgnored(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(yamlFileContent);

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromApplicationYml()
            .build();

        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isNull();
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isNull();
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isNull();
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(MISSING_CREDENTIAL_COMBINATIONS)
    void emptyOrMissingMessagingCredentialsInYamlFileWhenSsoTokenIsConfiguredFallsBackToUsingAccessTokenAsUsernameAndPasswordBeingNull(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(yamlFileContent);

        val config = tokenSetter
            .setAccessToken(ACCESS_TOKEN)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromApplicationYml()
            .build();

        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(INVALID_CREDENTIAL_COMBINATIONS)
    void configuringInvalidMessagingCredentialsInPropertiesFileWhenSsoTokenIsConfiguredThrowsException(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(propsFileContent);

        assertThatThrownBy(() ->
                tokenSetter
                    .setAccessToken(ACCESS_TOKEN)
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromSdkProperties()
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(MISSING_CREDENTIAL_COMBINATIONS)
    void emptyOrMissingMessagingCredentialsInPropertiesFileWhenSsoTokenIsConfiguredFallsBackToUsingAccessTokenAsUsernameAndPasswordBeingNull(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(propsFileContent);

        val config = tokenSetter
            .setAccessToken(ACCESS_TOKEN)
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromSdkProperties()
            .build();

        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo(ACCESS_TOKEN);
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(MISSING_CREDENTIAL_COMBINATIONS)
    void emptyOrMissingMessagingCredentialsInPropertiesFileWhenClientAuthenticationIsConfiguredAreIgnored(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(propsFileContent);

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromSdkProperties()
            .build();

        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isNull();
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isNull();
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isNull();
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(INVALID_CREDENTIAL_COMBINATIONS)
    void configuringInvalidMessagingCredentialsInYamlFileWhenClientAuthenticationIsConfiguredThrowsException(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(yamlFileContent);

        assertThatThrownBy(() ->
                tokenSetter
                    .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromApplicationYml()
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource(INVALID_CREDENTIAL_COMBINATIONS)
    void configuringInvalidMessagingCredentialsInPropertiesFileWhenClientAuthenticationIsConfiguredThrowsException(
        String scenarioDescription,
        Consumer<Map<String, String>> configureInvalidCredentials
    ) {
        configureInvalidCredentials.accept(propsFileContent);

        assertThatThrownBy(() ->
                tokenSetter
                    .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .loadConfigFromSdkProperties()
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void messagingCredentialsAreNullWhenClientAuthenticationIsConfiguredAndNoExplicitCredentialsAreConfigured() {
        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isNull();
        assertThat(config.getRabbit().getPassword()).isNull();
        assertThat(internalConfig.getMessagingUsername()).isNull();
        assertThat(internalConfig.getMessagingPassword()).isNull();
        assertThat(internalConfigForReplay.getMessagingUsername()).isNull();
        assertThat(internalConfigForReplay.getMessagingPassword()).isNull();
    }

    @Test
    void explicitlyConfiguresBothUsernameAndPasswordWhenClientAuthenticationIsConfigured() {
        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setMessagingCredentials("customUser", "customPass")
            .build();
        val internalConfig = new SdkInternalConfiguration(config);
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode);

        assertThat(config.getRabbit().getUsername()).isEqualTo("customUser");
        assertThat(config.getRabbit().getPassword()).isEqualTo("customPass");
        assertThat(internalConfig.getMessagingUsername()).isEqualTo("customUser");
        assertThat(internalConfig.getMessagingPassword()).isEqualTo("customPass");
        assertThat(internalConfigForReplay.getMessagingUsername()).isEqualTo("customUser");
        assertThat(internalConfigForReplay.getMessagingPassword()).isEqualTo("customPass");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void forbidsExplicitlySettingMessagingCredentialsWithInvalidUsernameWhenCommonIamIsConfigured(
        String invalidUsername
    ) {
        assertThatThrownBy(() ->
                tokenSetter
                    .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setMessagingCredentials(invalidUsername, "validPassword")
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("username");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void forbidsExplicitlySettingMessagingCredentialsWithInvalidPasswordWhenCommonIamIsConfigured(
        String invalidPassword
    ) {
        assertThatThrownBy(() ->
                tokenSetter
                    .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
                    .selectCustom()
                    .setDefaultLanguage(anyLanguage)
                    .setMessagingCredentials("validUsername", invalidPassword)
                    .build()
            )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("password");
    }

    @Test
    void loadingMessagingCredentialsFromYamlAfterExplicitConfigurationWithBuilderOverwritesCredentials() {
        yamlFileContent.put("uf.sdk.messagingUsername", "yamlUser");
        yamlFileContent.put("uf.sdk.messagingPassword", "yamlPass");

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setMessagingCredentials("shouldBeOverwrittenUser", "shouldBeOverwrittenPass")
            .loadConfigFromApplicationYml()
            .build();

        assertThat(config.getRabbit().getUsername()).isEqualTo("yamlUser");
        assertThat(config.getRabbit().getPassword()).isEqualTo("yamlPass");
    }

    @Test
    void loadingMessagingCredentialsFromPropertiesAfterExplicitConfigurationWithBuilderOverwritesCredentials() {
        propsFileContent.put("uf.sdk.messagingUsername", "propsUser");
        propsFileContent.put("uf.sdk.messagingPassword", "propsPass");

        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setMessagingCredentials("shouldBeOverwrittenUser", "shouldBeOverwrittenPass")
            .loadConfigFromSdkProperties()
            .build();

        assertThat(config.getRabbit().getUsername()).isEqualTo("propsUser");
        assertThat(config.getRabbit().getPassword()).isEqualTo("propsPass");
    }

    @Test
    void explicitlyConfiguringMessagingCredentialsWithBuilderAfterLoadingFromYamlOverwritesCredentials() {
        yamlFileContent.put("uf.sdk.messagingUsername", "shouldBeOverwrittenUser");
        yamlFileContent.put("uf.sdk.messagingPassword", "shouldBeOverwrittenPass");
        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromApplicationYml()
            .setMessagingCredentials("explicitUser", "explicitPass")
            .build();
        assertThat(config.getRabbit().getUsername()).isEqualTo("explicitUser");
        assertThat(config.getRabbit().getPassword()).isEqualTo("explicitPass");
    }

    @Test
    void explicitlyConfiguringMessagingCredentialsWithBuilderAfterLoadingFromPropertiesOverwritesCredentials() {
        propsFileContent.put("uf.sdk.messagingUsername", "shouldBeOverwrittenUser");
        propsFileContent.put("uf.sdk.messagingPassword", "shouldBeOverwrittenPass");
        val config = tokenSetter
            .setClientAuthentication(mock(UofClientAuthentication.PrivateKeyJwtData.class))
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .loadConfigFromSdkProperties()
            .setMessagingCredentials("explicitUser", "explicitPass")
            .build();
        assertThat(config.getRabbit().getUsername()).isEqualTo("explicitUser");
        assertThat(config.getRabbit().getPassword()).isEqualTo("explicitPass");
    }

    @Getter
    private enum TokenSource {
        SYSTEM_VARIABLE("System Variable"),
        APPLICATION_YAML("Application Yaml"),
        SDK_PROPERTIES("Sdk Properties"),
        BUILDER("Builder");

        private final String displayName;

        TokenSource(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private void configureAccessTokenFor(TokenSource tokenSource) {
        configureAccessTokenFor(tokenSource, "anyAccessToken");
    }

    private void configureAccessTokenFor(TokenSource tokenSource, String accessToken) {
        switch (tokenSource) {
            case SYSTEM_VARIABLE:
                System.setProperty("uf.accesstoken", accessToken);
                break;
            case APPLICATION_YAML:
                yamlFileContent.put("uf.sdk.accessToken", accessToken);
                break;
            case SDK_PROPERTIES:
                propsFileContent.put("uf.sdk.accessToken", accessToken);
                break;
            case BUILDER:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tokenSource);
        }
    }

    static Stream<Arguments> tokenSources() {
        return Stream.of(
            Arguments.arguments(
                (Function<TokenSetter, EnvironmentSelector>) TokenSetter::setAccessTokenFromSystemVar,
                TokenSource.SYSTEM_VARIABLE
            ),
            Arguments.arguments(
                (Function<TokenSetter, EnvironmentSelector>) TokenSetter::setAccessTokenFromApplicationYaml,
                TokenSource.APPLICATION_YAML
            ),
            Arguments.arguments(
                (Function<TokenSetter, EnvironmentSelector>) TokenSetter::setAccessTokenFromSdkProperties,
                TokenSource.SDK_PROPERTIES
            ),
            Arguments.arguments(
                (Function<TokenSetter, EnvironmentSelector>) setter -> setter.setAccessToken(ACCESS_TOKEN),
                TokenSource.BUILDER
            )
        );
    }

    static Stream<Arguments> invalidCredentialCombinations() {
        return Stream.of(
            Arguments.arguments(
                "Missing password",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingUsername", "valid-username");
                }
            ),
            Arguments.arguments(
                "Empty password",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingUsername", "valid-username");
                    fileContent.put("uf.sdk.messagingPassword", "");
                }
            ),
            Arguments.arguments(
                "Missing username",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingPassword", "valid-password");
                }
            ),
            Arguments.arguments(
                "Empty username",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingPassword", "valid-password");
                    fileContent.put("uf.sdk.messagingUsername", "");
                }
            )
        );
    }

    static Stream<Arguments> missingCredentialCombinations() {
        return Stream.of(
            Arguments.arguments(
                "Missing username and password",
                (Consumer<Map<String, String>>) fileContent -> {}
            ),
            Arguments.arguments(
                "Missing username and empty password",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingPassword", "");
                }
            ),
            Arguments.arguments(
                "Empty username and missing password",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingUsername", "");
                }
            ),
            Arguments.arguments(
                "Empty username and empty password",
                (Consumer<Map<String, String>>) fileContent -> {
                    fileContent.put("uf.sdk.messagingUsername", "");
                    fileContent.put("uf.sdk.messagingPassword", "");
                }
            )
        );
    }
}
