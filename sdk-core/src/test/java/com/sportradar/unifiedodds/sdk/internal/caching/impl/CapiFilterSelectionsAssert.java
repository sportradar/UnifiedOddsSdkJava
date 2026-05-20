/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.sportradar.uf.custombet.datamodel.CapiFilterOrSelectionsType;
import com.sportradar.uf.custombet.datamodel.CapiFilterSelectionMarketType;
import com.sportradar.uf.custombet.datamodel.CapiFilterSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiFilterSelections;
import com.sportradar.unifiedodds.sdk.conn.JaxbContexts;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CapiFilterSelectionsAssert
    extends AbstractAssert<CapiFilterSelectionsAssert, CapiFilterSelections> {

    private CapiFilterSelectionsAssert(CapiFilterSelections actual) {
        super(actual, CapiFilterSelectionsAssert.class);
    }

    public static CapiFilterSelectionsAssert assertThat(CapiFilterSelections actual) {
        return new CapiFilterSelectionsAssert(actual);
    }

    public CapiFilterSelectionsAssert isEqualTo(CapiFilterSelections expected) {
        Assertions.assertThat(toXml(actual)).isEqualTo(toXml(expected));
        return this;
    }

    public static CapiFilterSelections capiFilterSelections(CapiFilterSelectionType... items) {
        CapiFilterSelections selections = new CapiFilterSelections();
        for (CapiFilterSelectionType item : items) {
            selections.getSelections().add(item);
        }
        return selections;
    }

    public static CapiFilterSelectionType capiFilterSelectionFrom(Selection sel) {
        CapiFilterSelectionType filterSel = new CapiFilterSelectionType();
        filterSel.setId(sel.getEventId().toString());
        filterSel.getMarketsAndOrSelections().add(filterMarket(sel));
        return filterSel;
    }

    public static CapiFilterSelectionType capiFilterOrSelectionFrom(Selection... selections) {
        CapiFilterOrSelectionsType orType = new CapiFilterOrSelectionsType();
        for (Selection sel : selections) {
            orType.getMarkets().add(filterMarket(sel));
        }
        CapiFilterSelectionType filterSel = new CapiFilterSelectionType();
        if (selections.length > 0) {
            filterSel.setId(selections[0].getEventId().toString());
        }
        filterSel.getMarketsAndOrSelections().add(orType);
        return filterSel;
    }

    private static CapiFilterSelectionMarketType filterMarket(Selection sel) {
        CapiFilterSelectionMarketType market = new CapiFilterSelectionMarketType();
        market.setMarketId(sel.getMarketId());
        market.setOutcomeId(sel.getOutcomeId());
        market.setSpecifiers(sel.getSpecifiers());
        market.setOdds(sel.getOdds());
        return market;
    }

    private static String toXml(CapiFilterSelections obj) {
        return JaxbContexts.CustomBetApi.marshall(obj);
    }
}
