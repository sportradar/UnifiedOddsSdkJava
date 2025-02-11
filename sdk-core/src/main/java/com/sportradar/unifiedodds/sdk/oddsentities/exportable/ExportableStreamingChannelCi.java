package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableStreamingChannelCi implements Serializable {

    private int id;
    private String name;

    public ExportableStreamingChannelCi(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
