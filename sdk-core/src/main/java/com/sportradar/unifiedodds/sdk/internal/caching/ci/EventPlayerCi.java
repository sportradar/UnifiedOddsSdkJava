/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiEventPlayer;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableEventPlayerCi;
import com.sportradar.utils.Urn;

/**
 * Created on 24/11/2017.
 * // TODO @eti: Javadoc
 */
public class EventPlayerCi {

    private final Urn id;
    private final String name;
    private final String method;
    private final String bench;

    EventPlayerCi(SapiEventPlayer playerData) {
        Preconditions.checkNotNull(playerData);

        id = Urn.parse(playerData.getId());
        name = playerData.getName();
        method = playerData.getMethod();
        bench = playerData.getBench();
    }

    EventPlayerCi(ExportableEventPlayerCi exportable) {
        Preconditions.checkNotNull(exportable);

        id = Urn.parse(exportable.getId());
        name = exportable.getName();
        method = exportable.getMethod();
        bench = exportable.getBench();
    }

    public Urn getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public String getBench() {
        return bench;
    }

    public ExportableEventPlayerCi export() {
        return new ExportableEventPlayerCi(id.toString(), name, method, bench);
    }
}
