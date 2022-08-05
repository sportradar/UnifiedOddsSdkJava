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
import com.google.inject.name.Names;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.*;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketManagerImpl;
import com.sportradar.unifiedodds.sdk.impl.markets.mappings.MappingValidatorFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.FeedMessageFactoryImpl;
import com.sportradar.unifiedodds.sdk.impl.util.MdcScheduledExecutorService;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.*;
import javax.management.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * The {@link JAXBContext} used to unmarshall sports API responses
     */
    private final JAXBContext sportsApiJaxbContext;

    /**
     * The {@link JAXBContext} used to unmarshall custom bet API responses
     */
    private final JAXBContext customBetApiJaxbContext;

    /**
     * The associated SDK configuration
     */
    private final SDKInternalConfiguration configuration;

    /**
     * The HTTP client factory.
     */
    private final HttpClientFactory httpClientFactory;

    /**
     * Constructs a new instance of the {@link GeneralModule} class
     *
     * @param sdkListener A {@link SDKGlobalEventsListener}
     *        implementation used to notify the outside world about global events
     * @param configuration The associated SDK configuration
     */
    GeneralModule(SDKGlobalEventsListener sdkListener, SDKInternalConfiguration configuration, HttpClientFactory httpClientFactory) {
        checkNotNull(sdkListener, "sdkListener cannot be a null reference");
        checkNotNull(configuration, "the SDKInternalConfiguration can not be null");
        checkNotNull(httpClientFactory, "the httpClientFactory can not be null");

        this.sdkListener = sdkListener;
        this.configuration = configuration;
        this.httpClientFactory = httpClientFactory;

    try {
      messagesJaxbContext = JAXBContext.newInstance("com.sportradar.uf.datamodel");
      sportsApiJaxbContext = JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel");
      customBetApiJaxbContext = JAXBContext.newInstance("com.sportradar.uf.custombet.datamodel");
    } catch (JAXBException e) {
      throw new IllegalStateException("JAXB contexts creation failed, ex: ", e);
    }
  }

    /**
     * Registers required types with the injection container
     *
     * @param binder A {@link Binder} representing the injection container handle
     */
    @SuppressWarnings("java:S2119") // "Random" objects should be reused
    @Override
    public void configure(Binder binder) {
        // listeners instance bind
        binder.bind(SDKProducerStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SDKConnectionStatusListener.class).toInstance(this.sdkListener);
        binder.bind(SDKEventRecoveryStatusListener.class).toInstance(this.sdkListener);

        binder.bindConstant().annotatedWith(Names.named("version")).to(loadVersion());

        // rabbit MQ connection factory & rabbit MQ connection factory wrapper
        binder.bind(ConnectionFactory.class).in(Singleton.class);
        binder.bind(AMQPConnectionFactory.class).to(SingleInstanceAMQPConnectionFactory.class);
        binder.bind(SingleInstanceAMQPConnectionFactory.class).in(Singleton.class);

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
        binder.bind(CustomBetManager.class).to(CustomBetManagerImpl.class).in(Singleton.class);
        binder.bind(EventChangeManager.class).to(EventChangeManagerImpl.class).in(Singleton.class);

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
            InputStream is = SingleInstanceAMQPConnectionFactory.class.getResourceAsStream("/sr-sdk-version.properties");
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

  @Provides
  @Named("MessageJAXBContext")
  private JAXBContext provideMessageJAXBContext() {
    return messagesJaxbContext;
  }

  /**
   * Provides the {@link Deserializer} used to deserialize sports API xmls
   *
   * @return The {@link Deserializer} instance to be registered with the DI container
   */
  @Provides
  @Named("SportsApiJaxbDeserializer")
  private Deserializer provideSportsApiJaxbDeserializer() {
    return new DeserializerImpl(sportsApiJaxbContext);
  }

    /**
     * Provides the {@link Deserializer} used to deserialize custom bet API xmls
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("CustomBetApiJaxbDeserializer")
    private Deserializer provideCustomBetApiJaxbDeserializer() {
        return new DeserializerImpl(customBetApiJaxbContext);
    }

    /**
     * Provides the {@link Deserializer} used to deserialize message streams
     *
     * @return The {@link Deserializer} instance to be registered with the DI container
     */
    @Provides @Named("MessageDeserializer")
    private Deserializer provideMessageDeserializer() {
        return new DeserializerImpl(messagesJaxbContext);
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
    CloseableHttpClient provideHttpClient(){
        int maxTimeoutInMillis = Math.toIntExact(TimeUnit.MILLISECONDS.convert(configuration.getHttpClientTimeout(), TimeUnit.SECONDS));
        int connectionPoolSize = configuration.getHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getHttpClientMaxConnPerRoute();

        return httpClientFactory.create(maxTimeoutInMillis, connectionPoolSize, maxConcurrentConnectionsPerRoute);
    }

    /**
     * Provides the http client used to fetch data from the API on feed queue thread (profiles, variant market or summary)
     */
    @Provides @Singleton @Named("FastHttpClient")
    CloseableHttpClient provideCriticalHttpClient(){
        int maxTimeoutInMillis = (int) OperationManager.getFastHttpClientTimeout().toMillis();
        int connectionPoolSize = configuration.getHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getHttpClientMaxConnPerRoute();

        return httpClientFactory.create(maxTimeoutInMillis, connectionPoolSize, maxConcurrentConnectionsPerRoute);
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides @Singleton @Named("RecoveryHttpClient")
    CloseableHttpClient provideRecoveryHttpClient(){
        int maxTimeoutInMillis = Math.toIntExact(TimeUnit.MILLISECONDS.convert(configuration.getRecoveryHttpClientTimeout(), TimeUnit.SECONDS));
        int connectionPoolSize = configuration.getRecoveryHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getRecoveryHttpClientMaxConnPerRoute();

        return httpClientFactory.create(maxTimeoutInMillis, connectionPoolSize, maxConcurrentConnectionsPerRoute);
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides @Named("RecoveryHttpHelper")
    private HttpHelper provideRecoveryHttpHelper(SDKInternalConfiguration config, @Named("RecoveryHttpClient") CloseableHttpClient httpClient, @Named("SportsApiJaxbDeserializer") Deserializer apiDeserializer) {
        return new HttpHelper(config, httpClient, apiDeserializer);
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
