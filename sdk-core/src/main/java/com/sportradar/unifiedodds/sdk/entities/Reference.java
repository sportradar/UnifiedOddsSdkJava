/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportUnreachableCode;
import java.util.Map;

/**
 * An interface providing methods to access reference descriptions
 */
public interface Reference {
    /**
     * Returns the Betradar id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betradar id for this instance if provided amount reference ids, null otherwise
     */
    Integer getBetradarId();

    /**
     * Returns the Betfair id for this instance if provided amount reference ids, null otherwise
     *
     * @return - the Betfair id for this instance if provided amount reference ids, null otherwise
     */
    Integer getBetfairId();

    /**
     * Returns the rotation number for this instance if provided amount reference ids, null otherwise
     *
     * @return - the rotation number for this instance if provided amount reference ids, null otherwise
     */
    Integer getRotationNumber();

    /**
     * Returns the AAMS id for this instance if provided among reference ids, null otherwise
     *
     * @return - the AAMS id for this instance if provided among reference ids, null otherwise
     */
    default Integer getAamsId() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the Lugas id for this instance if provided among reference ids, null otherwise
     *
     * @return - the Lugas id for this instance if provided among reference ids, null otherwise
     */
    @ExcludeFromJacocoGeneratedReportUnreachableCode
    default String getLugasId() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns a {@link Map} with all the reference ids associated with the current instance
     *
     * @return - all the reference ids associated with the current instance
     */
    Map<String, String> getReferences();
}
