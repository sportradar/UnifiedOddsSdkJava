/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.sportradar.uf.custombet.datamodel.CapiEventRecommendationsType;
import com.sportradar.unifiedodds.sdk.entities.custombet.EventRecommendations;
import com.sportradar.unifiedodds.sdk.entities.custombet.Recommendation;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EventRecommendations}
 */
public class EventRecommendationsImpl implements EventRecommendations {

    private final Urn eventId;
    private final List<Recommendation> recommendations;
    private final int providedRecommendations;
    private final String source;

    public EventRecommendationsImpl(CapiEventRecommendationsType eventRecommendations) {
        this.eventId = Urn.parse(eventRecommendations.getId());
        this.recommendations =
            eventRecommendations
                .getRecommendations()
                .stream()
                .map(RecommendationImpl::new)
                .collect(Collectors.toList());
        this.providedRecommendations = eventRecommendations.getProvidedRecommendation();
        this.source = eventRecommendations.getSource();
    }

    @Override
    public Urn getEventId() {
        return eventId;
    }

    @Override
    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    @Override
    public int getProvidedRecommendations() {
        return providedRecommendations;
    }

    @Override
    public String getSource() {
        return source;
    }
}
