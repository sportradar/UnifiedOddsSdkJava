/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets.mappings;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;


/**
 * A {@link MappingValidator} which checks whether a value of the specific specifier has the required decimal part
 */
public class DecimalValueMappingValidator implements MappingValidator{

    /**
     * The name of the specifier as specified in the valid_for attribute
     */
    private final String specifierName;

    /**
     * The required value of the decimal part of the specifier
     */
    private final BigDecimal requiredDecimalValue;

    /**
     * Initializes a new instance of the {@link DecimalValueMappingValidator} class
     * @param specifierName The name of the specifier as specified in the valid_for attribute
     * @param requiredDecimalValue The required value of the decimal part of the specifier
     */
    DecimalValueMappingValidator(String specifierName, BigDecimal requiredDecimalValue) {
        Preconditions.checkArgument(specifierName != null && !specifierName.isEmpty(), "specifierName cannot be a null reference or empty string");
        Preconditions.checkArgument(requiredDecimalValue != null, "requiredDecimalValue cannot be a null reference");

        this.specifierName = specifierName;
        this.requiredDecimalValue = requiredDecimalValue;
    }

    /**
     * Determines whether a specific mapping can be used to map market with provided specifiers
     *
     * @param specifiers A {@link Map} containing market specifiers
     * @return True if the associated market can be mapped with associated mapping; Otherwise false.
     */
    @Override
    public boolean validate(Map<String, String> specifiers) {
        Preconditions.checkArgument(specifiers != null && !specifiers.isEmpty(), "specifier cannot be an empty map or a null reference");

        if (!specifiers.containsKey(specifierName)) {
            throw new IllegalArgumentException(String.format("The provided specifiers[%s] do not contain a specifier named %s", specifiers, specifierName));
        }

        BigDecimal value;
        try {
            value = new BigDecimal(specifiers.get(specifierName));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(String.format("Value %s is not a valid string representation of decimal value", specifiers.get(specifierName)));
        }

        BigDecimal roundedValue = value.setScale(0, RoundingMode.FLOOR);
        return requiredDecimalValue.compareTo(value.subtract(roundedValue)) == 0;
    }

    /**
     * Constructs and returns a string representation of the current instance
     * @return A string representation of the current instance
     */
    @Override
    public String toString() {
        return String.format("%s~%s", specifierName, requiredDecimalValue.toString().replace("0", "*"));
    }
}
