/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The basic implementation of the {@link CustomBetManager}
 */
public class CustomBetManagerImpl implements CustomBetManager {
    private static final Logger executionLogger = LoggerFactory.getLogger(CustomBetManagerImpl.class);
    private static final Logger clientInteractionLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);
    private final DataRouterManager dataRouterManager;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final CustomBetSelectionBuilder customBetSelectionBuilder;

    @Inject
    CustomBetManagerImpl(DataRouterManager dataRouterManager, SDKInternalConfiguration configuration, CustomBetSelectionBuilder customBetSelectionBuilder) {
        Preconditions.checkNotNull(dataRouterManager);
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(customBetSelectionBuilder);

        this.dataRouterManager = dataRouterManager;
        this.exceptionHandlingStrategy = configuration.getExceptionHandlingStrategy();
        this.customBetSelectionBuilder = customBetSelectionBuilder;
    }

    @Override
    public AvailableSelections getAvailableSelections(URN eventId) {
        Preconditions.checkNotNull(eventId);

        clientInteractionLogger.info("CustomBetManager.getAvailableSelections({})", eventId);

        try {
            return dataRouterManager.requestAvailableSelections(eventId);
        } catch (CommunicationException e) {
            return handleException("Event[" + eventId.toString() + "] get available selections failed", e);
        }
    }

    @Override
    public Calculation calculateProbability(List<Selection> selections) {
        Preconditions.checkNotNull(selections);

        clientInteractionLogger.info("CustomBetManager.calculateProbability()");

        try {
            return dataRouterManager.requestCalculateProbability(selections);
        } catch (CommunicationException e) {
            return handleException("Calculating probabilities failed", e);
        }
    }

    @Override
    public CustomBetSelectionBuilder getCustomBetSelectionBuilder() {
        return customBetSelectionBuilder;
    }

    private <T> T handleException(String message, Exception e) {
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Catch) {
            executionLogger.warn(message, e);
            return null;
        }
        throw new ObjectNotFoundException(message, e);
    }
}
