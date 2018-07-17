/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

/**
 * Created on 21/06/2017.
 * // TODO @eti: Javadoc
 */
public interface Operand {
    int getIntValue();
    double getDecimalValue();
    String getStringValue();
}
