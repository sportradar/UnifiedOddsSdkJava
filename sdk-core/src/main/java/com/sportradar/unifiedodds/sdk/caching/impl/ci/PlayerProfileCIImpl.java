/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SAPIPlayerCompetitor;
import com.sportradar.uf.sportsapi.datamodel.SAPIPlayerExtended;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportablePlayerProfileCI;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A player's profile representation used by caching components
 */
@SuppressWarnings(
    {
        "AbbreviationAsWordInName",
        "ClassFanOutComplexity",
        "ConstantName",
        "HiddenField",
        "LineLength",
        "MagicNumber",
        "ParameterNumber",
        "ReturnCount",
    }
)
class PlayerProfileCIImpl implements PlayerProfileCI, ExportableCacheItem {

    private static final Logger logger = LoggerFactory.getLogger(PlayerProfileCIImpl.class);
    /**
     * An {@link URN} specifying the id of the associated sport event
     */
    private final URN id;

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
     * A {@link Map} containing player names in different languages
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing player full names in different languages
     */
    private final Map<Locale, String> fullNames = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing player's nationality in different languages
     */
    private final Map<Locale, String> nationalities = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing player's abbreviations in different languages
     */
    private final Map<Locale, String> abbreviations = Maps.newConcurrentMap();

    /**
     * The value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     */
    private String type;

    /**
     * The {@link Date} specifying the date of birth of the player associated with the current instance
     */
    private Date dateOfBirth;

    /**
     * The height in centimeters of the player represented by the current instance
     */
    private Integer height;

    /**
     * The weight in kilograms of the player represented by the current instance
     */
    private Integer weight;

    /**
     * A {@link String} representation of a country code
     */
    private String countryCode;

    /**
     * The player jersey number
     */
    private Integer jerseyNumber;

    /**
     * The player nickname
     */
    private String nickname;

    /**
     * The gender of the player
     */
    private String gender;

    /**
     * The associated competitor id
     */
    private URN competitorId;

    /**
     * The locales which are merged into the CI
     */
    private final List<Locale> cachedLocales = Collections.synchronizedList(new ArrayList<>());

    private final ReentrantLock fetchLock = new ReentrantLock();

