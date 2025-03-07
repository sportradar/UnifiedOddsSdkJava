/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalReplay;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Replay;
import static java.util.Arrays.asList;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.exceptions.InvalidBookmakerDetailsException;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.internal.caching.*;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.cfg.SdkConfigurationPropertiesReaderFactory;
import com.sportradar.unifiedodds.sdk.internal.cfg.SdkConfigurationYamlReaderFactory;
import com.sportradar.unifiedodds.sdk.internal.cfg.TokenSetterImpl;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.internal.di.ConfigurationInjectingModule;
import com.sportradar.unifiedodds.sdk.internal.di.CustomisableSdkModule;
import com.sportradar.unifiedodds.sdk.internal.di.InternalCachesProvider;
import com.sportradar.unifiedodds.sdk.internal.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.AmqpConnectionFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.recovery.SingleRecoveryManagerSupervisor;
import com.sportradar.unifiedodds.sdk.managers.*;
import com.sportradar.utils.Urn;
import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel;
import java.io.IOException;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main SDK object, this is the starting point of the UF SDK.
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "ExplicitInitialization",
        "HiddenField",
        "IllegalCatch",
        "IllegalType",
        "LineLength",
        "MethodLength",
        "MultipleStringLiterals",
        "NPathComplexity",
        "NeedBraces",
        "NestedIfDepth",
        "VisibilityModifier",
    }
)
public class UofSdk implements AutoCloseable {

    /**
     * The logger instance used for the UofSdk logs
     */
    private static final Logger logger = LoggerFactory.getLogger(UofSdk.class);

    /**
     * The injector used by this feed instance
     */
    //TODO must be final back eventually, but until the class is testable it cannot be.
    //currently there is no way to test logic in the public constructor otherwise
    protected Injector injector;

    /**
     * The UofSdk main configuration file
     */
    protected final SdkInternalConfiguration oddsFeedConfiguration;

    protected final UofConfiguration uofConfiguration;

    private UofSessionImpl systemMessagesSession;
    /**
     * A HashSet that contains all the created sessions
     */
    private final HashSet<SessionData> createdSessionData = new HashSet<>();

    /**
     * A {@link SdkProducerManager} instance used to manage available producers
     */
    private SdkProducerManager producerManager;

    /**
     * The container of the sports data
     */
    private SportDataProvider sportDataProvider;

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
     * The {@link UofExtListener} used to dispatch raw feed messages and api data
     */
    private UofExtListener uofExtListener;

    /**
     * The instance used to automatically receive fixture and result changes
     */
    private EventChangeManager eventChangeManager;

    /**
     * The most basic feed constructor
     *
     * @param listener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link UofConfigurationImpl}, the configuration class used to configure the new feed,
     *                                            the configuration can be obtained using {@link #getUofConfigurationBuilder()}
     */
    public UofSdk(final UofGlobalEventsListener listener, final UofConfiguration config) {
        Preconditions.checkNotNull(listener, "listener");
        Preconditions.checkNotNull(config, "config");
        logger.info("UofSdk instance created with: {}", config);

        this.uofConfiguration = config;

        this.oddsFeedConfiguration =
            new SdkInternalConfiguration(
                config,
                asList(Replay, GlobalReplay).contains(config.getEnvironment()),
                SdkConfigurationPropertiesReaderFactory.create(),
                SdkConfigurationYamlReaderFactory.create()
            );
        this.injector = createSdkInjector(listener, null);
        checkLocales();
        this.uofExtListener = null;
    }

    /**
     * The following constructor is used to create the UofSdk instance directly with the internal configuration
     *
     * @param listener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link SdkInternalConfiguration}, the configuration class used to configure the new feed
     * @param uofExtListener {@link UofExtListener} used to receive raw feed and api data
     */
    protected UofSdk(
        final UofGlobalEventsListener listener,
        final UofConfiguration config,
        UofExtListener uofExtListener
    ) {
        Preconditions.checkNotNull(listener, "listener");
        Preconditions.checkNotNull(config, "config");

        logger.info("UofSdk instance created with \n{}", config);

        this.uofConfiguration = config;

        this.oddsFeedConfiguration =
            new SdkInternalConfiguration(
                config,
                asList(Replay, GlobalReplay).contains(config.getEnvironment()),
                SdkConfigurationPropertiesReaderFactory.create(),
                SdkConfigurationYamlReaderFactory.create()
            );
        this.injector = createSdkInjector(listener, null);
        this.uofExtListener = uofExtListener;
    }

