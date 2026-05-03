/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.CriticalPath;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.CacheMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.CompositeMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline.NoOpProcessedFixtureChangesTracker;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.ChannelSupervisor;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.RabbitMqMessageReceiver;
import com.sportradar.unifiedodds.sdk.managers.RecoveryManager;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.utils.domain.names.Languages;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class CriticalPathBuilders {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BuilderStubbingOutCachesAndListeners {

        private Optional<SdkInternalConfiguration> sdkInternalConfiguration = Optional.empty();
        private Optional<RecoveryManager> recoveryManager = Optional.empty();
        private Optional<SdkProducerManager> sdkProducerManager = Optional.empty();
        private Optional<SportDataProvider> sportDataProvider = Optional.empty();
        private Optional<SportEntityFactory> sportEntityFactory = Optional.empty();
        private Optional<UofExtListener> uofExtListener = Optional.empty();
        private Optional<NamedValuesProvider> namedValuesProvider = Optional.empty();
        private Optional<MarketDescriptionProvider> marketDescriptionProvider = Optional.empty();
        private Optional<MarketFactory> marketFactory = Optional.empty();
        private Optional<Cache<String, String>> dispatchedFixtureChangesCache = Optional.empty();
        private Optional<ChannelSupervisor> channelSupervisor = Optional.empty();

        // Internal configurations and state
        private Optional<AtomicBoolean> feedOpened = Optional.empty();
        private Optional<TimeUtils> timeUtils = Optional.empty();
        private Optional<ExceptionHandlingStrategy> exceptionHandlingStrategy = Optional.empty();
        private Optional<Locale> defaultLanguage = Optional.empty();

        public static BuilderStubbingOutCachesAndListeners stubbingOutCachesAndListeners() {
            return new BuilderStubbingOutCachesAndListeners();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(SdkInternalConfiguration sdkInternalConfiguration) {
            this.sdkInternalConfiguration = Optional.of(sdkInternalConfiguration);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(RecoveryManager recoveryManager) {
            this.recoveryManager = Optional.of(recoveryManager);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(SdkProducerManager sdkProducerManager) {
            this.sdkProducerManager = Optional.of(sdkProducerManager);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(SportDataProvider sportDataProvider) {
            this.sportDataProvider = Optional.of(sportDataProvider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(SportEntityFactory sportEntityFactory) {
            this.sportEntityFactory = Optional.of(sportEntityFactory);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(UofExtListener uofExtListener) {
            this.uofExtListener = Optional.of(uofExtListener);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(NamedValuesProvider namedValuesProvider) {
            this.namedValuesProvider = Optional.of(namedValuesProvider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(
            MarketDescriptionProvider marketDescriptionProvider
        ) {
            this.marketDescriptionProvider = Optional.of(marketDescriptionProvider);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(MarketFactory marketFactory) {
            this.marketFactory = Optional.of(marketFactory);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(
            Cache<String, String> dispatchedFixtureChangesCache
        ) {
            this.dispatchedFixtureChangesCache = Optional.of(dispatchedFixtureChangesCache);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(ChannelSupervisor channelSupervisor) {
            this.channelSupervisor = Optional.of(channelSupervisor);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(TimeUtils timeUtils) {
            this.timeUtils = Optional.of(timeUtils);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutCachesAndListeners with(
            ExceptionHandlingStrategy exceptionHandlingStrategy
        ) {
            this.exceptionHandlingStrategy = Optional.of(exceptionHandlingStrategy);
            return this;
        }

        public BuilderStubbingOutCachesAndListeners withDefaultLanguage(Locale language) {
            this.defaultLanguage = Optional.of(language);
            return this;
        }

        @SuppressWarnings({ "MethodLength", "LambdaBodyLength" })
        public CriticalPathFacade build() throws JAXBException {
            val config = configWithInjectedErrorStrategyAndLanguages();
            val sdkProducerManagerOrMock = sdkProducerManager.orElse(mock(SdkProducerManager.class));
            val namedValuesProviderOrMock = namedValuesProvider.orElse(mock(NamedValuesProvider.class));

            val feedMessageFactory = new FeedMessageFactoryImpl(
                marketFactory.orElse(mock(MarketFactory.class)),
                namedValuesProviderOrMock,
                sdkProducerManagerOrMock
            );

            val feedMessageValidator = new FeedMessageValidatorImpl(
                marketDescriptionProvider.orElse(mock(MarketDescriptionProvider.class)),
                config,
                namedValuesProviderOrMock
            );

            val channelMessageConsumer = new ChannelMessageConsumerImpl(
                new RegexRoutingKeyParser(),
                config,
                sdkProducerManagerOrMock,
                JAXBContext.newInstance("com.sportradar.uf.datamodel")
            );

            val messageReceiver = new RabbitMqMessageReceiver(
                channelSupervisor.orElse(mock(ChannelSupervisor.class)),
                channelMessageConsumer
            );

            val compositeMessageProcessor = new CompositeMessageProcessor(
                singletonList(
                    new CacheMessageProcessor(
                        mock(SportEventStatusCache.class),
                        mock(SportEventCache.class),
                        new NoOpProcessedFixtureChangesTracker(),
                        sdkProducerManagerOrMock
                    )
                )
            );

            Supplier<UofSessionImpl> sessionFactory = () ->
                new UofSessionImpl(
                    config,
                    messageReceiver,
                    recoveryManager.orElse(mock(RecoveryManager.class)),
                    compositeMessageProcessor,
                    sdkProducerManagerOrMock,
                    sportDataProvider.orElse(mock(SportDataProvider.class)),
                    sportEntityFactory.orElse(mock(SportEntityFactory.class)),
                    feedMessageFactory,
                    feedMessageValidator,
                    new UnifiedOddsStatistics(),
                    dispatchedFixtureChangesCache.orElse(mock(Cache.class))
                );

            val criticalPath = new CriticalPath(
                config,
                uofExtListener.orElse(mock(UofExtListener.class)),
                sessionFactory,
                feedOpened.orElse(new AtomicBoolean(false)),
                sdkProducerManagerOrMock
            );

            return new CriticalPathFacade(criticalPath, channelMessageConsumer);
        }

        @NotNull
        private SdkInternalConfiguration configWithInjectedErrorStrategyAndLanguages() {
            val config = sdkInternalConfiguration.orElse(mock(SdkInternalConfiguration.class));
            val strategy = this.exceptionHandlingStrategy.orElse(ExceptionHandlingStrategy.Throw);
            when(config.getExceptionHandlingStrategy()).thenReturn(strategy);
            val language = defaultLanguage.orElse(Languages.any());
            when(config.getDefaultLocale()).thenReturn(language);
            when(config.getDesiredLocales()).thenReturn(Arrays.asList(language));
            return config;
        }
    }
}
