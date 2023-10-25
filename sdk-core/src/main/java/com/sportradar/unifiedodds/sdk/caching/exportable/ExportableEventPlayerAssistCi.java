package com.sportradar.unifiedodds.sdk.caching.exportable;

@SuppressWarnings({ "HiddenField" })
public class ExportableEventPlayerAssistCi extends ExportableEventPlayerCi {

    private String type;

    public ExportableEventPlayerAssistCi(String id, String name, String type) {
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
