/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBetsRequest;
import com.sportradar.unifiedodds.sdk.internal.impl.custombetentities.PrebuiltBetsRequestImpl;
import com.sportradar.unifiedodds.sdk.managers.PrebuiltBetsRequestBuilder;
import com.sportradar.utils.Urn;

/**
 * Implementation of {@link PrebuiltBetsRequestBuilder}
 */
@SuppressWarnings("HiddenField")
class PrebuiltBetsRequestBuilderImpl implements PrebuiltBetsRequestBuilder {

    private Urn eventId;
    private Integer subBookmakerId;
    private String user;
    private Integer count;
    private Integer length;

    public PrebuiltBetsRequestBuilderImpl() {}

    @Override
    public PrebuiltBetsRequestBuilder setEventId(Urn eventId) {
        this.eventId = eventId;
        return this;
    }

    @Override
    public PrebuiltBetsRequestBuilder setSubBookmakerId(Integer subBookmakerId) {
        this.subBookmakerId = subBookmakerId;
        return this;
    }

    @Override
    public PrebuiltBetsRequestBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    @Override
    public PrebuiltBetsRequestBuilder setCount(Integer count) {
        this.count = count;
        return this;
    }

    @Override
    public PrebuiltBetsRequestBuilder setLength(Integer length) {
        this.length = length;
        return this;
    }

    @Override
    public PrebuiltBetsRequest build() {
        return new PrebuiltBetsRequestImpl(eventId, subBookmakerId, user, count, length);
    }
}
