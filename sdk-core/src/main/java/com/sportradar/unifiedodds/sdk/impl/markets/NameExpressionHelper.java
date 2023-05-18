/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName", "HideUtilityClassConstructor", "MagicNumber", "ModifiedControlVariable" })
class NameExpressionHelper {

    private static final List<String> definedOperators = ImmutableList.copyOf(
        new String[] { "+", "-", "$", "!", "%" }
    );

    static AbstractMap.SimpleImmutableEntry<String, List<String>> parseDescriptor(String descriptor) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(descriptor));

        List<String> expressions = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < descriptor.length(); currentIndex++) {
            int startIndex = descriptor.indexOf("{", currentIndex);
            int endIndex = descriptor.indexOf("}", currentIndex);

            if (startIndex < 0 && endIndex < 0) {
                break;
            }

            if (startIndex < 0 || endIndex < 0 || endIndex <= startIndex) {
                throw new IllegalArgumentException(
                    "Format of the descriptor is incorrect. Each opening '{' must be closed by corresponding '}'"
                );
            }

            String expression = descriptor.substring(startIndex, endIndex + 1);

            expressions.add(expression);
            currentIndex = endIndex;
        }

        if (expressions.isEmpty()) {
            return null;
        }

        String format = descriptor;
        for (String expression : expressions) {
            format = format.replace(expression, "%s");
        }

        return new AbstractMap.SimpleImmutableEntry<>(format, expressions);
    }

    static AbstractMap.SimpleImmutableEntry<String, String> parseExpression(String expression) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(expression));

        if (expression.length() < 3) {
            throw new IllegalArgumentException(
                "Format of the 'expression' is not correct. Minimum required length is 3"
            );
        }
        if (!expression.startsWith("{")) {
            throw new IllegalArgumentException(
                "Format of the 'expression' is not correct. It must start with char '{'"
            );
        }
        if (!expression.endsWith("}")) {
            throw new IllegalArgumentException(
                "Format of the 'expression' is not correct. It must end with char '}'"
            );
        }

        String operator = expression.substring(1, 2);
        String operand;
        if (!definedOperators.contains(operator)) {
            operator = null;
            operand = expression.substring(1, expression.length() - 1);
        } else {
            operand = expression.substring(2, expression.length() - 1);
        }

        return new AbstractMap.SimpleImmutableEntry<>(operand, operator);
    }
}
