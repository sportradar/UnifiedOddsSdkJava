package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings({ "HiddenField" })
public class ExportableSportEventConditionsCi implements Serializable {

    private String attendance;
    private String eventMode;
    private ExportableRefereeCi referee;
    private ExportableWeatherInfoCi weatherInfo;
    private List<ExportablePitcherCi> pitchers;

    public ExportableSportEventConditionsCi(
        String attendance,
        String eventMode,
        ExportableRefereeCi referee,
        ExportableWeatherInfoCi weatherInfo,
        List<ExportablePitcherCi> pitchers
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

    public ExportableRefereeCi getReferee() {
        return referee;
    }

    public void setReferee(ExportableRefereeCi referee) {
        this.referee = referee;
    }

    public ExportableWeatherInfoCi getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(ExportableWeatherInfoCi weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public List<ExportablePitcherCi> getPitchers() {
        return pitchers;
    }

    public void setPitchers(List<ExportablePitcherCi> pitchers) {
        this.pitchers = pitchers;
    }
}
