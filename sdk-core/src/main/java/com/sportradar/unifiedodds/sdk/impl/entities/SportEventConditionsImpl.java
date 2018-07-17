/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCI;
import com.sportradar.unifiedodds.sdk.entities.Referee;
import com.sportradar.unifiedodds.sdk.entities.SportEventConditions;
import com.sportradar.unifiedodds.sdk.entities.WeatherInfo;

import java.util.List;
import java.util.Locale;

/**
 * Provides information about sport event conditions
 */
public class SportEventConditionsImpl implements SportEventConditions {
    /**
     * A {@link String} specifying the attendance of the associated sport event
     */
    private final String attendance;

    /**
     * The mode of the event
     */
    private final String eventMode;

    /**
     * The {@link Referee} instance representing the referee presiding over the associated sport event
     */
    private final Referee referee;

    /**
     * The {@link WeatherInfo} instance representing the expected weather on the associated sport event
     */
    private final WeatherInfo weatherInfo;


    /**
     * Initializes a new instance of {@link SportEventConditionsImpl}
     *
     * @param sportEventConditionsCI - a {@link SportEventConditionsCI} used to build the instance
     * @param locales - a {@link List} of locales supported by the instance
     */
    public SportEventConditionsImpl(SportEventConditionsCI sportEventConditionsCI, List<Locale> locales) {
        Preconditions.checkNotNull(sportEventConditionsCI);
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        this.attendance = sportEventConditionsCI.getAttendance();
        this.eventMode = sportEventConditionsCI.getEventMode();
        this.referee = sportEventConditionsCI.getReferee() == null ? null :
                new RefereeImpl(sportEventConditionsCI.getReferee(), locales);
        this.weatherInfo = sportEventConditionsCI.getWeatherInfo() == null ? null :
                new WeatherInfoImpl(sportEventConditionsCI.getWeatherInfo());
    }


    /**
     * Returns a {@link String} specifying the attendance of the associated sport event
     *
     * @return - a {@link String} specifying the attendance of the associated sport event
     */
    @Override
    public String getAttendance() {
        return attendance;
    }

    /**
     * Returns the mode of the event
     *
     * @return - the mode of the event
     */
    @Override
    public String getEventMode() {
        return eventMode;
    }

    /**
     * Returns the {@link Referee} instance representing the referee presiding over the associated sport event
     *
     * @return - the {@link Referee} instance representing the referee presiding over the associated sport event, if available;
     *           otherwise null
     */
    @Override
    public Referee getReferee() {
        return referee;
    }

    /**
     * Returns the {@link WeatherInfo} instance representing the expected weather on the associated sport event
     *
     * @return - the {@link WeatherInfo} instance representing the expected weather on the associated sport event, if available;
     *           otherwise null
     */
    @Override
    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }

    /**
     * Returns a {@link String} describing the current {@link SportEventConditions} instance
     *
     * @return - a {@link String} describing the current {@link SportEventConditions} instance
     */
    @Override
    public String toString() {
        return "SportEventConditionsImpl{" +
                "attendance='" + attendance + '\'' +
                ", eventMode='" + eventMode + '\'' +
                ", referee=" + referee +
                ", weatherInfo=" + weatherInfo +
                '}';
    }
}
