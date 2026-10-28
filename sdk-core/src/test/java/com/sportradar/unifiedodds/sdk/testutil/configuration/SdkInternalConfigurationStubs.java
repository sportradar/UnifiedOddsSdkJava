/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.configuration;

import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.testutil.unitbuilder.providedvalue.ProvidedValue;
import java.util.*;

public class SdkInternalConfigurationStubs {

    private SdkInternalConfigurationStubs() {}

    public static Builder simpleStub() {
        return new Builder();
    }

    @SuppressWarnings("HiddenField")
    public static class Builder {

        private ProvidedValue<Locale> defaultLocale;
        private ProvidedValue<List<Locale>> desiredLocales;
        private ProvidedValue<String> messagingHost;
        private ProvidedValue<String> apiHost;
        private ProvidedValue<Integer> apiPort;
        private ProvidedValue<String> apiHostAndPort;
        private ProvidedValue<Environment> environment;
        private ProvidedValue<Integer> longestInactivityInterval;
        private ProvidedValue<Integer> maxRecoveryExecutionSeconds;
        private ProvidedValue<Integer> minIntervalBetweenRecoveryRequests;
        private ProvidedValue<Boolean> useMessagingSsl;
        private ProvidedValue<Boolean> useApiSsl;
        private ProvidedValue<Integer> port;
        private ProvidedValue<Boolean> isReplaySession;
        private ProvidedValue<String> messagingUsername;
        private ProvidedValue<String> messagingPassword;
        private ProvidedValue<String> messagingVirtualHost;
        private ProvidedValue<ExceptionHandlingStrategy> exceptionHandlingStrategy;
        private ProvidedValue<Integer> sdkNodeId;
        private ProvidedValue<Boolean> cleanTrafficLogEntries;
        private ProvidedValue<Integer> httpClientTimeout;
        private ProvidedValue<Integer> httpClientMaxConnTotal;
        private ProvidedValue<Integer> httpClientMaxConnPerRoute;
        private ProvidedValue<Integer> recoveryHttpClientTimeout;
        private ProvidedValue<Integer> recoveryHttpClientMaxConnTotal;
        private ProvidedValue<Integer> recoveryHttpClientMaxConnPerRoute;
        private ProvidedValue<List<Integer>> disabledProducers;
        private ProvidedValue<Set<String>> schedulerTasksToSkip;
        private ProvidedValue<Long> fastHttpClientTimeout;

        public Builder withDefaultLocale(Locale defaultLocale) {
            this.defaultLocale = new ProvidedValue<>(defaultLocale);
            return this;
        }

        public Builder withDesiredLocales(List<Locale> desiredLocales) {
            this.desiredLocales = new ProvidedValue<>(desiredLocales);
            return this;
        }

        public Builder withMessagingHost(String messagingHost) {
            this.messagingHost = new ProvidedValue<>(messagingHost);
            return this;
        }

        public Builder withApiHost(String apiHost) {
            this.apiHost = new ProvidedValue<>(apiHost);
            return this;
        }

        public Builder withApiPort(Integer apiPort) {
            this.apiPort = new ProvidedValue<>(apiPort);
            return this;
        }

        public Builder withApiHostAndPort(String apiHostAndPort) {
            this.apiHostAndPort = new ProvidedValue<>(apiHostAndPort);
            return this;
        }

        public Builder withEnvironment(Environment environment) {
            this.environment = new ProvidedValue<>(environment);
            return this;
        }

        public Builder withLongestInactivityInterval(Integer longestInactivityInterval) {
            this.longestInactivityInterval = new ProvidedValue<>(longestInactivityInterval);
            return this;
        }

        public Builder withMaxRecoveryExecutionSeconds(Integer maxRecoveryExecutionSeconds) {
            this.maxRecoveryExecutionSeconds = new ProvidedValue<>(maxRecoveryExecutionSeconds);
            return this;
        }

        public Builder withMinIntervalBetweenRecoveryRequests(Integer minIntervalBetweenRecoveryRequests) {
            this.minIntervalBetweenRecoveryRequests = new ProvidedValue<>(minIntervalBetweenRecoveryRequests);
            return this;
        }

