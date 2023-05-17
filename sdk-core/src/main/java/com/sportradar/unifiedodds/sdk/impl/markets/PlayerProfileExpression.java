/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import java.util.Collections;
import java.util.Locale;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
public class PlayerProfileExpression implements NameExpression {

    private final Operand operand;
    private final ProfileCache profileCache;

    PlayerProfileExpression(ProfileCache profileCache, Operand operand) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(operand);

        this.profileCache = profileCache;
        this.operand = operand;
    }

    @Override
    public String buildName(Locale locale) {
        URN playerId = URN.parse(operand.getStringValue());
        PlayerProfileCI playerProfile;
        try {
            playerProfile = profileCache.getPlayerProfile(playerId, Lists.newArrayList(locale), null);
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            throw new IllegalStateException(
                "Could not build the requested player profile expression, id:" +
                operand.getStringValue() +
                ", locale:" +
                locale,
                e
            );
        }

        if (playerProfile.getNames(Collections.singletonList(locale)).get(locale) != null) {
            return playerProfile.getNames(Collections.singletonList(locale)).get(locale);
        }

        throw new IllegalStateException(
            "Could not build the requested player profile expression with the provided locale, id:" +
            operand.getStringValue() +
            ", locale:" +
            locale
        );
    }
}
