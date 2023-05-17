package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.HoleCI;
import com.sportradar.unifiedodds.sdk.entities.Hole;

public class HoleImpl implements Hole {

    private final int number;
    private final int par;

    public HoleImpl(HoleCI ci) {
        Preconditions.checkNotNull(ci);
        this.number = ci.getNumber();
        this.par = ci.getPar();
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getPar() {
        return par;
    }
}
