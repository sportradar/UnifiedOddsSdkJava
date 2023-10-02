package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;
import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableDrawInfoCi implements Serializable {

    private DrawType drawType;
    private TimeType timeType;
    private String gameType;

    public ExportableDrawInfoCi(DrawType drawType, TimeType timeType, String gameType) {
        this.drawType = drawType;
        this.timeType = timeType;
        this.gameType = gameType;
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public void setDrawType(DrawType drawType) {
        this.drawType = drawType;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeType timeType) {
        this.timeType = timeType;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }
}
