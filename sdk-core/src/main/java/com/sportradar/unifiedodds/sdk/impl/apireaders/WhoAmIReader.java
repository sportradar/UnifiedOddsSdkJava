/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibm.icu.util.Calendar;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.DataWrapper;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class WhoAmIReader {
    private final static Logger logger = LoggerFactory.getLogger(WhoAmIReader.class);
    private final DataProvider<BookmakerDetails> configDataProvider;
    private final DataProvider<BookmakerDetails> productionDataProvider;
    private final DataProvider<BookmakerDetails> integrationDataProvider;
    private final SDKInternalConfiguration config;
    private boolean dataFetched;
    private boolean whoAmIValidated;
    private Map<String, String> associatedSdkMdcContextMap;
    private com.sportradar.unifiedodds.sdk.entities.BookmakerDetails bookmakerDetails;
    private Duration serverTimeDifference;

    @Inject
    public WhoAmIReader(
            SDKInternalConfiguration config,
            @Named("ConfigDataProvider") DataProvider<BookmakerDetails> configDataProvider,
            @Named("ProductionDataProvider") DataProvider<BookmakerDetails> productionDataProvider,
            @Named("IntegrationDataProvider") DataProvider<BookmakerDetails> integrationDataProvider) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(productionDataProvider);
        Preconditions.checkNotNull(integrationDataProvider);

        this.config = config;
        this.configDataProvider = configDataProvider;
        this.productionDataProvider = productionDataProvider;
        this.integrationDataProvider = integrationDataProvider;
        this.serverTimeDifference = Duration.ofSeconds(0);
    }

    public int getBookmakerId() {
        retrieveInfo();
        return bookmakerDetails.getBookmakerId();
    }

    public Date getExpiry() {
        retrieveInfo();
        return bookmakerDetails.getExpireAt();
    }

    public String getVirtualHost() {
        retrieveInfo();
        return bookmakerDetails.getVirtualHost();
    }

    public ResponseCode getResponseCode() {
        retrieveInfo();
        return bookmakerDetails.getResponseCode();
    }

    public String getMessage() {
        retrieveInfo();
        return bookmakerDetails.getMessage();
    }

    public com.sportradar.unifiedodds.sdk.entities.BookmakerDetails getBookmakerDetails() {
        retrieveInfo();
        return bookmakerDetails;
    }

    public String getSdkContextDescription() {
        Preconditions.checkState(whoAmIValidated);

        return String.format("uf-sdk-%s%s",
                bookmakerDetails.getBookmakerId(),
                config.getSdkNodeId() == null ? "" : "-" + config.getSdkNodeId());
    }

    public Map<String, String> getAssociatedSdkMdcContextMap() {
        Preconditions.checkState(whoAmIValidated);

        if (associatedSdkMdcContextMap == null) {
            associatedSdkMdcContextMap = ImmutableMap.<String, String>builder()
                    .put("uf-sdk-tag", getSdkContextDescription())
                    .build();
        }

        return associatedSdkMdcContextMap;
    }

    public void validateBookmakerDetails() {
        retrieveInfo();
        if (whoAmIValidated) {
            return;
        }
        logger.info("Bookmaker validation initiated");
        if (bookmakerDetails.getBookmakerId() != 0) {
            logger.info("Client id: " + bookmakerDetails.getBookmakerId());
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            if (now.after(bookmakerDetails.getExpireAt())) {
                String errMsg = "Access token has expired (" + bookmakerDetails.getExpireAt() + ")";
                logger.error(errMsg);
                throw new IllegalStateException("Access token has expired (" + bookmakerDetails.getExpireAt() + ")");
            }
            cal.add(Calendar.DATE, 7);
            if (cal.getTime().after(bookmakerDetails.getExpireAt())) {
                logger.warn("Access token will expire during the next 7 days ({})", bookmakerDetails.getExpireAt());
            }
            logger.info("Token validation completed successfully, valid until: {}", bookmakerDetails.getExpireAt());
            whoAmIValidated = true;
        } else {
            String errMsg;
            if (ResponseCode.NOT_FOUND == bookmakerDetails.getResponseCode()) {
                errMsg = String.format("Access token could not be validated. [%s]", bookmakerDetails.getResponseCode());
            } else if (ResponseCode.FORBIDDEN == bookmakerDetails.getResponseCode()) {
                errMsg = String.format("Looks like the access token has expired (or is invalid) - Access was denied. [msg: %s]", bookmakerDetails.getResponseCode());
            } else {
                errMsg = "Bookmaker token validation endpoint could not be reached, please verify your connection setup";
            }

            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    private void retrieveInfo() {
        if (dataFetched) {
            return;
        }

        BookmakerDetails bookmakerDetails = config.isReplaySession()
                ? fetchReplayBookmakerDetails()
                : fetchBookmakerDetails();

        dataFetched = true;

        if (bookmakerDetails == null) {
            throw new IllegalStateException("UOF SDK failed to fetch required bookmaker details, check logs for additional information");
        }

        this.bookmakerDetails = new com.sportradar.unifiedodds.sdk.impl.entities.BookmakerDetailsImpl(bookmakerDetails, serverTimeDifference);
    }

    private BookmakerDetails fetchBookmakerDetails() {
        logger.info("Attempting bookmaker details fetch from the configured environment[{}], API: '{}'", config.getEnvironment(), config.getAPIHost());

        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(configDataProvider);
        } catch (DataProviderException e) {
            logger.warn("Bookmaker settings failed to fetch from the configured environment[{}], exc:", config.getEnvironment(), e);
        }

        if (isBookmakerResponseOk(bookmakerDetails)) {
            return bookmakerDetails;
        }

        logger.warn("Bookmaker details fetch failed from the configured environment, checking token status on other available environments...");

        if (!config.getAPIHost().equalsIgnoreCase(EnvironmentManager.getApiHost(Environment.Integration))) {
            attemptTokenValidationOn(Environment.Integration, EnvironmentManager.getApiHost(Environment.Integration), integrationDataProvider);
        }
        if (!config.getAPIHost().equalsIgnoreCase(EnvironmentManager.getApiHost(Environment.Production))) {
            attemptTokenValidationOn(Environment.Production, EnvironmentManager.getApiHost(Environment.Production), productionDataProvider);
        }

        logger.info("Bookmaker details fetch failed on all available environments");

        return null;
    }

    private void attemptTokenValidationOn(Environment environment, String environmentApiUrl, DataProvider<BookmakerDetails> dataProvider) {
        logger.info("Attempting bookmaker details fetch from the '{}' environment, API URL: '{}'", environment, environmentApiUrl);

        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(dataProvider);
        } catch (DataProviderException e) {
            logger.warn("Bookmaker settings failed to fetch from the '{}' environment with exc:", environment, e);
        }

        if (isBookmakerResponseOk(bookmakerDetails)) {
            String message = String.format("The provided access token is for the '%s' environment but the SDK is configured to access the '%s' environment", environment, config.getEnvironment());
            logger.error(message);
            throw new IllegalStateException(message);
        }

        logger.info("Bookmaker details fetch failed on '{}'", environment);
    }

    private BookmakerDetails fetchReplayBookmakerDetails() {
        logger.info("Fetching 'production' WhoAmI endpoint");

        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(productionDataProvider);
        } catch (DataProviderException e) {
            logger.warn("Replay WhoAmI fetch failed on 'production' with exc:", e);
        }

        if (bookmakerDetails != null && bookmakerDetails.getResponseCode() != ResponseCode.FORBIDDEN) {
            logger.info("Production WhoAmI request successful, switching SDK configuration to production API");
            config.updateApiHost(EnvironmentManager.getApiHost(Environment.Production));
            return bookmakerDetails;
        }

        logger.info("Production API request failed, fetching 'integration' WhoAmI endpoint");
        try {
            bookmakerDetails = provideBookmakerDetails(integrationDataProvider);
        } catch (DataProviderException e) {
            logger.warn("Replay WhoAmI fetch failed on 'integration' with exc:", e);
        }

        if (bookmakerDetails != null && bookmakerDetails.getResponseCode() != ResponseCode.FORBIDDEN) {
            logger.info("Integration WhoAmI request successful, switching SDK configuration to integration API");
            config.updateApiHost(EnvironmentManager.getApiHost(Environment.Integration));
        }

        return bookmakerDetails;
    }

    private BookmakerDetails provideBookmakerDetails(DataProvider<BookmakerDetails> provider) throws DataProviderException {
        Preconditions.checkNotNull(provider);

        DataWrapper<BookmakerDetails> dataWithAdditionalInfo = provider.getDataWithAdditionalInfo(Locale.ENGLISH);

        validateLocalTimeWithServerTime(dataWithAdditionalInfo.getServerResponseTime());

        return dataWithAdditionalInfo.getData();
    }

    private void validateLocalTimeWithServerTime(ZonedDateTime serverResponseTime) {
        if (serverResponseTime == null) {
            logger.warn("Could not validate local time against server time - SDK time related operations might cause issues");
            return;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime localisedServerTime = serverResponseTime.withZoneSameInstant(now.getZone());
        long diff = ChronoUnit.SECONDS.between(localisedServerTime, now);

        long absDiff = Math.abs(diff);
        if (absDiff > 5) {
            logger.error("Local time is out of sync for more than 5s({}s), SDK time related operations might cause issues", diff);
        } else if (absDiff > 2) {
            logger.warn("Local time is out of sync for more than 2s({}s), SDK time related operations might cause issues", diff);
        }

        serverTimeDifference = Duration.between(localisedServerTime.toInstant(), Instant.now());
    }

    private static boolean isBookmakerResponseOk(BookmakerDetails bookmakerDetails) {
        return bookmakerDetails != null && bookmakerDetails.getResponseCode().equals(ResponseCode.OK);
    }
}
