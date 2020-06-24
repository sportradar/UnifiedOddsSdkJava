/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.di.CustomisableSDKModule;
import com.sportradar.unifiedodds.sdk.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.exceptions.InvalidBookmakerDetailsException;
import com.sportradar.unifiedodds.sdk.extended.OddsFeedExtListener;
import com.sportradar.unifiedodds.sdk.impl.AMQPConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.OddsFeedSessionImpl;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.replay.ReplayManager;
import com.sportradar.utils.URN;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

/**
 * The main SDK object, this is the starting point of the UF SDK.
 */
public class OddsFeed {

    /**
     * The logger instance used for the OddsFeed logs
     */
    private static final Logger logger = LoggerFactory.getLogger(OddsFeed.class);

    /**
     * The injector used by this feed instance
     */
    protected final Injector injector;

    /**
     * The OddsFeed main configuration file
     */
    private final SDKInternalConfiguration oddsFeedConfiguration;

    /**
     * A HashSet that contains all the created sessions
     */
    private final HashSet<SessionData> createdSessionData = new HashSet<>();

    /**
     * A {@link SDKProducerManager} instance used to manage available producers
     */
    private SDKProducerManager producerManager;

    /**
     * The container of the sports data
     */
    private SportsInfoManager sportsInfoManager;

    /**
     * The container of all the market descriptions
     */
    private MarketDescriptionManager marketDescriptionManager;

    /**
     * The CashOut probabilities manager used to access probabilities data
     */
    private CashOutProbabilitiesManager cashOutProbabilitiesManager;

    /**
     * The instance used to request specific event recoveries
     */
    private EventRecoveryRequestIssuer recoveryRequestIssuer;

    /**
     * The instance used to perform booking calendar operations
     */
    private BookingManager bookingManager;

    /**
     * The instance used to perform custom bet operations
     */
    private CustomBetManager customBetManager;

    /**
     * The instance used to get bookmaker and used token details
     */
    private BookmakerDetails bookmakerDetails;

    /**
     * Indicates if the feed object was initialized
     */
    private boolean feedInitialized = false;

    /**
     * Field that marks if the feed was already opened
     */
    private boolean feedOpened = false;

    /**
     * The {@link OddsFeedExtListener} used to dispatch raw feed messages and api data
     */
    private OddsFeedExtListener oddsFeedExtListener;

    /**
     * The most basic feed constructor
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link OddsFeedConfiguration}, the configuration class used to configure the new feed,
     *                                            the configuration can be obtained using {@link #getOddsFeedConfigurationBuilder()}
     */
    public OddsFeed(SDKGlobalEventsListener listener, OddsFeedConfiguration config) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkNotNull(config);

        logger.info("OddsFeed instance created with \n{}", config);

