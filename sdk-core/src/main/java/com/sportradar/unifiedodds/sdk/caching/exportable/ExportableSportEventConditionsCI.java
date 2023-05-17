package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings({ "AbbreviationAsWordInName", "HiddenField" })
public class ExportableSportEventConditionsCI implements Serializable {

    private String attendance;
    private String eventMode;
    private ExportableRefereeCI referee;
    private ExportableWeatherInfoCI weatherInfo;
    private List<ExportablePitcherCI> pitchers;

    public ExportableSportEventConditionsCI(
        String attendance,
        String eventMode,
        ExportableRefereeCI referee,
        ExportableWeatherInfoCI weatherInfo,
        List<ExportablePitcherCI> pitchers
    ) {
        this.attendance = attendance;
        this.eventMode = eventMode;
        this.referee = referee;
        this.weatherInfo = weatherInfo;
        this.pitchers = pitchers;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getEventMode() {
        return eventMode;
    }

    public void setEventMode(String eventMode) {
        this.eventMode = eventMode;
    }

    public ExportableRefereeCI getReferee() {
        return referee;
    }

    public void setReferee(ExportableRefereeCI referee) {
        this.referee = referee;
    }

    public ExportableWeatherInfoCI getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(ExportableWeatherInfoCI weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public List<ExportablePitcherCI> getPitchers() {
        return pitchers;
    }

    public void setPitchers(List<ExportablePitcherCI> pitchers) {
        this.pitchers = pitchers;
    }
}
