/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.player.exceptions;

/**
 * Created on 05/01/2018.
 * // TODO @eti: Javadoc
 */
public class LogFileNotFound extends MessagePlayerException {

    public LogFileNotFound(String s, Throwable e) {
        super(s, e);
    }
}
