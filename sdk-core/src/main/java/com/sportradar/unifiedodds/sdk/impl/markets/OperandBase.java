/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.Map;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
abstract class OperandBase {

    int parseSpecifierInt(String specifierName, Map<String, String> specifiers) {
        Preconditions.checkNotNull(specifiers);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(specifierName));
        Preconditions.checkArgument(!specifiers.isEmpty());

        String specifierValue = provideSpecifierValue(specifierName, specifiers);
        try {
            return Integer.parseInt(specifierValue);
        } catch (NumberFormatException e) {
            String sf = "Specifier[k=%s, v=%s] must be a string representation of an integer";
            throw new IllegalArgumentException(String.format(sf, specifierName, specifierValue), e);
        }
    }

    double parseSpecifierDouble(String specifierName, Map<String, String> specifiers) {
        Preconditions.checkNotNull(specifiers);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(specifierName));
        Preconditions.checkArgument(!specifiers.isEmpty());

        String specifierValue = provideSpecifierValue(specifierName, specifiers);
        try {
            return Double.parseDouble(specifierValue);
        } catch (NumberFormatException e) {
            String sf = "Specifier[k=%s, v=%s] must be a string representation of a double";
            throw new IllegalArgumentException(String.format(sf, specifierName, specifierValue), e);
        }
    }

    String provideSpecifierValue(String specifierName, Map<String, String> specifiers) {
        Preconditions.checkNotNull(specifiers);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(specifierName));
        Preconditions.checkArgument(!specifiers.isEmpty());

        if (!specifiers.containsKey(specifierName)) {
            throw new IllegalArgumentException(
                "Specifier with name " + specifierName + " does not exist on the provided market specifiers"
            );
        }

        return specifiers.get(specifierName);
    }
}
