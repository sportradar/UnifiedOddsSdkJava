/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import java.util.HashMap;
import java.util.Map;

/**
 * A factory used to build {@link MappingValidator} instances
 */
public interface MappingValidatorFactory {

    /**
     * Builds and returns a {@link MappingValidator} from it's string representation
     * @param validatorString A string representation of the mapping validator
     * @return A {@link MappingValidator} build from it's string representation
     */
    MappingValidator build(String validatorString);

    /**
     * Constructs a {@link Map} containing validation entries composed from the provided string representation
     * @param validationString A string representation of the required validators
     * @return A {@link Map} containing validation entries composed from the provided string representation
     */
    default Map<String, String> split(String validationString){

        String[] parts = validationString.split("\\|");

        Map<String, String> validators = new HashMap<>(parts.length);

        for(String part : parts){
            String[] validatorParts = part.split("[=~]");
            if(validatorParts.length != 2){
                throw new IllegalArgumentException(String.format("Value %s is not a valid validators string", validationString));
            }

            if(validatorParts[0] == null || validatorParts[0].isEmpty() || validatorParts[1] == null || validatorParts[1].isEmpty()){
                throw new IllegalArgumentException(String.format("Value %s is not a valid validators string", validationString));
            }

            if(validators.containsKey(validatorParts[0])){
                throw new IllegalArgumentException(String.format("Value %s is not a valid validators string. It contains duplicate validators", validationString));
            }
            validators.put(validatorParts[0], validatorParts[1]);
        }
        return validators;
    }
}
