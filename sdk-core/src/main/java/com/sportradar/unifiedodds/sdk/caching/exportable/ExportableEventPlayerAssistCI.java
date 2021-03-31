package com.sportradar.unifiedodds.sdk.caching.exportable;

public class ExportableEventPlayerAssistCI extends ExportableEventPlayerCI {
    private String type;

    public ExportableEventPlayerAssistCI(String id, String name, String type) {
        super(id, name, null, null);
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
