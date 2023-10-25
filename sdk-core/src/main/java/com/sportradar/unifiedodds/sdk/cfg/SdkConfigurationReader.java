/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({ "LambdaBodyLength", "MultipleStringLiterals" })
public abstract class SdkConfigurationReader {

    private final SdkProperties sdkProperties;

    SdkConfigurationReader() {
        sdkProperties = new SdkProperties(this::readConfiguration);
    }

    public Optional<String> readAccessToken() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.accessToken"));
    }

    public Optional<Locale> readDefaultLanguage() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.defaultLanguage")).map(Locale::forLanguageTag);
    }

    public List<Locale> readDesiredLanguages() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.desiredLanguages"))
            .map(locales ->
                Stream.of(locales.split(",")).map(Locale::forLanguageTag).collect(Collectors.toList())
            )
            .orElse(Collections.emptyList());
    }

    public Optional<String> readMessagingHost() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingHost"));
    }

    public Optional<Integer> readMessagingPort() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.messagingPort"))
            .map(value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "The provided uf.sdk.messagingPort is not a valid number, value: " + value
                    );
                }
            });
    }

    public Optional<String> readMessagingUsername() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingUsername"));
    }

    public Optional<String> readMessagingPassword() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingPassword"));
    }

    public Optional<String> readMessagingVirtualHost() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingVirtualHost"));
    }

    public Optional<Boolean> readMessagingUseSsl() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.messagingUseSsl"))
            .map(value -> value.equals("true"));
    }

    public Optional<String> readApiHost() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.apiHost"));
    }

    public Optional<Integer> readApiPort() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.apiPort"))
            .map(value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "The provided uf.sdk.apiPort is not a valid number, value: " + value
                    );
                }
            });
    }

    public Optional<Boolean> readApiUseSsl() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.apiUseSsl")).map(value -> value.equals("true"));
    }

    public List<Integer> readDisabledProducers() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.disabledProducers"))
            .map(locales ->
                Stream
                    .of(locales.split(","))
                    .map(value -> {
                        try {
                            return Integer.valueOf(value);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException(
                                "The provided uf.sdk.disabledProducers contains an invalid number, invalid value: " +
                                value
                            );
                        }
                    })
                    .collect(Collectors.toList())
            )
            .orElse(Collections.emptyList());
    }

    public Optional<Integer> readNodeId() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.nodeId"))
            .map(value -> {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "The provided uf.sdk.nodeId is not a valid number, value: " + value
                    );
                }
            });
    }

    public Optional<ExceptionHandlingStrategy> readExceptionHandlingStrategy() {
        return Optional
            .ofNullable(sdkProperties.get("uf.sdk.exceptionHandlingStrategy"))
            .map(value ->
                value.equalsIgnoreCase("throw")
                    ? ExceptionHandlingStrategy.Throw
                    : ExceptionHandlingStrategy.Catch
            );
    }

    public Environment readEnvironment() {
        Optional<String> ufEnv = Optional.ofNullable(sdkProperties.get("uf.sdk.environment"));
        if (ufEnv.isPresent()) {
            Environment configEnv = Environment.getEnvironment(ufEnv.get());
            if (configEnv != null) {
                return configEnv;
            }
        }

        return Environment.Integration;
    }

    abstract Map<String, String> readConfiguration();

    //NOTE: Access to SDKConfigurationReader must be single-threaded
    static class SdkProperties {

        private Map<String, String> sdkProperties;
        private final Supplier<Map<String, String>> configSupplier;

        SdkProperties(Supplier<Map<String, String>> configSupplier) {
            this.configSupplier = configSupplier;
        }

        public String get(String key) {
            if (sdkProperties == null) {
                sdkProperties = configSupplier.get();
            }

            return sdkProperties.get(key);
        }
    }
}
