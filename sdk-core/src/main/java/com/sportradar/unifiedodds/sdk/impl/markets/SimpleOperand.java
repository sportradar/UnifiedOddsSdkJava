/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;

import java.util.Map;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public class SimpleOperand extends OperandBase implements Operand {
    private final Map<String, String> specifiers;
    private final String operandExpression;

    SimpleOperand(Map<String, String> specifiers, String operandExpression) {
        Preconditions.checkNotNull(specifiers);
        Preconditions.checkNotNull(operandExpression);
        Preconditions.checkArgument(!specifiers.isEmpty());

        this.specifiers = specifiers;
        this.operandExpression = operandExpression;
    }

    @Override
    public int getIntValue() {
        return parseSpecifierInt(operandExpression, specifiers);
    }

    @Override
    public double getDecimalValue() {
        return parseSpecifierDouble(operandExpression, specifiers);
    }

    @Override
    public String getStringValue() {
        return provideSpecifierValue(operandExpression, specifiers);
    }
}
