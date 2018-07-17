package com.sportradar.unifiedodds.sdk.impl;

import java.time.Instant;

/**
 * Created on 06/04/2018.
 * // TODO @eti: Javadoc
 */
public interface TimeUtils {
    long now();

    Instant nowInstant();
}
