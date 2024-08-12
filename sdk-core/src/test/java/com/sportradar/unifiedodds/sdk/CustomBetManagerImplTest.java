/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.CalculationFilter;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.ArrayList;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CustomBetManagerImplTest {

    public static final String DISTINCT_TYPES_OF_EXCEPTION =
        "com.sportradar.unifiedodds.sdk.CustomBetManagerImplTest#distinctTypesOfExceptions";

    @Nested
    public class CustomBetManagerImplConstruction {

        @Test
        public void shouldNotBeCreatedWithNullArguments() {
            assertThatNullPointerException()
                .isThrownBy(() -> new CustomBetManagerImpl(null, mock(SdkInternalConfiguration.class)))
                .withMessage("dataRouterManager");
            assertThatNullPointerException()
                .isThrownBy(() -> new CustomBetManagerImpl(mock(DataRouterManager.class), null))
                .withMessage("configuration");
        }
    }

    @Nested
    public class RequestingAvailableSelection {

        private final Urn anyUrn = Urns.SportEvents.getForAnyMatch();
        private DataRouterManager dataRouterManager = mock(DataRouterManager.class);
        private SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);

        @Test
        public void shouldRequireNonNullUrn() {
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatNullPointerException().isThrownBy(() -> customBetManager.getAvailableSelections(null));
        }

        @Test
        public void shouldReturnSelections() throws CommunicationException {
            val selections = mock(AvailableSelections.class);
            when(dataRouterManager.requestAvailableSelections(any())).thenReturn(selections);
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertEquals(selections, customBetManager.getAvailableSelections(anyUrn));
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrow(final Class exceptionType)
            throws CommunicationException {
            when(dataRouterManager.requestAvailableSelections(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatThrownBy(() -> customBetManager.getAvailableSelections(anyUrn))
                .isInstanceOf(exceptionType);
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(
            final Class exceptionType
        ) throws CommunicationException {
            when(dataRouterManager.requestAvailableSelections(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Catch);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertNull(customBetManager.getAvailableSelections(anyUrn));
        }
    }

    @Nested
    public class RequestingProbabilitiesCalculation {

        private final List<Selection> anySelection = new ArrayList<>();
        private DataRouterManager dataRouterManager = mock(DataRouterManager.class);
        private SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);

        @Test
        public void shouldRequireNonNullSelection() {
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatNullPointerException().isThrownBy(() -> customBetManager.calculateProbability(null));
        }

        @Test
        public void shouldPassThroughCalculation() throws CommunicationException {
            val calculation = mock(Calculation.class);
            when(dataRouterManager.requestCalculateProbability(any())).thenReturn(calculation);
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertEquals(calculation, customBetManager.calculateProbability(anySelection));
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrow(final Class exceptionType)
            throws CommunicationException {
            when(dataRouterManager.requestCalculateProbability(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatThrownBy(() -> customBetManager.calculateProbability(anySelection))
                .isInstanceOf(exceptionType);
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(
            final Class exceptionType
        ) throws CommunicationException {
            when(dataRouterManager.requestCalculateProbability(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Catch);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertNull(customBetManager.calculateProbability(anySelection));
        }
    }

    @Nested
    public class RequestingProbabilitiesCalculationMeanwhileClientIsFilteringOutcomes {

        private final List<Selection> anySelection = new ArrayList<>();
        private DataRouterManager dataRouterManager = mock(DataRouterManager.class);
        private SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);

        @Test
        public void shouldRequireNonNullSelection() {
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatNullPointerException()
                .isThrownBy(() -> customBetManager.calculateProbabilityFilter(null));
        }

        @Test
        public void shouldPassThroughCalculation() throws CommunicationException {
            val calculation = mock(CalculationFilter.class);
            when(dataRouterManager.requestCalculateProbabilityFilter(any())).thenReturn(calculation);
            val customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertEquals(calculation, customBetManager.calculateProbabilityFilter(anySelection));
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrow(final Class exceptionType)
            throws CommunicationException {
            when(dataRouterManager.requestCalculateProbabilityFilter(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertThatThrownBy(() -> customBetManager.calculateProbabilityFilter(anySelection))
                .isInstanceOf(exceptionType);
        }

        @ParameterizedTest
        @MethodSource(DISTINCT_TYPES_OF_EXCEPTION)
        public void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(
            final Class exceptionType
        ) throws CommunicationException {
            when(dataRouterManager.requestCalculateProbabilityFilter(any())).thenThrow(exceptionType);
            when(config.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Catch);
            final CustomBetManagerImpl customBetManager = new CustomBetManagerImpl(dataRouterManager, config);

            assertNull(customBetManager.calculateProbabilityFilter(anySelection));
        }
    }

    public static Object[] distinctTypesOfExceptions() {
        return new Object[] { RuntimeException.class, CommunicationException.class };
    }
}
