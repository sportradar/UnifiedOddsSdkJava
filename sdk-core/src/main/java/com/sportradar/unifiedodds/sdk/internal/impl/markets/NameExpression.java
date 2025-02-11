/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import java.util.Locale;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
public interface NameExpression {
    String buildName(Locale locale);
}
