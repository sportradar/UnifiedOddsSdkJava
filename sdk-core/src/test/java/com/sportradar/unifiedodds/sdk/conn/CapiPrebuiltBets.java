/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.custombet.datamodel.CapiEventRecommendationsType;
import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBets;
import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBetsSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiRecommendationsType;
import com.sportradar.utils.Urn;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public final class CapiPrebuiltBets {

    public static final String EVENT_ID = "sr:match:61073663";
    public static final String SECOND_EVENT_ID = "sr:match:36677016";
    public static final int REQUESTED_RECOMMENDATIONS = 4;
    public static final int PROVIDED_RECOMMENDATIONS = 4;
    public static final String GENERATED_AT = "2024-10-01T15:50:00+00:00";

    private CapiPrebuiltBets() {}

    public static CapiPreBuiltBets prebuiltBets() {
        return prebuiltBetsFor(Urn.parse(EVENT_ID));
    }

    public static CapiPreBuiltBets prebuiltBetsForTwoEvents() {
        CapiPreBuiltBets preBuiltBets = new CapiPreBuiltBets();
        preBuiltBets.setRequestedRecommendations(REQUESTED_RECOMMENDATIONS);
        preBuiltBets.setGeneratedAt(GENERATED_AT);
        preBuiltBets.getEvents().add(eventRecommendations(Urn.parse(EVENT_ID)));
        preBuiltBets.getEvents().add(eventRecommendations(Urn.parse(SECOND_EVENT_ID)));
        return preBuiltBets;
    }

    public static CapiPreBuiltBets prebuiltBetsFor(Urn sportEventId) {
        CapiPreBuiltBets preBuiltBets = new CapiPreBuiltBets();
        preBuiltBets.setRequestedRecommendations(REQUESTED_RECOMMENDATIONS);
        preBuiltBets.setGeneratedAt(GENERATED_AT);
        preBuiltBets.getEvents().add(eventRecommendations(sportEventId));
        return preBuiltBets;
    }

    private static CapiEventRecommendationsType eventRecommendations(Urn sportEventId) {
        CapiEventRecommendationsType event = new CapiEventRecommendationsType();
        event.setId(sportEventId.toString());
        event.setProvidedRecommendation(PROVIDED_RECOMMENDATIONS);
        event.getRecommendations().add(firstRecommendation());
        event.getRecommendations().add(secondRecommendation());
        event.getRecommendations().add(thirdRecommendation());
        event.getRecommendations().add(fourthRecommendation());
        return event;
    }

    private static CapiRecommendationsType firstRecommendation() {
        CapiRecommendationsType recommendation = new CapiRecommendationsType();
        recommendation.setOdds(1.3311019050614308);
        recommendation.setProbability(0.7075527003667736);
        recommendation.getSelections().add(selectionMarketOutcome(10, "10"));
        recommendation.getSelections().add(selectionMarketOutcomeSpecifiers(92, "13", "total=2.5"));
        return recommendation;
    }

    private static CapiRecommendationsType secondRecommendation() {
        CapiRecommendationsType recommendation = new CapiRecommendationsType();
        recommendation.setOdds(3.371573563327487);
        recommendation.setProbability(0.24958249698146445);
        recommendation.getSelections().add(selectionMarketOutcome(1, "1"));
        recommendation.getSelections().add(selectionMarketOutcomeSpecifiers(91, "13", "total=1.5"));
        return recommendation;
    }

    private static CapiRecommendationsType thirdRecommendation() {
        CapiRecommendationsType recommendation = new CapiRecommendationsType();
        recommendation.setOdds(1.3400922471341583);
        recommendation.setProbability(0.7021926051218123);
        recommendation.getSelections().add(selectionMarketOutcome(10, "10"));
        recommendation.getSelections().add(selectionMarketOutcomeSpecifiers(69, "13", "total=2.5"));
        return recommendation;
    }

    private static CapiRecommendationsType fourthRecommendation() {
        CapiRecommendationsType recommendation = new CapiRecommendationsType();
        recommendation.setOdds(10.05972221462805);
        recommendation.setProbability(0.07174612914185095);
        recommendation.getSelections().add(selectionMarketOutcome(1, "2"));
        recommendation.getSelections().add(selectionMarketOutcome(75, "74"));
        return recommendation;
    }

    private static CapiPreBuiltBetsSelectionType selectionMarketOutcome(int marketId, String outcomeId) {
        return selectionMarketOutcomeSpecifiers(marketId, outcomeId, null);
    }

    private static CapiPreBuiltBetsSelectionType selectionMarketOutcomeSpecifiers(
        int marketId,
        String outcomeId,
        String specifiers
    ) {
        CapiPreBuiltBetsSelectionType selection = new CapiPreBuiltBetsSelectionType();
        selection.setMarketId(marketId);
        selection.setOutcomeId(outcomeId);
        selection.setSpecifiers(specifiers);
        return selection;
    }
}
