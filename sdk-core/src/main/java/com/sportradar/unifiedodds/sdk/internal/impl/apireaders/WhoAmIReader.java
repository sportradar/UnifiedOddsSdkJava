/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.apireaders;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.ibm.icu.util.Calendar;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.cfg.ApiHostUpdater;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.DataWrapper;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.BookmakerDetailsImpl;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "HiddenField",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "UnusedPrivateField",
    }
)
public class WhoAmIReader {

    private static final Logger logger = LoggerFactory.getLogger(WhoAmIReader.class);
    private final DataProvider<BookmakerDetails> configDataProvider;
    private final DataProvider<BookmakerDetails> productionDataProvider;
    private final DataProvider<BookmakerDetails> integrationDataProvider;
    private final UofConfiguration config;
    private boolean dataFetched;
    private boolean whoAmIValidated;
    private Map<String, String> associatedSdkMdcContextMap;
    private com.sportradar.unifiedodds.sdk.entities.BookmakerDetails bookmakerDetails;
    private Duration serverTimeDifference;
    private final ApiHostUpdater apiHostUpdater;

    @Inject
    public WhoAmIReader(
        UofConfiguration config,
        ApiHostUpdater apiHostUpdater,
        @Named("ConfigDataProvider") DataProvider<BookmakerDetails> configDataProvider,
        @Named("ProductionDataProvider") DataProvider<BookmakerDetails> productionDataProvider,
        @Named("IntegrationDataProvider") DataProvider<BookmakerDetails> integrationDataProvider
    ) {
        this.apiHostUpdater = apiHostUpdater;
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

        return Concatenator
            .separatingWith("-")
            .appendIfNotNull("uf")
            .appendIfNotNull("sdk")
            .appendIfNotNull(bookmakerDetails.getBookmakerId())
            .appendIfNotNull(config.getNodeId())
            .retrieve();
    }

    public Map<String, String> getAssociatedSdkMdcContextMap() {
        Preconditions.checkState(whoAmIValidated);

        if (associatedSdkMdcContextMap == null) {
            associatedSdkMdcContextMap =
                ImmutableMap.<String, String>builder().put("uf-sdk-tag", getSdkContextDescription()).build();
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
                throw new IllegalStateException(
                    "Access token has expired (" + bookmakerDetails.getExpireAt() + ")"
                );
            }
            cal.add(Calendar.DATE, 7);
            if (cal.getTime().after(bookmakerDetails.getExpireAt())) {
                logger.warn(
                    "Access token will expire during the next 7 days ({})",
                    bookmakerDetails.getExpireAt()
                );
            }
            logger.info(
                "Token validation completed successfully, valid until: {}",
                bookmakerDetails.getExpireAt()
            );
            whoAmIValidated = true;
        } else {
            String errMsg;
            if (ResponseCode.NOT_FOUND == bookmakerDetails.getResponseCode()) {
                errMsg =
                    String.format(
                        "Access token could not be validated. [%s]",
                        bookmakerDetails.getResponseCode()
                    );
            } else if (ResponseCode.FORBIDDEN == bookmakerDetails.getResponseCode()) {
                errMsg =
                    String.format(
                        "Looks like the access token has expired (or is invalid) - Access was denied. [msg: %s]",
                        bookmakerDetails.getResponseCode()
                    );
            } else {
                errMsg =
                    "Bookmaker token validation endpoint could not be reached, please verify your connection setup";
            }

            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    private void retrieveInfo() {
        if (dataFetched) {
            return;
        }

        BookmakerDetails fetchBookmakerDetails = fetchBookmakerDetails();

        dataFetched = true;

        if (fetchBookmakerDetails == null) {
            throw new IllegalStateException(
                "UOF SDK failed to fetch required bookmaker details, check logs for additional information"
            );
        }

        this.bookmakerDetails = new BookmakerDetailsImpl(fetchBookmakerDetails, serverTimeDifference);
    }

    private BookmakerDetails fetchBookmakerDetails() {
        logger.info(
            "Attempting bookmaker details fetch from the configured environment[{}], API: '{}'",
            config.getEnvironment(),
            config.getApi().getHost()
        );

        BookmakerDetails bookmakerDetails = null;
        try {
            bookmakerDetails = provideBookmakerDetails(configDataProvider);
        } catch (DataProviderException e) {
            logger.warn(
                "Bookmaker details fetch failed from the configured environment[{}]: {}",
                config.getEnvironment(),
                e.getMessage()
            );
        }

        if (isBookmakerResponseOk(bookmakerDetails)) {
            return bookmakerDetails;
        }

        logger.info("Bookmaker details fetch failed for environment[{}]", config.getEnvironment());
        return null;
    }

    private BookmakerDetails provideBookmakerDetails(DataProvider<BookmakerDetails> provider)
        throws DataProviderException {
        Preconditions.checkNotNull(provider);

        DataWrapper<BookmakerDetails> dataWithAdditionalInfo = provider.getDataWithAdditionalInfo(
            Locale.ENGLISH
        );

        validateLocalTimeWithServerTime(dataWithAdditionalInfo.getServerResponseTime());

        return dataWithAdditionalInfo.getData();
    }

    private void validateLocalTimeWithServerTime(ZonedDateTime serverResponseTime) {
        if (serverResponseTime == null) {
            logger.warn(
                "Could not validate local time against server time - SDK time related operations might cause issues"
            );
            return;
        }

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime localisedServerTime = serverResponseTime.withZoneSameInstant(now.getZone());
        long diff = ChronoUnit.SECONDS.between(localisedServerTime, now);

        long absDiff = Math.abs(diff);
        if (absDiff > 5) {
            logger.error(
                "Local time is out of sync for more than 5s({}s), SDK time related operations might cause issues",
                diff
            );
        } else if (absDiff > 2) {
            logger.warn(
                "Local time is out of sync for more than 2s({}s), SDK time related operations might cause issues",
                diff
            );
        }

        serverTimeDifference = Duration.between(localisedServerTime.toInstant(), Instant.now());
    }

    private static boolean isBookmakerResponseOk(BookmakerDetails bookmakerDetails) {
        return bookmakerDetails != null && bookmakerDetails.getResponseCode().equals(ResponseCode.OK);
    }
}
