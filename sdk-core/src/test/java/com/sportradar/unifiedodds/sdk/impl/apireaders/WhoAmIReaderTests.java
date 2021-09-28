/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.*;
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
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidProductionDataProvider(), getValidProductionDataProvider(), getInvalidIntegrationDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void validTokenIntegrationConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Integration));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidIntegrationDataProvider(), getInvalidProductionDataProvider(), getValidIntegrationDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void invalidTokenProductionConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getEnvironment()).thenReturn(Environment.Production);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getInvalidIntegrationDataProvider());

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("UOF SDK failed to fetch required bookmaker details, check logs for additional information", e.getMessage());
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void invalidTokenIntegrationConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getEnvironment()).thenReturn(Environment.Integration);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Integration));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidIntegrationDataProvider(), getInvalidProductionDataProvider(), getInvalidIntegrationDataProvider());

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("UOF SDK failed to fetch required bookmaker details, check logs for additional information", e.getMessage());
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void replayServerConfigSelectionTestValidProductionEndpoint() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getValidProductionDataProvider(), getValidProductionDataProvider(), getInvalidIntegrationDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Mockito.verify(config, Mockito.times(0)).updateApiHost(EnvironmentManager.getApiHost(Environment.Integration));
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void replayServerConfigSelectionTestValidIntegrationEndpoint() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getValidIntegrationDataProvider());
        whoAmIReader.validateBookmakerDetails();

        Mockito.verify(config, Mockito.times(1)).updateApiHost(EnvironmentManager.getApiHost(Environment.Integration));
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
    }

    @Test
    public void replayServerConfigSelectionTestBothEndpointsInvalid() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(true);
        Mockito.when(config.getEnvironment()).thenReturn(Environment.Production);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getInvalidIntegrationDataProvider());

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("Looks like the access token has expired (or is invalid) - Access was denied. [msg: FORBIDDEN]", e.getMessage());
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void switchedTokenProductionIntegrationConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getEnvironment()).thenReturn(Environment.Production);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Production));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidProductionDataProvider(), getInvalidProductionDataProvider(), getValidIntegrationDataProvider());

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("The provided access token is for the 'Integration' environment but the SDK is configured to access the 'Production' environment", e.getMessage());
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void switchedTokenIntegrationProductionConfig() throws DataProviderException {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getEnvironment()).thenReturn(Environment.Integration);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Integration));

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, getInvalidIntegrationDataProvider(), getValidProductionDataProvider(), getInvalidIntegrationDataProvider());

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("The provided access token is for the 'Production' environment but the SDK is configured to access the 'Integration' environment", e.getMessage());
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void validXmlDataProvider() {
        SDKInternalConfiguration config = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(config.isReplaySession()).thenReturn(false);
        Mockito.when(config.getAPIHost()).thenReturn(EnvironmentManager.getApiHost(Environment.Integration));
        TestingDataProvider<BookmakerDetails> dataProvider = new TestingDataProvider<>("test/rest/bookmaker_details.xml");

        WhoAmIReader whoAmIReader = new WhoAmIReader(config, dataProvider, dataProvider, dataProvider);
        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("Access token has expired (Tue Jul 26 19:44:24 CEST 2016)", e.getMessage());
        }

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 1);
        Assert.assertEquals(whoAmIReader.getResponseCode(),  ResponseCode.OK);
        Assert.assertEquals(whoAmIReader.getVirtualHost(),  "/virtualhost/1");
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidIntegrationDataProvider() throws DataProviderException {
        XMLGregorianCalendar expirationDate = XMLGregorianCalendarImpl.LEAP_YEAR_DEFAULT;
        expirationDate.setYear(2040);

        BookmakerDetails sampleIntegrationWhoAmIResponse = new BookmakerDetails();
        sampleIntegrationWhoAmIResponse.setBookmakerId(3311);
        sampleIntegrationWhoAmIResponse.setExpireAt(expirationDate);
        sampleIntegrationWhoAmIResponse.setMessage("Token valid integration");
        sampleIntegrationWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleIntegrationWhoAmIResponse.setVirtualHost("/vhost3311-integration");

        DataProvider<BookmakerDetails> integrationDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(integrationDataProvider.getData()).thenReturn(sampleIntegrationWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleIntegrationWhoAmIResponse);
        Mockito.when(integrationDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return integrationDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidProductionDataProvider() throws DataProviderException {
        XMLGregorianCalendar expirationDate = XMLGregorianCalendarImpl.LEAP_YEAR_DEFAULT;
        expirationDate.setYear(2040);

        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setBookmakerId(33);
        sampleProductionWhoAmIResponse.setExpireAt(expirationDate);
        sampleProductionWhoAmIResponse.setMessage("Token valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleProductionWhoAmIResponse.setVirtualHost("/vhost33-production");

        DataProvider<BookmakerDetails> productionDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(productionDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        Mockito.when(productionDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return productionDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidIntegrationDataProvider() throws DataProviderException {
        BookmakerDetails sampleIntegrationWhoAmIResponse = new BookmakerDetails();
        sampleIntegrationWhoAmIResponse.setMessage("Token invalid integration");
        sampleIntegrationWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> integrationDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(integrationDataProvider.getData()).thenReturn(sampleIntegrationWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleIntegrationWhoAmIResponse);
        Mockito.when(integrationDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return integrationDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidProductionDataProvider() throws DataProviderException {
        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setMessage("Token invalid valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> productionDataProvider = (DataProvider<BookmakerDetails>) Mockito.mock(DataProvider.class);
        Mockito.when(productionDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        Mockito.when(productionDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return productionDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataWrapper<BookmakerDetails> getDataWrapperWith(BookmakerDetails bookmakerDetails) {
        DataWrapper mock = Mockito.mock(DataWrapper.class);
        Mockito.when(mock.getData()).thenReturn(bookmakerDetails);
        Mockito.when(mock.getServerResponseTime()).thenReturn(ZonedDateTime.now());
        return mock;
    }
}
