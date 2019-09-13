package com.sportradar.unifiedodds.sdk.caching.exportable;

public class ExportableProducerInfoLinkCI {
    private String reference;
    private String name;

    public ExportableProducerInfoLinkCI(String reference, String name) {
        this.reference = reference;
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
