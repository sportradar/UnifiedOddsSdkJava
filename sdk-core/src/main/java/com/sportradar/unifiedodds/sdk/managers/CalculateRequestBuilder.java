/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.managers;

import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;

/**
 * Builder for creating calculate probability requests that support both AND and OR selections.
 *
 * <p>Use this builder when you need to include OR groups (alternative outcomes) in a custom bet
 * request. For simple AND-only requests, the existing
 * {@link CustomBetManager#calculateProbability(java.util.List)} overload remains available.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * CalculateRequestBuilder request = manager.getCalculateRequestBuilder()
 *     .andSelection(selA)              // leg 1: AND selection
 *     .andSelection(selB)              // leg 2: AND selection
 *     .andAnyOfSelections(selC, selD)   // leg 3: any of C or D
 *     .andAnyOfSelections(selE, selF);  // leg 4: any of E or F
 *
 * Calculation result = manager.calculateProbability(request);
 * }</pre>
 */
public interface CalculateRequestBuilder {
    /**
     * Appends a single selection as an AND leg to the request.
     *
     * @param selection the {@link Selection} to add
     * @return this builder instance
     */
    CalculateRequestBuilder andSelection(Selection selection);

    /**
     * Appends an OR leg — any one of the provided selections satisfies this leg.
     *
     * @param selections the {@link Selection} instances forming the OR leg
     * @return this builder instance
     */
    CalculateRequestBuilder andAnyOfSelections(Selection... selections);
}
