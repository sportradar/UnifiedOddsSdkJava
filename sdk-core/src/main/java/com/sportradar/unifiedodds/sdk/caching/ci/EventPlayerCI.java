/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIEventPlayer;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableEventPlayerCI;
import com.sportradar.utils.URN;

/**
 * Created on 24/11/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class EventPlayerCI {

    private final URN id;
    private final String name;
    private final String method;
    private final String bench;

    EventPlayerCI(SAPIEventPlayer playerData) {
        Preconditions.checkNotNull(playerData);

        id = URN.parse(playerData.getId());
        name = playerData.getName();
        method = playerData.getMethod();
        bench = playerData.getBench();
    }

    EventPlayerCI(ExportableEventPlayerCI exportable) {
        Preconditions.checkNotNull(exportable);

        id = URN.parse(exportable.getId());
        name = exportable.getName();
        method = exportable.getMethod();
        bench = exportable.getBench();
    }

    public URN getId() {
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

    public ExportableEventPlayerCI export() {
        return new ExportableEventPlayerCI(id.toString(), name, method, bench);
    }
}
