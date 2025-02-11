/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.assertj.core.api.Assertions.assertThatNullPointerException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.unifiedodds.sdk.CapiCustomBet;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.managers.CustomBetSelectionBuilder;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings({ "MagicNumber" })
public class CustomBetManagerImplTest {

    private DataProvider<CapiAvailableSelections> availableSelectionsDataProvider;
    private DataProvider<CapiCalculationResponse> calculateDataProvider;
    private DataProvider<CapiFilteredCalculationResponse> calculateFilterDataProvider;
    private CustomBetManagerImpl customBetManagerThrow;
    private CustomBetManagerImpl customBetManagerCatch;
    private Urn eventId;
    private List<Selection> selections;

    @BeforeEach
    void setUp() {
        availableSelectionsDataProvider = mock(DataProvider.class);
        calculateDataProvider = mock(DataProvider.class);
        calculateFilterDataProvider = mock(DataProvider.class);

        List<Locale> desiredLocales = new ArrayList<Locale>();
        desiredLocales.add(Locale.ENGLISH);
        SdkInternalConfiguration configThrow = mock(SdkInternalConfiguration.class);
        when(configThrow.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        when(configThrow.getDesiredLocales()).thenReturn(desiredLocales);
        SdkInternalConfiguration configCatch = mock(SdkInternalConfiguration.class);
        when(configCatch.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Catch);
        when(configCatch.getDesiredLocales()).thenReturn(desiredLocales);
        DataRouterImpl dataRouter = new DataRouterImpl();
        dataRouter.setDataListeners(new ArrayList<>());
        DataRouterManager dataRouterManager = new DataRouterManagerBuilder()
            .withCbAvailableSelections(availableSelectionsDataProvider)
            .withCbCalculation(calculateDataProvider)
            .withCbCalculationFilter(calculateFilterDataProvider)
            .with(dataRouter)
            .build();
        customBetManagerThrow = new CustomBetManagerImpl(dataRouterManager, configThrow);
        customBetManagerCatch = new CustomBetManagerImpl(dataRouterManager, configCatch);
        CustomBetSelectionBuilder customBetSelectionBuilder = new CustomBetSelectionBuilderImpl();

        eventId = Urn.parse("sr:match:1000");
        val selection = customBetSelectionBuilder
            .setEventId(eventId)
            .setMarketId(1)
            .setOutcomeId("2")
            .setSpecifiers("specifier=value")
            .setOdds(1.5)
            .build();
        selections = new ArrayList<Selection>();
        selections.add(selection);
    }

    @Nested
    class CustomBetManagerImplConstruction {

        @Test
        void shouldNotBeCreatedWithNullDataRouterManager() {
            assertThatNullPointerException()
                .isThrownBy(() -> new CustomBetManagerImpl(null, mock(SdkInternalConfiguration.class)))
                .withMessage("dataRouterManager");
        }

        @Test
        void shouldNotBeCreatedWithNullConfiguration() {
            assertThatNullPointerException()
                .isThrownBy(() -> new CustomBetManagerImpl(mock(DataRouterManager.class), null))
                .withMessage("configuration");
        }
    }

    @Nested
    class RequestingAvailableSelection {

        private final Urn anyUrn = Urns.SportEvents.getForAnyMatch();

        @Test
        void shouldRequireNonNullUrnForThrow() {
            assertThatNullPointerException()
                .isThrownBy(() -> customBetManagerThrow.getAvailableSelections(null));
        }

        @Test
        void shouldRequireNonNullUrnForCatch() {
            assertThatNullPointerException()
                .isThrownBy(() -> customBetManagerCatch.getAvailableSelections(null));
        }

        @Test
        void shouldReturnSelections() throws CommunicationException, DataProviderException {
            val capiSelections = CapiCustomBet.getAvailableSelectionsResponse(Urn.parse("sr:match:1000"), 10);
            when(availableSelectionsDataProvider.getData(capiSelections.getEvent().getId()))
                .thenReturn(capiSelections);

            val availableSelections = customBetManagerThrow.getAvailableSelections(
                Urn.parse(capiSelections.getEvent().getId())
            );

            Assertions.assertNotNull(availableSelections);
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForRuntimeException()
            throws DataProviderException {
            when(availableSelectionsDataProvider.getData(anyString())).thenThrow(RuntimeException.class);

            assertThatThrownBy(() -> customBetManagerThrow.getAvailableSelections(anyUrn))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForDataProviderException()
            throws DataProviderException {
            when(availableSelectionsDataProvider.getData(anyString())).thenThrow(DataProviderException.class);

            assertThatThrownBy(() -> customBetManagerThrow.getAvailableSelections(anyUrn))
                .isInstanceOf(CommunicationException.class);
        }

        @ParameterizedTest
        @ValueSource(classes = { RuntimeException.class, DataProviderException.class })
        void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(final Class exceptionType)
            throws DataProviderException, CommunicationException {
            when(availableSelectionsDataProvider.getData(anyString())).thenThrow(exceptionType);

            Assertions.assertNull(customBetManagerCatch.getAvailableSelections(anyUrn));
        }
    }

    @Nested
    class RequestingProbabilitiesCalculation {

        @Test
        void shouldRequireNonNullSelection() {
            assertThatNullPointerException()
                .isThrownBy(() -> customBetManagerThrow.calculateProbability(null));
        }

        @Test
        void shouldPassThroughCalculation() throws CommunicationException, DataProviderException {
            val calculation = CapiCustomBet.getCalculationResponse(eventId, 10);
            when(calculateDataProvider.postData(any())).thenReturn(calculation);

            val calculationResponse = customBetManagerThrow.calculateProbability(selections);

            Assertions.assertNotNull(calculationResponse);
            Assertions.assertEquals(calculation.getCalculation().getOdds(), calculationResponse.getOdds());
            Assertions.assertEquals(
                calculation.getCalculation().getProbability(),
                calculationResponse.getProbability()
            );
            Assertions.assertEquals(
                calculation.getCalculation().isHarmonization(),
                calculationResponse.isHarmonization()
            );
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForRuntimeException()
            throws DataProviderException {
            when(calculateDataProvider.postData(any())).thenThrow(RuntimeException.class);

            assertThatThrownBy(() -> customBetManagerThrow.calculateProbability(selections))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForDataProviderException()
            throws DataProviderException {
            when(calculateDataProvider.postData(any())).thenThrow(DataProviderException.class);

            assertThatThrownBy(() -> customBetManagerThrow.calculateProbability(selections))
                .isInstanceOf(CommunicationException.class);
        }

        @ParameterizedTest
        @ValueSource(classes = { RuntimeException.class, DataProviderException.class })
        void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(final Class exceptionType)
            throws CommunicationException, DataProviderException {
            when(calculateDataProvider.postData(any())).thenThrow(exceptionType);

            Assertions.assertNull(customBetManagerCatch.calculateProbability(selections));
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        @NullSource
        void shouldPassThroughCalculationWithHarmonization(final Boolean harmonization)
            throws CommunicationException, DataProviderException {
            val calculation = CapiCustomBet.getCalculationResponse(eventId, 10);
            calculation.getCalculation().setHarmonization(harmonization);
            when(calculateDataProvider.postData(any())).thenReturn(calculation);

            val calculationResponse = customBetManagerThrow.calculateProbability(selections);

            Assertions.assertEquals(harmonization, calculationResponse.isHarmonization());
        }
    }

    @Nested
    class RequestingProbabilitiesCalculationFilter {

        @Test
        void shouldRequireNonNullSelection() {
            assertThatNullPointerException()
                .isThrownBy(() -> customBetManagerThrow.calculateProbabilityFilter(null));
        }

        @Test
        void shouldPassThroughCalculation() throws CommunicationException, DataProviderException {
            val calculation = CapiCustomBet.getFilteredCalculationResponse(eventId, 10);
            when(calculateFilterDataProvider.postData(any())).thenReturn(calculation);

            val calculationResponse = customBetManagerThrow.calculateProbabilityFilter(selections);

            Assertions.assertNotNull(calculationResponse);
            Assertions.assertEquals(calculation.getCalculation().getOdds(), calculationResponse.getOdds());
            Assertions.assertEquals(
                calculation.getCalculation().getProbability(),
                calculationResponse.getProbability()
            );
            Assertions.assertEquals(
                calculation.getCalculation().isHarmonization(),
                calculationResponse.isHarmonization()
            );
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForRuntimeException()
            throws DataProviderException {
            when(calculateFilterDataProvider.postData(any())).thenThrow(RuntimeException.class);

            assertThatThrownBy(() -> customBetManagerThrow.calculateProbabilityFilter(selections))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        void failingDueToExceptionShouldRethrowItWhenSdkIsConfiguredToThrowForDataProviderException()
            throws DataProviderException {
            when(calculateFilterDataProvider.postData(any())).thenThrow(DataProviderException.class);

            assertThatThrownBy(() -> customBetManagerThrow.calculateProbabilityFilter(selections))
                .isInstanceOf(CommunicationException.class);
        }

        @ParameterizedTest
        @ValueSource(classes = { RuntimeException.class, DataProviderException.class })
        void failingDueToExceptionShouldReturnNullWhenSdkIsConfiguredToCatch(final Class exceptionType)
            throws CommunicationException, DataProviderException {
            when(calculateFilterDataProvider.postData(any())).thenThrow(exceptionType);

            Assertions.assertNull(customBetManagerCatch.calculateProbabilityFilter(selections));
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        @NullSource
        void shouldPassThroughCalculationWithHarmonization(final Boolean harmonization)
            throws CommunicationException, DataProviderException {
            val calculation = CapiCustomBet.getFilteredCalculationResponse(eventId, 10);
            calculation.getCalculation().setHarmonization(harmonization);
            when(calculateFilterDataProvider.postData(any())).thenReturn(calculation);

            val calculationResponse = customBetManagerThrow.calculateProbabilityFilter(selections);

            Assertions.assertEquals(harmonization, calculationResponse.isHarmonization());
        }
    }
}
