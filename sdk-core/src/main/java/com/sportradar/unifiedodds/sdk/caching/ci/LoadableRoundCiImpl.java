/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchRound;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CacheItem;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableLoadableRoundCi;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataRouterStreamException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A round representation used by caching components. The cache item properties are loaded on demand.
 */
@SuppressWarnings(
    {
        "ConstantName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "JavaNCSS",
        "MethodLength",
        "NPathComplexity",
        "ParameterNumber",
        "ReturnCount",
        "UnnecessaryParentheses",
    }
)
public class LoadableRoundCiImpl implements LoadableRoundCi {

    private static final Logger logger = LoggerFactory.getLogger(LoadableRoundCiImpl.class);

    /**
     * A {@link Map} containing round names in different languages
     */
    private final Map<Locale, String> names = Maps.newConcurrentMap();

    /**
     * A {@link Map} containing phase or group name in different languages
     */
    private final Map<Locale, String> phaseOrGroupLongNames = Maps.newConcurrentMap();

    /**
     * The CI default {@link Locale} used to fetch data which is not translatable
     */
    private final Locale defaultLocale;

    /**
     * The associated event cache item
     */
    private final CacheItem associatedEventCi;

    /**
     * The associated event identifier - used to initiate {@link DataRouterManager} requests
     */
    private final Urn associatedEventId;

    /**
     * An indication on how should be the SDK exceptions handled
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * The {@link DataRouterManager} instance used to initiate data fetch requests
     */
    private final DataRouterManager dataRouterManager;

    /**
     * Type of the round
     */
    private String type;

    /**
     * The name of the group associated with the current round
     */
    private String group;

    /**
     * The id of the group associated with the current round
     */
    private Urn groupId;

    /**
     * The id of the other match
     */
    private String otherMatchId;

    /**
     * A value specifying the round number
     */
    private Integer number;

    /**
     * A value specifying the number of matches in the current cup round
     */
    private Integer cupRoundMatches;

    /**
     * A value specifying the number of the match in the current cup round
     */
    private Integer cupRoundMatchNumber;

    /**
     * The betradar identifier
     */
    private Integer betradarId;

    /**
     * The phase
     */
    private String phase;

    /**
     * The betradar name
     */
    private String betradarName;

    /**
     * A list of cached locales
     */
    private final List<Locale> cachedSummaryLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     *  A list of cached fixture locales
     */
    private final List<Locale> cachedFixtureLocales = Collections.synchronizedList(new ArrayList<>());

    /**
     * An {@link ReentrantLock} used to synchronize summary request operations
     */
    private final ReentrantLock summaryRequest = new ReentrantLock();

    /**
     * An {@link ReentrantLock} used to synchronize fixture request operations
     */
    private final ReentrantLock fixtureRequest = new ReentrantLock();

    public LoadableRoundCiImpl(
        CacheItem associatedEventCi,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        Preconditions.checkNotNull(associatedEventCi);
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(defaultLocale);
        Preconditions.checkNotNull(exceptionHandlingStrategy);

        this.associatedEventCi = associatedEventCi;
        this.associatedEventId = associatedEventCi.getId();
        this.dataRouterManager = dataRouterManager;
        this.defaultLocale = defaultLocale;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
    }

    public LoadableRoundCiImpl(
        SapiMatchRound roundData,
        boolean isFixtureEndpoint,
        Locale dataLocale,
        CacheItem associatedEventCi,
        DataRouterManager dataRouterManager,
        Locale defaultLocale,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        this(associatedEventCi, dataRouterManager, defaultLocale, exceptionHandlingStrategy);
        Preconditions.checkNotNull(roundData);
        Preconditions.checkNotNull(dataLocale);

        merge(roundData, dataLocale, isFixtureEndpoint);
    }

    public LoadableRoundCiImpl(
        CacheItem associatedEventCi,
        ExportableLoadableRoundCi exportable,
        DataRouterManager dataRouterManager,
        ExceptionHandlingStrategy exceptionHandlingStrategy
    ) {
        this(associatedEventCi, dataRouterManager, exportable.getDefaultLocale(), exceptionHandlingStrategy);
        this.names.putAll(exportable.getNames());
        this.phaseOrGroupLongNames.putAll(exportable.getPhaseOrGroupLongNames());
        this.type = exportable.getType();
        this.group = exportable.getGroup();
        this.groupId = exportable.getGroupId() != null ? Urn.parse(exportable.getGroupId()) : null;
        this.otherMatchId = exportable.getOtherMatchId();
        this.number = exportable.getNumber();
        this.cupRoundMatches = exportable.getCupRoundMatches();
        this.cupRoundMatchNumber = exportable.getCupRoundMatchNumber();
        this.betradarId = exportable.getBetradarId();
        this.phase = exportable.getPhase();
        this.betradarName = exportable.getBetradarName();
        this.cachedSummaryLocales.addAll(exportable.getCachedSummaryLocales());
        this.cachedFixtureLocales.addAll(exportable.getCachedFixtureLocales());
    }

