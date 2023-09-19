/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiBonusDrumType;
import com.sportradar.uf.sportsapi.datamodel.SapiLottery;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableBonusInfoCi;
import com.sportradar.unifiedodds.sdk.entities.BonusDrumType;

/**
 * A basic bonus info cache representation
 */
public class BonusInfoCi {

    private final Integer bonusBalls;
    private final BonusDrumType bonusDrumType;
    private final String bonusRange;

    public BonusInfoCi(SapiLottery.SapiBonusInfo bonusInfo) {
        Preconditions.checkNotNull(bonusInfo);

        bonusBalls = bonusInfo.getBonusBalls();
        bonusDrumType = map(bonusInfo.getBonusDrum());
        bonusRange = bonusInfo.getBonusRange();
    }

    public BonusInfoCi(ExportableBonusInfoCi exportable) {
        Preconditions.checkNotNull(exportable);

        bonusBalls = exportable.getBonusBalls();
        bonusDrumType = exportable.getBonusDrumType();
        bonusRange = exportable.getBonusRange();
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

    private static BonusDrumType map(SapiBonusDrumType bonusDrum) {
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

    public ExportableBonusInfoCi export() {
        return new ExportableBonusInfoCi(bonusBalls, bonusDrumType, bonusRange);
    }
}
