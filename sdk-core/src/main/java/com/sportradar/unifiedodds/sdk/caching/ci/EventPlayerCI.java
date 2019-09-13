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
public class EventPlayerCI {

    private final URN id;
    private final String name;

    EventPlayerCI(SAPIEventPlayer playerData) {
        Preconditions.checkNotNull(playerData);

        id = URN.parse(playerData.getId());
        name = playerData.getName();
    }

    EventPlayerCI(ExportableEventPlayerCI exportable) {
        Preconditions.checkNotNull(exportable);

        id = URN.parse(exportable.getId());
        name = exportable.getName();
    }

    public URN getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ExportableEventPlayerCI export() {
        return new ExportableEventPlayerCI(
                id.toString(),
                name
        );
    }
}
