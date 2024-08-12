/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.MessageAndActionExtractor;
import com.sportradar.unifiedodds.sdk.shared.TestHttpHelper;
import com.sportradar.utils.Urn;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpStatus;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "ConstantName", "MagicNumber", "ClassFanOutComplexity" })
public class BookingManagerTest {

    private final String eventUrl = "https://null/v1/liveodds/booking-calendar/events/sr:match:12345/book";

    private final Urn eventId = Urn.parse("sr:match:12345");
    private final SportEventCache sportEventCache = mock(SportEventCache.class);
    private final SdkInternalConfiguration configInternal = mock(SdkInternalConfiguration.class);
    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );
    private final UserAgentProvider userAgent = mock(UserAgentProvider.class);
    private BookingManager bookingManager;
    private TestHttpHelper testHttpHelper;

    @BeforeEach
    public void setup() {
        Deserializer deserializer = injector.getInstance(
            Key.get(Deserializer.class, Names.named("SportsApiJaxbDeserializer"))
        );
        when(configInternal.getExceptionHandlingStrategy()).thenReturn(ExceptionHandlingStrategy.Throw);
        val messageExtractor = new MessageAndActionExtractor();
        testHttpHelper = new TestHttpHelper(configInternal, httpClient, messageExtractor, userAgent);
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
