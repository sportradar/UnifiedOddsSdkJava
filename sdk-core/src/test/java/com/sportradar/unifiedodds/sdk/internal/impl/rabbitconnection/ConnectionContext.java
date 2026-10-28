/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.MessageInterest.AllMessages;
import static com.sportradar.unifiedodds.sdk.caching.markets.GenericAnswers.withAllMethodsThrowingByDefault;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.ShutdownSignalException;
import com.sportradar.unifiedodds.sdk.SdkConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.conn.RabbitMqMessageListener;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.utils.thread.sleep.Sleep;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class ConnectionContext implements AutoCloseable {

    private SingleInstanceAmqpConnectionFactory realConnectionFactory;

    public RabbitMqChannelBuilder channelBuilder() {
        return new RabbitMqChannelBuilder();
    }

    public void close() throws IOException {
        if (realConnectionFactory != null) {
            realConnectionFactory.close(true);
        }
    }

    @SuppressWarnings({ "HiddenField" })
    public class RabbitMqChannelBuilder {

        private RabbitMqMessageListener messageListener;
        private RabbitMqSystemListener rabbitMqSystemListener;
        private Consumer<Recoverable> handleRecoveryListener;
        private Consumer<Recoverable> handleRecoveryStartedListener;
        private Consumer<Recoverable> handleTopologyRecoveryStartedListener;
        private Consumer<ShutdownSignalException> shutdownCompletedListener;
        private SdkConnectionStatusListener sdkConnectionStatusListener;
        private WhoAmIReader whoAmIReader;
        private TimeUtils timeUtils;
        private Sleep sleep;
        private String sdkVersion;
        private SdkInternalConfiguration deprecatedConfiguration;
        private String consumerDescription;
        private List<String> routingKeys;
        private OAuth2TokenCache tokenCache;
        private UofConfiguration configuration;

        private RabbitMqChannelBuilder() {}

        public RabbitMqChannelBuilder with(RabbitMqMessageListener listener) {
            this.messageListener = listener;
            return this;
        }

        public RabbitMqChannelBuilder with(RabbitMqSystemListener rabbitMqSystemListener) {
            this.rabbitMqSystemListener = rabbitMqSystemListener;
            return this;
        }

        public RabbitMqChannelBuilder with(SdkConnectionStatusListener sdkConnectionStatusListener) {
            this.sdkConnectionStatusListener = sdkConnectionStatusListener;
            return this;
        }

        public RabbitMqChannelBuilder with(WhoAmIReader whoAmIReader) {
            this.whoAmIReader = whoAmIReader;
            return this;
        }

        public RabbitMqChannelBuilder with(TimeUtils timeUtils) {
            this.timeUtils = timeUtils;
            return this;
        }

        public RabbitMqChannelBuilder with(SdkInternalConfiguration configuration) {
            this.deprecatedConfiguration = configuration;
            return this;
        }

        public RabbitMqChannelBuilder with(OAuth2TokenCache tokenCache) {
            this.tokenCache = tokenCache;
            return this;
        }

        public RabbitMqChannelBuilder with(UofConfiguration config) {
            this.configuration = config;
            return this;
        }

        public RabbitMqChannelBuilder withSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }

        public RabbitMqChannelBuilder withRoutingKeys(String... routingKeys) {
            this.routingKeys = asList(routingKeys);
            return this;
        }

        public RabbitMqChannelBuilder withHandleRecoveryListener(
            Consumer<Recoverable> handleRecoveryListener
        ) {
            this.handleRecoveryListener = handleRecoveryListener;
            return this;
        }

        public RabbitMqChannelBuilder withHandleRecoveryStartedListener(
            Consumer<Recoverable> handleRecoveryStartedListener
        ) {
            this.handleRecoveryStartedListener = handleRecoveryStartedListener;
            return this;
        }

        public RabbitMqChannelBuilder withHandleTopologyRecoveryStartedListener(
            Consumer<Recoverable> handleTopologyRecoveryStartedListener
        ) {
            this.handleTopologyRecoveryStartedListener = handleTopologyRecoveryStartedListener;
            return this;
        }

        public RabbitMqChannelBuilder withShutdownCompletedListener(
            Consumer<ShutdownSignalException> shutdownCompletedListener
        ) {
            this.shutdownCompletedListener = shutdownCompletedListener;
            return this;
        }

        public RabbitMqChannelBuilder withConsumerDescription(String consumerDescription) {
            this.consumerDescription = consumerDescription;
            return this;
        }

        public RabbitMqChannelImpl buildOpened() throws IOException {
            val rabbitMqSystemListener = ofNullable(this.rabbitMqSystemListener)
                .orElse(createCompositeSystemListener());
            val whoAmIReader = ofNullable(this.whoAmIReader).orElse(createErroringOnAllMethodsWhoAmIReader());
            val sdkConnectionStatusListener = ofNullable(this.sdkConnectionStatusListener)
                .orElse(mock(SdkConnectionStatusListener.class));
            val timeUtils = ofNullable(this.timeUtils).orElse(new TimeUtilsImpl());
            val tokenCache = ofNullable(this.tokenCache).orElse(createErroringOnAllMethodsTokenCache());

            if (realConnectionFactory == null) {
                realConnectionFactory =
                    createRealConnectionFactory(
                        sdkConnectionStatusListener,
                        whoAmIReader,
                        timeUtils,
                        tokenCache
                    );
            }

            RabbitMqChannelImpl channel = new RabbitMqChannelImpl(
                rabbitMqSystemListener,
                whoAmIReader,
                ofNullable(sdkVersion).orElseThrow(() -> exceptionDueToMissing("SdkVersion")),
                realConnectionFactory,
                timeUtils,
                new Sleep()
            );

            channel.open(
                ofNullable(routingKeys).orElseThrow(() -> exceptionDueToMissing("RoutingKeys")),
                createConsumerWithDescriptionAndListenerIfPresent(),
                AllMessages.toShortString()
            );
            return channel;
        }

        private OAuth2TokenCache createErroringOnAllMethodsTokenCache() {
            return mock(
                OAuth2TokenCache.class,
                invocation -> {
                    throw new UnsupportedOperationException(
                        "OAuth2TokenCache not injected - use with(OAuth2TokenCache)"
                    );
                }
            );
        }

        @NotNull
        @SuppressWarnings("AnonInnerLength")
        private ChannelMessageConsumer createConsumerWithDescriptionAndListenerIfPresent() {
            val messageListener = ofNullable(this.messageListener)
                .orElse(mock(RabbitMqMessageListener.class));
            return new ChannelMessageConsumer() {
                @Override
                public void open(MessageConsumer messageConsumer) {}

                @Override
                public void onMessageReceived(
                    String routingKey,
                    byte[] body,
                    AMQP.BasicProperties properties,
                    long receivedAt
                ) {
                    messageListener.onMessageReceived(routingKey, body, properties, receivedAt);
                }

                @Override
                public String getConsumerDescription() {
                    return ofNullable(consumerDescription)
                        .orElseThrow(() -> exceptionDueToMissing("ConsumerDescription"));
                }

                @Override
                public void close() throws IOException {}
            };
        }

        private RuntimeException exceptionDueToMissing(String injectableComponent) {
            return new RuntimeException(
                injectableComponent + " not injected - use with(" + injectableComponent + ")"
            );
        }

        private WhoAmIReader createErroringOnAllMethodsWhoAmIReader() {
            return mock(
                WhoAmIReader.class,
                invocation -> {
                    throw new UnsupportedOperationException(
                        "WhoAmIReader not injected - use with(WhoAmIReader)"
                    );
                }
            );
        }

        private SingleInstanceAmqpConnectionFactory createRealConnectionFactory(
            SdkConnectionStatusListener sdkConnectionStatusListener,
            WhoAmIReader whoAmIReader,
            TimeUtils timeUtils,
            OAuth2TokenCache tokenCache
        ) {
            val deprecatedConfig = ofNullable(deprecatedConfiguration)
                .orElse(stubDeprecatedConfigurationErroringOnAllMethods());
            val config = ofNullable(configuration).orElse(stubConfigurationErroringOnAllMethods());
            val configuredConnectionFactory = createConfiguredConnectionFactory(
                deprecatedConfig,
                sdkConnectionStatusListener,
                timeUtils,
                tokenCache,
                config
            );
            val firewallChecker = createFirewallChecker();
            val sslProtocolsProvider = new SslProtocolsProvider();

            return new SingleInstanceAmqpConnectionFactory(
                configuredConnectionFactory,
                deprecatedConfig,
                sdkConnectionStatusListener,
                whoAmIReader,
                firewallChecker,
                timeUtils,
                sslProtocolsProvider
            );
        }

        private UofConfiguration stubConfigurationErroringOnAllMethods() {
            return mock(UofConfiguration.class, withAllMethodsThrowingByDefault());
        }

        private SdkInternalConfiguration stubDeprecatedConfigurationErroringOnAllMethods() {
            return mock(SdkInternalConfiguration.class, withAllMethodsThrowingByDefault());
        }

        private ConfiguredConnectionFactory createConfiguredConnectionFactory(
            SdkInternalConfiguration config,
            SdkConnectionStatusListener sdkConnectionStatusListener,
            TimeUtils timeUtils,
            OAuth2TokenCache tokenCache,
            UofConfiguration uofConfiguration
        ) {
            val rabbitConnectionFactory = new ConnectionFactory();
            val dedicatedExecutor = Executors.newSingleThreadExecutor();
            val version = ofNullable(sdkVersion).orElse("test-version");

            return new ConfiguredConnectionFactory(
                rabbitConnectionFactory,
                config,
                uofConfiguration,
                version,
                sdkConnectionStatusListener,
                dedicatedExecutor,
                timeUtils,
                tokenCache
            );
        }

        private FirewallChecker createFirewallChecker() {
            val socketFactory = new SocketFactory();
            val bodyOnlyFetchingHttpClient = new BodyOnlyFetchingHttpClient();
            return new FirewallChecker(socketFactory, bodyOnlyFetchingHttpClient);
        }

        @SuppressWarnings("AnonInnerLength")
        private RabbitMqSystemListener createCompositeSystemListener() {
            return new RabbitMqSystemListener() {
                @Override
                public void handleRecovery(Recoverable recoverable) {
                    ofNullable(handleRecoveryListener).ifPresent(listener -> listener.accept(recoverable));
                }

                @Override
                public void handleRecoveryStarted(Recoverable recoverable) {
                    ofNullable(handleRecoveryStartedListener)
                        .ifPresent(listener -> listener.accept(recoverable));
                }

                @Override
                public void handleTopologyRecoveryStarted(Recoverable recoverable) {
                    ofNullable(handleTopologyRecoveryStartedListener)
                        .ifPresent(listener -> listener.accept(recoverable));
                }

                @Override
                public void shutdownCompleted(ShutdownSignalException cause) {
                    ofNullable(shutdownCompletedListener).ifPresent(listener -> listener.accept(cause));
                }
            };
        }
    }
}