    /**
     * The following constructor is used to crate the UofSdk instance with a custom injection module
     *
     * @param listener {@link UofGlobalEventsListener} that handles global feed events
     * @param config {@link UofConfigurationImpl}, the configuration class used to configure the new feed
     * @param customisableSdkModule the customised injection module
     * @param uofExtListener {@link UofExtListener} used to receive raw feed and api data
     */
    protected UofSdk(
        final UofGlobalEventsListener listener,
        final UofConfiguration config,
        boolean useReplay,
        CustomisableSdkModule customisableSdkModule,
        UofExtListener uofExtListener
    ) {
        Preconditions.checkNotNull(listener, "listener");
        Preconditions.checkNotNull(config, "config");
        logger.info("UofSdk instance created with \n{}", config);

        this.uofConfiguration = config;

        this.oddsFeedConfiguration =
            new SdkInternalConfiguration(
                config,
                useReplay,
                SdkConfigurationPropertiesReaderFactory.create(),
                SdkConfigurationYamlReaderFactory.create()
            );
        this.injector = createSdkInjector(listener, customisableSdkModule);
        this.uofExtListener = uofExtListener;
    }

    /**
     * The following constructor should be used only for testing purposes
     *
     * @param injector a predefined injector
     * @param config {@link SdkInternalConfiguration}, the configuration class used to configure the new feed
     */
    @ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel
    protected UofSdk(final UofConfiguration config, Injector injector) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(injector);

        logger.info("UofSdk instance created with \n{}", config);

        this.uofConfiguration = config;
        this.oddsFeedConfiguration =
            new SdkInternalConfiguration(
                config,
                asList(Replay, GlobalReplay).contains(config.getEnvironment()),
                SdkConfigurationPropertiesReaderFactory.create(),
                SdkConfigurationYamlReaderFactory.create()
            );
        this.injector = injector;

