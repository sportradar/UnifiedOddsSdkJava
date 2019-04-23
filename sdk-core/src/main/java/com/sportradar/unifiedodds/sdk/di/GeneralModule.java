/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.BookingManager;
import com.sportradar.unifiedodds.sdk.BookingManagerImpl;
import com.sportradar.unifiedodds.sdk.CashOutProbabilitiesManager;
import com.sportradar.unifiedodds.sdk.EventRecoveryRequestIssuer;
import com.sportradar.unifiedodds.sdk.MarketDescriptionManager;
import com.sportradar.unifiedodds.sdk.RecoveryManager;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SDKEventRecoveryStatusListener;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SDKProducerStatusListener;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.impl.AMQPConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.CashOutProbabilitiesManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.DeserializerImpl;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageFactory;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageValidator;
import com.sportradar.unifiedodds.sdk.impl.FeedMessageValidatorImpl;
import com.sportradar.unifiedodds.sdk.impl.IncrementalSequenceGenerator;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProvider;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProviderImpl;
import com.sportradar.unifiedodds.sdk.impl.MessageReceiver;
import com.sportradar.unifiedodds.sdk.impl.OddsFeedSessionImpl;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderImpl;
import com.sportradar.unifiedodds.sdk.impl.ProducerManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqChannel;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqChannelImpl;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqMessageReceiver;
import com.sportradar.unifiedodds.sdk.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.impl.RecoveryManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.RegexRoutingKeyParser;
import com.sportradar.unifiedodds.sdk.impl.RoutingKeyParser;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskSchedulerImpl;
import com.sportradar.unifiedodds.sdk.impl.SequenceGenerator;
import com.sportradar.unifiedodds.sdk.impl.SingleInstanceAMQPConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.SportsInfoManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.UnifiedOddsStatistics;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.mappings.MappingValidatorFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.util.MdcScheduledExecutorService;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link Module} implementation used to set-up general SDK dependency injection settings
 */
public class GeneralModule implements Module {
    private static final Logger logger = LoggerFactory.getLogger(GeneralModule.class);
    /**
     * A {@link com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener} implementation used to
     * notify the outside world about global events
     */
    private final SDKGlobalEventsListener sdkListener;

    /**
     * The {@link JAXBContext} used to unmarshall received messages
     */
    private final JAXBContext messagesJaxbContext;

    /**
     * The {@link JAXBContext} used to unmarshall API responses
     */
    private final JAXBContext apiJaxbContext;

    /**
     * The associated SDK configuration
     */
    private final SDKInternalConfiguration configuration;


    /**
     * Constructs a new instance of the {@link GeneralModule} class
     *
     * @param sdkListener A {@link SDKGlobalEventsListener}
     *        implementation used to notify the outside world about global events
     * @param configuration The associated SDK configuration
     */
    GeneralModule(SDKGlobalEventsListener sdkListener, SDKInternalConfiguration configuration) {
        checkNotNull(sdkListener, "sdkListener cannot be a null reference");
        checkNotNull(configuration, "the SDKInternalConfiguration can not be null");

        this.sdkListener = sdkListener;
        this.configuration = configuration;

        try {
            messagesJaxbContext = JAXBContext.newInstance("com.sportradar.uf.datamodel");
            apiJaxbContext = JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel");
        } catch (JAXBException e) {
            throw new IllegalStateException("JAXB contexts creation failed, ex: ", e);
        }
    }

