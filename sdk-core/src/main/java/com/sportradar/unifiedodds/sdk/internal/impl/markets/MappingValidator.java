/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import java.util.Map;

/**
 * Represents a mapping validator used to determine whether a specific mapping can be used with specific market
 */
public interface MappingValidator {
    /**
     * Determines whether a specific mapping can be used to map market with provided specifiers
     * @param specifiers A {@link Map} containing market specifiers
     * @return True if the associated market can be mapped with associated mapping; Otherwise false.
     */
    boolean validate(Map<String, String> specifiers);
}