    PlayerProfileCIImpl(
        URN id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        URN competitorId
    ) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.id = id;
        this.defaultLocale = defaultLocale;
        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.competitorId = competitorId;
    }

    PlayerProfileCIImpl(
        URN id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SAPIPlayerExtended data,
        Locale dataLocale,
        URN competitorId
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, competitorId);
        merge(data, dataLocale, competitorId);
    }

    PlayerProfileCIImpl(
        URN id,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        SAPIPlayerCompetitor data,
        Locale dataLocale,
        URN competitorId
    ) {
        this(id, dataRouterManager, defaultLocale, exceptionHandlingStrategy, competitorId);
        merge(data, dataLocale, competitorId);
    }

    PlayerProfileCIImpl(
        ExportablePlayerProfileCI exportable,
        DataRouterManager dataRouterManager,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        URN competitorId
    ) {
        Preconditions.checkNotNull(exportable);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;

        this.id = URN.parse(exportable.getId());
        this.defaultLocale = exportable.getDefaultLocale();
        this.competitorId = competitorId;
        mergePlayerProfile(exportable);
    }

    /**
     * Returns the {@link URN} representing id of the related entity
     *
     * @return the {@link URN} representing id of the related entity
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the {@link Map} containing translated names of the item
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated names of the item
     */
    @Override
    public Map<Locale, String> getNames(List<Locale> locales) {
        if (names.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(names);
        }

        requestMissingPlayerData(locales);

        return ImmutableMap.copyOf(names);
    }

    /**
     * Returns the {@link Map} containing translated full names of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated full names of the player
     */
    @Override
    public Map<Locale, String> getFullNames(List<Locale> locales) {
        if (fullNames.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(fullNames);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(fullNames);
        }

        requestMissingPlayerData(locales);

        return ImmutableMap.copyOf(fullNames);
    }

    /**
     * Returns the {@link Map} containing translated nationalities of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated nationalities of the player
     */
    @Override
    public Map<Locale, String> getNationalities(List<Locale> locales) {
        if (nationalities.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(nationalities);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(nationalities);
        }

        requestMissingPlayerData(locales);

        return ImmutableMap.copyOf(nationalities);
    }

    /**
     * Returns the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     *
     * @return - the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     */
    @Override
    public String getType() {
        ensureDataLoaded(type);

        return type;
    }

    /**
     * Returns the {@link Date} specifying the date of birth of the player associated with the current instance
     *
     * @return - the {@link Date} specifying the date of birth of the player associated with the current instance
     */
    @Override
    public Date getDateOfBirth() {
        ensureDataLoaded(dateOfBirth);

        return dateOfBirth;
    }

    /**
     * Returns the height in centimeters of the player represented by the current instance or a null reference if height is not known
     *
     * @return - the height in centimeters of the player represented by the current instance or a null reference if height is not known
     */
    @Override
    public Integer getHeight() {
        ensureDataLoaded(height);

        return height;
    }

    /**
     * Returns the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     *
     * @return - the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     */
    @Override
    public Integer getWeight() {
        ensureDataLoaded(weight);

        return weight;
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
     * Returns the player jersey number
     *
     * @return the jersey number if available; otherwise null
     */
    @Override
    public Integer getJerseyNumber() {
        ensureDataLoaded(jerseyNumber);

        return jerseyNumber;
    }

    /**
     * Returns the player nickname
     *
     * @return the player nickname if available; otherwise null
     */
    @Override
    public String getNickname() {
        ensureDataLoaded(nickname);

        return nickname;
    }

    /**
     * Returns the {@link Map} containing translated abbreviations of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated abbreviations of the player
     */
    @Override
    public Map<Locale, String> getAbbreviations(List<Locale> locales) {
        if (abbreviations.keySet().containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        if (cachedLocales.containsAll(locales)) {
            return ImmutableMap.copyOf(abbreviations);
        }

        requestMissingPlayerData(locales);

        return ImmutableMap.copyOf(abbreviations);
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

    @Override
    public URN getCompetitorId() {
        return competitorId;
    }

    /**
     * Determines whether the current instance has translations for the specified languages
     *
     * @param localeList a {@link List} specifying the required languages
     * @return <code>true</code> if the current instance contains data in the required locals, otherwise <code>false</code>.
     */
    @Override
    public boolean hasTranslationsLoadedFor(List<Locale> localeList) {
        return cachedLocales.containsAll(localeList);
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale) {
        merge(endpointData, dataLocale, null);
    }

    @Override
    public <T> void merge(T endpointData, Locale dataLocale, URN competitorId) {
        if (endpointData instanceof SAPIPlayerExtended) {
            mergePlayerExtended((SAPIPlayerExtended) endpointData, dataLocale, competitorId);
        } else if (endpointData instanceof SAPIPlayerCompetitor) {
            mergePlayerCompetitor((SAPIPlayerCompetitor) endpointData, dataLocale, competitorId);
        } else if (endpointData instanceof ExportablePlayerProfileCI) {
            mergePlayerProfile((ExportablePlayerProfileCI) endpointData);
        }
    }

    @Override
    public List<Locale> getCachedLocales() {
        return cachedLocales;
    }

    private void mergePlayerProfile(ExportablePlayerProfileCI exportable) {
        names.putAll(exportable.getNames());
        fullNames.putAll(exportable.getFullNames());
        nationalities.putAll(exportable.getNationalities());
        abbreviations.putAll(exportable.getAbbreviations());
        type = exportable.getType();
        dateOfBirth = exportable.getDateOfBirth();
        height = exportable.getHeight();
        weight = exportable.getWeight();
        countryCode = exportable.getCountryCode();
        jerseyNumber = exportable.getJerseyNumber();
        nickname = exportable.getNickname();
        gender = exportable.getGender();
        competitorId =
            exportable.getCompetitorId() == null || exportable.getCompetitorId().isEmpty()
                ? null
                : URN.parse(exportable.getCompetitorId());
        cachedLocales.addAll(SdkHelper.findMissingLocales(cachedLocales, exportable.getCachedLocales()));
    }

    public void mergePlayerExtended(SAPIPlayerExtended player, Locale dataLocale, URN competitorId) {
        type = player.getType();

        try {
            dateOfBirth =
                player.getDateOfBirth() == null
                    ? null
                    : Date.from(
                        LocalDate.parse(player.getDateOfBirth()).atStartOfDay().toInstant(ZoneOffset.UTC)
                    );
        } catch (DateTimeParseException | IllegalArgumentException e) {
            logger.warn("Player[{}] date of birth is malformed -> {}", id, player.getDateOfBirth(), e);
            dateOfBirth = null;
        }

        height = player.getHeight();
        weight = player.getWeight();
        countryCode = player.getCountryCode();
        jerseyNumber = player.getJerseyNumber();
        nickname = player.getNickname();

        Optional
            .ofNullable(player.getNationality())
            .ifPresent(nationality -> nationalities.put(dataLocale, nationality));
        Optional.ofNullable(player.getName()).ifPresent(name -> names.put(dataLocale, name));
        Optional.ofNullable(player.getFullName()).ifPresent(name -> fullNames.put(dataLocale, name));

        if (!abbreviations.containsKey(dataLocale)) {
            abbreviations.put(dataLocale, SdkHelper.getAbbreviationFromName(player.getName(), 3));
        }
        if (player.getGender() != null) {
            gender = player.getGender();
        }
        if (competitorId != null) {
            this.competitorId = competitorId;
        }

        cachedLocales.add(dataLocale);
    }

    public void mergePlayerCompetitor(SAPIPlayerCompetitor player, Locale dataLocale, URN competitorId) {
        Optional
            .ofNullable(player.getNationality())
            .ifPresent(nationality -> nationalities.put(dataLocale, nationality));
        Optional.ofNullable(player.getName()).ifPresent(name -> names.put(dataLocale, name));
        Optional.ofNullable(player.getAbbreviation()).ifPresent(abbr -> abbreviations.put(dataLocale, abbr));

        if (!abbreviations.containsKey(dataLocale)) {
            abbreviations.put(dataLocale, SdkHelper.getAbbreviationFromName(player.getName(), 3));
        }
        if (competitorId != null) {
            this.competitorId = competitorId;
        }
    }

    private void requestMissingPlayerData(List<Locale> requiredLocales) {
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

            String missingLocalesStr = missingLocales
                .stream()
                .map(Locale::getLanguage)
                .collect(Collectors.joining(", "));
            logger.debug("Fetching player profile for id='{}' for languages '{}'", id, missingLocalesStr);
            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestPlayerProfileEndpoint(l, id, this);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("requestMissingPlayerData(%s)", missingLocales), e);
        } finally {
            fetchLock.unlock();
        }
    }

    private void ensureDataLoaded(Object object) {
        if (object != null || !cachedLocales.isEmpty()) {
            return;
        }

        requestMissingPlayerData(Collections.singletonList(defaultLocale));
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException("PlayerProfileCI[" + id + "], request(" + request + ")");
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn("Error providing PlayerProfileCI[{}] request({})", id, request);
            } else {
                logger.warn("Error providing PlayerProfileCI[{}] request({}), ex:", id, request, e);
            }
        }
    }

    @Override
    public ExportableCI export() {
        return new ExportablePlayerProfileCI(
            id.toString(),
            new HashMap<>(names),
            defaultLocale,
            new HashMap<>(fullNames),
            new HashMap<>(nationalities),
            new HashMap<>(abbreviations),
            type,
            dateOfBirth,
            height,
            weight,
            countryCode,
            jerseyNumber,
            nickname,
            gender,
            new ArrayList<>(cachedLocales),
            competitorId == null ? null : competitorId.toString()
        );
    }
}