    /**
     * Registers required types with the injection container
     * 
     * @param binder A {@link Binder} representing the injection container handle
     */
    @Override
    public void configure(Binder binder) {
        // TODO @eti check & cleanup the bindings
        // listeners instance bind
        binder.bind(SDKProducerStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SDKConnectionStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SDKEventRecoveryStatusListener.class).toInstance(this.sdkListener);

        binder.bindConstant().annotatedWith(Names.named("version")).to(loadVersion());

        // rabbit MQ connection factory & rabbit MQ connection factory wrapper
        binder.bind(ConnectionFactory.class).in(Singleton.class);
        binder.bind(AMQPConnectionFactory.class).to(SingleInstanceAMQPConnectionFactory.class).in(Singleton.class);

        // other rabbit instances
        binder.bind(RabbitMqChannel.class).to(RabbitMqChannelImpl.class);
        binder.bind(MessageReceiver.class).to(RabbitMqMessageReceiver.class);

        // managers
        binder.bind(ProducerDataProvider.class).to(ProducerDataProviderImpl.class).in(Singleton.class);
        binder.bind(SDKProducerManager.class).to(ProducerManagerImpl.class).in(Singleton.class);
        binder.bind(SportsInfoManager.class).to(SportsInfoManagerImpl.class).in(Singleton.class);
        binder.bind(MarketDescriptionManager.class).to(MarketManagerImpl.class).in(Singleton.class);
        binder.bind(CashOutProbabilitiesManager.class).to(CashOutProbabilitiesManagerImpl.class).in(Singleton.class);
        binder.bind(MappingTypeProvider.class).to(MappingTypeProviderImpl.class).in(Singleton.class);
        binder.bind(BookingManager.class).to(BookingManagerImpl.class).in(Singleton.class);

        // session
        binder.bind(OddsFeedSessionImpl.class);

        // recovery manager related stuff
        Random random = new Random();
        int min = random.nextInt(10000);
        IncrementalSequenceGenerator sequenceGenerator = new IncrementalSequenceGenerator(min, 1000000);
        binder.bind(SequenceGenerator.class).toInstance(sequenceGenerator);

        binder.bind(RecoveryManagerImpl.class).in(Singleton.class);
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

    private String loadVersion() {
        try {
            InputStream is =
                        SingleInstanceAMQPConnectionFactory.class.getResourceAsStream("/sr-sdk-version.properties");
            Properties props = new Properties();
            props.load(is);
            is.close();
            return props.getProperty("version");
        } catch (IOException ioe) {
            return "0.0";
        }
    }

    /**
     * Provides the {@link Unmarshaller} used to unmarshal incoming messages
     * 
     * @return The {@link Unmarshaller} instance to be registered with the DI container
     */
    @Provides @Named("MessageUnmarshaller")
    private Unmarshaller provideMessageUnmarshaller() {
        try {
            return messagesJaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to create unmarshaller for 'AMQP messages', ex: ", e);
        }
    }

    /**
     * Provides the {@link Deserializer} used to deserialize API xmls
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("ApiJaxbDeserializer")
    private Deserializer provideApiJaxbDeserializer() {
        try {
            return new DeserializerImpl(apiJaxbContext.createUnmarshaller());
        } catch (JAXBException e) {
            throw new IllegalStateException("Failed to create unmarshaller for 'api', ex: ", e);
        }
    }

    /**
     * Provides the {@link Deserializer} used to deserialize message streams
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("MessageDeserializer")
    private Deserializer provideMessageDeserializer(@Named("MessageUnmarshaller") Unmarshaller unmarshaller) {
            return new DeserializerImpl(unmarshaller);
    }

    /**
     * Provides the {@link Channel} used to communicate with the Rabbit MQ broker
     * 
     * @param connectionFactory - the connection factory used to create the channel
     * @return - the {@link Channel} used to communicate with the Rabbit MQ broker
     */
    @Provides
    private Supplier<Channel> provideChannel(AMQPConnectionFactory connectionFactory) {
        return () -> {
            try {
                Connection connection = connectionFactory.newConnection();
                return connection.createChannel();
            } catch (NoSuchAlgorithmException | KeyManagementException | TimeoutException | IOException e) {
                throw new IllegalStateException("Failed to create Rabbit MQ channel, ex: ", e);
            }
        };
    }

    /**
     * Returns the statistics collection object used by the sdk
     *
     * @return the statistics collection object used by the sdk
     */
    @Provides @Singleton
    private UnifiedOddsStatistics provideUnifiedOddsStatistics(){
        UnifiedOddsStatistics statsBean = null;

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("com.sportradar.unifiedodds.sdk.impl:type=UnifiedOdds");
            statsBean = new UnifiedOddsStatistics();
            if (!mbeanServer.isRegistered(name)) {
                mbeanServer.registerMBean(statsBean, name);
            }
        } catch (MalformedObjectNameException | NotCompliantMBeanException | MBeanRegistrationException | InstanceAlreadyExistsException e) {
            logger.warn("UnifiedOddsStatistics initialization failed w/ ex.:", e);
        }

        return statsBean;
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides @Singleton
    private CloseableHttpClient provideHttpClient(){
        int maxTimeout = Math.toIntExact(TimeUnit.MILLISECONDS.convert(configuration.getHttpClientTimeout(), TimeUnit.SECONDS));
        RequestConfig.Builder requestBuilder = RequestConfig.custom()
                .setConnectTimeout(maxTimeout)
                .setConnectionRequestTimeout(maxTimeout)
                .setSocketTimeout(maxTimeout);

        return HttpClientBuilder.create()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestBuilder.build())
                .setMaxConnPerRoute(15)
                .build();
    }

