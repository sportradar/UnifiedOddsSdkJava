/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class OutcomeSettlementDefaultMethodsTest {

    @SuppressWarnings("AnonInnerLength")
    private final OutcomeSettlement minimalImplementation = new OutcomeSettlement() {
        @Override
        public String getId() {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getName(Locale locale) {
            return null;
        }

        @Override
        public OutcomeDefinition getOutcomeDefinition() {
            return null;
        }

        @Override
        public double getVoidFactor() {
            return 0;
        }

        @Override
        public double getDeadHeatFactor() {
            return 0;
        }

        @Override
        public OutcomeResult getOutcomeResult() {
            return null;
        }
    };

    @Test
    void getEachWayResultReturnsNullByDefault() {
        assertThat(minimalImplementation.getEachWayResult()).isNull();
    }

    @Test
    void getEachWayFactorReturnsNullByDefault() {
        assertThat(minimalImplementation.getEachWayFactor()).isNull();
    }

    @Test
    void getDeadHeatFactorPlaceReturnsNullByDefault() {
        assertThat(minimalImplementation.getDeadHeatFactorPlace()).isNull();
    }
}