        public Builder withUseMessagingSsl(Boolean useMessagingSsl) {
            this.useMessagingSsl = new ProvidedValue<>(useMessagingSsl);
            return this;
        }

        public Builder withUseApiSsl(Boolean useApiSsl) {
            this.useApiSsl = new ProvidedValue<>(useApiSsl);
            return this;
        }

        public Builder withPort(Integer port) {
            this.port = new ProvidedValue<>(port);
            return this;
        }

        public Builder withIsReplaySession(Boolean isReplaySession) {
            this.isReplaySession = new ProvidedValue<>(isReplaySession);
            return this;
        }

        public Builder withMessagingUsername(String messagingUsername) {
            this.messagingUsername = new ProvidedValue<>(messagingUsername);
            return this;
        }

        public Builder withMessagingPassword(String messagingPassword) {
            this.messagingPassword = new ProvidedValue<>(messagingPassword);
            return this;
        }

        public Builder withMessagingVirtualHost(String messagingVirtualHost) {
            this.messagingVirtualHost = new ProvidedValue<>(messagingVirtualHost);
            return this;
        }

        public Builder withExceptionHandlingStrategy(ExceptionHandlingStrategy exceptionHandlingStrategy) {
            this.exceptionHandlingStrategy = new ProvidedValue<>(exceptionHandlingStrategy);
            return this;
        }

        public Builder withSdkNodeId(Integer sdkNodeId) {
            this.sdkNodeId = new ProvidedValue<>(sdkNodeId);
            return this;
        }

        public Builder withCleanTrafficLogEntries(Boolean cleanTrafficLogEntries) {
            this.cleanTrafficLogEntries = new ProvidedValue<>(cleanTrafficLogEntries);
            return this;
        }

        public Builder withHttpClientTimeout(Integer httpClientTimeout) {
            this.httpClientTimeout = new ProvidedValue<>(httpClientTimeout);
            return this;
        }

        public Builder withHttpClientMaxConnTotal(Integer httpClientMaxConnTotal) {
            this.httpClientMaxConnTotal = new ProvidedValue<>(httpClientMaxConnTotal);
            return this;
        }

        public Builder withHttpClientMaxConnPerRoute(Integer httpClientMaxConnPerRoute) {
            this.httpClientMaxConnPerRoute = new ProvidedValue<>(httpClientMaxConnPerRoute);
            return this;
        }

        public Builder withRecoveryHttpClientTimeout(Integer recoveryHttpClientTimeout) {
            this.recoveryHttpClientTimeout = new ProvidedValue<>(recoveryHttpClientTimeout);
            return this;
        }

        public Builder withRecoveryHttpClientMaxConnTotal(Integer recoveryHttpClientMaxConnTotal) {
            this.recoveryHttpClientMaxConnTotal = new ProvidedValue<>(recoveryHttpClientMaxConnTotal);
            return this;
        }

        public Builder withRecoveryHttpClientMaxConnPerRoute(Integer recoveryHttpClientMaxConnPerRoute) {
            this.recoveryHttpClientMaxConnPerRoute = new ProvidedValue<>(recoveryHttpClientMaxConnPerRoute);
            return this;
        }

        public Builder withDisabledProducers(List<Integer> disabledProducers) {
            this.disabledProducers = new ProvidedValue<>(disabledProducers);
            return this;
        }

        public Builder withSchedulerTasksToSkip(Set<String> schedulerTasksToSkip) {
            this.schedulerTasksToSkip = new ProvidedValue<>(schedulerTasksToSkip);
            return this;
        }

        public Builder withFastHttpClientTimeout(Long fastHttpClientTimeout) {
            this.fastHttpClientTimeout = new ProvidedValue<>(fastHttpClientTimeout);
            return this;
        }