    /**
     * Provides a service class used to schedule repeating tasks
     *
     * @return a service class used to schedule repeating tasks
     */
    @Provides @Singleton
    private SDKTaskScheduler provideSDKTaskScheduler(SDKInternalConfiguration configuration, WhoAmIReader whoAmIReader) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(whoAmIReader);

        String sdkContextDescription = whoAmIReader.getSdkContextDescription();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(sdkContextDescription + "-t-%d").build();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, namedThreadFactory);

        Map<String, String> mdcContext = whoAmIReader.getAssociatedSdkMdcContextMap();

        MdcScheduledExecutorService mdcScheduledExecutorService = new MdcScheduledExecutorService(scheduledExecutorService, mdcContext);

        return new SDKTaskSchedulerImpl(mdcScheduledExecutorService, configuration);
    }

    /**
     * Provides an {@link ExecutorService} which is being used exclusively in the {@link RecoveryManager}
     *
     * @return the {@link ExecutorService} exclusive to the {@link RecoveryManager}
     */
    @Provides @Singleton @Named("DedicatedRecoveryManagerExecutor")
    private ScheduledExecutorService provideDedicatedRecoveryManagerExecutor(SDKInternalConfiguration configuration, WhoAmIReader whoAmIReader) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(whoAmIReader);

        String sdkContextDescription = whoAmIReader.getSdkContextDescription();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(sdkContextDescription + "-rm-t-%d").build();
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1, namedThreadFactory);

        Map<String, String> mdcContext = whoAmIReader.getAssociatedSdkMdcContextMap();

        return new MdcScheduledExecutorService(scheduledExecutorService, mdcContext);
    }

    /**
     * Provides an {@link ExecutorService} which is being used exclusively in the {@link SingleInstanceAMQPConnectionFactory}
     *
     * @return the {@link ExecutorService} exclusive to the {@link SingleInstanceAMQPConnectionFactory}
     */
    @Provides @Singleton @Named("DedicatedRabbitMqExecutor")
    private ExecutorService providesDedicatedRabbitMqExecutor(WhoAmIReader whoAmIReader) {
        Preconditions.checkNotNull(whoAmIReader);

        ThreadFactory namedThreadFactory =
                new ThreadFactoryBuilder()
                        .setNameFormat(whoAmIReader.getSdkContextDescription() + "-amqp-t-%d")
                        .build();

        // current max channels is 4(Prematch + Live + Virtuals + System), so max 4 concurrent consumptions
        return Executors.newFixedThreadPool(5, namedThreadFactory);
    }
}
