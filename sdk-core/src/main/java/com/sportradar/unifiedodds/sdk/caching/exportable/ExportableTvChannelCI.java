package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.TvChannel;
import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({ "HiddenField" })
public class ExportableTvChannelCi implements Serializable {

    private String name;
    private Date time;
    private String streamUrl;

    public ExportableTvChannelCi(String name, Date time, String streamUrl) {
        this.name = name;
        this.time = time;
        this.streamUrl = streamUrl;
    }

    public ExportableTvChannelCi(TvChannel tvChannel) {
        this.name = tvChannel.getName();
        this.time = tvChannel.getTime();
        this.streamUrl = tvChannel.getStreamUrl();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }
}