        @SuppressWarnings(
            {
                "ExecutableStatementCount",
                "MethodLength",
                "CyclomaticComplexity",
                "JavaNCSS",
                "NPathComplexity",
            }
        )
        public SdkInternalConfiguration build() {
            SdkInternalConfiguration mock = mock(
                SdkInternalConfiguration.class,
                invocation -> {
                    String methodName = invocation.getMethod().getName();
                    throw new UnsupportedOperationException(
                        "SdkInternalConfiguration." + methodName + "() is not stubbed"
                    );
                }
            );

            if (defaultLocale != null) {
                doReturn(defaultLocale.get()).when(mock).getDefaultLocale();
            }

            if (desiredLocales != null) {
                doReturn(desiredLocales.get()).when(mock).getDesiredLocales();
            }

            if (messagingHost != null) {
                doReturn(messagingHost.get()).when(mock).getMessagingHost();
            }

            if (apiHost != null) {
                doReturn(apiHost.get()).when(mock).getApiHost();
            }

            if (apiPort != null) {
                doReturn(apiPort.get()).when(mock).getApiPort();
            }

            if (apiHostAndPort != null) {
                doReturn(apiHostAndPort.get()).when(mock).getApiHostAndPort();
            }

            if (environment != null) {
                doReturn(environment.get()).when(mock).getEnvironment();
            }

            if (longestInactivityInterval != null) {
                doReturn(longestInactivityInterval.get()).when(mock).getLongestInactivityInterval();
            }

            if (maxRecoveryExecutionSeconds != null) {
                doReturn(maxRecoveryExecutionSeconds.get()).when(mock).getMaxRecoveryExecutionSeconds();
            }

            if (minIntervalBetweenRecoveryRequests != null) {
                doReturn(minIntervalBetweenRecoveryRequests.get())
                    .when(mock)
                    .getMinIntervalBetweenRecoveryRequests();
            }

            if (useMessagingSsl != null) {
                doReturn(useMessagingSsl.get()).when(mock).getUseMessagingSsl();
            }

            if (useApiSsl != null) {
                doReturn(useApiSsl.get()).when(mock).getUseApiSsl();
            }

            if (port != null) {
                doReturn(port.get()).when(mock).getPort();
            }

            if (isReplaySession != null) {
                doReturn(isReplaySession.get()).when(mock).isReplaySession();
            }

            if (messagingUsername != null) {
                doReturn(messagingUsername.get()).when(mock).getMessagingUsername();
            }

            if (messagingPassword != null) {
                doReturn(messagingPassword.get()).when(mock).getMessagingPassword();
            }

            if (messagingVirtualHost != null) {
                doReturn(messagingVirtualHost.get()).when(mock).getMessagingVirtualHost();
            }

            if (exceptionHandlingStrategy != null) {
                doReturn(exceptionHandlingStrategy.get()).when(mock).getExceptionHandlingStrategy();
            }

            if (sdkNodeId != null) {
                doReturn(sdkNodeId.get()).when(mock).getSdkNodeId();
            }

            if (cleanTrafficLogEntries != null) {
                doReturn(cleanTrafficLogEntries.get()).when(mock).isCleanTrafficLogEntriesEnabled();
            }

            if (httpClientTimeout != null) {
                doReturn(httpClientTimeout.get()).when(mock).getHttpClientTimeout();
            }

            if (httpClientMaxConnTotal != null) {
                doReturn(httpClientMaxConnTotal.get()).when(mock).getHttpClientMaxConnTotal();
            }

            if (httpClientMaxConnPerRoute != null) {
                doReturn(httpClientMaxConnPerRoute.get()).when(mock).getHttpClientMaxConnPerRoute();
            }

            if (recoveryHttpClientTimeout != null) {
                doReturn(recoveryHttpClientTimeout.get()).when(mock).getRecoveryHttpClientTimeout();
            }

            if (recoveryHttpClientMaxConnTotal != null) {
                doReturn(recoveryHttpClientMaxConnTotal.get()).when(mock).getRecoveryHttpClientMaxConnTotal();
            }

            if (recoveryHttpClientMaxConnPerRoute != null) {
                doReturn(recoveryHttpClientMaxConnPerRoute.get())
                    .when(mock)
                    .getRecoveryHttpClientMaxConnPerRoute();
            }

            if (disabledProducers != null) {
                doReturn(disabledProducers.get()).when(mock).getDisabledProducers();
            }

            if (schedulerTasksToSkip != null) {
                doReturn(schedulerTasksToSkip.get()).when(mock).getSchedulerTasksToSkip();
            }

            if (fastHttpClientTimeout != null) {
                doReturn(fastHttpClientTimeout.get()).when(mock).getFastHttpClientTimeout();
            }

            return mock;
        }
    }
}
