/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchRound;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCompleteRoundCi;
import com.sportradar.utils.Urn;
import java.util.*;

/**
 * A round representation used by caching components. The cache item exists as a whole object,
 * there is no support for partial loading
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class CompleteRoundCiImpl implements CompleteRoundCi {

    /**
     * A {@link Map} containing round names in different languages
     */
    private final Map<Locale, String> names;

    /**
     * A {@link Map} containing phase or group name in different languages
     */
    private final Map<Locale, String> phaseOrGroupLongNames;

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

    private final List<Locale> cachedLocales;

    /**
     * Initializes a new instance of the {@link CompleteRoundCiImpl} class
     *
     * @param round - {@link SapiMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    public CompleteRoundCiImpl(SapiMatchRound round, Locale locale) {
        Preconditions.checkNotNull(round);
        Preconditions.checkNotNull(locale);

        names = Maps.newConcurrentMap();
        phaseOrGroupLongNames = Maps.newConcurrentMap();
        cachedLocales = Collections.synchronizedList(new ArrayList<>());

        merge(round, locale);
    }

    public CompleteRoundCiImpl(ExportableCompleteRoundCi exportable) {
        Preconditions.checkNotNull(exportable);

        this.names = Maps.newConcurrentMap();
        this.names.putAll(exportable.getNames());
        this.phaseOrGroupLongNames = Maps.newConcurrentMap();
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
        this.cachedLocales = Collections.synchronizedList(new ArrayList<>(exportable.getCachedLocales()));
    }

    /**
     * Merges the information from the provided {@link SapiMatchRound} into the current instance
     *
     * @param round - {@link SapiMatchRound} containing information about the round
     * @param locale - {@link Locale} specifying the language of the <i>round</i>
     */
    @Override
    public void merge(SapiMatchRound round, Locale locale) {
        Preconditions.checkNotNull(round);
        Preconditions.checkNotNull(locale);

        type = round.getType();
        group = round.getGroup();
        groupId = Strings.isNullOrEmpty(round.getGroupId()) ? null : Urn.parse(round.getGroupId());
        otherMatchId = round.getOtherMatchId();
        number = round.getNumber();
        cupRoundMatches = round.getCupRoundMatches();
        cupRoundMatchNumber = round.getCupRoundMatchNumber();
        betradarId = round.getBetradarId();
        phase = round.getPhase();

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
    }

    /**
     * Returns the type of the round
     *
     * @return - the type of the round
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Returns the name of the group associated with the current round
     *
     * @return - the name of the group associated with the current round
     */
    @Override
    public String getGroup() {
        return group;
    }

    /**
     * Returns the id of the group associated with the current round
     *
     * @return - the id of the group associated with the current round
     */
    @Override
    public Urn getGroupId() {
        return groupId;
    }

    /**
     * Returns the id of the other match
     *
     * @return - the id of the other match
     */
    @Override
    public String getOtherMatchId() {
        return otherMatchId;
    }

    /**
     * Returns a value specifying the round number or a null reference if round number is not defined
     *
     * @return - a value specifying the round number or a null reference if round number is not defined
     */
    @Override
    public Integer getNumber() {
        return number;
    }

    /**
     * Returns a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     *
     * @return - a value specifying the number of matches in the current cup round or a null reference
     * if number of matches is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatches() {
        return cupRoundMatches;
    }

    /**
     * Returns a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     *
     * @return - a value specifying the number of the match in the current cup round or a null reference
     * if match number is not applicable to current instance
     */
    @Override
    public Integer getCupRoundMatchNumber() {
        return cupRoundMatchNumber;
    }

    /**
     * Returns the betradar identifier
     *
     * @return - the betradar identifier; or null if unavailable
     */
    @Override
    public Integer getBetradarId() {
        return betradarId;
    }

    /**
     * Returns the name for specific locale
     *
     * @param locale - {@link Locale} specifying the language of the returned nationality
     * @return - Return the name if exists, or null
     */
    @Override
    public String getName(Locale locale) {
        return names.getOrDefault(locale, null);
    }

    /**
     * Returns the name or group long name for the specified locale
     *
     * @param locale {@link Locale} specifying the language of the value
     * @return the name or group long name if exists, or null
     */
    @Override
    public String getPhaseOrGroupLongName(Locale locale) {
        return phaseOrGroupLongNames.getOrDefault(locale, null);
    }

    /**
     * Returns the phase of the round
     *
     * @return the phase of the round
     */
    @Override
    public String getPhase() {
        return phase;
    }

    /**
     * Returns the betradar name
     * @return the betradar name
     */
    @Override
    public String getBetradarName() {
        return betradarName;
    }

    /**
     * Checks if the associated cache item contains all the provided {@link Locale}s
     *
     * @param locales the {@link Locale}s that should be checked
     * @return <code>true</code> if all the provided {@link Locale}s are cached, otherwise <code>false</code>
     */
    @Override
    public boolean hasTranslationsFor(List<Locale> locales) {
        return cachedLocales.containsAll(locales);
    }

    @Override
    public String toString() {
        return (
            "CompleteRoundCIImpl{" +
            "names=" +
            names +
            ", phaseOrGroupLongNames=" +
            phaseOrGroupLongNames +
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
            ", phase=" +
            phase +
            ", betradarName=" +
            betradarName +
            ", cachedLocales=" +
            cachedLocales +
            '}'
        );
    }

    public ExportableCompleteRoundCi export() {
        return new ExportableCompleteRoundCi(
            new HashMap<>(names),
            new HashMap<>(phaseOrGroupLongNames),
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
            new ArrayList<>(cachedLocales)
        );
    }
}
