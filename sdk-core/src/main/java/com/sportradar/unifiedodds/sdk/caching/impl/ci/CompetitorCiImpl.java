/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.ci.*;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCompetitorCi;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the {@link CompetitorCi}
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "LambdaBodyLength",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "NPathComplexity",
        "ReturnCount",
    }
)
class CompetitorCiImpl implements CompetitorCi, ExportableCacheItem {

    private static final Logger logger = LoggerFactory.getLogger(CompetitorCiImpl.class);
    /**
     * An {@link Urn} specifying the id of the associated sport event
     */
    private final Urn id;

    /**
     * A {@link Locale} specifying the default language
     */
    private final Locale defaultLocale;

    /**
     * The {@link DataRouterManager} which is used to trigger data fetches
     */
    private final DataRouterManager dataRouterManager;

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * A {@link Map} containing competitor names in different languages
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing competitor's country name in different languages
     */
    private final Map<Locale, String> countryNames = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing competitor abbreviations in different languages
     */
    private final Map<Locale, String> abbreviations = Maps.newConcurrentMap();

    /**
     * A value indicating whether represented competitor is virtual
     */
    private Boolean isVirtual;

    /**
     * A {@link String} representation of a country code
     */
    private String countryCode;

    /**
     * The reference ids associated with the current instance
     */
    private ReferenceIdCi referenceId;

    /**
     * A {@link List} of associated player {@link Urn}s
     */
    private List<Urn> associatedPlayerIds;

    /**
     * A {@link Map} of associated player jersey numbers
     */
    private Map<Urn, Integer> associatedPlayerJerseyNumbers;

    /**
     * A {@link List} of known competitor jerseys
     */
    private List<JerseyCi> jerseys;

    /**
     * The associated competitor manager
     */
    private ManagerCi manager;

    /**
     * The associated competitor home venue
     */
    private VenueCi venue;

    /**
     * The gender of the competitor
     */
    private String gender;

    /**
     * The age group of the competitor
     */
    private String ageGroup;

    private String state;

    /**
     * The race driver profile of the competitor
     */
    private RaceDriverProfileCi raceDriverProfile;

    /**
     * The associated sport id
     */
    private Urn sportId;

    /**
     * The associated category id
     */
    private Urn categoryId;

    /**
     * The short name
     */
    private String shortName;

    /**
     * The locales which are merged into the CI
     */
    private final List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    private final ReentrantLock fetchLock = new ReentrantLock();

    private Date lastTimeCompetitorProfileIsFetched;
    private List<Locale> cultureCompetitorProfileFetched;
    private DivisionCi division;

    CompetitorCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.defaultLocale = defaultLocale;
        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.lastTimeCompetitorProfileIsFetched = new Date(Long.MIN_VALUE); // Calendar.getInstance().getTime();
        this.cultureCompetitorProfileFetched = Collections.synchronizedList(new ArrayList<>());
    }

    CompetitorCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiCompetitorProfileEndpoint data,
        Locale dataLocale
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        if (data != null && data.getPlayers() != null && !data.getPlayers().getPlayer().isEmpty()) {
            this.lastTimeCompetitorProfileIsFetched = Calendar.getInstance().getTime();
            if (cultureCompetitorProfileFetched == null) {
                this.cultureCompetitorProfileFetched = Collections.synchronizedList(new ArrayList<>());
            }
            this.cultureCompetitorProfileFetched.add(dataLocale);
        }

        merge(data, dataLocale);
    }

    CompetitorCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiTeam data,
        Locale dataLocale
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        if (data != null && data.getPlayers() != null && !data.getPlayers().getPlayer().isEmpty()) {
            this.lastTimeCompetitorProfileIsFetched = Calendar.getInstance().getTime();
            if (cultureCompetitorProfileFetched == null) {
                this.cultureCompetitorProfileFetched = Collections.synchronizedList(new ArrayList<>());
            }
            this.cultureCompetitorProfileFetched.add(dataLocale);
        }

        merge(data, dataLocale);
    }

    CompetitorCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiPlayerCompetitor data,
        Locale dataLocale
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        merge(data, dataLocale);
    }

    CompetitorCiImpl(
        Urn id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SapiSimpleTeamProfileEndpoint data,
        Locale dataLocale
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        merge(data, dataLocale);
    }

    CompetitorCiImpl(
        ExportableCompetitorCi exportable,
        DataRouterManager dataRouterManager,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(exportable);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = Urn.parse(exportable.getId());
        this.defaultLocale = exportable.getDefaultLocale();
        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;

        internalMerge(exportable);
    }

    /**
     * Returns the {@link Urn} representing id of the related entity
     *
     * @return the {@link Urn} representing id of the related entity
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the {@link Map} containing translated names of the competitor
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the competitor
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (names.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the {@link Map} containing translated country names
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated country names
     */
    @Override
    public Map<Locale, String> getCountryNames(List<Locale> locales) {
        if (countryNames.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(countryNames);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(countryNames);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(countryNames);
    }

    /**
     * Returns the {@link Map} containing translated competitor abbreviations
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated competitor abbreviations
     */
    @Override
    public Map<Locale, String> getAbbreviations(List<Locale> locales) {
        if (abbreviations.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        requestMissingCompetitorData(locales);

        return ImmutableMap.copyOf(abbreviations);
    }

    /**
     * Returns a value indicating whether represented competitor is virtual
     *
     * @return - a value indicating whether represented competitor is virtual
     */
    @Override
    public boolean isVirtual() {
        ensureDataLoaded();
        return Boolean.TRUE.equals(isVirtual);
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        ensureDataLoaded(countryCode);
        return countryCode;
    }

    /**
     * Returns the reference ids associated with the current instance
     *
     * @return - the reference ids associated with the current instance
     */
    @Override
    public ReferenceIdCi getReferenceId() {
        ensureDataLoaded(referenceId);
        return referenceId;
    }

    /**
     * Returns a {@link List} of associated player ids
     *
     * @param locales the locales in which the players data should be pre-fetched
     * @return {@link List} of associated player ids
     */
    @Override
    public List<Urn> getAssociatedPlayerIds(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return associatedPlayerIds == null ? null : ImmutableList.copyOf(associatedPlayerIds);
        }

        requestMissingCompetitorData(locales);

        return associatedPlayerIds == null ? null : ImmutableList.copyOf(associatedPlayerIds);
    }

    /**
     * Returns a {@link Map} of associated player jersey numbers
     *
     * @param locales the locales in which the players data should be pre-fetched
     * @return {@link Map} of associated player jersey numbers
     */
    @Override
    public Map<Urn, Integer> getAssociatedPlayerJerseyNumbers(List<Locale> locales) {
        if (cachedLocales.containsAll(locales)) {
            return associatedPlayerJerseyNumbers == null
                ? null
                : ImmutableMap.copyOf(associatedPlayerJerseyNumbers);
        }

        requestMissingCompetitorData(locales);

        return associatedPlayerJerseyNumbers == null
            ? null
            : ImmutableMap.copyOf(associatedPlayerJerseyNumbers);
    }

    /**
     * Returns a {@link List} of known competitor jerseys
     *
     * @return {@link List} of known competitor jerseys
     */
    @Override
    public List<JerseyCi> getJerseys() {
        ensureDataLoaded(jerseys);
        return jerseys;
    }

    /**
     * Returns the associated competitor manager
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor manager
     */
    @Override
    public ManagerCi getManager(List<Locale> locales) {
        if (manager != null && manager.hasTranslationsFor(locales)) {
            return manager;
        }

        if (cachedLocales.containsAll(locales)) {
            return manager;
        }

        requestMissingCompetitorData(locales);

        return manager;
    }

    /**
     * Return the associated competitor home venue
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor home venue
     */
    @Override
    public VenueCi getVenue(List<Locale> locales) {
        if (venue != null && venue.hasTranslationsFor(locales)) {
            return venue;
        }

        if (cachedLocales.containsAll(locales)) {
            return venue;
        }

        requestMissingCompetitorData(locales);

        return venue;
    }

    /**
     * Get the gender of the player
     *
     * @return the gender
     */
    @Override
    public String getGender() {
        ensureDataLoaded(gender);
        return gender;
    }

    /**
     * Get the age group of the player
     *
     * @return the age group
     */
    @Override
    public String getAgeGroup() {
        ensureDataLoaded(ageGroup);
        return ageGroup;
    }

    @Override
    public String getState() {
        ensureDataLoaded(state);
        return state;
    }

    /**
     * Returns id of the associated sport
     *
     * @return sport id
     */
    @Override
    public Urn getSportId() {
        ensureDataLoaded(sportId);
        return sportId;
    }

    /**
     * Returns id of the associated category
     *
     * @return category id
     */
    @Override
    public Urn getCategoryId() {
        ensureDataLoaded(categoryId);
        return categoryId;
    }

    /**
     * Get the short name
     *
     * @return the short name
     */
    @Override
    public String getShortName() {
        ensureDataLoaded(shortName);
        return shortName;
    }

    /**
     * Returns race driver of the competitor
     *
     * @return the race driver of the competitor if available; otherwise null
     */
    @Override
    public RaceDriverProfileCi getRaceDriver() {
        ensureDataLoaded(raceDriverProfile);
        return raceDriverProfile;
    }

    @Override
    public DivisionCi getDivision() {
        ensureDataLoaded(division);
        return division;
    }

    @Override
    public Date getLastTimeCompetitorProfileIsFetched() {
        return lastTimeCompetitorProfileIsFetched;
    }

    @Override
    public List<Locale> getCultureCompetitorProfileFetched() {
        return cultureCompetitorProfileFetched;
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return false;
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        if (endpointData instanceof SapiTeamCompetitor) {
            internalMerge((SapiTeamCompetitor) endpointData, dataLocale);
        } else if (endpointData instanceof SapiTeam) {
            internalMerge((SapiTeam) endpointData, dataLocale);
        } else if (endpointData instanceof SapiCompetitorProfileEndpoint) {
            internalMerge((SapiCompetitorProfileEndpoint) endpointData, dataLocale);
            cachedLocales.add(dataLocale);
        } else if (endpointData instanceof SapiPlayerCompetitor) {
            internalMerge((SapiPlayerCompetitor) endpointData, dataLocale);
        } else if (endpointData instanceof SapiSimpleTeamProfileEndpoint) {
            internalMerge((SapiSimpleTeamProfileEndpoint) endpointData, dataLocale);
            cachedLocales.add(dataLocale);
        } else if (endpointData instanceof ExportableCompetitorCi) {
            internalMerge((ExportableCompetitorCi) endpointData);
        }
    }

    private void internalMerge(ExportableCompetitorCi exportable) {
        names.putAll(exportable.getNames());
        countryNames.putAll(exportable.getCountryNames());
        abbreviations.putAll(exportable.getAbbreviations());
        isVirtual = exportable.isVirtual();
        countryCode = exportable.getCountryCode();
        referenceId =
            exportable.getReferenceId() != null ? new ReferenceIdCi(exportable.getReferenceId()) : null;
        List<Urn> missingAssociatedPlayerIds = exportable.getAssociatedPlayerIds() != null
            ? exportable
                .getAssociatedPlayerIds()
                .stream()
                .map(Urn::parse)
                .filter(i -> associatedPlayerIds == null || !associatedPlayerIds.contains(i))
                .collect(Collectors.toList())
            : new ArrayList<>();
        if (associatedPlayerIds == null) {
            associatedPlayerIds = new ArrayList<>(missingAssociatedPlayerIds);
        } else {
            associatedPlayerIds.addAll(missingAssociatedPlayerIds);
        }

        associatedPlayerJerseyNumbers =
            exportable.getAssociatedPlayerJerseyNumbers() != null
                ? exportable
                    .getAssociatedPlayerJerseyNumbers()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(pl -> Urn.parse(pl.getKey()), Map.Entry::getValue))
                : new HashMap<>();

        jerseys =
            exportable.getJerseys() != null
                ? exportable.getJerseys().stream().map(JerseyCi::new).collect(Collectors.toList())
                : null;
        manager = exportable.getManager() != null ? new ManagerCi(exportable.getManager()) : null;
        venue = exportable.getVenue() != null ? new VenueCi(exportable.getVenue()) : null;
        gender = exportable.getGender();
        ageGroup = exportable.getAgeGroup();
        raceDriverProfile =
            exportable.getRaceDriverProfile() != null
                ? new RaceDriverProfileCi(exportable.getRaceDriverProfile())
                : null;
        cachedLocales.addAll(SdkHelper.findMissingLocales(cachedLocales, exportable.getCachedLocales()));
        state = exportable.getState();
        sportId = Optional.ofNullable(exportable.getSportId()).map(Urn::parse).orElse(null);
        categoryId = Optional.ofNullable(exportable.getCategoryId()).map(Urn::parse).orElse(null);
        shortName = exportable.getShortName();
        division = exportable.getDivision() != null ? new DivisionCi(exportable.getDivision()) : null;
    }

    private void internalMerge(SapiTeamCompetitor data, Locale dataLocale) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        internalMerge((SapiTeam) data, dataLocale);
    }

    private void internalMerge(SapiCompetitorProfileEndpoint data, Locale dataLocale) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        internalMerge(data.getCompetitor(), dataLocale);

        if (data.getPlayers() != null && !data.getPlayers().getPlayer().isEmpty()) {
            associatedPlayerIds =
                Optional
                    .ofNullable(data.getPlayers())
                    .map(p ->
                        p.getPlayer().stream().map(pp -> Urn.parse(pp.getId())).collect(Collectors.toList())
                    )
                    .orElse(null);
            associatedPlayerJerseyNumbers =
                Optional
                    .ofNullable(data.getPlayers())
                    .map(p ->
                        p
                            .getPlayer()
                            .stream()
                            .filter(pl -> pl.getJerseyNumber() != null)
                            .collect(
                                Collectors.toMap(
                                    pl -> Urn.parse(pl.getId()),
                                    SapiPlayerExtended::getJerseyNumber
                                )
                            )
                    )
                    .orElse(null);
        }
        jerseys =
            Optional
                .ofNullable(data.getJerseys())
                .map(j -> j.getJersey().stream().map(JerseyCi::new).collect(Collectors.toList()))
                .orElse(null);

        if (data.getManager() != null) {
            if (manager == null) {
                manager = new ManagerCi(data.getManager(), dataLocale);
            } else {
                manager.merge(data.getManager(), dataLocale);
            }
        }

        if (data.getVenue() != null) {
            if (venue == null) {
                venue = new VenueCi(data.getVenue(), dataLocale);
            } else {
                venue.merge(data.getVenue(), dataLocale);
            }
        }

        if (!abbreviations.containsKey(dataLocale)) {
            if (data.getCompetitor().getAbbreviation() == null) {
                abbreviations.put(
                    dataLocale,
                    SdkHelper.getAbbreviationFromName(data.getCompetitor().getName(), 3)
                );
            } else {
                abbreviations.put(dataLocale, data.getCompetitor().getAbbreviation());
            }
        }

        if (data.getRaceDriverProfile() != null) {
            SapiTeam raceDriver = data.getRaceDriverProfile().getRaceDriver();
            SapiTeam raceTeam = data.getRaceDriverProfile().getRaceTeam();
            SapiCar car = data.getRaceDriverProfile().getCar();

            Urn raceDriverId = raceDriver != null ? Urn.parse(raceDriver.getId()) : null;
            Urn raceTeamId = raceTeam != null ? Urn.parse(raceTeam.getId()) : null;
            CarCi carCi = car != null
                ? new CarCi(car.getName(), car.getChassis(), car.getEngineName())
                : null;
            raceDriverProfile = new RaceDriverProfileCi(raceDriverId, raceTeamId, carCi);
        }
    }

    private void internalMerge(SapiTeamExtended competitor, Locale dataLocale) {
        Preconditions.checkNotNull(competitor);
        Preconditions.checkNotNull(dataLocale);

        Optional.ofNullable(competitor.getSport()).ifPresent(s -> sportId = Urn.parse(s.getId()));
        Optional.ofNullable(competitor.getCategory()).ifPresent(c -> categoryId = Urn.parse(c.getId()));

        internalMerge((SapiTeam) competitor, dataLocale);
    }

    private void internalMerge(SapiTeam competitor, Locale dataLocale) {
        Preconditions.checkNotNull(competitor);
        Preconditions.checkNotNull(dataLocale);

        mergeVirtual(competitor.isVirtual());

        countryCode = competitor.getCountryCode();

        Optional.ofNullable(competitor.getName()).ifPresent(s -> names.put(dataLocale, s));
        Optional.ofNullable(competitor.getCountry()).ifPresent(s -> countryNames.put(dataLocale, s));
        Optional.ofNullable(competitor.getAbbreviation()).ifPresent(s -> abbreviations.put(dataLocale, s));

        referenceId =
            competitor.getReferenceIds() == null
                ? null
                : new ReferenceIdCi(
                    competitor
                        .getReferenceIds()
                        .getReferenceId()
                        .stream()
                        .filter(r -> r.getName() != null && r.getValue() != null)
                        .collect(
                            HashMap::new,
                            (map, i) -> map.put(i.getName(), i.getValue()),
                            HashMap::putAll
                        )
                );
        if (id.isSimpleTeam() || id.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE)) {
            handleSimpleTeamReference();
        }

        if (competitor.getAbbreviation() == null) {
            abbreviations.put(dataLocale, SdkHelper.getAbbreviationFromName(competitor.getName(), 3));
        } else {
            abbreviations.put(dataLocale, competitor.getAbbreviation());
        }
        if (competitor.getGender() != null) {
            gender = competitor.getGender();
        }
        if (competitor.getAgeGroup() != null) {
            ageGroup = competitor.getAgeGroup();
        }

        if (competitor.getState() != null) {
            state = competitor.getState();
        }
        if (competitor.getShortName() != null) {
            shortName = competitor.getShortName();
        }
        if (competitor.getPlayers() != null && !competitor.getPlayers().getPlayer().isEmpty()) {
            associatedPlayerIds =
                Optional
                    .ofNullable(competitor.getPlayers())
                    .map(p ->
                        p.getPlayer().stream().map(pp -> Urn.parse(pp.getId())).collect(Collectors.toList())
                    )
                    .orElse(null);
        }
        if (competitor.getDivision() != null) {
            division = new DivisionCi(competitor.getDivision(), competitor.getDivisionName());
        }
    }

    private void internalMerge(SapiPlayerCompetitor competitor, Locale dataLocale) {
        Preconditions.checkNotNull(competitor);
        Preconditions.checkNotNull(dataLocale);

        Optional.ofNullable(competitor.getName()).ifPresent(s -> names.put(dataLocale, s));
        //        Optional.ofNullable(competitor.getNationality()).ifPresent(s -> nat.put(dataLocale, s));
        Optional.ofNullable(competitor.getAbbreviation()).ifPresent(s -> abbreviations.put(dataLocale, s));

        if (competitor.getAbbreviation() == null) {
            abbreviations.put(dataLocale, SdkHelper.getAbbreviationFromName(competitor.getName(), 3));
        } else {
            abbreviations.put(dataLocale, competitor.getAbbreviation());
        }
    }

    private void internalMerge(SapiSimpleTeamProfileEndpoint data, Locale dataLocale) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(dataLocale);

        internalMerge(data.getCompetitor(), dataLocale);
    }

    private void mergeVirtual(Boolean newValue) {
        if (isVirtual == null) {
            isVirtual = newValue != null && newValue;
        } else {
            if (newValue != null) {
                isVirtual = newValue;
            }
        }
    }

    private void requestMissingCompetitorData(List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);

        List<Locale> missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        fetchLock.lock();
        try {
            // recheck missing locales after lock
            missingLocales = SdkHelper.findMissingLocales(cachedLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug(
                "Fetching competitor data for id='{}' for languages '{}'",
                id,
                missingLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", "))
            );

            missingLocales.forEach(l -> {
                try {
                    if (
                        id.isSimpleTeam() ||
                        id.toString().startsWith(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE)
                    ) {
                        dataRouterManager.requestSimpleTeamEndpoint(l, id, this);
                    } else {
                        dataRouterManager.requestCompetitorEndpoint(l, id, this);
                    }
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingCompetitorData(%s)", missingLocales), e);
        } finally {
            fetchLock.unlock();
        }
    }

    private void handleSimpleTeamReference() {
        if (referenceId != null && referenceId.getReferenceIds().containsKey("betradar")) {
            return;
        }

        referenceId =
            new ReferenceIdCi(
                ImmutableMap
                    .<String, String>builder()
                    .put("betradar", String.valueOf(id.getId()))
                    .putAll(referenceId != null ? referenceId.getReferenceIds() : ImmutableMap.of())
                    .build()
            );
    }

    private void ensureDataLoaded() {
        if (!cachedLocales.isEmpty()) {
            return;
        }

        requestMissingCompetitorData(Collections.singletonList(defaultLocale));
    }

    private void ensureDataLoaded(Object object) {
        if (object != null || !cachedLocales.isEmpty()) {
            return;
        }

        requestMissingCompetitorData(Collections.singletonList(defaultLocale));
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("CompetitorCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing CompetitorCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing CompetitorCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    @Override
    public ExportableCi export() {
        return new ExportableCompetitorCi(
            id.toString(),
            new HashMap<>(names),
            defaultLocale,
            new HashMap<>(countryNames),
            new HashMap<>(abbreviations),
            Boolean.TRUE.equals(isVirtual),
            countryCode,
            referenceId != null ? new HashMap<>(referenceId.getReferenceIds()) : null,
            associatedPlayerIds != null
                ? associatedPlayerIds.stream().map(Urn::toString).collect(Collectors.toList())
                : null,
            associatedPlayerJerseyNumbers != null
                ? associatedPlayerJerseyNumbers
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(p -> p.getKey().toString(), Map.Entry::getValue))
                : null,
            jerseys != null ? jerseys.stream().map(JerseyCi::export).collect(Collectors.toList()) : null,
            manager != null ? manager.export() : null,
            venue != null ? venue.export() : null,
            gender,
            ageGroup,
            raceDriverProfile != null ? raceDriverProfile.export() : null,
            new ArrayList<>(cachedLocales),
            state,
            Optional.ofNullable(sportId).map(Urn::toString).orElse(null),
            Optional.ofNullable(categoryId).map(Urn::toString).orElse(null),
            shortName,
            division != null ? division.export() : null
        );
    }
}
