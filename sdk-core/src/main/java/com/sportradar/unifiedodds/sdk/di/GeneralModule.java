/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.MessageAndActionExtractor;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.mappings.MappingValidatorFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.*;
import com.sportradar.unifiedodds.sdk.impl.recovery.RecoveryManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.recovery.SingleRecoveryManagerSupervisor;
import com.sportradar.unifiedodds.sdk.impl.util.MdcScheduledExecutorService;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * A {@link Module} implementation used to set-up general SDK dependency injection settings
 */

@SuppressWarnings("ClassFanOutComplexity")
public class GeneralModule implements Module {

    private final Random random = new Random();

    /**
     * A {@link UofGlobalEventsListener} implementation used to
     * notify the outside world about global events
     */
    private final UofGlobalEventsListener sdkListener;

    GeneralModule(UofGlobalEventsListener sdkListener) {
        checkNotNull(sdkListener, "sdkListener cannot be a null reference");

        this.sdkListener = sdkListener;
    }

    /**
     * Registers required types with the injection container
     *
     * @param binder A {@link Binder} representing the injection container handle
     */
    @Override
    @SuppressWarnings({ "ExecutableStatementCount", "MethodLength", "MagicNumber" })
    public void configure(Binder binder) {
        // listeners instance bind
        binder.bind(SdkProducerStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SdkConnectionStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SdkEventRecoveryStatusListener.class).toInstance(this.sdkListener);

        // rabbit MQ connection factory & rabbit MQ connection factory wrapper
        binder.bind(ConnectionFactory.class).in(Singleton.class);
        binder.bind(AmqpConnectionFactory.class).to(SingleInstanceAmqpConnectionFactory.class);
        binder.bind(SingleInstanceAmqpConnectionFactory.class).in(Singleton.class);

        // other rabbit instances
        binder.bind(OnDemandChannelSupervisor.class).to(RabbitMqChannelImpl.class);
        binder.bind(ChannelSupervisor.class).to(ChannelSupervisionScheduler.class);
        binder.bind(MessageReceiver.class).to(RabbitMqMessageReceiver.class);

        // managers
        binder.bind(SdkProducerManager.class).to(ProducerManagerImpl.class).in(Singleton.class);
        binder.bind(SportDataProvider.class).to(SportDataProviderImpl.class).in(Singleton.class);
        binder.bind(MarketDescriptionManager.class).to(MarketManagerImpl.class).in(Singleton.class);
        binder
            .bind(CashOutProbabilitiesManager.class)
            .to(CashOutProbabilitiesManagerImpl.class)
            .in(Singleton.class);
        binder.bind(MappingTypeProvider.class).to(MappingTypeProviderImpl.class).in(Singleton.class);
        binder.bind(BookingManager.class).to(BookingManagerImpl.class).in(Singleton.class);
        binder.bind(CustomBetManager.class).to(CustomBetManagerImpl.class).in(Singleton.class);
        binder.bind(EventChangeManager.class).to(EventChangeManagerImpl.class).in(Singleton.class);

        // session
        binder.bind(UofSessionImpl.class);

        // recovery manager related stuff
        int min = random.nextInt(10000);
        IncrementalSequenceGenerator sequenceGenerator = new IncrementalSequenceGenerator(min, 1000000);
        binder.bind(SequenceGenerator.class).toInstance(sequenceGenerator);

        binder.bind(SingleRecoveryManagerSupervisor.class).in(Singleton.class);
        binder.bind(RecoveryManager.class).to(RecoveryManagerImpl.class);
        binder.bind(RabbitMqSystemListener.class).to(RecoveryManagerImpl.class);
        binder.bind(EventRecoveryRequestIssuer.class).to(RecoveryManagerImpl.class);

        // util
        binder.bind(RoutingKeyParser.class).to(RegexRoutingKeyParser.class);
        binder.bind(FeedMessageFactory.class).to(FeedMessageFactoryImpl.class);
        binder.bind(MappingValidatorFactory.class).to(MappingValidatorFactoryImpl.class);
        binder.bind(SportEventStatusFactory.class).to(SportEventStatusFactoryImpl.class);
        binder.bind(FeedMessageValidator.class).to(FeedMessageValidatorImpl.class);
        binder.bind(TimeUtils.class).to(TimeUtilsImpl.class);
    }

    @Provides
    @Singleton
    private RecoveryManagerImpl provideRecoveryManger(
        SingleRecoveryManagerSupervisor singleRecoveryManagerSupervisor
    ) {
        return singleRecoveryManagerSupervisor.getRecoveryManager();
    }

    /**
     * Provides a service class used to schedule repeating tasks
     *
     * @return a service class used to schedule repeating tasks
     */
    @Provides
    @Singleton
    private SdkTaskScheduler provideSdkTaskScheduler(
        SdkInternalConfiguration configuration,
        WhoAmIReader whoAmIReader
    ) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(whoAmIReader);

        String sdkContextDescription = whoAmIReader.getSdkContextDescription();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(sdkContextDescription + "-t-%d")
            .build();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
            1,
            namedThreadFactory
        );

        Map<String, String> mdcContext = whoAmIReader.getAssociatedSdkMdcContextMap();

        MdcScheduledExecutorService mdcScheduledExecutorService = new MdcScheduledExecutorService(
            scheduledExecutorService,
            mdcContext
        );

        return new SdkTaskSchedulerImpl(mdcScheduledExecutorService, configuration);
    }

    /**
     * Provides an {@link ExecutorService} which is being used exclusively in the {@link RecoveryManager}
     *
     * @return the {@link ExecutorService} exclusive to the {@link RecoveryManager}
     */
    @Provides
    @Singleton
    @Named("DedicatedRecoveryManagerExecutor")
    private ScheduledExecutorService provideDedicatedRecoveryManagerExecutor(
        SdkInternalConfiguration configuration,
        WhoAmIReader whoAmIReader
    ) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(whoAmIReader);

        String sdkContextDescription = whoAmIReader.getSdkContextDescription();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(sdkContextDescription + "-rm-t-%d")
            .build();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(
            1,
            namedThreadFactory
        );

        Map<String, String> mdcContext = whoAmIReader.getAssociatedSdkMdcContextMap();

        return new MdcScheduledExecutorService(scheduledExecutorService, mdcContext);
    }

    @Provides
    @Singleton
    @Named("DedicatedRabbitMqExecutor")
    private ExecutorService providesDedicatedRabbitMqExecutor(WhoAmIReader whoAmIReader) {
        Preconditions.checkNotNull(whoAmIReader);

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat(whoAmIReader.getSdkContextDescription() + "-amqp-t-%d")
            .build();

        final int concurrencyLevelOf4ToDealWithPrematchLiveVirtualsAndSystemConsumption = 4;
        return Executors.newFixedThreadPool(
            concurrencyLevelOf4ToDealWithPrematchLiveVirtualsAndSystemConsumption,
            namedThreadFactory
        );
    }
}
