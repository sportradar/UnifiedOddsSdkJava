/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBetsRequest;
import com.sportradar.utils.Urn;

/**
 * Implementation of {@link PrebuiltBetsRequest}
 */
public class PrebuiltBetsRequestImpl implements PrebuiltBetsRequest {

    private final Urn eventId;
    private final Integer subBookmakerId;
    private final String user;
    private final Integer count;
    private final Integer length;

    public PrebuiltBetsRequestImpl(
        Urn eventId,
        Integer subBookmakerId,
        String user,
        Integer count,
        Integer length
    ) {
        this.eventId = eventId;
        this.subBookmakerId = subBookmakerId;
        this.user = user;
        this.count = count;
        this.length = length;
    }

    @Override
    public Urn getEventId() {
        return eventId;
    }

    @Override
    public Integer getSubBookmakerId() {
        return subBookmakerId;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public Integer getCount() {
        return count;
    }

    @Override
    public Integer getLength() {
        return length;
    }

    @Override
    public String toString() {
        return String.format(
            "PrebuiltBetsRequest {eventId=%s, subBookmakerId=%s, user=%s, count=%s, length=%s}",
            eventId,
            subBookmakerId,
            user,
            count,
            length
        );
    }
}
