/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBets;
import com.sportradar.unifiedodds.sdk.entities.custombet.EventRecommendations;
import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBets;
import com.sportradar.utils.SdkHelper;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link PrebuiltBets}
 */
@SuppressWarnings({ "EmptyCatchBlock" })
public class PrebuiltBetsImpl implements PrebuiltBets {

    private final List<EventRecommendations> events;
    private final int requestedRecommendations;
    private final Date generatedAt;

    public PrebuiltBetsImpl(CapiPreBuiltBets preBuiltBets) {
        this.events =
            preBuiltBets.getEvents().stream().map(EventRecommendationsImpl::new).collect(Collectors.toList());
        this.requestedRecommendations = preBuiltBets.getRequestedRecommendations();
        generatedAt = parseGeneratedAt(preBuiltBets);
    }

    private Date parseGeneratedAt(CapiPreBuiltBets preBuiltBets) {
        if (preBuiltBets.getGeneratedAt() != null) {
            try {
                return SdkHelper.toDate(preBuiltBets.getGeneratedAt());
            } catch (ParseException e) {
                // ignore
            }
        }
        return null;
    }

    @Override
    public List<EventRecommendations> getEvents() {
        return events;
    }

    @Override
    public int getRequestedRecommendations() {
        return requestedRecommendations;
    }

    @Override
    public Date getGeneratedAt() {
        return generatedAt;
    }
}
