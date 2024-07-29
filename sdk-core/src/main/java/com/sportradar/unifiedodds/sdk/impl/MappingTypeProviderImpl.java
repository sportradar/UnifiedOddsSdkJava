/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.utils.Urn;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements methods used find the proper associated mapping type
 */
@SuppressWarnings({ "CyclomaticComplexity", "ReturnCount" })
public class MappingTypeProviderImpl implements MappingTypeProvider {

    private static Logger logger = LoggerFactory.getLogger(MappingTypeProviderImpl.class);

    @Inject
    public MappingTypeProviderImpl() {
        // nothing yet - mapping is statically available within the SDK
    }

    /**
     * Identifies the proper mapping type associated with the
     *
     * @param identifier the type to which the provided identifier can be mapped
     * @return the {@link Class} to which the id can be mapped, empty optional if the mapping type could not be provided
     */
    @Override
    public Optional<Class> getMappingType(Urn identifier) {
        Preconditions.checkNotNull(identifier);

        switch (identifier.getType()) {
            case "stage":
            case "race_tournament":
            case "race_event":
            case "competition_group":
                return Optional.of(Stage.class);
            case "match":
                return Optional.of(Match.class);
            case "simple_tournament":
                return Optional.of(BasicTournament.class);
            case "season":
                return Optional.of(Season.class);
            case "tournament":
                return Optional.of(Tournament.class);
            case "lottery":
                return Optional.of(Lottery.class);
            case "draw":
                return Optional.of(Draw.class);
            default:
                logger.warn("Mapping type could not be provided for [{}]", identifier);
                return Optional.empty();
        }
    }
}
