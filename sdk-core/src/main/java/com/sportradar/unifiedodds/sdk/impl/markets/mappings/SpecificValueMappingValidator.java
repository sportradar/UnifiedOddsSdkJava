/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets.mappings;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;

import java.util.Map;


/**
 * A {@link MappingValidator} which checks the value of the specifier against a specific value
 */
public class SpecificValueMappingValidator implements MappingValidator {

    /**
     * The name of the specifier as specified in the valid_for attribute
     */
    private final String specifierName;

    /**
     * The required value of the specifier
     */
    private final String specifierValue;

    /**
     * Initializes a new instance of the {@link SpecificValueMappingValidator} class
     * @param specifierName The name of the specifier as specified in the valid_for attribute
     * @param specifierValue The required value of the specifier
     */
    SpecificValueMappingValidator(String specifierName, String specifierValue) {
        Preconditions.checkArgument(specifierName != null && !specifierName.isEmpty(), "specifierName cannot be a null reference or empty string");
        Preconditions.checkArgument(specifierValue != null && !specifierValue.isEmpty(), "specifierValue cannot be a null reference or empty string");

        this.specifierName = specifierName;
        this.specifierValue = specifierValue;
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

        if(!specifiers.containsKey(specifierName)){
            throw new IllegalArgumentException(String.format("The provided specifiers do not contain a specifier named %s", specifierName));
        }
        return specifierValue.equals(specifiers.get(specifierName));
    }

    /**
     * Constructs and returns a string representation of the current instance
     * @return A string representation of the current instance
     */
    @Override
    public String toString() {
        return String.format("%s=%s", specifierName,  specifierValue);
    }
}