        this.oddsFeedConfiguration = new SDKInternalConfiguration(config, config.getEnvironment() == Environment.Replay, new SDKConfigurationPropertiesReader(), new SDKConfigurationYamlReader());
        this.injector = createSdkInjector(listener, null);
        checkLocales();
        this.oddsFeedExtListener = null;
    }

    /**
     * The following constructor is used to crate the OddsFeed instance directly with the internal configuration
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link SDKInternalConfiguration}, the configuration class used to configure the new feed
     * @param oddsFeedExtListener {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    protected OddsFeed(SDKGlobalEventsListener listener, SDKInternalConfiguration config, OddsFeedExtListener oddsFeedExtListener) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkNotNull(config);

        logger.info("OddsFeed instance created with \n{}", config);

        this.oddsFeedConfiguration = config;
        this.injector = createSdkInjector(listener, null);
        this.oddsFeedExtListener = oddsFeedExtListener;
    }

    /**
     * The following constructor is used to crate the OddsFeed instance with a custom injection module
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link OddsFeedConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     * @param oddsFeedExtListener {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    protected OddsFeed(SDKGlobalEventsListener listener, OddsFeedConfiguration config, CustomisableSDKModule customisableSDKModule, OddsFeedExtListener oddsFeedExtListener) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkNotNull(config);

        logger.info("OddsFeed instance created with \n{}", config);

        this.oddsFeedConfiguration = new SDKInternalConfiguration(config, new SDKConfigurationPropertiesReader(), new SDKConfigurationYamlReader());
        this.injector = createSdkInjector(listener, customisableSDKModule);
        this.oddsFeedExtListener = oddsFeedExtListener;
    }

    /**
     * The following constructor is used to crate the OddsFeed instance directly with the internal configuration and
     * the customisable module
     *
     * @param listener {@link SDKGlobalEventsListener} that handles global feed events
     * @param config {@link SDKInternalConfiguration}, the configuration class used to configure the new feed
     * @param customisableSDKModule the customised injection module
     * @param oddsFeedExtListener {@link OddsFeedExtListener} used to receive raw feed and api data
     */
    protected OddsFeed(SDKGlobalEventsListener listener, SDKInternalConfiguration config, CustomisableSDKModule customisableSDKModule, OddsFeedExtListener oddsFeedExtListener) {
        Preconditions.checkNotNull(listener);
        Preconditions.checkNotNull(config);

        logger.info("OddsFeed instance created with \n{}", config);

        this.oddsFeedConfiguration = config;
        this.injector = createSdkInjector(listener, customisableSDKModule);
        this.oddsFeedExtListener = oddsFeedExtListener;
    }

    /**
     * The following constructor should be used only for testing purposes
     *
     * @param injector a predefined injector
     * @param config {@link SDKInternalConfiguration}, the configuration class used to configure the new feed
     */
    protected OddsFeed(SDKInternalConfiguration config, Injector injector) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(injector);

        logger.info("OddsFeed instance created with \n{}", config);

        this.oddsFeedConfiguration = config;
        this.injector = injector;

        logger.warn("OddsFeed initialised with a provided predefined injector");
    }

    /**
     * Returns a builder used to make {@link OddsFeedConfiguration} instances
     *
     * @deprecated in favour of the {@link #getOddsFeedConfigurationBuilder()}
     *
     * @return a builder used to make {@link OddsFeedConfiguration} instances
     */
    @Deprecated
    public static ConfigurationAccessTokenSetter getConfigurationBuilder() {
        return new OddsFeedConfigurationBuilderImpl(new SDKConfigurationPropertiesReader());
    }

    /**
     * Returns a builder used to make {@link OddsFeedConfiguration} instances
     *
     * @since 2.0.5
     *
     * @return a builder used to make {@link OddsFeedConfiguration} instances
     */
    public static TokenSetter getOddsFeedConfigurationBuilder() {
        return new TokenSetterImpl(new SDKConfigurationPropertiesReader(), new SDKConfigurationYamlReader());
    }

    public static TokenSetter getOddsFeedConfigurationBuilder(String propertiesUri, String yamlUri) {
        return new TokenSetterImpl(
                new SDKConfigurationPropertiesReader(propertiesUri),
                new SDKConfigurationYamlReader(yamlUri)
        );
    }

    /**
     * Builder used to create the required sessions
     *
     * @return current feed session builder
     */
    public OddsFeedSessionBuilder getSessionBuilder() {
        this.initOddsFeedInstance(); // init so the initial token validation gets triggered
        return new OddsFeedSessionBuilderImpl(this);
    }

    /**
     * Returns the {@link MarketDescriptionManager} used to access markets data trough our API
     *
     * @return {@link MarketDescriptionManager} used to access markets data
     */
    public MarketDescriptionManager getMarketDescriptionManager(){
        this.initOddsFeedInstance();
        return this.marketDescriptionManager;
    }

    /**
     * Returns the {@link SportsInfoManager} helper that contains useful methods for specific event data retrieval
     *
     * @return {@link SportsInfoManager} used to access various sports data
     */
    public SportsInfoManager getSportsInfoManager(){
        this.initOddsFeedInstance();
        return this.sportsInfoManager;
    }

    /**
     * Returns the {@link ProducerManager} instance used to manage available producers
     *
     * @return a {@link ProducerManager} instance used to manage available producers
     */
    public ProducerManager getProducerManager() {
        this.initOddsFeedInstance();
        return producerManager;
    }

    /**
     * Returns the {@link CashOutProbabilitiesManager} instance used to access probabilities data
     *
     * @return a {@link CashOutProbabilitiesManager} instance which can be used to access probabilities data
     */
    public CashOutProbabilitiesManager getCashOutProbabilitiesManager() {
        this.initOddsFeedInstance();
        return cashOutProbabilitiesManager;
    }

    /**
     * Returns the {@link EventRecoveryRequestIssuer} instance which provides utility methods used to initialize
     * event message recoveries
     *
     * @return the {@link EventRecoveryRequestIssuer} instance associated with the current feed instance
     */
    public EventRecoveryRequestIssuer getEventRecoveryRequestIssuer() {
        this.initOddsFeedInstance();
        return recoveryRequestIssuer;
    }

    /**
     * Returns the {@link BookingManager} instance which can be used to perform booking calendar operations
     *
     * @return the {@link BookingManager} associated with the current {@link OddsFeed} instance
     */
    public BookingManager getBookingManager() {
        this.initOddsFeedInstance();
        return bookingManager;
    }

    /**
     * Returns the {@link CustomBetManager} instance which can be used to perform custom bet operations
     *
     * @return the {@link CustomBetManager} associated with the current {@link OddsFeed} instance
     */
    public CustomBetManager getCustomBetManager() {
        this.initOddsFeedInstance();
        return customBetManager;
    }

    /**
     * Returns the {@link BookmakerDetails} instance with bookmaker and token info
     *
     * @return the {@link BookmakerDetails} associated with the current {@link OddsFeed} instance
     */
    public BookmakerDetails getBookmakerDetails() {
        this.initOddsFeedInstance();
        return bookmakerDetails;
    }

    /**
     * This method opens/starts the feed with all the built sessions and
     * creates the various tasks needed for optimal OddsFeed operation
     *
     * @throws InitException if the feed fails to initialize
     */
    public void open() throws InitException {
        if (!this.feedOpened) {
            this.initOddsFeedInstance();
            if (!createdSessionData.isEmpty()) {
                // disable the producers that are not requested by specified message interests
                Set<Integer> requestedProducers = new HashSet<>();
                for (SessionData createdSession : createdSessionData) {
                    requestedProducers.addAll(createdSession.messageInterest.getPossibleSourceProducers(producerManager.getAvailableProducers()));
                }

                producerManager.getAvailableProducers().keySet().forEach(id -> {
                    if (!requestedProducers.contains(id)) {
                        producerManager.disableProducer(id);
                    }
                });

                if (producerManager.getActiveProducers().isEmpty()) {
                    String interests = createdSessionData.stream()
                            .map(sessionData -> sessionData.messageInterest.toString())
                            .collect(Collectors.joining(", "));
                    throw new IllegalStateException(String.format("Message interests [%s] cannot be used. There are no suitable active producers.", interests));
                }

                Map<Integer, List<String>> sessionRoutingKeys =
                        OddsFeedRoutingKeyBuilder.generateKeys(createdSessionData.stream()
                                .collect(Collectors.toMap(Object::hashCode, v -> new SimpleEntry<>(v.messageInterest, v.eventIds))), oddsFeedConfiguration);

                try {
                    boolean aliveRoutingKeySessionPresent = createdSessionData.stream()
                            .anyMatch(cs -> cs.messageInterest == MessageInterest.SystemAliveMessages);
                    if (!aliveRoutingKeySessionPresent) {
                        OddsFeedSessionImpl systemMessagesSession = injector.getInstance(OddsFeedSessionImpl.class);
                        SessionData firstCreatedSession = createdSessionData.stream()
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("Feed created without sessions?"));

                        systemMessagesSession.open(
                                Lists.newArrayList(MessageInterest.SystemAliveMessages.getRoutingKeys()),
                                MessageInterest.SystemAliveMessages,
                                firstCreatedSession.oddsFeedListener,
                                oddsFeedExtListener
                        );
                    }

                    for (SessionData sessionData : createdSessionData) {
                        sessionData.session.open(
                                sessionRoutingKeys.get(sessionData.hashCode()),
                                sessionData.messageInterest,
                                sessionData.oddsFeedListener,
                                oddsFeedExtListener
                        );
                    }

                    injector.getInstance(RecoveryManager.class).init();
                    injector.getInstance(SDKTaskScheduler.class).open();
                } catch (IOException exception) {
                    throw new InitException("Unexpected issue initializing OddsFeed", exception);
                }
            } else {
                logger.warn("Feed opened without sessions");
            }
            this.feedOpened = true;
            this.producerManager.open();
        } else {
            throw new InitException("Feed can not be reopened once it has been closed");
        }
    }

    /**
     * Method used to close the feed and all its sessions
     *
     * @throws IOException if the AMQP connection closure fails
     */
    public void close() throws IOException {
        if (!this.feedOpened) {
            throw new IllegalStateException("Can't close an already closed OddsFeed instance");
        }

        logger.warn("OddsFeed.close invoked - closing the feed instance");

        injector.getInstance(AMQPConnectionFactory.class).close();
        injector.getInstance(CloseableHttpClient.class).close();
        injector.getInstance(SDKTaskScheduler.class).shutdownNow();
        injector.getInstance(Key.get(ScheduledExecutorService.class, Names.named("DedicatedRecoveryManagerExecutor"))).shutdownNow();
        injector.getInstance(Key.get(ExecutorService.class, Names.named("DedicatedRabbitMqExecutor"))).shutdownNow();
    }

    public List<Locale> getAvailableLanguages() {
        String[] languages = "sqi,zht,heb,aze,kaz,srl,ukr,aa,bs,br,bg,my,zh,hr,cs,da,nl,en,et,fi,fr,ka,de,el,hi,hu,Id,ja,km,ko,lo,lv,lt,ml,ms,no,fa,pl,pt,ro,ru,sr,sk,sl,es,sw,se,th,tr,vi,it".split(",");
        return Arrays.stream(languages)
                .sorted()
                .map(Locale::forLanguageTag)
                .collect(Collectors.toList());
    }

    private void checkLocales() {
        List<Locale> availableLanguages = getAvailableLanguages();
        List<Locale> unsupportedLocales = oddsFeedConfiguration.getDesiredLocales().stream()
                .filter(l -> !availableLanguages.contains(l))
                .collect(Collectors.toList());
        if (!unsupportedLocales.isEmpty())
            logger.warn("Unsupported locales: {}", unsupportedLocales);
    }

    protected void initOddsFeedInstance() {
        if (feedInitialized) {
            return;
        }
        String version = injector.getInstance(Key.get(String.class, Names.named("version")));
        logger.info("Initializing the OddsFeed instance (Sportradar Unified Odds SDK {})", version);

        // validate the client token
        WhoAmIReader whoAmI = injector.getInstance(WhoAmIReader.class);

        try {
            whoAmI.validateBookmakerDetails();
        } catch (IllegalStateException e) {
            throw new InvalidBookmakerDetailsException("Feed initialization failed", e);
        }

        // Hack for now, until we implement the Cache manager
        DataRouter dataRouter = injector.getInstance(DataRouter.class);
        if (dataRouter instanceof DataRouterImpl) {
            ((DataRouterImpl) dataRouter).setDataListeners(
                    Lists.newArrayList(
                            (DataRouterListener) injector.getInstance(SportEventCache.class),
                            (DataRouterListener) injector.getInstance(SportsDataCache.class),
                            (DataRouterListener) injector.getInstance(ProfileCache.class),
                            (DataRouterListener) injector.getInstance(SportEventStatusCache.class)
                    ));
        }

        this.sportsInfoManager = injector.getInstance(SportsInfoManager.class);
        this.marketDescriptionManager = injector.getInstance(MarketDescriptionManager.class);
        this.producerManager = injector.getInstance(SDKProducerManager.class);
        this.recoveryRequestIssuer = injector.getInstance(EventRecoveryRequestIssuer.class);
        this.cashOutProbabilitiesManager = injector.getInstance(CashOutProbabilitiesManager.class);
        this.bookingManager = injector.getInstance(BookingManager.class);
        this.customBetManager = injector.getInstance(CustomBetManager.class);
        this.bookmakerDetails = whoAmI.getBookmakerDetails();

        feedInitialized = true;
    }

    private Injector createSdkInjector(SDKGlobalEventsListener listener, CustomisableSDKModule customisableSDKModule) {
        return Guice.createInjector(new MasterInjectionModule(listener, this.oddsFeedConfiguration, customisableSDKModule));
    }

    private void createSession(OddsFeedSessionImpl session, MessageInterest oddsInterest, Set<URN> eventIds, OddsFeedListener oddsFeedListener) {
        if (this.feedOpened){
            throw new IllegalStateException("Sessions can not be created once the feed has been opened");
        } else {
            SessionData sessionData = new SessionData(session, oddsInterest, eventIds, oddsFeedListener);

            createdSessionData.add(sessionData);
        }
    }

    /**
     * Returns the replay manager for the current feed that can be used to add SportEvents and test-scenarios to replay.
     *
     * @return - the replay manager for the current feed that can be used to add SportEvents and test-scenarios to replay
     */
    protected ReplayManager getReplayManager() {
        if (!oddsFeedConfiguration.isReplaySession()) {
            return null;
        } else {
            initOddsFeedInstance();
            return injector.getInstance(ReplayManager.class);
        }
    }

    class SessionData {
        private final OddsFeedSessionImpl session;
        private final MessageInterest messageInterest;
        private final Set<URN> eventIds;
        private final OddsFeedListener oddsFeedListener;

        SessionData(OddsFeedSessionImpl session, MessageInterest messageInterest, Set<URN> eventIds, OddsFeedListener oddsFeedListener) {
            this.session = session;
            this.messageInterest = messageInterest;
            this.eventIds = eventIds;
            this.oddsFeedListener = oddsFeedListener;
        }
    }

    class OddsFeedSessionBuilderImpl implements OddsFeedSessionBuilder {
        private OddsFeed oddsFeed;
        private OddsFeedListener mainOddsFeedListener;
        private MessageInterest msgInterestLevel;
        private HashSet<URN> eventIds;
        private HashSet<GenericOddsFeedListener> specificOddsFeedListeners;

        OddsFeedSessionBuilderImpl(OddsFeed oddsFeed) {
            this.oddsFeed = oddsFeed;
        }

        @Override
        public OddsFeedSessionBuilder setListener(OddsFeedListener listener) {
            this.mainOddsFeedListener = listener;
            return this;
        }

        @Override
        public OddsFeedSessionBuilder setMessageInterest(MessageInterest msgInterest) {
            this.msgInterestLevel = msgInterest;
            return this;
        }

        @Override
        public OddsFeedSessionBuilder setSpecificListeners(HashSet<GenericOddsFeedListener> specificOddsFeedListeners){
            if (this.specificOddsFeedListeners == null)
                this.specificOddsFeedListeners = new HashSet<>();

            this.specificOddsFeedListeners.addAll(specificOddsFeedListeners);

            return this;
        }

        @Override
        public OddsFeedSessionBuilder setSpecificListeners(GenericOddsFeedListener specificOddsFeedListener){
            if (this.specificOddsFeedListeners == null)
                this.specificOddsFeedListeners = new HashSet<>();

            this.specificOddsFeedListeners.add(specificOddsFeedListener);

            return this;
        }

        @Override
        public OddsFeedSessionBuilder setSpecificEventsOnly(Set<URN> specificEventsOnly) {
            this.msgInterestLevel = MessageInterest.SpecifiedMatchesOnly;

            if (this.eventIds == null)
                this.eventIds = new HashSet<>();

            this.eventIds.addAll(specificEventsOnly);

            return this;
        }

        @Override
        public OddsFeedSessionBuilder setSpecificEventsOnly(URN specificEventsOnly) {
            return setSpecificEventsOnly(Collections.singleton(specificEventsOnly));
        }

        @Override
        public OddsFeedSession build() {
            // TODO @eti: handle specific event listeners
            OddsFeedSessionImpl session = injector.getInstance(OddsFeedSessionImpl.class);
            this.oddsFeed.createSession(session, msgInterestLevel, eventIds, mainOddsFeedListener);

            this.msgInterestLevel = null;
            this.eventIds = null;
            this.mainOddsFeedListener = null;
            this.specificOddsFeedListeners = null;

            return session;
        }
    }
}
