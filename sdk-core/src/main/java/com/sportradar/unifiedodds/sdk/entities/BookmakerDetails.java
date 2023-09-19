/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.uf.sportsapi.datamodel.ResponseCode;
import java.time.Duration;
import java.util.Date;
import org.apache.hc.core5.http.HttpStatus;

/**
 * An interface providing methods to access bookmaker details
 */
public interface BookmakerDetails {
    /**
     * Returns the message of the request
     *
     * @return - the message of the request
     */
    String getMessage();

    /**
     * The expiry date of the access token
     *
     * @return - the expiry date of the access token
     */
    Date getExpireAt();

    /**
     * Returns the bookmaker id
     *
     * @return - the bookmaker id
     */
    int getBookmakerId();

    /**
     * Returns the response code of the server's response
     *
     * @return - the response code of the server's response
     * @see HttpStatus
     */
    ResponseCode getResponseCode();

    /**
     * Returns the specific virtual host of the bookmaker
     *
     * @return - the specific virtual host of the bookmaker
     */
    String getVirtualHost();

    /**
     * Returns the difference with the server time
     *
     * @return - the difference with the server time
     */
    Duration getServerTimeDifference();
}
