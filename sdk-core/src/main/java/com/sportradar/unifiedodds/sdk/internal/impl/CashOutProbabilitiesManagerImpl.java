/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.UfCashout;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.MessageTimestampImpl;
import com.sportradar.unifiedodds.sdk.managers.CashOutProbabilitiesManager;
import com.sportradar.unifiedodds.sdk.oddsentities.CashOutProbabilities;
import com.sportradar.utils.Urn;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements methods used to access sport event/market probabilities
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "LineLength" })
public class CashOutProbabilitiesManagerImpl implements CashOutProbabilitiesManager {

    /**
     * The logger instance used to log possible execution problems
     */
    private static final Logger logger = LoggerFactory.getLogger(CashOutProbabilitiesManagerImpl.class);

    /**
     * A {@link DataProvider} instance used to fetch various CashOut data
     */
    private final DataProvider<UfCashout> cashoutDataProvider;

    /**
     * A {@link SportEntityFactory} used to build sport events
     */
    private final SportEntityFactory sportEntityFactory;

    /**
     * The {@link FeedMessageFactory} instance used to build return messages
     */
    private final FeedMessageFactory feedMessageFactory;

    /**
     * The configured default locale
     */
    private final Locale defaultLocale;

    /**
     * A {@link List} of default {@link Locale}s
     */
    private final List<Locale> defaultLocales;

    /**
     * The {@link ExceptionHandlingStrategy} that the implementation should follow
     */
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    /**
     * Construct a new {@link CashOutProbabilitiesManagerImpl} instance with the provided data
     *
     * @param cashoutDataProvider a {@link DataProvider} instance used to fetch CashOut information
     * @param feedMessageFactory the factory used to build return objects
     * @param sportEntityFactory the factory used to build sport events
     * @param configuration the SDK internal configuration
     */
    @Inject
    CashOutProbabilitiesManagerImpl(
        DataProvider<UfCashout> cashoutDataProvider,
        FeedMessageFactory feedMessageFactory,
        SportEntityFactory sportEntityFactory,
        SdkInternalConfiguration configuration
    ) {
        Preconditions.checkNotNull(cashoutDataProvider);
        Preconditions.checkNotNull(feedMessageFactory);
        Preconditions.checkNotNull(sportEntityFactory);
        Preconditions.checkNotNull(configuration);

        this.cashoutDataProvider = cashoutDataProvider;
        this.feedMessageFactory = feedMessageFactory;
        this.sportEntityFactory = sportEntityFactory;
        this.defaultLocale = configuration.getDefaultLocale();
        this.defaultLocales = configuration.getDesiredLocales();
        this.exceptionHandlingStrategy = configuration.getExceptionHandlingStrategy();
    }

    /**
     * Returns a {@link CashOutProbabilities} instance providing the CashOut probabilities for the specified event
     * (the provided data is translated in the default language)
     *
     * @param eventId the {@link Urn} identifier of the event
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event
     */
    @Override
    public <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(Urn eventId) {
        Preconditions.checkNotNull(eventId);

        return getCashOutProbabilities(eventId, defaultLocale);
    }

    /**
     * Returns a {@link CashOutProbabilities} instance providing the CashOut probabilities for the specified event
     *
     * @param eventId the {@link Urn} identifier of the event
     * @param locale  the {@link Locale} in which to provide the data
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event
     */
    @Override
    public <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        Locale locale
    ) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkNotNull(locale);

        return getCashOutProbabilities(eventId, eventId.toString(), locale);
    }

    /**
     * Returns a {@link CashOutProbabilities} instance providing the CashOut probabilities for the specified market on the associated event
     * (the provided data is translated in the default language)
     *
     * @param eventId    the {@link Urn} identifier of the event
     * @param marketId   the market identifier
     * @param specifiers a {@link Map} containing market specifiers or a null reference if market has no specifiers
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event/market combination
     */
    @Override
    public <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        int marketId,
        Map<String, String> specifiers
    ) {
        Preconditions.checkNotNull(eventId);

        return getCashOutProbabilities(eventId, marketId, specifiers, defaultLocale);
    }

    /**
     * Returns a {@link CashOutProbabilities} instance providing the CashOut probabilities for the specified market on the associated event
     *
     * @param eventId    the {@link Urn} identifier of the event
     * @param marketId   the market identifier
     * @param specifiers a {@link Map} containing market specifiers or a null reference if market has no specifiers
     * @param locale     the {@link Locale} in which to provide the data
     * @return a {@link CashOutProbabilities} providing the probabilities of the associated event/market combination
     */
    @Override
    public <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        int marketId,
        Map<String, String> specifiers,
        Locale locale
    ) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkNotNull(locale);

        String param = eventId.toString() + "/" + marketId;

        if (specifiers != null && !specifiers.isEmpty()) {
            StringJoiner sj = new StringJoiner("|");
            specifiers.forEach((key, value) -> sj.add(key + "=" + value));

            param = param + "/" + sj.toString();
        }

        return getCashOutProbabilities(eventId, param, locale);
    }

    /**
     * Builds the requested instance with the provided data
     *
     * @param param the request parameter
     * @param locale the locale in which the data should be provided
     * @return a {@link CashOutProbabilities} providing the probabilities for the provided param
     */
    private <T extends SportEvent> CashOutProbabilities<T> getCashOutProbabilities(
        Urn eventId,
        String param,
        Locale locale
    ) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(param));

        UfCashout cashoutData;
        try {
            cashoutData = cashoutDataProvider.getData((Locale) null, param);
        } catch (DataProviderException e) {
            return handleException("Error providing CashOutProbabilities for '" + param + "'", e);
        }

        if (cashoutData == null) {
            return null;
        }

        T sportEvent;
        try {
            sportEvent =
                provideSportEvent(
                    eventId,
                    locale != null ? Collections.singletonList(locale) : defaultLocales
                );
        } catch (com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException e) {
            return handleException("Sport event data could not be found - " + eventId, e);
        }

        return feedMessageFactory.buildCashOutProbabilities(
            sportEvent,
            cashoutData,
            new MessageTimestampImpl(new TimeUtilsImpl().now())
        );
    }

    @SuppressWarnings("unchecked")
    private <T extends SportEvent> T provideSportEvent(Urn eventId, List<Locale> dataLocales)
        throws com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException {
        Preconditions.checkNotNull(eventId);

        return (T) sportEntityFactory.buildSportEvent(eventId, dataLocales, true);
    }

    private <T> T handleException(String message, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Catch) {
            logger.warn(message, e);
            return null;
        }
        throw new ObjectNotFoundException(message, e);
    }
}
