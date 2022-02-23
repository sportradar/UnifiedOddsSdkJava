/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods implemented by classes representing weather conditions
 */
public interface WeatherInfo {
    /**
     * Returns the pitch weather
     *
     * @return - the pitch weather
     */
    String getPitch();

    /**
     * Returns the temperature in degrees Celsius
     *
     * @return - the temperature in degrees Celsius or a null reference if the temperature is not known
     */
    Integer getTemperature();

    /**
     * Returns the summary of the weather conditions
     *
     * @return - the summary of the weather conditions or a null reference if the weather conditions are not known
     */
    String getWeatherConditions();

    /**
     * Returns the wind conditions
     *
     * @return - the wind conditions or a null reference if the wind status is not known
     */
    String getWind();

    /**
     * Returns the wind advantage information
     *
     * @return - the wind advantage information or a null reference if the wind advantage is not known
     */
    String getWindAdvantage();
}
