/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIWeatherInfo;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableWeatherInfoCI;

/**
 * A weather info representation used by caching components
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class WeatherInfoCI {

    /**
     * The temperature in degrees celsius
     */
    private final Integer temperatureCelsius;

    /**
     * The wind conditions
     */
    private final String wind;

    /**
     * The wind advantage information
     */
    private final String windAdvantage;

    /**
     * The pitch weather
     */
    private final String pitch;

    /**
     * The summary of the weather conditions(cloudy, sunny, ...)
     */
    private final String weatherConditions;

    /**
     * Initializes a new instance of the {@link WeatherInfoCI} class
     *
     * @param weatherInfo - {@link SAPIWeatherInfo} containing information about the weather
     */
    WeatherInfoCI(SAPIWeatherInfo weatherInfo) {
        Preconditions.checkNotNull(weatherInfo);

        temperatureCelsius = weatherInfo.getTemperatureCelsius();
        wind = weatherInfo.getWind();
        windAdvantage = weatherInfo.getWindAdvantage();
        pitch = weatherInfo.getPitch();
        weatherConditions = weatherInfo.getWeatherConditions();
    }

    WeatherInfoCI(ExportableWeatherInfoCI exportable) {
        Preconditions.checkNotNull(exportable);

        temperatureCelsius = exportable.getTemperatureCelsius();
        wind = exportable.getWind();
        windAdvantage = exportable.getWindAdvantage();
        pitch = exportable.getPitch();
        weatherConditions = exportable.getWeatherConditions();
    }

    /**
     * Returns the temperature in degrees celsius or a null reference if the temperature is not known
     *
     * @return - the temperature in degrees celsius or a null reference if the temperature is not known
     */
    public Integer getTemperatureCelsius() {
        return temperatureCelsius;
    }

    /**
     * Returns the wind conditions or a null reference if the wind status is not known
     *
     * @return - the wind conditions or a null reference if the wind status is not known
     */
    public String getWind() {
        return wind;
    }

    /**
     * Returns the wind advantage information or a null reference if the wind advantage is not known
     *
     * @return - the wind advantage information or a null reference if the wind advantage is not known
     */
    public String getWindAdvantage() {
        return windAdvantage;
    }

    /**
     * Returns the pitch weather
     *
     * @return - the pitch weather
     */
    public String getPitch() {
        return pitch;
    }

    /**
     * Returns the summary of the weather conditions or a null reference if the weather conditions are not known
     *
     * @return - the summary of the weather conditions or a null reference if the weather conditions are not known
     */
    public String getWeatherConditions() {
        return weatherConditions;
    }

    public ExportableWeatherInfoCI export() {
        return new ExportableWeatherInfoCI(temperatureCelsius, wind, windAdvantage, pitch, weatherConditions);
    }
}
