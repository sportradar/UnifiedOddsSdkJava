/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets.mappings;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Represents a {@link MappingValidator} wrapper around other mapping validators
 */
public class CompositeMappingValidator implements MappingValidator {

    /**
     * A list of {@link MappingValidator} used for actual validation
     */
    private final List<MappingValidator> validators;


    CompositeMappingValidator(List<MappingValidator> validators){
        Preconditions.checkArgument(validators != null && !validators.isEmpty(), "validators cannot be a null reference or an empty list");

        this.validators = new ArrayList<>(validators);
    }

    /**
     * Determines whether a specific mapping can be used to map market with provided specifiers
     *
     * @param specifiers A {@link Map} containing market specifiers
     * @return True if the associated market can be mapped with associated mapping; Otherwise false.
     */
    @Override
    public boolean validate(Map<String, String> specifiers) {
        return validators.stream().allMatch(v -> v.validate(specifiers));
    }

    /**
     * Constructs and returns a string representation of the current instance
     * @return A string representation of the current instance
     */
    @Override
    public String toString() {
        return String.join("|", validators.stream().map(Object::toString).collect(Collectors.toList()));
    }
}
