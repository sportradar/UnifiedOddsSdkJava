/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import org.apache.http.HttpStatus;

import java.util.Date;

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
    int getResponseCode();

    /**
     * Returns the specific virtual host of the bookmaker
     *
     * @return - the specific virtual host of the bookmaker
     */
    String getVirtualHost();
}
