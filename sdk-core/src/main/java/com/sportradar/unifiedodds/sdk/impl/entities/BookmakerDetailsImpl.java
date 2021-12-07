/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.utils.SdkHelper;
import org.apache.http.HttpStatus;

import java.time.Duration;
import java.util.Date;

/**
 * Represents the current bookmaker details
 */
public class BookmakerDetailsImpl implements BookmakerDetails {
    /**
     * The bookmaker id
     */
    private final int bookmakerId;

    /**
     * The specific virtual host of the bookmaker
     */
    private final String virtualHost;

    /**
     * The expiry date of the access token
     */
    private final Date expireAt;

    /**
     * The response code of the server's response
     * @see HttpStatus
     */
    private final ResponseCode responseCode;

    /**
     * The message of the request
     */
    private final String message;

    /**
     * Returns the difference with the server time
     */
    private final Duration serverTimeDifference;

    /**
     * Initializes a new instance of {@link BookmakerDetailsImpl}
     *
     * @param bookmakerDetails - a {@link com.sportradar.uf.sportsapi.datamodel.BookmakerDetails} containing the API response data
     *
     */
    public BookmakerDetailsImpl(com.sportradar.uf.sportsapi.datamodel.BookmakerDetails bookmakerDetails, Duration period) {
        Preconditions.checkNotNull(bookmakerDetails);

        this.bookmakerId = bookmakerDetails.getBookmakerId() == null ? 0 : bookmakerDetails.getBookmakerId();
        this.virtualHost = bookmakerDetails.getVirtualHost();
        this.expireAt = bookmakerDetails.getExpireAt() == null ? null : SdkHelper.toDate(bookmakerDetails.getExpireAt());
        this.responseCode = bookmakerDetails.getResponseCode();
        this.message = bookmakerDetails.getMessage();
        this.serverTimeDifference = period;
    }

    /**
     * Initializes a new instance of {@link BookmakerDetailsImpl}
     *
     */
    public BookmakerDetailsImpl(int bookmakerId, String virtualHost, Date expireAt, ResponseCode responseCode, String message, Duration period) {

        this.bookmakerId = bookmakerId;
        this.virtualHost = virtualHost;
        this.expireAt = expireAt;
        this.responseCode = responseCode;
        this.message = message;
        this.serverTimeDifference = period;
    }

    /**
     * Returns the message of the request
     *
     * @return - the message of the request
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * The expiry date of the access token
     *
     * @return - the expiry date of the access token
     */
    @Override
    public Date getExpireAt() {
        return expireAt;
    }

    /**
     * Returns the bookmaker id
     *
     * @return - the bookmaker id
     */
    @Override
    public int getBookmakerId() {
        return bookmakerId;
    }

    /**
     * Returns the response code of the server's response
     *
     * @return - the response code of the server's response
     * @see HttpStatus
     */
    @Override
    public ResponseCode getResponseCode() {
        return responseCode;
    }

    /**
     * Returns the specific virtual host of the bookmaker
     *
     * @return - the specific virtual host of the bookmaker
     */
    @Override
    public String getVirtualHost() {
        return virtualHost;
    }

    /**
     * Returns the difference with the server time
     *
     * @return - the difference with the server time
     */
    @Override
    public Duration getServerTimeDifference() {
        return serverTimeDifference;
    }

    /**
     * Maps the server response enum to a valid HTTP response code using the {@link HttpStatus} interface
     *
     * @param responseCode - the {@link ResponseCode} which the server returned
     * @return - a valid value from {@link HttpStatus}
     * @see HttpStatus
     */
    private int mapResponseCode2HttpStatus(ResponseCode responseCode) {
        switch (responseCode) {
            case OK:
                return HttpStatus.SC_OK;
            case CREATED:
                return HttpStatus.SC_CREATED;
            case ACCEPTED:
                return HttpStatus.SC_ACCEPTED;
            case CONFLICT:
                return HttpStatus.SC_CONFLICT;
            case FORBIDDEN:
                return HttpStatus.SC_FORBIDDEN;
            case NOT_FOUND:
                return HttpStatus.SC_NOT_FOUND;
            case NOT_IMPLEMENTED:
                return HttpStatus.SC_NOT_IMPLEMENTED;
            case MOVED_PERMANENTLY:
                return HttpStatus.SC_MOVED_PERMANENTLY;
            case SERVICE_UNAVAILABLE:
                return HttpStatus.SC_UNAUTHORIZED;
            default:
                return HttpStatus.SC_SEE_OTHER;
        }
    }
}
