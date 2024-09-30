/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import java.util.Map;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "MissingSwitchDefault" })
public class ExpressionOperand extends OperandBase implements Operand {

    private final Map<String, String> specifiers;
    private final String operandString;
    private final SimpleMathOperation operation;
    private final int staticValue;

    ExpressionOperand(
        Map<String, String> specifiers,
        String operandString,
        SimpleMathOperation operation,
        int staticValue
    ) {
        Preconditions.checkNotNull(specifiers);
        Preconditions.checkNotNull(operation);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(operandString));
        Preconditions.checkArgument(!specifiers.isEmpty());

        this.specifiers = specifiers;
        this.operandString = operandString;
        this.operation = operation;
        this.staticValue = staticValue;
    }

    @Override
    public int getIntValue() {
        int val = parseSpecifierInt(operandString, specifiers);

        return (int) calculateValue(val);
    }

    @Override
    public double getDecimalValue() {
        double val = parseSpecifierDouble(operandString, specifiers);

        return calculateValue(val);
    }

    @Override
    public String getStringValue() {
        String value = provideSpecifierValue(operandString, specifiers);
        Integer intValue = Ints.tryParse(value);
        if (intValue != null) {
            return String.valueOf(getIntValue());
        }
        throw new UnsupportedOperationException();
    }

    private double calculateValue(double value) {
        switch (operation) {
            case ADD:
                return value + staticValue;
            case SUBTRACT:
                return value - staticValue;
        }

        throw new IllegalArgumentException("Unsupported operation type " + operation);
    }
}
