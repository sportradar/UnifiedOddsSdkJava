/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.sportradar.uf.custombet.datamodel.CapiOrSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiSelections;
import com.sportradar.unifiedodds.sdk.conn.JaxbContexts;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CapiSelectionsAssert extends AbstractAssert<CapiSelectionsAssert, CapiSelections> {

    private CapiSelectionsAssert(CapiSelections actual) {
        super(actual, CapiSelectionsAssert.class);
    }

    public static CapiSelectionsAssert assertThat(CapiSelections actual) {
        return new CapiSelectionsAssert(actual);
    }

    public CapiSelectionsAssert isEqualTo(CapiSelections expected) {
        Assertions.assertThat(toXml(actual)).isEqualTo(toXml(expected));
        return this;
    }

    public static CapiSelections capiSelections(Object... items) {
        CapiSelections selections = new CapiSelections();
        for (Object item : items) {
            selections.getSelectionsAndOrSelections().add(item);
        }
        return selections;
    }

    public static CapiSelectionType capiSelectionFrom(Selection sel) {
        CapiSelectionType s = new CapiSelectionType();
        s.setId(sel.getEventId().toString());
        s.setMarketId(sel.getMarketId());
        s.setOutcomeId(sel.getOutcomeId());
        s.setSpecifiers(sel.getSpecifiers());
        s.setOdds(sel.getOdds());
        return s;
    }

    public static CapiOrSelectionType capiOrSelectionFrom(Selection... selections) {
        CapiOrSelectionType orType = new CapiOrSelectionType();
        for (Selection sel : selections) {
            orType.getSelections().add(capiSelectionFrom(sel));
        }
        return orType;
    }

    private static String toXml(CapiSelections obj) {
        return JaxbContexts.CustomBetApi.marshall(obj);
    }
}
