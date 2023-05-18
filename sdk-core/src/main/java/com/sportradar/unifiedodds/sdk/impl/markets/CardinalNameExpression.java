/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import java.util.Locale;

/**
 * Created on 21/06/2017.
 * // TODO @eti: Javadoc
 */
public class CardinalNameExpression implements NameExpression {

    private final Operand operand;

    CardinalNameExpression(Operand operand) {
        Preconditions.checkNotNull(operand);

        this.operand = operand;
    }

    @Override
    public String buildName(Locale locale) {
        return operand.getStringValue();
    }
}
