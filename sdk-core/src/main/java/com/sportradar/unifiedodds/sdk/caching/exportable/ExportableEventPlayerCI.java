package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

public class ExportableEventPlayerCI implements Serializable {
    private String id;
    private String name;
    private String method;
    private String bench;

    public ExportableEventPlayerCI(String id, String name, String method, String bench) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.bench = bench;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getBench() {
        return bench;
    }

    public void setBench(String bench) {
        this.bench = bench;
    }
}