        logger.warn("UofSdk initialised with a provided predefined injector");
    }

    /**
     * Returns a builder used to make {@link UofConfiguration} instances
     *
     * @since 2.0.5
     *
     * @return a builder used to make {@link UofConfiguration} instances
     */
    public static TokenSetter getUofConfigurationBuilder() {
        return new TokenSetterImpl(
            SdkConfigurationPropertiesReaderFactory.create(),
            SdkConfigurationYamlReaderFactory.create(),
            config -> createSdkInjectorForConfiguration(config).getInstance(WhoAmIReader.class),
            config -> createSdkInjectorForConfiguration(config).getInstance(ProducerDataProvider.class)
        );
    }

    public static TokenSetter getUofConfigurationBuilder(String propertiesUri, String yamlUri) {
        return new TokenSetterImpl(
            SdkConfigurationPropertiesReaderFactory.create(propertiesUri),
            SdkConfigurationYamlReaderFactory.create(yamlUri),
            config -> createSdkInjectorForConfiguration(config).getInstance(WhoAmIReader.class),
            config -> createSdkInjectorForConfiguration(config).getInstance(ProducerDataProvider.class)
        );
    }

    private static Injector createSdkInjectorForConfiguration(UofConfiguration uofConfiguration) {
        return Guice.createInjector(
            new ConfigurationInjectingModule(
                uofConfiguration,
                new SdkInternalConfiguration(
                    uofConfiguration,
                    asList(Replay, GlobalReplay).contains(uofConfiguration.getEnvironment()),
                    SdkConfigurationPropertiesReaderFactory.create(),
                    SdkConfigurationYamlReaderFactory.create()
                )
            )
        );
    }

    /**
     * Builder used to create the required sessions
     *
     * @return current feed session builder
     */
    public UofSessionBuilder getSessionBuilder() {
        this.initOddsFeedInstance(); // init so the initial token validation gets triggered
        return new UofSessionBuilderImpl(this);
    }

    /**
     * Returns the {@link com.sportradar.unifiedodds.sdk.managers.MarketDescriptionManager} used to access markets data trough our API
     *
     * @return {@link com.sportradar.unifiedodds.sdk.managers.MarketDescriptionManager} used to access markets data
     */
    public MarketDescriptionManager getMarketDescriptionManager() {
        this.initOddsFeedInstance();
        return this.marketDescriptionManager;
    }

    /**
     * Returns the {@link com.sportradar.unifiedodds.sdk.managers.SportDataProvider} helper that contains useful methods for specific event data retrieval
     *
     * @return {@link com.sportradar.unifiedodds.sdk.managers.SportDataProvider} used to access various sports data
     */
    public SportDataProvider getSportDataProvider() {
        this.initOddsFeedInstance();
        return this.sportDataProvider;
    }

    /**
     * Returns the {@link com.sportradar.unifiedodds.sdk.managers.ProducerManager} instance used to manage available producers
     *
     * @return a {@link com.sportradar.unifiedodds.sdk.managers.ProducerManager} instance used to manage available producers
     */
    public ProducerManager getProducerManager() {
        this.initOddsFeedInstance();
        return producerManager;
    }

    /**
     * Returns the {@link com.sportradar.unifiedodds.sdk.managers.CashOutProbabilitiesManager} instance used to access probabilities data
     *
     * @return a {@link com.sportradar.unifiedodds.sdk.managers.CashOutProbabilitiesManager} instance which can be used to access probabilities data
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
     * @return the {@link BookingManager} associated with the current {@link UofSdk} instance
     */
    public BookingManager getBookingManager() {
        this.initOddsFeedInstance();
        return bookingManager;
    }

    /**
     * Returns the {@link CustomBetManager} instance which can be used to perform custom bet operations
     *
     * @return the {@link CustomBetManager} associated with the current {@link UofSdk} instance
     */
    public CustomBetManager getCustomBetManager() {
        this.initOddsFeedInstance();
        return customBetManager;
    }

    /**
     * Returns the {@link BookmakerDetails} instance with bookmaker and token info
     *
     * @return the {@link BookmakerDetails} associated with the current {@link UofSdk} instance
     */
    public BookmakerDetails getBookmakerDetails() {
        this.initOddsFeedInstance();
        return bookmakerDetails;
    }

    /**
     * Returns the {@link EventChangeManager} instance used to automatically receive fixture and result changes
     *
     * @return a {@link EventChangeManager} instance used to automatically receive fixture and result changes
     */
    public EventChangeManager getEventChangeManager() {
        this.initOddsFeedInstance();
        return eventChangeManager;
    }

    /**
     * This method opens/starts the feed with all the built sessions and
     * creates the various tasks needed for optimal UofSdk operation
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
                    requestedProducers.addAll(
                        createdSession.messageInterest.getPossibleSourceProducers(
                            producerManager.getAvailableProducers()
                        )
                    );
                }

                producerManager
                    .getAvailableProducers()
                    .keySet()
                    .forEach(id -> {
                        if (!requestedProducers.contains(id)) {
                            producerManager.disableProducer(id);
                        }
                    });

                if (producerManager.getActiveProducers().isEmpty()) {
                    String interests = createdSessionData
                        .stream()
                        .map(sessionData -> sessionData.messageInterest.toString())
                        .collect(Collectors.joining(", "));
                    throw new IllegalStateException(
                        String.format(
                            "Message interests [%s] cannot be used. There are no suitable active producers.",
                            interests
                        )
                    );
                }

                Map<Integer, List<String>> sessionRoutingKeys = OddsFeedRoutingKeyBuilder.generateKeys(
                    createdSessionData
                        .stream()
                        .collect(
                            Collectors.toMap(
                                Object::hashCode,
                                v -> new SimpleEntry<>(v.messageInterest, v.eventIds)
                            )
                        ),
                    oddsFeedConfiguration
                );

                try {
                    boolean aliveRoutingKeySessionPresent = createdSessionData
                        .stream()
                        .anyMatch(cs -> cs.messageInterest == MessageInterest.SystemAliveMessages);
                    if (!aliveRoutingKeySessionPresent) {
                        systemMessagesSession = injector.getInstance(UofSessionImpl.class);
                        SessionData firstCreatedSession = createdSessionData
                            .stream()
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Feed created without sessions?"));

                        systemMessagesSession.open(
                            Lists.newArrayList(MessageInterest.SystemAliveMessages.getRoutingKeys()),
                            MessageInterest.SystemAliveMessages,
                            firstCreatedSession.uofListener,
                            uofExtListener
                        );
                    }

                    for (SessionData sessionData : createdSessionData) {
                        sessionData.session.open(
                            sessionRoutingKeys.get(sessionData.hashCode()),
                            sessionData.messageInterest,
                            sessionData.uofListener,
                            uofExtListener
                        );
                    }

                    injector.getInstance(SingleRecoveryManagerSupervisor.class).startSupervising();
                    injector.getInstance(SdkTaskScheduler.class).open();
                } catch (IOException exception) {
                    throw new InitException("Unexpected issue initializing UofSdk", exception);
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
     * Returns true if feed is opened
     *
     * @return true if opened
     */
    public boolean isOpen() {
        return feedOpened;
    }

    /**
     * Method used to close the feed and all its sessions
     *
     * @throws IOException if the AMQP connection closure fails
     */
    @Override
    @ExcludeFromJacocoGeneratedReportAsDiIsNotTestedAtUnitTestLevel
    public void close() throws IOException {
        if (!this.feedOpened) {
            logger.warn("Invoked close on already closed UofSdk instance");
        }

        logger.warn("UofSdk.close invoked - closing the feed instance");

        try {
            injector.getInstance(AmqpConnectionFactory.class).close(true);
        } catch (Exception ex) {
            logger.warn("Error during close - AMQPConnectionFactory", ex);
        }
        try {
            this.feedOpened = false;
            if (systemMessagesSession != null) {
                systemMessagesSession.close();
            }
            for (SessionData sessionData : createdSessionData) {
                sessionData.session.close();
            }
        } catch (Exception ex) {
            logger.warn("Error during close - Sessions", ex);
        }
        try {
            DataRouterManager dataRouterManager = injector.getInstance(Key.get(DataRouterManager.class));
            dataRouterManager.close();
        } catch (Exception ex) {
            logger.warn("Error during close - DataRouterManager", ex);
        }
        try {
            injector.getInstance(CloseableHttpClient.class).close();
        } catch (Exception ex) {
            logger.warn("Error during close - HttpClient", ex);
        }
        try {
            injector.getInstance(CloseableHttpAsyncClient.class).close();
        } catch (Exception ex) {
            logger.warn("Error during close - AsyncHttpClient", ex);
        }
        try {
            injector
                .getInstance(Key.get(CloseableHttpAsyncClient.class, Names.named("FastHttpClient")))
                .close();
        } catch (Exception ex) {
            logger.warn("Error during close - FastAsyncHttpClient", ex);
        }
        try {
            injector
                .getInstance(Key.get(CloseableHttpClient.class, Names.named("RecoveryHttpClient")))
                .close();
        } catch (Exception ex) {
            logger.warn("Error during close - RecoveryHttpClient", ex);
        }
        try {
            injector.getInstance(SdkTaskScheduler.class).shutdownNow();
        } catch (Exception ex) {
            logger.warn("Error during close - SDKTaskScheduler", ex);
        }
        try {
            injector
                .getInstance(
                    Key.get(ScheduledExecutorService.class, Names.named("DedicatedRecoveryManagerExecutor"))
                )
                .shutdownNow();
        } catch (Exception ex) {
            logger.warn("Error during close - ScheduledExecutorService", ex);
        }
        try {
            injector
                .getInstance(Key.get(ExecutorService.class, Names.named("DedicatedRabbitMqExecutor")))
                .shutdownNow();
        } catch (Exception ex) {
            logger.warn("Error during close - ExecutorService", ex);
        }
        try {
            InternalCachesProvider internalCachesProvider = injector.getInstance(
                Key.get(InternalCachesProvider.class)
            );
            internalCachesProvider.close();
        } catch (Exception ex) {
            logger.warn("Error during close - InternalCachesProvider", ex);
        }
    }

    public List<Locale> getAvailableLanguages() {
        String[] languages =
            "sqi,zht,heb,aze,kaz,srl,ukr,aa,bs,br,bg,my,zh,hr,cs,da,nl,en,et,fi,fr,ka,de,el,hi,hu,Id,ja,km,ko,lo,lv,lt,ml,ms,no,fa,pl,pt,ro,ru,sr,sk,sl,es,sw,se,th,tr,vi,it".split(
                    ","
                );
        return Arrays.stream(languages).sorted().map(Locale::forLanguageTag).collect(Collectors.toList());
    }

    private void checkLocales() {
        List<Locale> availableLanguages = getAvailableLanguages();
        List<Locale> unsupportedLocales = oddsFeedConfiguration
            .getDesiredLocales()
            .stream()
            .filter(l -> !availableLanguages.contains(l))
            .collect(Collectors.toList());
        if (!unsupportedLocales.isEmpty()) logger.warn("Unsupported locales: {}", unsupportedLocales);
    }

    protected void initOddsFeedInstance() {
        if (feedInitialized) {
            return;
        }
        String version = injector.getInstance(Key.get(String.class, Names.named("version")));
        logger.info("Initializing the UofSdk instance (Sportradar Unified Odds SDK {})", version);

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
                    )
                );
        }

        this.sportDataProvider = injector.getInstance(SportDataProvider.class);
        this.marketDescriptionManager = injector.getInstance(MarketDescriptionManager.class);
        this.producerManager = injector.getInstance(SdkProducerManager.class);
        this.recoveryRequestIssuer = injector.getInstance(EventRecoveryRequestIssuer.class);
        this.cashOutProbabilitiesManager = injector.getInstance(CashOutProbabilitiesManager.class);
        this.bookingManager = injector.getInstance(BookingManager.class);
        this.customBetManager = injector.getInstance(CustomBetManager.class);
        this.bookmakerDetails = whoAmI.getBookmakerDetails();
        this.eventChangeManager = injector.getInstance(EventChangeManager.class);

        feedInitialized = true;
    }

    protected Injector createSdkInjector(
        UofGlobalEventsListener listener,
        CustomisableSdkModule customisableSdkModule
    ) {
        return Guice.createInjector(
            new MasterInjectionModule(
                listener,
                new SdkInternalConfiguration(
                    uofConfiguration,
                    asList(Replay, GlobalReplay).contains(uofConfiguration.getEnvironment()),
                    SdkConfigurationPropertiesReaderFactory.create(),
                    SdkConfigurationYamlReaderFactory.create()
                ),
                this.uofConfiguration,
                customisableSdkModule
            )
        );
    }

    private void createSession(
        UofSessionImpl session,
        MessageInterest oddsInterest,
        Set<Urn> eventIds,
        UofListener uofListener
    ) {
        if (this.feedOpened) {
            throw new IllegalStateException("Sessions can not be created once the feed has been opened");
        } else {
            SessionData sessionData = new SessionData(session, oddsInterest, eventIds, uofListener);

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

        private final UofSessionImpl session;
        private final MessageInterest messageInterest;
        private final Set<Urn> eventIds;
        private final UofListener uofListener;

        SessionData(
            UofSessionImpl session,
            MessageInterest messageInterest,
            Set<Urn> eventIds,
            UofListener uofListener
        ) {
            this.session = session;
            this.messageInterest = messageInterest;
            this.eventIds = eventIds;
            this.uofListener = uofListener;
        }
    }

    class UofSessionBuilderImpl implements UofSessionBuilder {

        private UofSdk uofSdk;
        private UofListener mainUofListener;
        private MessageInterest msgInterestLevel;
        private HashSet<Urn> eventIds;

        UofSessionBuilderImpl(UofSdk uofSdk) {
            this.uofSdk = uofSdk;
        }

        @Override
        public UofSessionBuilder setListener(UofListener listener) {
            this.mainUofListener = listener;
            return this;
        }

        @Override
        public UofSessionBuilder setMessageInterest(MessageInterest msgInterest) {
            this.msgInterestLevel = msgInterest;
            return this;
        }

        @Override
        public UofSessionBuilder setSpecificEventsOnly(Set<Urn> specificEventsOnly) {
            this.msgInterestLevel = MessageInterest.SpecifiedMatchesOnly;

            if (this.eventIds == null) this.eventIds = new HashSet<>();

            this.eventIds.addAll(specificEventsOnly);

            return this;
        }

        @Override
        public UofSessionBuilder setSpecificEventsOnly(Urn specificEventsOnly) {
            return setSpecificEventsOnly(Collections.singleton(specificEventsOnly));
        }

        @Override
        public UofSession build() {
            // TODO @eti: handle specific event listeners
            UofSessionImpl session = injector.getInstance(UofSessionImpl.class);
            this.uofSdk.createSession(session, msgInterestLevel, eventIds, mainUofListener);

            this.msgInterestLevel = null;
            this.eventIds = null;
            this.mainUofListener = null;

            return session;
        }
    }
}
