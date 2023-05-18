/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created on 03/10/2017.
 * // TODO @eti: Javadoc
 */
public class CompetitorProfileExpression implements NameExpression {

    private final Operand operand;
    private final ProfileCache profileCache;

    CompetitorProfileExpression(ProfileCache profileCache, Operand operand) {
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(operand);

        this.profileCache = profileCache;
        this.operand = operand;
    }

    @Override
    public String buildName(Locale locale) {
        URN competitorId = URN.parse(operand.getStringValue());
        CompetitorCI competitorProfile;
        try {
            competitorProfile = profileCache.getCompetitorProfile(competitorId, Lists.newArrayList(locale));
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            throw new IllegalStateException(
                "Could not build the requested competitor profile expression, id:" +
                operand.getStringValue() +
                ", locale:" +
                locale,
                e
            );
        }

        List<Locale> locales = Collections.singletonList(locale);
        if (competitorProfile.getNames(locales).get(locale) != null) {
            return competitorProfile.getNames(locales).get(locale);
        }

        throw new IllegalStateException(
            "Could not build the requested competitor profile expression with the provided locale, id:" +
            operand.getStringValue() +
            ", locale:" +
            locale
        );
    }
}
