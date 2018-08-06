/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.DataWrapper;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZonedDateTime;
import java.util.Locale;

/**
 * Created on 28/03/2018.
 * // TODO @eti: Javadoc
 */
public class WhoAmIReaderTests {

    @Test
    public void validTokenProductionConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.PRODUCTION_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidProductionDataProvider(), getValidProductionDataProvider(), getInvalidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void validTokenStagingConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.STAGING_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidStagingDataProvider(), getInvalidProductionDataProvider(), getValidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test(expected = IllegalStateException.class)
    public void invalidTokenProductionConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.PRODUCTION_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getInvalidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();
    }

    @Test(expected = IllegalStateException.class)
    public void invalidTokenStagingConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.STAGING_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidStagingDataProvider(), getInvalidProductionDataProvider(), getInvalidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();
    }

    @Test
    public void replayServerConfigSelectionTestValidProductionEndpoint() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.PRODUCTION_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidProductionDataProvider(), getValidProductionDataProvider(), getInvalidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Mockito.verify(config, Mockito.times(0)).updateApiHost(UnifiedFeedConstants.STAGING_API_HOST);
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void replayServerConfigSelectionTestValidStagingEndpoint() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.PRODUCTION_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getValidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Mockito.verify(config, Mockito.times(1)).updateApiHost(UnifiedFeedConstants.STAGING_API_HOST);
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test(expected = IllegalStateException.class)
    public void replayServerConfigSelectionTestBothEndpointsInvalid() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getAPIHost()).thenReturn(UnifiedFeedConstants.PRODUCTION_API_HOST);

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getInvalidStagingDataProvider());
        whoAmIReader.validateBookmakerDetails();
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidStagingDataProvider() throws DataProviderException {
        XMLGregorianCalendar expirationDate = XMLGregorianCalendarImpl.LEAP_YEAR_DEFAULT;
        expirationDate.setYear(2020);

        BookmakerDetails sampleStagingWhoAmIResponse = new BookmakerDetails();
        sampleStagingWhoAmIResponse.setBookmakerId(3311);
        sampleStagingWhoAmIResponse.setExpireAt(expirationDate);
        sampleStagingWhoAmIResponse.setMessage("Token valid staging");
        sampleStagingWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleStagingWhoAmIResponse.setVirtualHost("/vhost3311-staging");

        DataProvider<BookmakerDetails> stagingDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(stagingDataProvider.getData()).thenReturn(sampleStagingWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleStagingWhoAmIResponse);
        Mockito.when(stagingDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return stagingDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidProductionDataProvider() throws DataProviderException {
        XMLGregorianCalendar expirationDate = XMLGregorianCalendarImpl.LEAP_YEAR_DEFAULT;
        expirationDate.setYear(2020);

        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setBookmakerId(33);
        sampleProductionWhoAmIResponse.setExpireAt(expirationDate);
        sampleProductionWhoAmIResponse.setMessage("Token valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleProductionWhoAmIResponse.setVirtualHost("/vhost33-production");

        DataProvider<BookmakerDetails> stagingDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(stagingDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        Mockito.when(stagingDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return stagingDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidStagingDataProvider() throws DataProviderException {
        BookmakerDetails sampleStagingWhoAmIResponse = new BookmakerDetails();
        sampleStagingWhoAmIResponse.setMessage("Token invalid staging");
        sampleStagingWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> stagingDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(stagingDataProvider.getData()).thenReturn(sampleStagingWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleStagingWhoAmIResponse);
        Mockito.when(stagingDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return stagingDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidProductionDataProvider() throws DataProviderException {
        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setMessage("Token invalid valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> stagingDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(stagingDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        Mockito.when(stagingDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return stagingDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataWrapper<BookmakerDetails> getDataWrapperWith(BookmakerDetails bookmakerDetails) {
        DataWrapper mock = Mockito.mock(DataWrapper.class);
        Mockito.when(mock.getData()).thenReturn(bookmakerDetails);
        Mockito.when(mock.getServerResponseTime()).thenReturn(ZonedDateTime.now());
        return mock;
    }
}
