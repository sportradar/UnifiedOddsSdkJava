/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.internal.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import com.sportradar.utils.Urn;
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
        Urn competitorId = Urn.parse(operand.getStringValue());
        CompetitorCi competitorProfile;
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
