/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.unifiedodds.sdk.entities.custombet.CalculationFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.managers.CustomBetManager;
import com.sportradar.unifiedodds.sdk.managers.CustomBetSelectionBuilder;
import com.sportradar.utils.Urn;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of the {@link CustomBetManager}
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName" })
public class CustomBetManagerImpl implements CustomBetManager {

    private static final Logger executionLogger = LoggerFactory.getLogger(CustomBetManagerImpl.class);
    private static final Logger clientInteractionLogger = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkClientInteractionLog.class
    );
    private final DataRouterManager dataRouterManager;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    CustomBetManagerImpl(DataRouterManager dataRouterManager, SdkInternalConfiguration configuration) {
        Preconditions.checkNotNull(dataRouterManager, "dataRouterManager");
        Preconditions.checkNotNull(configuration, "configuration");

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = configuration.getExceptionHandlingStrategy();
    }

    @Override
    public CustomBetSelectionBuilder getCustomBetSelectionBuilder() {
        return new CustomBetSelectionBuilderImpl();
    }

    @Override
    @SuppressWarnings("IllegalCatch")
    public AvailableSelections getAvailableSelections(Urn eventId) throws CommunicationException {
        Preconditions.checkNotNull(eventId);
        clientInteractionLogger.info("CustomBetManager.getAvailableSelections({})", eventId);

        try {
            return dataRouterManager.requestAvailableSelections(eventId);
        } catch (RuntimeException e) {
            return handleException("Event[" + eventId.toString() + "] get available selections failed", e);
        } catch (CommunicationException e) {
            return handleException("Event[" + eventId.toString() + "] get available selections failed", e);
        }
    }

    @Override
    @SuppressWarnings("IllegalCatch")
    public Calculation calculateProbability(List<Selection> selections) throws CommunicationException {
        Preconditions.checkNotNull(selections);

        clientInteractionLogger.info("CustomBetManager.calculateProbability()");

        try {
            return dataRouterManager.requestCalculateProbability(selections);
        } catch (CommunicationException e) {
            return handleException("Calculating probabilities failed", e);
        } catch (RuntimeException e) {
            return handleException("Calculating probabilities failed", e);
        }
    }

    @Override
    @SuppressWarnings("IllegalCatch")
    public CalculationFilter calculateProbabilityFilter(List<Selection> selections)
        throws CommunicationException {
        Preconditions.checkNotNull(selections);

        clientInteractionLogger.info("CustomBetManager.calculateProbabilityFilter()");

        try {
            return dataRouterManager.requestCalculateProbabilityFilter(selections);
        } catch (CommunicationException e) {
            return handleException("Calculating probabilities (filtered) failed", e);
        } catch (RuntimeException e) {
            return handleException("Calculating probabilities (filtered) failed", e);
        }
    }

    private <T> T handleException(String message, CommunicationException e) throws CommunicationException {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Catch) {
            executionLogger.warn(message, e);
            return null;
        } else {
            throw e;
        }
    }

    private <T> T handleException(String message, RuntimeException e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Catch) {
            executionLogger.warn(message, e);
            return null;
        } else {
            throw e;
        }
    }
}
