/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPISportEventConditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSportEventConditionsCI;
import com.sportradar.unifiedodds.sdk.entities.Pitcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A sport event conditions representation used by caching components
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class SportEventConditionsCI {

    /**
     * A {@link String} specifying the attendance of the associated sport event
     */
    private String attendance;

    /**
     * The mode of the event
     */
    private String eventMode;

    /**
     * The {@link RefereeCI} instance representing the referee presiding over the associated sport event
     */
    private RefereeCI referee;

    /**
     * The {@link WeatherInfoCI} instance representing the expected weather on the associated sport event
     */
    private WeatherInfoCI weatherInfo;

    /**
     * The list of associated {@link Pitcher}
     */
    private List<PitcherCI> pitchers;

    /**
     * Initializes a new instance of the {@link SportEventConditionsCI} class
     *
     * @param seConditions - {@link SAPISportEventConditions} containing information about the competitor
     * @param locale - {@link Locale} specifying the language of the <i>seConditions</i>
     */
    public SportEventConditionsCI(SAPISportEventConditions seConditions, Locale locale) {
        Preconditions.checkNotNull(seConditions);
        Preconditions.checkNotNull(locale);

        merge(seConditions, locale);
    }

    public SportEventConditionsCI(ExportableSportEventConditionsCI exportable) {
        Preconditions.checkNotNull(exportable);

        this.attendance = exportable.getAttendance();
        this.eventMode = exportable.getEventMode();
        this.referee = exportable.getReferee() != null ? new RefereeCI(exportable.getReferee()) : null;
        this.weatherInfo =
            exportable.getWeatherInfo() != null ? new WeatherInfoCI(exportable.getWeatherInfo()) : null;
        this.pitchers =
            exportable.getPitchers() != null
                ? exportable.getPitchers().stream().map(PitcherCI::new).collect(Collectors.toList())
                : null;
    }

    /**
     * Merges the information from the provided {@link SAPISportEventConditions} into the current instance
     *
     * @param seConditions - {@link SAPISportEventConditions} containing information about the competitor
     * @param locale - {@link Locale} specifying the language of the <i>seConditions</i>
     */
    public void merge(SAPISportEventConditions seConditions, Locale locale) {
        Preconditions.checkNotNull(seConditions);
        Preconditions.checkNotNull(locale);

        attendance = seConditions.getAttendance();
        eventMode = seConditions.getMatchMode();

        if (seConditions.getReferee() != null) {
            if (referee == null) {
                referee = new RefereeCI(seConditions.getReferee(), locale);
            } else {
                referee.merge(seConditions.getReferee(), locale);
            }
        }

        if (seConditions.getWeatherInfo() != null) {
            weatherInfo = new WeatherInfoCI(seConditions.getWeatherInfo());
        }

        if (seConditions.getPitchers() != null && !seConditions.getPitchers().getPitcher().isEmpty()) {
            pitchers = new ArrayList<>();
            seConditions
                .getPitchers()
                .getPitcher()
                .forEach(pitcher -> pitchers.add(new PitcherCI(pitcher, locale)));
        }
    }

    /**
     * Returns a {@link String} specifying the attendance of the associated sport event
     *
     * @return - a {@link String} specifying the attendance of the associated sport event
     */
    public String getAttendance() {
        return attendance;
    }

    /**
     * Returns the mode of the event
     *
     * @return - the mode of the event
     */
    public String getEventMode() {
        return eventMode;
    }

    /**
     * Returns the {@link RefereeCI} instance representing the referee presiding over the associated sport event
     *
     * @return - the {@link RefereeCI} instance representing the referee presiding over the associated sport event
     */
    public RefereeCI getReferee() {
        return referee;
    }

    /**
     * Returns the {@link WeatherInfoCI} instance representing the expected weather on the associated sport event
     *
     * @return - the {@link WeatherInfoCI} instance representing the expected weather on the associated sport event
     */
    public WeatherInfoCI getWeatherInfo() {
        return weatherInfo;
    }

    /**
     * Returns the list of associated {@link Pitcher}
     * @return the list of assocaited {@link Pitcher}
     */
    public List<PitcherCI> getPitchers() {
        return pitchers;
    }

    public ExportableSportEventConditionsCI export() {
        return new ExportableSportEventConditionsCI(
            attendance,
            eventMode,
            referee != null ? referee.export() : null,
            weatherInfo != null ? weatherInfo.export() : null,
            pitchers != null ? pitchers.stream().map(PitcherCI::export).collect(Collectors.toList()) : null
        );
    }
}
