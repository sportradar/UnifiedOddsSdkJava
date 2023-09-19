package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableWeatherInfoCi implements Serializable {

    private Integer temperatureCelsius;
    private String wind;
    private String windAdvantage;
    private String pitch;
    private String weatherConditions;

    public ExportableWeatherInfoCi(
        Integer temperatureCelsius,
        String wind,
        String windAdvantage,
        String pitch,
        String weatherConditions
    ) {
        this.temperatureCelsius = temperatureCelsius;
        this.wind = wind;
        this.windAdvantage = windAdvantage;
        this.pitch = pitch;
        this.weatherConditions = weatherConditions;
    }

    public Integer getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(Integer temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getWindAdvantage() {
        return windAdvantage;
    }

    public void setWindAdvantage(String windAdvantage) {
        this.windAdvantage = windAdvantage;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getWeatherConditions() {
        return weatherConditions;
    }

    public void setWeatherConditions(String weatherConditions) {
        this.weatherConditions = weatherConditions;
    }
}
