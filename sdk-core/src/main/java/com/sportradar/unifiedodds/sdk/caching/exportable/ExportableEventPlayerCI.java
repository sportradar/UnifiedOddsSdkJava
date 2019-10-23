package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

public class ExportableEventPlayerCI implements Serializable {
    private String id;
    private String name;

    public ExportableEventPlayerCI(String id, String name) {
        this.id = id;
        this.name = name;
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
}
