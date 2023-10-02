package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableHoleCi;

public class HoleCi {

    private final int number;
    private final int par;

    public HoleCi(int number, int par) {
        this.number = number;
        this.par = par;
    }

    HoleCi(ExportableHoleCi exportable) {
        Preconditions.checkNotNull(exportable);
        this.number = exportable.getNumber();
        this.par = exportable.getPar();
    }

    public int getNumber() {
        return number;
    }

    public int getPar() {
        return par;
    }

    public ExportableHoleCi export() {
        return new ExportableHoleCi(number, par);
    }
}
