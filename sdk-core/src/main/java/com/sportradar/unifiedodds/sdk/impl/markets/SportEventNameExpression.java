/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;

import java.util.Locale;

/**
 * Created on 12/12/2017.
 * // TODO @eti: Javadoc
 */
public class SportEventNameExpression implements NameExpression {
    private final SportEvent sportEvent;

    SportEventNameExpression(SportEvent sportEvent) {
        Preconditions.checkNotNull(sportEvent);

        this.sportEvent = sportEvent;
    }

    @Override
    public String buildName(Locale locale) {
        Preconditions.checkNotNull(locale);

        String name = sportEvent.getName(Locale.ENGLISH);

        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalStateException("Could not provide the requested sport event name, event:" + sportEvent + ", locale:" + locale);
        }

        return name;
    }
}
