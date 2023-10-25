/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiEventPlayerAssist;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableEventPlayerAssistCi;

/**
 * Created on 24/11/2017.
 * // TODO @eti: Javadoc
 */
public class EventPlayerAssistCi extends EventPlayerCi {

    private final String type;

    EventPlayerAssistCi(SapiEventPlayerAssist assistData) {
        super(assistData);
        Preconditions.checkNotNull(assistData);

        type = assistData.getType();
    }

    EventPlayerAssistCi(ExportableEventPlayerAssistCi exportable) {
        super(exportable);
        type = exportable.getType();
    }

    public String getType() {
        return type;
    }

    public ExportableEventPlayerAssistCi export() {
        return new ExportableEventPlayerAssistCi(getId().toString(), getName(), type);
    }
}
