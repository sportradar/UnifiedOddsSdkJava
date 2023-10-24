/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static com.sportradar.uf.sportsapi.datamodel.BookmakerDetailsDtos.bet365;
import static com.sportradar.uf.sportsapi.datamodel.BookmakerDetailsDtos.notForRequestedEnvironment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;
import java.time.ZonedDateTime;
import java.util.Locale;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings(
    {
        "IllegalCatch",
        "LineLength",
        "MagicNumber",
        "MultipleStringLiterals",
        "checkstyle:ClassDataAbstractionCoupling",
        "checkstyle:ClassFanOutComplexity",
    }
)
public class WhoAmIReaderTests {

    private static final DataProvider ANY_DATA_PROVIDER = mock(DataProvider.class);
    private static final ApiHostUpdater ANY_ENVIRONMENT_UPDATER = mock(ApiHostUpdater.class);

    @Test
    public void validTokenProductionConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Production);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getValidProductionDataProvider(),
            getValidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void validTokenIntegrationConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Integration);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getValidIntegrationDataProvider(),
            getInvalidProductionDataProvider(),
            getValidIntegrationDataProvider()
        );
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void invalidTokenProductionConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Production);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getInvalidProductionDataProvider(),
            getInvalidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals(
                "UOF SDK failed to fetch required bookmaker details, check logs for additional information",
                e.getMessage()
            );
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void invalidTokenIntegrationConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Integration);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getInvalidIntegrationDataProvider(),
            getInvalidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals(
                "UOF SDK failed to fetch required bookmaker details, check logs for additional information",
                e.getMessage()
            );
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void replayServerConfigSelectionTestValidProductionEndpoint() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Production);
        config.resetNbrSetEnvironmentCalled();

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getValidProductionDataProvider(),
            getValidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(0, config.getNbrSetEnvironmentCalled());
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 33);
        Assert.assertEquals(whoAmIReader.getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void replayServerConfigSelectionTestValidIntegrationEndpoint() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Replay);
        config.resetNbrSetEnvironmentCalled();
        val environmentUpdater = mock(ApiHostUpdater.class);
        doAnswer(p -> {
                config.setEnvironment(Environment.Integration);
                return null;
            })
            .when(environmentUpdater)
            .updateToIntegration();

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            environmentUpdater,
            getInvalidProductionDataProvider(),
            getInvalidProductionDataProvider(),
            getValidIntegrationDataProvider()
        );
        whoAmIReader.validateBookmakerDetails();

        Assert.assertEquals(Environment.Integration, config.getEnvironment());
        Assert.assertEquals(whoAmIReader.getBookmakerId(), 3311);
        Assert.assertEquals(whoAmIReader.getResponseCode(), ResponseCode.OK);
    }

    @Test
    public void replayServerConfigSelectionTestBothEndpointsInvalid() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Replay);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getInvalidProductionDataProvider(),
            getInvalidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals(
                "Looks like the access token has expired (or is invalid) - Access was denied. [msg: FORBIDDEN]",
                e.getMessage()
            );
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void switchedTokenProductionIntegrationConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Production);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getInvalidProductionDataProvider(),
            getInvalidProductionDataProvider(),
            getValidIntegrationDataProvider()
        );

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals(
                "The provided access token is for the 'Integration' environment but the SDK is configured to access the 'Production' environment",
                e.getMessage()
            );
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void switchedTokenIntegrationProductionConfig() throws DataProviderException {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Integration);

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            getInvalidIntegrationDataProvider(),
            getValidProductionDataProvider(),
            getInvalidIntegrationDataProvider()
        );

        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals(
                "The provided access token is for the 'Production' environment but the SDK is configured to access the 'Integration' environment",
                e.getMessage()
            );
            return;
        }

        Assert.fail("Should not be reached");
    }

    @Test
    public void validXmlDataProvider() {
        StubUofConfiguration config = new StubUofConfiguration();
        config.setEnvironment(Environment.Integration);

        TestingDataProvider<BookmakerDetails> dataProvider = new TestingDataProvider<>(
            "test/rest/bookmaker_details.xml"
        );

        WhoAmIReader whoAmIReader = new WhoAmIReader(
            config,
            ANY_ENVIRONMENT_UPDATER,
            dataProvider,
            dataProvider,
            dataProvider
        );
        try {
            whoAmIReader.validateBookmakerDetails();
        } catch (Exception e) {
            Assert.assertEquals(IllegalStateException.class, e.getClass());
            Assert.assertEquals("Access token has expired (Tue Jul 26 19:44:24 CEST 2016)", e.getMessage());
        }

        Assert.assertEquals(whoAmIReader.getBookmakerId(), 1);
        Assert.assertEquals(whoAmIReader.getResponseCode(), ResponseCode.OK);
        Assert.assertEquals(whoAmIReader.getVirtualHost(), "/virtualhost");
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidIntegrationDataProvider()
        throws DataProviderException {
        XMLGregorianCalendar expirationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        expirationDate.setYear(2040);

        BookmakerDetails sampleIntegrationWhoAmIResponse = new BookmakerDetails();
        sampleIntegrationWhoAmIResponse.setBookmakerId(3311);
        sampleIntegrationWhoAmIResponse.setExpireAt(expirationDate);
        sampleIntegrationWhoAmIResponse.setMessage("Token valid integration");
        sampleIntegrationWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleIntegrationWhoAmIResponse.setVirtualHost("/vhost3311-integration");

        DataProvider<BookmakerDetails> integrationDataProvider = (DataProvider<BookmakerDetails>) mock(
            DataProvider.class
        );
        when(integrationDataProvider.getData()).thenReturn(sampleIntegrationWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleIntegrationWhoAmIResponse);
        when(integrationDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return integrationDataProvider;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getValidProductionDataProvider()
        throws DataProviderException {
        XMLGregorianCalendar expirationDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        expirationDate.setYear(2040);

        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setBookmakerId(33);
        sampleProductionWhoAmIResponse.setExpireAt(expirationDate);
        sampleProductionWhoAmIResponse.setMessage("Token valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.OK);
        sampleProductionWhoAmIResponse.setVirtualHost("/vhost33-production");

        DataProvider<BookmakerDetails> productionDataProvider = (DataProvider<BookmakerDetails>) mock(
            DataProvider.class
        );
        when(productionDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        when(productionDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return productionDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidIntegrationDataProvider()
        throws DataProviderException {
        BookmakerDetails sampleIntegrationWhoAmIResponse = new BookmakerDetails();
        sampleIntegrationWhoAmIResponse.setMessage("Token invalid integration");
        sampleIntegrationWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> integrationDataProvider = (DataProvider<BookmakerDetails>) mock(
            DataProvider.class
        );
        when(integrationDataProvider.getData()).thenReturn(sampleIntegrationWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleIntegrationWhoAmIResponse);
        when(integrationDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return integrationDataProvider;
    }

    @SuppressWarnings("unchecked")
    private static DataProvider<BookmakerDetails> getInvalidProductionDataProvider()
        throws DataProviderException {
        BookmakerDetails sampleProductionWhoAmIResponse = new BookmakerDetails();
        sampleProductionWhoAmIResponse.setMessage("Token invalid valid prod");
        sampleProductionWhoAmIResponse.setResponseCode(ResponseCode.FORBIDDEN);

        DataProvider<BookmakerDetails> productionDataProvider = (DataProvider<BookmakerDetails>) mock(
            DataProvider.class
        );
        when(productionDataProvider.getData()).thenReturn(sampleProductionWhoAmIResponse);

        DataWrapper<BookmakerDetails> dataWrapperWith = getDataWrapperWith(sampleProductionWhoAmIResponse);
        when(productionDataProvider.getDataWithAdditionalInfo(Locale.ENGLISH)).thenReturn(dataWrapperWith);

        return productionDataProvider;
    }

    @Test
    public void changesEnvironmentToProductionIfTokenIsForProduction()
        throws DataProviderException, DatatypeConfigurationException {
        val productionBookmakers = mock(DataProvider.class);
        final DataWrapper dataWrapper = wrapped(bet365());
        when(productionBookmakers.getDataWithAdditionalInfo(any())).thenReturn(dataWrapper);
        ApiHostUpdater apiHostUpdater = mock(ApiHostUpdater.class);
        WhoAmIReader whoAmIReader = new WhoAmIReader(
            replayConfig(),
            apiHostUpdater,
            ANY_DATA_PROVIDER,
            productionBookmakers,
            ANY_DATA_PROVIDER
        );
        whoAmIReader.getBookmakerId();

        verify(apiHostUpdater).updateToProduction();
    }

    @Test
    public void changesEnvironmentToIntegrationIfTokenIsForIntegration()
        throws DataProviderException, DatatypeConfigurationException {
        final DataWrapper productionBookmaker = wrapped(notForRequestedEnvironment());
        val productionBookmakers = mock(DataProvider.class);
        when(productionBookmakers.getDataWithAdditionalInfo(any())).thenReturn(productionBookmaker);
        final DataWrapper integrationBookmaker = wrapped(bet365());
        val integrationBookmakers = mock(DataProvider.class);
        when(integrationBookmakers.getDataWithAdditionalInfo(any())).thenReturn(integrationBookmaker);
        ApiHostUpdater apiHostUpdater = mock(ApiHostUpdater.class);
        WhoAmIReader whoAmIReader = new WhoAmIReader(
            replayConfig(),
            apiHostUpdater,
            ANY_DATA_PROVIDER,
            productionBookmakers,
            integrationBookmakers
        );
        whoAmIReader.getBookmakerId();

        verify(apiHostUpdater).updateToIntegration();
    }

    @Test
    public void changesEnvironmentToIntegrationIfTokenIsForIntegrationEvenIfProductionCheckFails()
        throws DataProviderException, DatatypeConfigurationException {
        val productionBookmakers = mock(DataProvider.class);
        when(productionBookmakers.getDataWithAdditionalInfo(any())).thenThrow(DataProviderException.class);
        final DataWrapper integrationBookmaker = wrapped(bet365());
        val integrationBookmakers = mock(DataProvider.class);
        when(integrationBookmakers.getDataWithAdditionalInfo(any())).thenReturn(integrationBookmaker);
        ApiHostUpdater apiHostUpdater = mock(ApiHostUpdater.class);
        WhoAmIReader whoAmIReader = new WhoAmIReader(
            replayConfig(),
            apiHostUpdater,
            ANY_DATA_PROVIDER,
            productionBookmakers,
            integrationBookmakers
        );
        whoAmIReader.getBookmakerId();
        verify(apiHostUpdater).updateToIntegration();
    }

    private static UofConfigurationStub replayConfig() {
        UofConfigurationStub config = new UofConfigurationStub();
        config.setEnvironment(Environment.Replay);
        return config;
    }

    @Test
    public void contextDescriptionIsFixedPrefixWithBookmakerId()
        throws DataProviderException, DatatypeConfigurationException {
        DataProvider bookmakerProvider = mock(DataProvider.class);
        WhoAmIReader whoAmIReader = new WhoAmIReader(
            configWithAnyApiHost(),
            ANY_ENVIRONMENT_UPDATER,
            bookmakerProvider,
            ANY_DATA_PROVIDER,
            ANY_DATA_PROVIDER
        );
        final DataWrapper dataWrapper = wrapped(bet365());
        when(bookmakerProvider.getDataWithAdditionalInfo(any())).thenReturn(dataWrapper);
        whoAmIReader.validateBookmakerDetails();

        assertThat(whoAmIReader.getSdkContextDescription()).isEqualTo("uf-sdk-" + bet365().getBookmakerId());
    }

    @Test
    public void contextDescriptionIsFixedPrefixWithBookmakerIdAndNodeIdWhenItIsPresent()
        throws DataProviderException, DatatypeConfigurationException {
        DataProvider bookmakerProvider = mock(DataProvider.class);
        final int nodeId = 5544;
        WhoAmIReader whoAmIReader = new WhoAmIReader(
            configWithAnyApiHostAndNodeId(nodeId),
            ANY_ENVIRONMENT_UPDATER,
            bookmakerProvider,
            ANY_DATA_PROVIDER,
            ANY_DATA_PROVIDER
        );
        final DataWrapper dataWrapper = wrapped(bet365());
        when(bookmakerProvider.getDataWithAdditionalInfo(any())).thenReturn(dataWrapper);
        whoAmIReader.validateBookmakerDetails();

        assertThat(whoAmIReader.getSdkContextDescription())
            .isEqualTo("uf-sdk-" + bet365().getBookmakerId() + "-" + nodeId);
    }

    private static DataWrapper wrapped(BookmakerDetails bookmaker) {
        val dataWrapper = mock(DataWrapper.class);
        when(dataWrapper.getData()).thenReturn(bookmaker);
        return dataWrapper;
    }

    private static UofConfigurationStub configWithAnyApiHost() {
        UofConfigurationStub config = new UofConfigurationStub();
        ((UofApiConfigurationStub) config.getApi()).setHost("anyNonNullHost");
        return config;
    }

    private static UofConfigurationStub configWithAnyApiHostAndNodeId(int nodeId) {
        UofConfigurationStub config = new UofConfigurationStub();
        ((UofApiConfigurationStub) config.getApi()).setHost("anyNonNullHost");
        config.setNodeId(nodeId);
        return config;
    }

    @SuppressWarnings("unchecked")
    private static DataWrapper<BookmakerDetails> getDataWrapperWith(BookmakerDetails bookmakerDetails) {
        DataWrapper mock = mock(DataWrapper.class);
        when(mock.getData()).thenReturn(bookmakerDetails);
        when(mock.getServerResponseTime()).thenReturn(ZonedDateTime.now());
        return mock;
    }
}
