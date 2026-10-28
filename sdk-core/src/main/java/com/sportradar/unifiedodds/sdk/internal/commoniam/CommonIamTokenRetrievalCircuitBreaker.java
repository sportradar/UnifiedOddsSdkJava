/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

class CommonIamTokenRetrievalCircuitBreaker {

    private static final int FAILURE_THRESHOLD_FOR_EXTENDED_BACKOFF = 10;
    private static final Duration INITIAL_BACKOFF = Duration.ofSeconds(5);
    private static final Duration EXTENDED_BACKOFF = Duration.ofSeconds(300);

    private final TimeUtils timeUtils;
    private boolean open;
    private Instant lastFailureTime;
    private int failureCount;

    CommonIamTokenRetrievalCircuitBreaker(TimeUtils timeUtils) {
        this.timeUtils = timeUtils;
    }

    void recordFailure() {
        open = true;
        lastFailureTime = timeUtils.nowInstant();
        failureCount++;
    }

    void recordSuccess() {
        open = false;
        failureCount = 0;
    }

    @SuppressWarnings("ReturnCount")
    void throwIfOpen() {
        if (!open) {
            return;
        }

        if (shouldAllowRetry()) {
            open = false;
            return;
        }
        throw new OAuth2TokenCache.OAuth2TokenRetrievalException(
            "Failed to retrieve OAuth token - returning error without calling CommonIAM"
        );
    }

    private boolean shouldAllowRetry() {
        Instant endExclusive = timeUtils.nowInstant();
        Duration timeSinceFailure = Duration.between(lastFailureTime, endExclusive);
        Duration requiredBackoff = getRequiredBackoff();
        return timeSinceFailure.getSeconds() >= requiredBackoff.getSeconds();
    }

    private Duration getRequiredBackoff() {
        if (failureCount >= FAILURE_THRESHOLD_FOR_EXTENDED_BACKOFF) {
            return EXTENDED_BACKOFF;
        }
        return INITIAL_BACKOFF;
    }
}
