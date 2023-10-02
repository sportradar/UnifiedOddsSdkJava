package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableProducerInfoLinkCi implements Serializable {

    private String reference;
    private String name;

    public ExportableProducerInfoLinkCi(String reference, String name) {
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
