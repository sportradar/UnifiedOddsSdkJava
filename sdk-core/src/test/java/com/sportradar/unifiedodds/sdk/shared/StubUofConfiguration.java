/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.shared;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.anyProducerDataProvider;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.anyBookmakerDetailsReader;

import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.BookmakerDetailsImpl;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

public class StubUofConfiguration extends UofConfigurationImpl implements UofConfiguration {

    private int nbrSetEnvironmentCalled;

    public StubUofConfiguration() {
        super(anyConfig -> anyBookmakerDetailsReader(), anyConfig -> anyProducerDataProvider());
        setAccessToken("accessToken");
        setDefaultLanguage(Locale.ENGLISH);
        setNodeId(1);
        updateSdkEnvironment(Environment.GlobalIntegration);
        setExceptionHandlingStrategy(ExceptionHandlingStrategy.Throw);
        setEnableUsageExport(false);

        validateMinimumSettings();
    }

    public void setEnvironment(Environment environment) {
        nbrSetEnvironmentCalled = +1;
        updateSdkEnvironment(environment);
    }

    public int getNbrSetEnvironmentCalled() {
        return nbrSetEnvironmentCalled;
    }

    public void resetNbrSetEnvironmentCalled() {
        nbrSetEnvironmentCalled = 0;
    }

    private static BookmakerDetails getStupBookmakerDetails() {
        BookmakerDetails bookmakerDetails = new BookmakerDetailsImpl(
            2,
            getVirtualHost(2),
            Date.from(Instant.now().plus(2, ChronoUnit.DAYS)),
            ResponseCode.ACCEPTED,
            "All good",
            Duration.ofSeconds(1)
        );
        return bookmakerDetails;
    }

    private static String getVirtualHost(int bookmakerId) {
        return "/unifiedfeed/" + bookmakerId;
    }
}
