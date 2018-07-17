/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets.mappings;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidator;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A {@link MappingValidatorFactory} used to construct {@link MappingValidator} instances
 */
public class MappingValidatorFactoryImpl implements MappingValidatorFactory{

    /**
     * A regex pattern used to detect validators requiring specific decimal value
     */
    private static final Pattern decimalPatternRegex = Pattern.compile("\\A\\*\\.\\d{1,2}\\z");

    /**
     * Constructs and returns a {@link MappingValidator} for a single specifier
     * @param name The name of the specifier
     * @param value The required value or required value format
     * @return A {@link MappingValidator} constructed from provided data
     */
    private static MappingValidator buildSingle(String name, String value){
        Preconditions.checkArgument(name != null && !name.isEmpty(), "name cannot be a null reference or an empty string");
        Preconditions.checkArgument(value != null && !value.isEmpty(), "value cannot be a null reference or an empty string");

        Matcher matcher = decimalPatternRegex.matcher(value);
        return matcher.find()
                ? new DecimalValueMappingValidator(name, new BigDecimal(value.replace("*", "0")))
                : new SpecificValueMappingValidator(name, value);
    }

    /**
     * Builds and returns a {@link MappingValidator} from it's string representation
     *
     * @param validatorString A string representation of the mapping validator
     * @return A {@link MappingValidator} build from it's string representation
     */
    @Override
    public MappingValidator build(String validatorString) {
        Preconditions.checkArgument(validatorString != null && ! validatorString.isEmpty(), "validatiorString cannot be a null reference or an empty string");

        Map<String, String> specifiers = split(validatorString);

        if(specifiers.size() == 1){
            String firstKey = specifiers.keySet().iterator().next();
            return buildSingle(firstKey, specifiers.get(firstKey));
        }

        List<MappingValidator> validators = new ArrayList<>(specifiers.size());
        for(String key : specifiers.keySet()){
            validators.add(buildSingle(key, specifiers.get(key)));
        }
        return new CompositeMappingValidator(validators);

    }
}
