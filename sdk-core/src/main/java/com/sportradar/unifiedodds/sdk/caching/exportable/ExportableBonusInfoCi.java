package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BonusDrumType;
import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableBonusInfoCi implements Serializable {

    private Integer bonusBalls;
    private BonusDrumType bonusDrumType;
    private String bonusRange;

    public ExportableBonusInfoCi(Integer bonusBalls, BonusDrumType bonusDrumType, String bonusRange) {
        this.bonusBalls = bonusBalls;
        this.bonusDrumType = bonusDrumType;
        this.bonusRange = bonusRange;
    }

    public Integer getBonusBalls() {
        return bonusBalls;
    }

    public void setBonusBalls(Integer bonusBalls) {
        this.bonusBalls = bonusBalls;
    }

    public BonusDrumType getBonusDrumType() {
        return bonusDrumType;
    }

    public void setBonusDrumType(BonusDrumType bonusDrumType) {
        this.bonusDrumType = bonusDrumType;
    }

    public String getBonusRange() {
        return bonusRange;
    }

    public void setBonusRange(String bonusRange) {
        this.bonusRange = bonusRange;
    }
}