    /**
     * Returns the type of the round
     *
     * @return the type of the round
     */
    @Override
    public String getType() {
        if (summaryLoadedCheck(type, defaultLocale)) {
            return type;
        }

        initiateSummaryRequest(defaultLocale);

        return type;
    }

    /**
     * Returns the name of the group associated with the current round
     *
     * @return the name of the group associated with the current round
     */
    @Override
    public String getGroup() {
        if (summaryLoadedCheck(group, defaultLocale)) {
            return group;
        }

        initiateSummaryRequest(defaultLocale);

        return group;
    }

    /**
     * Returns the id of the group associated with the current round
     *
     * @return the id of the group associated with the current round
     */
    @Override
    public Urn getGroupId() {
        if (summaryLoadedCheck(groupId, defaultLocale)) {
            return groupId;
        }

        initiateSummaryRequest(defaultLocale);

        return groupId;
    }

    /**
     * Returns the id of the other match
     *
     * @return the id of the other match
     */
    @Override
    public String getOtherMatchId() {
        if (summaryLoadedCheck(otherMatchId, defaultLocale)) {
            return otherMatchId;
        }

        initiateSummaryRequest(defaultLocale);

        return otherMatchId;
    }

    /**
     * Returns a value specifying the round number or a null reference if round number is not defined
     *
     * @return a value specifying the round number or a null reference if round number is not defined
     */
    @Override
    public Integer getNumber() {
        if (summaryLoadedCheck(number, defaultLocale)) {
            return number;
        }

        initiateSummaryRequest(defaultLocale);

        return number;
    }

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     *
     * @return a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatches() {
        if (summaryLoadedCheck(cupRoundMatches, defaultLocale)) {
            return cupRoundMatches;
        }

        initiateSummaryRequest(defaultLocale);

        return cupRoundMatches;
    }

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     *
     * @return a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatchNumber() {
        if (summaryLoadedCheck(cupRoundMatchNumber, defaultLocale)) {
            return cupRoundMatchNumber;
        }

        initiateSummaryRequest(defaultLocale);

        return cupRoundMatchNumber;
    }

    /**
     * Returns the betradar identifier
     *
     * @return the betradar identifier; or null if unavailable
     */
    @Override
    public Integer getBetradarId() {
        if (!cachedFixtureLocales.isEmpty()) {
            return betradarId;
        }

        initiateSummaryRequest(defaultLocale);

        return betradarId;
    }

    /**
     * Returns the name for specific locale
     *
     * @param locale {@link Locale} specifying the language of the returned name
     * @return the name if exists, or null
     */
    @Override
    public String getName(Locale locale) {
        if (summaryLoadedCheck(names.get(locale), locale)) {
            return names.get(locale);
        }

        initiateSummaryRequest(locale);

        return names.get(locale);
    }

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    @Override
    public String getPhaseOrGroupLongName(Locale locale) {
        if (phaseOrGroupLongNames.containsKey(locale) || cachedFixtureLocales.contains(locale)) {
            return phaseOrGroupLongNames.get(locale);
        }

        initiateSummaryRequest(locale);

        return phaseOrGroupLongNames.get(locale);
    }

    /**
     * Returns the phase of the round
     *
     * @return the phase of the round
     */
    @Override
    public String getPhase() {
        if (summaryLoadedCheck(phase, defaultLocale)) {
            return phase;
        }

        initiateSummaryRequest(defaultLocale);

        return phase;
    }

    /**
     * Returns the betradar name
     * @return the betradar name
     */
    @Override
    public String getBetradarName() {
        if (summaryLoadedCheck(betradarName, defaultLocale)) {
            return betradarName;
        }

        initiateSummaryRequest(defaultLocale);

        return betradarName;
    }

    /**
     * Merges the information from the provided {@link SapiMatchRound} into the current instance
     *
     * @param round             {@link SapiMatchRound} containing information about the round
     * @param locale            {@link Locale} specifying the language of the provided data
     * @param isFixtureEndpoint an indication if the data provided was extracted from the fixture endpoint
     */
    @Override
    public void merge(SapiMatchRound round, Locale locale, boolean isFixtureEndpoint) {
        Preconditions.checkNotNull(round);
        Preconditions.checkNotNull(locale);

        if (round.getType() != null) {
            type = round.getType();
        }

        if (round.getGroup() != null) {
            group = round.getGroup();
        }

        if (!Strings.isNullOrEmpty(round.getGroupId())) {
            groupId = Urn.parse(round.getGroupId());
        }

        if (round.getOtherMatchId() != null) {
            otherMatchId = round.getOtherMatchId();
        }

        if (round.getNumber() != null) {
            number = round.getNumber();
        }

        if (round.getCupRoundMatches() != null) {
            cupRoundMatches = round.getCupRoundMatches();
        }

        if (round.getCupRoundMatchNumber() != null) {
            cupRoundMatchNumber = round.getCupRoundMatchNumber();
        }

        if (round.getBetradarId() != null) {
            betradarId = round.getBetradarId();
        }

        if (round.getPhase() != null) {
            phase = round.getPhase();
        }

        if (round.getName() != null) {
            names.put(locale, round.getName());
        } else {
            names.put(locale, "");
        }

        if (round.getGroupLongName() != null) {
            phaseOrGroupLongNames.put(locale, round.getGroupLongName());
        } else {
            phaseOrGroupLongNames.put(locale, "");
        }

        betradarName = round.getBetradarName();

        cachedSummaryLocales.add(locale);

        if (isFixtureEndpoint) {
            cachedFixtureLocales.add(locale);
        }
    }

