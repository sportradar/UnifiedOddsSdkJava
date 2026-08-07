/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.sportradar.uf.custombet.datamodel.CapiRecommendationsType;
import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBetSelection;
import com.sportradar.unifiedodds.sdk.entities.custombet.Recommendation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link Recommendation}
 */
public class RecommendationImpl implements Recommendation {

    private final List<PrebuiltBetSelection> selections;
    private final double odds;
    private final double probability;

    public RecommendationImpl(CapiRecommendationsType recommendation) {
        this.selections =
            recommendation
                .getSelections()
                .stream()
                .map(PrebuiltBetSelectionImpl::new)
                .collect(Collectors.toList());
        this.odds = recommendation.getOdds();
        this.probability = recommendation.getProbability();
    }

    @Override
    public List<PrebuiltBetSelection> getSelections() {
        return selections;
    }

    @Override
    public double getOdds() {
        return odds;
    }

    @Override
    public double getProbability() {
        return probability;
    }
}
