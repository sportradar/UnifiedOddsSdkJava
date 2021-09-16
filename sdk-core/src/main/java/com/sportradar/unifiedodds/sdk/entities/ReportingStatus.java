/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.uf.datamodel.UFReportingStatus;

import java.util.Arrays;
import java.util.Optional;

/**
 * An enum describing the reporting status of a sport event
 */
@SuppressWarnings("java:S115") // Constant names should comply with a naming convention
public enum ReportingStatus {
    /**
     * The reporting status of the sport event is not available
     */
    NotAvailable(0),

    /**
     * The reporting status of the sport event is currently live
     */
    Live(1),

    /**
     * The reporting status of the sport event is suspended or temporary lost contact
     */
    Suspended(-1),

    /**
     * The reporting status of the sport event is unknown
     */
    Unknown;

    private final Integer statusFromMessage;

    ReportingStatus() {
        statusFromMessage = null;
    }

    ReportingStatus(int v) {
        statusFromMessage = v;
    }

    /**
     * Maps the value received trough a message to the {@link ReportingStatus}
     *
     * @param status - the sport event status received as a message
     * @return - if the received sport event status is in a known state, the mapped state; otherwise {@link #Unknown}
     */
    public static ReportingStatus valueFromMessageStatus(UFReportingStatus status) {
        if (status != null) {
            Optional<ReportingStatus> first =
                    Arrays.stream(ReportingStatus.values())
                            .filter(v -> (v.statusFromMessage != null && v.statusFromMessage == status.value()))
                            .findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return Unknown;
    }

    public Integer getIntValue() {
        return statusFromMessage;
    }
}
