/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIBonusDrumType;
import com.sportradar.uf.sportsapi.datamodel.SAPILottery;
import com.sportradar.unifiedodds.sdk.entities.BonusDrumType;

/**
 * A basic bonus info cache representation
 */
public class BonusInfoCI {

    private final Integer bonusBalls;
    private final BonusDrumType bonusDrumType;
    private final String bonusRange;

    public BonusInfoCI(SAPILottery.SAPIBonusInfo bonusInfo) {
        Preconditions.checkNotNull(bonusInfo);

        bonusBalls = bonusInfo.getBonusBalls();
        bonusDrumType = map(bonusInfo.getBonusDrum());
        bonusRange = bonusInfo.getBonusRange();
    }

    public Integer getBonusBalls() {
        return bonusBalls;
    }

    public BonusDrumType getBonusDrumType() {
        return bonusDrumType;
    }

    public String getBonusRange() {
        return bonusRange;
    }

    private static BonusDrumType map(SAPIBonusDrumType bonusDrum) {
        if (bonusDrum == null) {
            return null;
        }

        switch (bonusDrum) {
            case SAME:
                return BonusDrumType.Same;
            case ADDITIONAL:
                return BonusDrumType.Additional;
            default:
                return null;
        }
    }
}
