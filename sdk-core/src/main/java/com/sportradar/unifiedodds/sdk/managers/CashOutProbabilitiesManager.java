/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.managers;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.CashOutProbabilities;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used retrieve markets with probability information used for cash out
 */
@SuppressWarnings({ "LineLength" })
public interface CashOutProbabilitiesManager {
    /**
     * Returns a {@link CashOutProbabilities} instance providing the cashout probabilities for the specified event
     * (the provided data is translated in the default language)
     *
     * @param eventId the {@link Urn} identifier of the event
     * @param <T> a SportEvent inherited instance
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event
     */
    <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(Urn eventId);

    /**
     * Returns a {@link CashOutProbabilities} instance providing the cashout probabilities for the specified event
     *
     * @param eventId the {@link Urn} identifier of the event
     * @param locale the {@link Locale} in which to provide the data
     * @param <T> a SportEvent inherited instance
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event
     */
    <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(Urn eventId, Locale locale);

    /**
     * Returns a {@link CashOutProbabilities} instance providing the cashout probabilities for the specified market on the associated event
     * (the provided data is translated in the default language)
     *
     * @param eventId the {@link Urn} identifier of the event
     * @param marketId the market identifier
     * @param specifiers a {@link Map} containing market specifiers or a null reference if market has no specifiers
     * @param <T> a SportEvent inherited instance
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event/market combination
     */
    <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        int marketId,
        Map<String, String> specifiers
    );

    /**
     * Returns a {@link CashOutProbabilities} instance providing the cashout probabilities for the specified market on the associated event
     *
     * @param eventId the {@link Urn} identifier of the event
     * @param marketId the market identifier
     * @param specifiers a {@link Map} containing market specifiers or a null reference if market has no specifiers
     * @param locale the {@link Locale} in which to provide the data
     * @param <T> a SportEvent inherited instance
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event/market combination
     */
    <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        int marketId,
        Map<String, String> specifiers,
        Locale locale
    );
}
