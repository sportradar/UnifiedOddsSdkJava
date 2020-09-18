/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 10/04/2018.
 * // TODO @eti: Javadoc
 */
public abstract class SDKConfigurationReader {
    private final SdkProperties sdkProperties;

    SDKConfigurationReader() {
        sdkProperties = new SdkProperties(this::readConfiguration);
    }

    public Optional<String> readAccessToken() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.accessToken"));
    }

    public Optional<Integer> readMaxInactivitySeconds() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.maxInactivitySeconds")).map(value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The provided uf.sdk.maxInactivitySeconds is not a valid number, value: " + value);
            }
        });
    }

    public Optional<Locale> readDefaultLocale() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.defaultLocale")).map(Locale::forLanguageTag);
    }

    public List<Locale> readDesiredLocales() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.desiredLocales")).map(locales ->
                Stream.of(locales.split(",")).map(Locale::forLanguageTag).collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }

    public Optional<String> readApiHost() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.apiHost"));
    }

    public Optional<String> readMessagingHost() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingHost"));
    }

    public Optional<Integer> readMessagingPort() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.messagingPort")).map(value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The provided uf.sdk.messagingPort is not a valid number, value: " + value);
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

    public Optional<Boolean> readUseApiSsl() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.useApiSsl"))
                .map(value -> value.equals("true"));
    }

    public Optional<Boolean> readUseMessagingSsl() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.useMessagingSsl"))
                .map(value -> value.equals("true"));
    }

    public List<Integer> readDisabledProducers() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.disabledProducers")).map(locales ->
                Stream.of(locales.split(",")).map(value -> {
                    try {
                        return Integer.valueOf(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.disabledProducers contains an invalid number, invalid value: " + value);
                    }
                }).collect(Collectors.toList())
        ).orElse(Collections.emptyList());
    }

    public Optional<Integer> readMaxRecoveryTime() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.maxRecoveryTime")).map(value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The provided uf.sdk.maxRecoveryTime is not a valid number, value: " + value);
            }
        });
    }

    public Optional<Boolean> readUseIntegration() {
        Optional<Boolean> useIntegration = Optional.ofNullable(sdkProperties.get("uf.sdk.useIntegration"))
                .map(value -> value.equals("true"));

        if (!useIntegration.isPresent())
            useIntegration = Optional.ofNullable(sdkProperties.get("uf.sdk.useStaging"))
                    .map(value -> value.equals("true"));

        return useIntegration;
    }

    public Optional<Integer> readSdkNodeId() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.nodeId"))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.nodeId is not a valid number, value: " + value);
                    }
                });
    }

    public Optional<ExceptionHandlingStrategy> readExceptionHandlingStrategy() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.exceptionHandlingStrategy"))
                .map(value -> value.equalsIgnoreCase("throw") ? ExceptionHandlingStrategy.Throw : ExceptionHandlingStrategy.Catch);
    }

    public Optional<Boolean> readCleanTrafficLogEntries() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.cleanTrafficLogEntries"))
                .map(value -> value.equals("true"));
    }

    public Optional<Integer> readHttpClientTimeout() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.httpClientTimeout"))
                .map(value -> {
                    int timeout = Integer.parseInt(value);

                    if (timeout < 0) {
                        throw new IllegalArgumentException("The provided uf.sdk.httpClientTimeout is not a valid timeout value, value: " + value);
                    }

                    return timeout;
                });
    }

    public Optional<Integer> readHttpClientMaxConnTotal() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.httpClientMaxConnTotal"))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.httpClientMaxConnTotal is not a valid number, value: " + value);
                    }
                });
    }

    public Optional<Integer> readHttpClientMaxConnPerRoute() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.httpClientMaxConnPerRoute"))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.httpClientMaxConnPerRoute is not a valid number, value: " + value);
                    }
                });
    }

    public Optional<Integer> readRecoveryHttpClientTimeout() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.recoveryHttpClientTimeout"))
                .map(value -> {
                    int timeout = Integer.parseInt(value);

                    if (timeout < 0) {
                        throw new IllegalArgumentException("The provided uf.sdk.recoveryHttpClientTimeout is not a valid timeout value, value: " + value);
                    }

                    return timeout;
                });
    }

    public Optional<Integer> readRecoveryHttpClientMaxConnTotal() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.recoveryHttpClientMaxConnTotal"))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.recoveryHttpClientMaxConnTotal is not a valid number, value: " + value);
                    }
                });
    }

    public Optional<Integer> readRecoveryHttpClientMaxConnPerRoute() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.recoveryHttpClientMaxConnPerRoute"))
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("The provided uf.sdk.recoveryHttpClientMaxConnPerRoute is not a valid number, value: " + value);
                    }
                });
    }

    public Optional<Boolean> readSimpleVariantCaching() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.simpleVariantCaching"))
                .map(value -> value.equals("true"));
    }

    public Set<String> readSchedulerTasksToSkip() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.schedulerTasksToSkip"))
                .map(locales -> Stream.of(locales.split(",")).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    public Optional<Integer> readMinIntervalBetweenRecoveryRequests() {
        return Optional.ofNullable(sdkProperties.get("uf.sdk.minIntervalBetweenRecoveryRequests")).map(value -> {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("The provided uf.sdk.minIntervalBetweenRecoveryRequests is not a valid number, value: " + value);
            }
        });
    }

    abstract Map<String,String> readConfiguration();

    //NOTE: Access to SDKConfigurationReader must be single-threaded
    static class SdkProperties {
        private Map<String, String> sdkProperties;
        private final Supplier<Map<String, String>> configSuplier;

        SdkProperties(Supplier<Map<String, String>> configSuplier) {
            this.configSuplier = configSuplier;
        }

        public String get(String key) {
            if (sdkProperties == null) {
                sdkProperties = configSuplier.get();
            }

            return sdkProperties.get(key);
        }
    }
}
