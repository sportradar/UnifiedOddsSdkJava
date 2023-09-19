/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.EventPlayerCi;
import com.sportradar.unifiedodds.sdk.entities.GoalScorer;
import com.sportradar.unifiedodds.sdk.entities.Player;
import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a player or driver in a sport event
 */
public class GoalScorerImpl extends PlayerImpl implements GoalScorer {

    /**
     * The method value
     */
    private final String method;

    /**
     * Initializes a new instance of the {@link GoalScorerImpl} class
     *
     * @param eventPlayerCi - the {@link EventPlayerCi} instance
     */
    GoalScorerImpl(EventPlayerCi eventPlayerCi, Locale dataLocale) {
        super(eventPlayerCi.getId(), new HashMap<>());
        Preconditions.checkNotNull(eventPlayerCi);

        super.names.put(dataLocale, eventPlayerCi.getName());
        this.method = eventPlayerCi.getMethod();
    }

    /**
     * Returns a {@link String} describing the current {@link Player} instance
     *
     * @return - a {@link String} describing the current {@link Player} instance
     */
    @Override
    public String toString() {
        return "GoalScorerImpl{" + "id=" + super.getId() + ", names=" + names + ", method=" + method + '}';
    }

    @Override
    public String getMethod() {
        return method;
    }
}
