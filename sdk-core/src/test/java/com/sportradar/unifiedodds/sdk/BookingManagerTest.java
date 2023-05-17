/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.shared.TestHttpHelper;
import com.sportradar.utils.URN;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({ "ConstantName", "MagicNumber", "ClassFanOutComplexity" })
public class BookingManagerTest {

    private final String eventUrl = "https://null/v1/liveodds/booking-calendar/events/sr:match:12345/book";

    private final URN eventId = URN.parse("sr:match:12345");
    private final SportEventCache sportEventCache = mock(SportEventCache.class);
    private final SDKInternalConfiguration configInternal = mock(SDKInternalConfiguration.class);
    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );
    private BookingManager bookingManager;
    private TestHttpHelper testHttpHelper;

    @Before
    public void setup() {
        Deserializer deserializer = injector.getInstance(
            Key.get(Deserializer.class, Names.named("SportsApiJaxbDeserializer"))
        );
        when(configInternal.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        testHttpHelper = new TestHttpHelper(configInternal, httpClient, deserializer);
        bookingManager = new BookingManagerImpl(sportEventCache, configInternal, testHttpHelper);
    }

    @Test
    public void bookingManagerIsSetup() {
        Assert.assertNotNull(bookingManager);
    }

    @Test
    public void bookEventId() throws CommunicationException {
        testHttpHelper.PostResponses.add(
            new TestHttpHelper.UrlReplacement(eventUrl, 1, HttpStatus.SC_ACCEPTED)
        );
        boolean isBooked = bookingManager.bookLiveOddsEvent(eventId);
        assertTrue(isBooked);
    }

    @Test
    public void failsOnBookingBadEventId() throws CommunicationException {
        testHttpHelper.PostResponses.add(
            new TestHttpHelper.UrlReplacement(eventUrl, 0, HttpStatus.SC_BAD_REQUEST)
        );
        boolean isBooked = false;
        boolean exceptionThrown = false;
        try {
            isBooked = bookingManager.bookLiveOddsEvent(eventId);
        } catch (CommunicationException exception) {
            exceptionThrown = true;
            Assert.assertNotNull(exception);
            Assert.assertEquals(HttpStatus.SC_BAD_REQUEST, exception.getHttpStatusCode());
        }
        Assert.assertTrue(exceptionThrown);
        Assert.assertFalse(isBooked);
        Assert.assertEquals(1, testHttpHelper.CalledUrls.size());
    }

    @Test
    public void failsOnInternalExceptionBookingThrowEventId() throws CommunicationException {
        testHttpHelper.PostResponses.add(
            new TestHttpHelper.UrlReplacement(eventUrl, 2, HttpStatus.SC_NOT_FOUND)
        );
        boolean isBooked = false;
        boolean exceptionThrown = false;
        try {
            isBooked = bookingManager.bookLiveOddsEvent(eventId);
        } catch (CommunicationException exception) {
            exceptionThrown = true;
            Assert.assertNotNull(exception);
            Assert.assertEquals(HttpStatus.SC_NOT_FOUND, exception.getHttpStatusCode());
        }
        Assert.assertTrue(exceptionThrown);
        Assert.assertFalse(isBooked);
        Assert.assertEquals(1, testHttpHelper.CalledUrls.size());
    }

    @Test
    public void bookLiveOddsEventCallsCorrectEndpoint() throws CommunicationException {
        final String xml = "/booking-calendar/";
        testHttpHelper.PostResponses.add(new TestHttpHelper.UrlReplacement(xml, 1, HttpStatus.SC_ACCEPTED));
        Assert.assertEquals(0, testHttpHelper.CalledUrls.size());

        bookingManager.bookLiveOddsEvent(eventId);
        Assert.assertEquals(1, testHttpHelper.CalledUrls.size());
        Assert.assertTrue(testHttpHelper.CalledUrls.get(0).contains(xml));
    }

    @Test
    public void bookLiveOddsEventCallRespectExceptionHandlingStrategyCatch() throws CommunicationException {
        final String exceptionMessage = "Not found for id";
        final String xml = "/booking-calendar/";
        CommunicationException notFoundException = new CommunicationException(
            exceptionMessage,
            xml,
            HttpStatus.SC_NOT_FOUND
        );
        testHttpHelper.UriExceptions.put(xml, notFoundException);
        when(configInternal.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Catch);

        boolean isBooked = bookingManager.bookLiveOddsEvent(eventId);
        Assert.assertFalse(isBooked);
        Assert.assertEquals(1, testHttpHelper.CalledUrls.size());
        Assert.assertTrue(testHttpHelper.CalledUrls.get(0).contains(xml));
    }

    @Test
    public void bookLiveOddsEventCallRespectExceptionHandlingStrategyThrow() {
        final String exceptionMessage = "Not found for id";
        final String xml = "/booking-calendar/";
        testHttpHelper.UriExceptions.put(
            xml,
            new CommunicationException(exceptionMessage, xml, HttpStatus.SC_NOT_FOUND)
        );

        boolean isBooked = false;
        boolean exceptionThrown = false;
        try {
            isBooked = bookingManager.bookLiveOddsEvent(eventId);
        } catch (CommunicationException exception) {
            exceptionThrown = true;
            Assert.assertNotNull(exception);
            Assert.assertEquals(HttpStatus.SC_NOT_FOUND, exception.getHttpStatusCode());
            Assert.assertEquals(exceptionMessage, exception.getMessage());
            Assert.assertEquals(xml, exception.getUrl());
        }
        Assert.assertTrue(exceptionThrown);
        Assert.assertFalse(isBooked);
        Assert.assertEquals(1, testHttpHelper.CalledUrls.size());
        Assert.assertTrue(testHttpHelper.CalledUrls.get(0).contains(xml));
    }
}
