package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.Hole;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.HoleCi;

public class HoleImpl implements Hole {

    private final int number;
    private final int par;

    public HoleImpl(HoleCi ci) {
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
