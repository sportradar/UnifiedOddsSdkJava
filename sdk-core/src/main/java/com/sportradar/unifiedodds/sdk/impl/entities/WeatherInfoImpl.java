/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.WeatherInfoCi;
import com.sportradar.unifiedodds.sdk.entities.WeatherInfo;

/**
 * Provides information about weather conditions
 */
@SuppressWarnings({ "UnnecessaryParentheses" })
public class WeatherInfoImpl implements WeatherInfo {

    /**
     * The pitch weather
     */
    private final String pitch;

    /**
     * The temperature in degrees celsius
     */
    private final Integer temperature;

    /**
     * The summary of the weather conditions(cloudy, sunny, ...)
     */
    private final String weatherConditions;

    /**
     * The wind conditions
     */
    private final String wind;

    /**
     * The wind advantage information
     */
    private final String windAdvantage;

    /**
     * Initializes a new instance of the {@link WeatherInfoImpl} class
     *
     * @param weatherInfo - a {@link WeatherInfoCi} used to build the instance
     */
    public WeatherInfoImpl(WeatherInfoCi weatherInfo) {
        Preconditions.checkNotNull(weatherInfo);

        this.pitch = weatherInfo.getPitch();
        this.temperature = weatherInfo.getTemperatureCelsius();
        this.weatherConditions = weatherInfo.getWeatherConditions();
        this.wind = weatherInfo.getWind();
        this.windAdvantage = weatherInfo.getWindAdvantage();
    }

    /**
     * Returns the pitch weather
     *
     * @return - the pitch weather
     */
    @Override
    public String getPitch() {
        return pitch;
    }

    /**
     * Returns the temperature in degrees Celsius
     *
     * @return - the temperature in degrees Celsius or a null reference if the temperature is not known
     */
    @Override
    public Integer getTemperature() {
        return temperature;
    }

    /**
     * Returns the summary of the weather conditions
     *
     * @return - the summary of the weather conditions or a null reference if the weather conditions are not known
     */
    @Override
    public String getWeatherConditions() {
        return weatherConditions;
    }

    /**
     * Returns the wind conditions
     *
     * @return - the wind conditions or a null reference if the wind status is not known
     */
    @Override
    public String getWind() {
        return wind;
    }

    /**
     * Returns the wind advantage information
     *
     * @return - the wind advantage information or a null reference if the wind advantage is not known
     */
    @Override
    public String getWindAdvantage() {
        return windAdvantage;
    }

    /**
     * Returns a {@link String} describing the current {@link WeatherInfo} instance
     *
     * @return - a {@link String} describing the current {@link WeatherInfo} instance
     */
    @Override
    public String toString() {
        return (
            "WeatherInfoImpl{" +
            "pitch='" +
            pitch +
            '\'' +
            ", temperature=" +
            temperature +
            ", weatherConditions='" +
            weatherConditions +
            '\'' +
            ", wind='" +
            wind +
            '\'' +
            ", windAdvantage='" +
            windAdvantage +
            '\'' +
            '}'
        );
    }
}