    private boolean summaryLoadedCheck(Object value2check, Locale locale) {
        return value2check != null || cachedSummaryLocales.contains(locale);
    }

    private void initiateSummaryRequest(Locale locale) {
        Preconditions.checkNotNull(locale);

        List<Locale> requiredLocales = Collections.singletonList(locale);
        List<Locale> missingLocales = SdkHelper.findMissingLocales(cachedSummaryLocales, requiredLocales);
        if (missingLocales.isEmpty()) {
            return;
        }

        summaryRequest.lock();
        try {
            // recheck missing locales after lock
            missingLocales = SdkHelper.findMissingLocales(cachedSummaryLocales, requiredLocales);
            if (missingLocales.isEmpty()) {
                return;
            }

            logger.debug(
                "Fetching summary for LoadableRoundCIImpl[EventId:'{}'] for languages '{}'",
                associatedEventId,
                missingLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(", "))
            );

            missingLocales.forEach(l -> {
                try {
                    dataRouterManager.requestSummaryEndpoint(l, associatedEventId, associatedEventCi);
                } catch (CommunicationException e) {
                    throw new DataRouterStreamException(e.getMessage(), e);
                }
            });
        } catch (DataRouterStreamException e) {
            handleException(String.format("initiateSummaryRequest(%s)", missingLocales), e);
        } finally {
            summaryRequest.unlock();
        }
    }

    private void handleException(String request, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (e == null) {
                throw new ObjectNotFoundException(
                    "LoadableRoundCIImpl[SportEventId:'" + associatedEventId + "'], request(" + request + ")"
                );
            } else {
                throw new ObjectNotFoundException(request, e);
            }
        } else {
            if (e == null) {
                logger.warn(
                    "Error providing LoadableRoundCIImpl[SportEventId:'{}'] request({})",
                    associatedEventId,
                    request
                );
            } else {
                logger.warn(
                    "Error providing LoadableRoundCIImpl[SportEventId:'{}'] request({}), ex:",
                    associatedEventId,
                    request,
                    e
                );
            }
        }
    }

    @Override
    public String toString() {
        return (
            "LoadableRoundCIImpl{" +
            "names=" +
            names +
            ", phaseOrGroupLongNames=" +
            phaseOrGroupLongNames +
            ", defaultLocale=" +
            defaultLocale +
            ", associatedEventCI=" +
            associatedEventCi +
            ", associatedEventId=" +
            associatedEventId +
            ", exceptionHandlingStrategy=" +
            exceptionHandlingStrategy +
            ", dataRouterManager=" +
            dataRouterManager +
            ", type='" +
            type +
            '\'' +
            ", group='" +
            group +
            '\'' +
            ", groupId='" +
            groupId +
            '\'' +
            ", otherMatchId='" +
            otherMatchId +
            '\'' +
            ", number=" +
            number +
            ", cupRoundMatches=" +
            cupRoundMatches +
            ", cupRoundMatchNumber=" +
            cupRoundMatchNumber +
            ", betradarId=" +
            betradarId +
            ", cachedSummaryLocales=" +
            cachedSummaryLocales +
            ", cachedFixtureLocales=" +
            cachedFixtureLocales +
            ", summaryRequest=" +
            summaryRequest +
            ", fixtureRequest=" +
            fixtureRequest +
            ", phase=" +
            phase +
            ", betradarName=" +
            betradarName +
            '}'
        );
    }

    public ExportableLoadableRoundCi export() {
        return new ExportableLoadableRoundCi(
            new HashMap<>(names),
            new HashMap<>(phaseOrGroupLongNames),
            defaultLocale,
            type,
            group,
            groupId != null ? groupId.toString() : null,
            otherMatchId,
            number,
            cupRoundMatches,
            cupRoundMatchNumber,
            betradarId,
            phase,
            betradarName,
            new ArrayList<>(cachedSummaryLocales),
            new ArrayList<>(cachedFixtureLocales)
        );
    }
}
