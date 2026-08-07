/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.impl.CalculationDataProviders.*;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiFilterSelectionsAssert.capiFilterOrSelectionFrom;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiFilterSelectionsAssert.capiFilterSelectionFrom;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiFilterSelectionsAssert.capiFilterSelections;
import static com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiSelectionsAssert.*;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.CapiCustomBet;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiFilterSelectionsAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.CapiSelectionsAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.internal.impl.CustomBetManagers;
import com.sportradar.unifiedodds.sdk.internal.impl.CustomBetSelectionBuilderImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.BookmakerDetailsImpl;
import com.sportradar.unifiedodds.sdk.managers.CalculateRequestBuilder;
import com.sportradar.utils.Urn;
import lombok.val;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
class CustomBetManagerOrSelectionsTest {

    private static final int ANY_BOOKMAKER_ID = 1;
    private static final Urn FIRST_EVENT_ID = Urn.parse("sr:match:1001");
    private static final Urn SECOND_EVENT_ID = Urn.parse("sr:match:1002");
    private static final Urn THIRD_EVENT_ID = Urn.parse("sr:match:1003");

    @Test
    void calculatesProbability() throws Exception {
        val capiResponse = CapiCustomBet.getCalculationResponse(FIRST_EVENT_ID, 2);
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(providing(capiResponse))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 1, "1");
        val selectionB = selection(SECOND_EVENT_ID, 2, "2");
        val selectionC = selection(THIRD_EVENT_ID, 3, "3");

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selectionA)
            .andSelection(selectionB)
            .andAnyOfSelections(selectionC);

        val result = customBetManager.calculateProbability(request);

        assertThat(result).isNotNull();
        assertThat(result.getOdds()).isEqualTo(capiResponse.getCalculation().getOdds());
        assertThat(result.getProbability()).isEqualTo(capiResponse.getCalculation().getProbability());
    }

    @Test
    void calculatesProbabilityForOrGroupsOnly() throws Exception {
        val capiResponse = CapiCustomBet.getCalculationResponse(FIRST_EVENT_ID, 1);
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(providing(capiResponse))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 1, "1");
        val selectionB = selection(SECOND_EVENT_ID, 2, "2");

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andAnyOfSelections(selectionA, selectionB);

        val result = customBetManager.calculateProbability(request);

        assertThat(result).isNotNull();
    }

    @Test
    void onAttemptToCalculatesProbabilityThrowsCommunicationExceptionOnProviderFailure() {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(failingWith("provider error"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThatThrownBy(() -> customBetManager.calculateProbability(request))
            .isInstanceOf(CommunicationException.class);
    }

    @Test
    void calculatesProbabilityAndReturnsNullWhenProviderFailsForCatchStrategy()
        throws CommunicationException {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(failingWith("provider error"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithCatchStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThat(customBetManager.calculateProbability(request)).isNull();
    }

    @Test
    void onAttemptToCalculateProbabilityWithNullBuilderThrowsNullPointerException() {
        val dataRouterManager = new DataRouterManagerBuilder().build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        assertThatThrownBy(() -> customBetManager.calculateProbability((CalculateRequestBuilder) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void calculatesProbabilityFilter() throws Exception {
        val capiResponse = CapiCustomBet.getFilteredCalculationResponse(FIRST_EVENT_ID, 2);
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(providingFilter(capiResponse))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 1, "1");
        val selectionB = selection(SECOND_EVENT_ID, 2, "2");
        val selectionC = selection(THIRD_EVENT_ID, 3, "3");

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selectionA)
            .andAnyOfSelections(selectionB, selectionC);

        val result = customBetManager.calculateProbabilityFilter(request);

        assertThat(result).isNotNull();
        assertThat(result.getOdds()).isEqualTo(capiResponse.getCalculation().getOdds());
        assertThat(result.getProbability()).isEqualTo(capiResponse.getCalculation().getProbability());
    }

    @Test
    void calculatesProbabilityFilterForOrGroupsOnly() throws Exception {
        val capiResponse = CapiCustomBet.getFilteredCalculationResponse(FIRST_EVENT_ID, 1);
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(providingFilter(capiResponse))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 1, "1");
        val selectionB = selection(SECOND_EVENT_ID, 2, "2");

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andAnyOfSelections(selectionA, selectionB);

        val result = customBetManager.calculateProbabilityFilter(request);

        assertThat(result).isNotNull();
    }

    @Test
    void onAttemptToCalculateProbabilityFilterThrowsCommunicationExceptionOnProviderFailure() {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(failingFilterWith("provider error"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThatThrownBy(() -> customBetManager.calculateProbabilityFilter(request))
            .isInstanceOf(CommunicationException.class);
    }

    @Test
    void calculatesProbabilityFilterAndReturnsNullWhenProviderFailsForCatchStrategy()
        throws CommunicationException {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(failingFilterWith("provider error"))
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithCatchStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThat(customBetManager.calculateProbabilityFilter(request)).isNull();
    }

    @Test
    void onAttemptToCalculateProbabilityFilterWithNullBuilderThrowsNullPointerException() {
        val dataRouterManager = new DataRouterManagerBuilder().build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        assertThatThrownBy(() -> customBetManager.calculateProbabilityFilter((CalculateRequestBuilder) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void calculatesProbabilityMapingSelectionToCapiSelectionType() throws Exception {
        val provider = providing(CapiCustomBet.getCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculation(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selection = selection(FIRST_EVENT_ID, 19, "1", "total=2.5");
        customBetManager.calculateProbability(
            customBetManager.getCalculateRequestBuilder().andSelection(selection)
        );

        provider.verify(body ->
            CapiSelectionsAssert
                .assertThat(body)
                .isEqualTo(capiSelections(with(capiSelectionFrom(selection))))
        );
    }

    @Test
    void calculatesProbabilityMappingOrGroupToCapiOrSelectionType() throws Exception {
        val provider = providing(CapiCustomBet.getCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculation(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 1, "1", null);
        val selectionB = selection(SECOND_EVENT_ID, 2, "2", "score=1:0");
        customBetManager.calculateProbability(
            customBetManager.getCalculateRequestBuilder().andAnyOfSelections(selectionA, selectionB)
        );

        provider.verify(body ->
            CapiSelectionsAssert
                .assertThat(body)
                .isEqualTo(capiSelections(with(capiOrSelectionFrom(selectionA, selectionB))))
        );
    }

    @Test
    void calculatesProbabilityAndPreservesInsertionOrderOfMixedAndOrItems() throws Exception {
        val provider = providing(CapiCustomBet.getCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculation(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selection = selection(FIRST_EVENT_ID, 10, "1", "total=1.5");
        val orSelection1 = selection(SECOND_EVENT_ID, 20, "2", null);
        val orSelection2 = selection(THIRD_EVENT_ID, 30, "3", "hcp=0:1");
        customBetManager.calculateProbability(
            customBetManager
                .getCalculateRequestBuilder()
                .andSelection(selection)
                .andAnyOfSelections(orSelection1, orSelection2)
        );

        provider.verify(body ->
            CapiSelectionsAssert
                .assertThat(body)
                .isEqualTo(
                    capiSelections(
                        with(capiSelectionFrom(selection)),
                        with(capiOrSelectionFrom(orSelection1, orSelection2))
                    )
                )
        );
    }

    @Test
    void calculateProbabilityFilterMapsAndSelectionToCapiFilterSelectionTypeWithMarketFields()
        throws Exception {
        val provider = providingFilter(CapiCustomBet.getFilteredCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculationFilter(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selection = selection(FIRST_EVENT_ID, 19, "1", "total=2.5");
        customBetManager.calculateProbabilityFilter(
            customBetManager.getCalculateRequestBuilder().andSelection(selection)
        );

        provider.verify(body ->
            CapiFilterSelectionsAssert
                .assertThat(body)
                .isEqualTo(capiFilterSelections(with(capiFilterSelectionFrom(selection))))
        );
    }

    @Test
    void calculatesProbabilityFilterAndMapsOrGroupToCapiFilterOrSelectionsTypeWithMarketFields()
        throws Exception {
        val provider = providingFilter(CapiCustomBet.getFilteredCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculationFilter(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selectionA = selection(FIRST_EVENT_ID, 10, "1", "score=1:0");
        val selectionB = selection(SECOND_EVENT_ID, 20, "2", null);
        customBetManager.calculateProbabilityFilter(
            customBetManager.getCalculateRequestBuilder().andAnyOfSelections(selectionA, selectionB)
        );

        provider.verify(body ->
            CapiFilterSelectionsAssert
                .assertThat(body)
                .isEqualTo(capiFilterSelections(with(capiFilterOrSelectionFrom(selectionA, selectionB))))
        );
    }

    @Test
    void calculatesProbabilityFilterAndPreservesInsertionOrderOfMixedAndOrItems() throws Exception {
        val provider = providingFilter(CapiCustomBet.getFilteredCalculationResponse(FIRST_EVENT_ID, 1));
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(new DataRouterManagerBuilder().withCbCalculationFilter(provider).build())
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        val selection = selection(FIRST_EVENT_ID, 10, "1", "total=1.5");
        val orSelection1 = selection(SECOND_EVENT_ID, 20, "2", null);
        val orSelection2 = selection(THIRD_EVENT_ID, 30, "3", "hcp=0:1");
        customBetManager.calculateProbabilityFilter(
            customBetManager
                .getCalculateRequestBuilder()
                .andSelection(selection)
                .andAnyOfSelections(orSelection1, orSelection2)
        );

        provider.verify(body ->
            CapiFilterSelectionsAssert
                .assertThat(body)
                .isEqualTo(
                    capiFilterSelections(
                        with(capiFilterSelectionFrom(selection)),
                        with(capiFilterOrSelectionFrom(orSelection1, orSelection2))
                    )
                )
        );
    }

    @Test
    void onAttemptToCalculatesProbabilityThrowsRuntimeExceptionWhenProviderThrowsOne() {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(failingWithRuntimeException())
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        CalculateRequestBuilder request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThatThrownBy(() -> customBetManager.calculateProbability(request))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void calculatesProbabilityAndReturnsNullWhenProviderThrowsRuntimeExceptionForCatchStrategy()
        throws CommunicationException {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculation(failingWithRuntimeException())
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithCatchStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        CalculateRequestBuilder request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThat(customBetManager.calculateProbability(request)).isNull();
    }

    @Test
    void onAttemptToCalculatesProbabilityFilterRethrowsRuntimeExceptionWhenProviderThrowsOne() {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(failingFilterWithRuntimeException())
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithThrowStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        CalculateRequestBuilder request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThatThrownBy(() -> customBetManager.calculateProbabilityFilter(request))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    void calculatesProbabilityFilterAndReturnsNullWhenProviderThrowsRuntimeExceptionAndCatchStrategy()
        throws CommunicationException {
        val dataRouterManager = new DataRouterManagerBuilder()
            .withCbCalculationFilter(failingFilterWithRuntimeException())
            .build();
        val customBetManager = CustomBetManagers
            .createCustomBetManager()
            .with(dataRouterManager)
            .with(configWithCatchStrategyAndBookmakerId(ANY_BOOKMAKER_ID))
            .build();

        CalculateRequestBuilder request = customBetManager
            .getCalculateRequestBuilder()
            .andSelection(selection(FIRST_EVENT_ID, 1, "1"));

        assertThat(customBetManager.calculateProbabilityFilter(request)).isNull();
    }

    private static UofConfiguration configWithThrowStrategyAndBookmakerId(int bookmakerId) {
        val config = new UofConfigurationStub();
        config.setExceptionHandlingStrategy(Throw);
        config.setBookmakerDetails(bookmakerDetailsWith(bookmakerId));
        return config;
    }

    private static UofConfiguration configWithCatchStrategyAndBookmakerId(int bookmakerId) {
        val config = new UofConfigurationStub();
        config.setExceptionHandlingStrategy(Catch);
        config.setBookmakerDetails(bookmakerDetailsWith(bookmakerId));
        return config;
    }

    private static BookmakerDetailsImpl bookmakerDetailsWith(int bookmakerId) {
        return new BookmakerDetailsImpl(bookmakerId, null, null, null, null, null);
    }

    private static Selection selection(Urn eventId, int marketId, String outcomeId) {
        return new CustomBetSelectionBuilderImpl()
            .setEventId(eventId)
            .setMarketId(marketId)
            .setOutcomeId(outcomeId)
            .build();
    }

    private static Selection selection(Urn eventId, int marketId, String outcomeId, String specifiers) {
        return new CustomBetSelectionBuilderImpl()
            .setEventId(eventId)
            .setMarketId(marketId)
            .setOutcomeId(outcomeId)
            .setSpecifiers(specifiers)
            .build();
    }
}
