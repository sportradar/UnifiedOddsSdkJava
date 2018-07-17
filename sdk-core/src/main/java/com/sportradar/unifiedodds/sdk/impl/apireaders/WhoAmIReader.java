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
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.DataWrapper;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class WhoAmIReader {
    private final static Logger logger = LoggerFactory.getLogger(WhoAmIReader.class);
    private final DataProvider<BookmakerDetails> configDataProvider;
    private final DataProvider<BookmakerDetails> productionDataProvider;
    private final DataProvider<BookmakerDetails> stagingDataProvider;
    private final SDKInternalConfiguration config;
    private int bookmakerId;
    private Date expireAt;
    private ResponseCode responseCode;
    private String message;
    private boolean dataFetched;
    private boolean whoAmIValidated;
    private Map<String, String> associatedSdkMdcContextMap;

    @Inject
    public WhoAmIReader(
            SDKInternalConfiguration config,
            @Named("ConfigDataProvider") DataProvider<BookmakerDetails> configDataProvider,
            @Named("ProductionDataProvider") DataProvider<BookmakerDetails> productionDataProvider,
            @Named("StagingDataProvider") DataProvider<BookmakerDetails> stagingDataProvider) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(productionDataProvider);
        Preconditions.checkNotNull(stagingDataProvider);

        this.config = config;
        this.configDataProvider = configDataProvider;
        this.productionDataProvider = productionDataProvider;
        this.stagingDataProvider = stagingDataProvider;
    }

    private void retrieveInfo() {
        if (dataFetched) {
            return;
        }

        BookmakerDetails bookmakerDetails = config.isReplaySession() ? fetchReplayBookmakerDetails() : fetchBookmakerDetails();

        dataFetched = true;

        if (bookmakerDetails == null) {
            responseCode = ResponseCode.NOT_FOUND;
            message = "Bookmaker details could not be fetched, please verify your connection";
            return;
        }

        bookmakerId = bookmakerDetails.getBookmakerId() == null ? 0 : bookmakerDetails.getBookmakerId();
        expireAt = bookmakerDetails.getExpireAt() == null ? null : bookmakerDetails.getExpireAt().toGregorianCalendar().getTime();
        responseCode = bookmakerDetails.getResponseCode();
        message = bookmakerDetails.getMessage();
    }

    public int getBookmakerId() {
        retrieveInfo();
        return bookmakerId;
    }

    public Date getExpiry() {
        retrieveInfo();
        return expireAt;
    }

    public ResponseCode getResponseCode() {
        retrieveInfo();
        return responseCode;
    }

    public String getSdkContextDescription() {
        return String.format("uf-sdk-%s%s",
                getBookmakerId(),
                config.getSdkNodeId() == null ? "" : "-" + config.getSdkNodeId());
    }

    public Map<String, String> getAssociatedSdkMdcContextMap() {
        if (associatedSdkMdcContextMap == null) {
            associatedSdkMdcContextMap = ImmutableMap.<String, String>builder()
                    .put("uf-sdk-tag", getSdkContextDescription())
                    .build();
        }

        return associatedSdkMdcContextMap;
    }

    public String getMessage() {
        return message;
    }

    public void validateBookmakerDetails() {
        retrieveInfo();
        if (whoAmIValidated) {
            return;
        }
        logger.info("Bookmaker validation initiated");
        if (bookmakerId != 0) {
            logger.info("Client id: " + bookmakerId);
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            if (now.after(expireAt)) {
                String errMsg = "Access token has expired (" + expireAt + ")";
                logger.error(errMsg);
                throw new IllegalStateException("Access token has expired (" + expireAt + ")");
            }
            cal.add(Calendar.DATE, 7);
            if (cal.getTime().after(expireAt)) {
                logger.warn("Access token will expire during the next 7 days ({})", expireAt);
            }
            logger.info("Token validation completed successfully, valid until: {}", expireAt);
            whoAmIValidated = true;
        } else {
            String errMsg;
            if (ResponseCode.NOT_FOUND == responseCode) {
                errMsg = String.format("Access token could not be validated. [%s]", responseCode);
            } else if (ResponseCode.FORBIDDEN == responseCode) {
                errMsg = String.format("Looks like the access token has expired (or is invalid) - Access was denied. [msg: %s]", responseCode);
            } else {
                errMsg = "Bookmaker token validation endpoint could not be reached, please verify your connection setup";
            }

            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    private BookmakerDetails fetchBookmakerDetails() {
        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(configDataProvider);
        } catch (DataProviderException e) {
            // bookmaker details fetch failed
        }
        return bookmakerDetails;
    }

    private BookmakerDetails fetchReplayBookmakerDetails() {
        logger.info("Fetching production WhoAmI endpoint");
        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(productionDataProvider);
        } catch (DataProviderException e) {
            // bookmaker details fetch failed
        }

        if (bookmakerDetails != null && bookmakerDetails.getResponseCode() != ResponseCode.FORBIDDEN) {
            return bookmakerDetails;
        }

        logger.info("Production API request failed, fetching staging WhoAmI endpoint");
        try {
            bookmakerDetails = provideBookmakerDetails(stagingDataProvider);
        } catch (DataProviderException e) {
            // bookmaker details fetch failed
        }

        if (bookmakerDetails != null && bookmakerDetails.getResponseCode() != ResponseCode.FORBIDDEN) {
            logger.info("Staging WhoAmI request successful, switching SDK configuration to staging API");
            config.updateApiHost(UnifiedFeedConstants.STAGING_API_HOST);
        }

        return bookmakerDetails;
    }

    private static BookmakerDetails provideBookmakerDetails(DataProvider<BookmakerDetails> provider) throws DataProviderException {
        Preconditions.checkNotNull(provider);

        DataWrapper<BookmakerDetails> dataWithAdditionalInfo = provider.getDataWithAdditionalInfo(Locale.ENGLISH);

        validateLocalTimeWithServerTime(dataWithAdditionalInfo.getServerResponseTime());

        return dataWithAdditionalInfo.getData();
    }

    private static void validateLocalTimeWithServerTime(ZonedDateTime serverResponseTime) {
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
    }
}
