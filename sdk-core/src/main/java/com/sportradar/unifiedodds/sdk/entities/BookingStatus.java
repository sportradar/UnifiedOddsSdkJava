/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * The enum describing various booking statuses
 */
public enum BookingStatus {
    /**
     * Indicates if the associated event is buyable
     */
    Buyable,

    /**
     * Indicates that the associated event is not booked and information associated with it will not be provided,
     * but the event could be booked
     */
    Bookable,

    /**
     * Indicates if the associated event is booked and information associated with it will be provided
     */
    Booked,

    /**
     * Indicates if the associated event is not available for booking
     */
    Unavailable;


    /**
     * Maps the provided {@link String} to a {@link BookingStatus}
     *
     * @param liveBookingStatus - a {@link String} describing a {@link BookingStatus}
     * @return - a calculated {@link BookingStatus}
     */
    public static BookingStatus getLiveBookingStatus(String liveBookingStatus) {
        if (liveBookingStatus == null) {
            return null;
        }
        switch (liveBookingStatus) {
            case "booked":
                return BookingStatus.Booked;
            case "bookable":
                return BookingStatus.Bookable;
            case "buyable":
                return BookingStatus.Buyable;
            default:
                return BookingStatus.Unavailable;
        }
    }
}
