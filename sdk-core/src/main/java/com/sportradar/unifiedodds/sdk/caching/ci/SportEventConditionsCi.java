/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiSportEventConditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableSportEventConditionsCi;
import com.sportradar.unifiedodds.sdk.entities.Pitcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * A sport event conditions representation used by caching components
 */
public class SportEventConditionsCi {

    /**
     * A {@link String} specifying the attendance of the associated sport event
     */
    private String attendance;

    /**
     * The mode of the event
     */
    private String eventMode;

    /**
     * The {@link RefereeCi} instance representing the referee presiding over the associated sport event
     */
    private RefereeCi referee;

    /**
     * The {@link WeatherInfoCi} instance representing the expected weather on the associated sport event
     */
    private WeatherInfoCi weatherInfo;

    /**
     * The list of associated {@link Pitcher}
     */
    private List<PitcherCi> pitchers;

    /**
     * Initializes a new instance of the {@link SportEventConditionsCi} class
     *
     * @param seConditions - {@link SapiSportEventConditions} containing information about the competitor
     * @param locale - {@link Locale} specifying the language of the <i>seConditions</i>
     */
    public SportEventConditionsCi(SapiSportEventConditions seConditions, Locale locale) {
        Preconditions.checkNotNull(seConditions);
        Preconditions.checkNotNull(locale);

        merge(seConditions, locale);
    }

    public SportEventConditionsCi(ExportableSportEventConditionsCi exportable) {
        Preconditions.checkNotNull(exportable);

        this.attendance = exportable.getAttendance();
        this.eventMode = exportable.getEventMode();
        this.referee = exportable.getReferee() != null ? new RefereeCi(exportable.getReferee()) : null;
        this.weatherInfo =
            exportable.getWeatherInfo() != null ? new WeatherInfoCi(exportable.getWeatherInfo()) : null;
        this.pitchers =
            exportable.getPitchers() != null
                ? exportable.getPitchers().stream().map(PitcherCi::new).collect(Collectors.toList())
                : null;
    }

    /**
     * Merges the information from the provided {@link SapiSportEventConditions} into the current instance
     *
     * @param seConditions - {@link SapiSportEventConditions} containing information about the competitor
     * @param locale - {@link Locale} specifying the language of the <i>seConditions</i>
     */
    public void merge(SapiSportEventConditions seConditions, Locale locale) {
        Preconditions.checkNotNull(seConditions);
        Preconditions.checkNotNull(locale);

        attendance = seConditions.getAttendance();
        eventMode = seConditions.getMatchMode();

        if (seConditions.getReferee() != null) {
            if (referee == null) {
                referee = new RefereeCi(seConditions.getReferee(), locale);
            } else {
                referee.merge(seConditions.getReferee(), locale);
            }
        }

        if (seConditions.getWeatherInfo() != null) {
            weatherInfo = new WeatherInfoCi(seConditions.getWeatherInfo());
        }

        if (seConditions.getPitchers() != null && !seConditions.getPitchers().getPitcher().isEmpty()) {
            pitchers = new ArrayList<>();
            seConditions
                .getPitchers()
                .getPitcher()
                .forEach(pitcher -> pitchers.add(new PitcherCi(pitcher, locale)));
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
     * Returns the {@link RefereeCi} instance representing the referee presiding over the associated sport event
     *
     * @return - the {@link RefereeCi} instance representing the referee presiding over the associated sport event
     */
    public RefereeCi getReferee() {
        return referee;
    }

    /**
     * Returns the {@link WeatherInfoCi} instance representing the expected weather on the associated sport event
     *
     * @return - the {@link WeatherInfoCi} instance representing the expected weather on the associated sport event
     */
    public WeatherInfoCi getWeatherInfo() {
        return weatherInfo;
    }

    /**
     * Returns the list of associated {@link Pitcher}
     * @return the list of assocaited {@link Pitcher}
     */
    public List<PitcherCi> getPitchers() {
        return pitchers;
    }

    public ExportableSportEventConditionsCi export() {
        return new ExportableSportEventConditionsCi(
            attendance,
            eventMode,
            referee != null ? referee.export() : null,
            weatherInfo != null ? weatherInfo.export() : null,
            pitchers != null ? pitchers.stream().map(PitcherCi::export).collect(Collectors.toList()) : null
        );
    }
}
