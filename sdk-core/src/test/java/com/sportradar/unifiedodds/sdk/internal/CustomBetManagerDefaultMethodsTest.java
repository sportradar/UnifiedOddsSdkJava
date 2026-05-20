/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.managers.CalculateRequestBuilder;
import com.sportradar.unifiedodds.sdk.managers.CustomBetManager;
import com.sportradar.unifiedodds.sdk.managers.CustomBetSelectionBuilder;
import com.sportradar.utils.Urn;
import java.util.List;
import org.junit.jupiter.api.Test;

class CustomBetManagerDefaultMethodsTest {

    @SuppressWarnings("AnonInnerLength")
    private final CustomBetManager minimalImplementation = new CustomBetManager() {
        @Override
        public CustomBetSelectionBuilder getCustomBetSelectionBuilder() {
            return null;
        }

        @Override
        public AvailableSelections getAvailableSelections(Urn eventId) {
            return null;
        }

        @Override
        public Calculation calculateProbability(List<Selection> selections) {
            return null;
        }

        @Override
        public CalculationFilter calculateProbabilityFilter(List<Selection> selections) {
            return null;
        }
    };

    @Test
    void getPrebuiltBetsRequestBuilderThrowsUnsupportedOperationException() {
        assertThatThrownBy(minimalImplementation::getPrebuiltBetsRequestBuilder)
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getPrebuiltBetsThrowsUnsupportedOperationException() {
        assertThatThrownBy(() -> minimalImplementation.getPrebuiltBets(null))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getCalculateRequestBuilderThrowsUnsupportedOperationException() {
        assertThatThrownBy(minimalImplementation::getCalculateRequestBuilder)
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void calculateProbabilityForRequestBuilderThrowsUnsupportedOperationException() {
        assertThatThrownBy(() -> minimalImplementation.calculateProbability(new NoOpCalculateRequestBuilder())
            )
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void calculateProbabilityFilterForRequestBuilderThrowsUnsupportedOperationException() {
        assertThatThrownBy(() ->
                minimalImplementation.calculateProbabilityFilter(new NoOpCalculateRequestBuilder())
            )
            .isInstanceOf(UnsupportedOperationException.class);
    }

    private static final class NoOpCalculateRequestBuilder implements CalculateRequestBuilder {

        @Override
        public CalculateRequestBuilder andSelection(Selection selection) {
            return null;
        }

        @Override
        public CalculateRequestBuilder andAnyOfSelections(Selection... selections) {
            return null;
        }
    }
}
