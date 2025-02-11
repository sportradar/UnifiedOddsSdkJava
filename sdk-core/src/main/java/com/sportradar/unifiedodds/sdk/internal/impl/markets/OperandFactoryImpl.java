/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import java.util.Map;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "BooleanExpressionComplexity", "ParameterAssignment", "UnnecessaryParentheses" })
public class OperandFactoryImpl implements OperandFactory {

    @Override
    public Operand buildOperand(Map<String, String> specifiers, String operandExpression) {
        if (
            (operandExpression.startsWith("(") && !operandExpression.endsWith(")")) ||
            (!operandExpression.startsWith("(") && operandExpression.endsWith(")"))
        ) {
            throw new IllegalArgumentException(
                "Format of the operand " +
                operandExpression +
                " is not correct. It contains un-closing parenthesis."
            );
        }

        if (operandExpression.startsWith("(") && operandExpression.endsWith(")")) {
            operandExpression = operandExpression.substring(1, operandExpression.length() - 1);

            SimpleMathOperation operation;
            String[] parts;
            if (operandExpression.contains("+")) {
                operation = SimpleMathOperation.ADD;
                parts = operandExpression.split("\\+");
            } else if (operandExpression.contains("-")) {
                operation = SimpleMathOperation.SUBTRACT;
                parts = operandExpression.split("-");
            } else {
                throw new IllegalArgumentException(
                    "Format of operand " +
                    operandExpression +
                    " is not correct. It does not contain an operation identifier"
                );
            }

            if (parts.length != 2) {
                throw new IllegalArgumentException(
                    "Format of operand " +
                    operandExpression +
                    " is not correct. It contains more than one operation identifier"
                );
            }

            int staticValue = Integer.parseInt(parts[1]);
            return new ExpressionOperand(specifiers, parts[0], operation, staticValue);
        }

        return new SimpleOperand(specifiers, operandExpression);
    }
}
