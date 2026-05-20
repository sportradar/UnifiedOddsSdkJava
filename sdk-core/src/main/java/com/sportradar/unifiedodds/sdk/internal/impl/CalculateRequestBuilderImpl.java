/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.managers.CalculateRequestBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of {@link CalculateRequestBuilder}.
 *
 * <p>Maintains an ordered list of items (AND selections and OR groups) in insertion order.
 * Used internally by {@link CustomBetManagerImpl} to build calculate probability requests.
 */
public class CalculateRequestBuilderImpl implements CalculateRequestBuilder {

    private final List<Object> items = new ArrayList<>();

    @Override
    public CalculateRequestBuilder andSelection(Selection selection) {
        items.add(selection);
        return this;
    }

    @Override
    public CalculateRequestBuilder andAnyOfSelections(Selection... selections) {
        items.add(Collections.unmodifiableList(Arrays.asList(selections)));
        return this;
    }

    /**
     * Returns the ordered list of items.
     *
     * <p>Each item is either a {@link Selection} (AND leg) or a
     * {@code List<Selection>} (OR group).
     *
     * @return unmodifiable view of the items list
     */
    public List<Object> getItems() {
        return Collections.unmodifiableList(items);
    }
}
