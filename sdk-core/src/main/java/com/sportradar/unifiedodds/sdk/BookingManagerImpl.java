/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of the {@link BookingManager}
 */
@SuppressWarnings({ "ConstantName" })
public class BookingManagerImpl implements BookingManager {

    private static final Logger executionLogger = LoggerFactory.getLogger(BookingManagerImpl.class);
    private static final Logger clientInteractionLogger = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkClientInteractionLog.class
    );
    private final HttpHelper httpHelper;
    private final SdkInternalConfiguration configuration;
    private final SportEventCache sportEventCache;

    @Inject
    BookingManagerImpl(
        SportEventCache sportEventCache,
        SdkInternalConfiguration configuration,
        HttpHelper httpHelper
    ) {
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(httpHelper);

        this.sportEventCache = sportEventCache;
        this.configuration = configuration;
        this.httpHelper = httpHelper;
    }

    /**
     * Performs a request on the API which books the event associated with the provided {@link Urn} identifier
     *
     * @param eventId the {@link Urn} identifier of the event which needs to be booked
     * @return <code>true</code> if the booking was successful; otherwise <code>false</code>
     */
    @Override
    public boolean bookLiveOddsEvent(Urn eventId) throws CommunicationException {
        Preconditions.checkNotNull(eventId);

        clientInteractionLogger.info("BookingManager.bookLiveOddsEvent({})", eventId);

        String requestUri = String.format(
            "https://%s/v1/liveodds/booking-calendar/events/%s/book",
            configuration.getApiHostAndPort(),
            eventId
        );

        try {
            HttpHelper.ResponseData post = httpHelper.post(requestUri);
            clientInteractionLogger.info(
                "BookingManager.bookLiveOddsEvent({}) - completed, status: {}, message: {}",
                eventId,
                post.getStatusCode(),
                post.getMessage()
            );
            if (post.isSuccessful()) {
                sportEventCache.onEventBooked(eventId);
                return true;
            }
            String filteredResponse = SdkHelper.extractHttpResponseMessage(post.getMessage());
            executionLogger.warn(
                "Event[{}] booking failed. API response code: '{}', message: '{}'",
                eventId,
                post.getStatusCode(),
                filteredResponse
            );
            throw new CommunicationException(
                String.format("Event[%s] booking failed.", eventId),
                requestUri,
                post.getStatusCode()
            );
        } catch (CommunicationException e) {
            String msg = String.format("Event[%s] booking failed, ex: %s", eventId, e.getMessage());
            executionLogger.warn(msg, e);
            if (configuration.getExceptionHandlingStrategy() == ExceptionHandlingStrategy.Throw) {
                throw e;
            }
        }

        return false;
    }
}
