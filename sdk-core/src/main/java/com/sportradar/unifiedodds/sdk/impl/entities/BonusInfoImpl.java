/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.BonusInfoCI;
import com.sportradar.unifiedodds.sdk.entities.BonusDrumType;
import com.sportradar.unifiedodds.sdk.entities.BonusInfo;

/**
 * A basic implementation of the {@link BonusInfo}
 */
public class BonusInfoImpl implements BonusInfo {

    private final String bonusRange;
    private final BonusDrumType bonusDrumType;
    private final Integer bonusBalls;

    BonusInfoImpl(BonusInfoCI bonusInfo) {
        Preconditions.checkNotNull(bonusInfo);

        bonusBalls = bonusInfo.getBonusBalls();
        bonusDrumType = bonusInfo.getBonusDrumType();
        bonusRange = bonusInfo.getBonusRange();
    }

    /**
     * Returns the number of bonus balls
     *
     * @return the number of bonus balls
     */
    @Override
    public Integer getBonusBalls() {
        return bonusBalls;
    }

    /**
     * Returns a description of the bonus drum
     *
     * @return a description of the bonus drum
     */
    @Override
    public BonusDrumType getBonusDrumType() {
        return bonusDrumType;
    }

    /**
     * Returns the bonus range
     *
     * @return the bonus range
     */
    @Override
    public String getBonusRange() {
        return bonusRange;
    }
}
