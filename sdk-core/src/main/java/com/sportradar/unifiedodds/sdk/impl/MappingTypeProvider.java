/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.utils.Urn;
import java.util.Optional;

/**
 * Defines methods used to get the associated mapping type
 */
public interface MappingTypeProvider {
    /**
     * Identifies the proper mapping type associated with the
     * @param identifier the type to which the provided identifier can be mapped
     * @return the {@link Class} to which the id can be mapped, empty optional if the mapping type could not be provided
     */
    Optional<Class> getMappingType(Urn identifier);
}
