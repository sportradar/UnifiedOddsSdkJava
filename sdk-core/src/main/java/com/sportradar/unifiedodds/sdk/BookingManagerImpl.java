/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The basic implementation of the {@link BookingManager}
 */
public class BookingManagerImpl implements BookingManager {
    private static final Logger executionLogger = LoggerFactory.getLogger(BookingManagerImpl.class);
    private static final Logger clientInteractionLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);
    private final HttpHelper httpHelper;
    private final SDKInternalConfiguration configuration;
    private final SportEventCache sportEventCache;


    @Inject
    BookingManagerImpl(SportEventCache sportEventCache, SDKInternalConfiguration configuration, HttpHelper httpHelper) {
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(httpHelper);

        this.sportEventCache = sportEventCache;
        this.configuration = configuration;
        this.httpHelper = httpHelper;
    }


    /**
     * Performs a request on the API which books the event associated with the provided {@link URN} identifier
     *
     * @param eventId the {@link URN} identifier of the event which needs to be booked
     * @return <code>true</code> if the booking was successful; otherwise <code>false</code>
     */
    @Override
    public boolean bookLiveOddsEvent(URN eventId) {
        Preconditions.checkNotNull(eventId);

        clientInteractionLogger.info("BookingManager.bookLiveOddsEvent({})", eventId);

        String requestUri = String.format("https://%s/v1/liveodds/booking-calendar/events/%s/book", configuration.getApiHostAndPort(), eventId);

        try {
            HttpHelper.ResponseData post = httpHelper.post(requestUri);
            clientInteractionLogger.info("BookingManager.bookLiveOddsEvent({}) - completed, status: {}, message: {}", eventId, post.getStatusCode(), post.getMessage());
            if (post.isSuccessful()) {
                sportEventCache.onEventBooked(eventId);
                return true;
            } else {
                executionLogger.warn("Event[{}] booking failed. API response code: '{}', message: '{}'", eventId, post.getStatusCode(), post.getMessage());
            }
        } catch (CommunicationException e) {
            executionLogger.warn("Event[{}] booking failed, ex: ", eventId, e);
        }

        return false;
    }
}
