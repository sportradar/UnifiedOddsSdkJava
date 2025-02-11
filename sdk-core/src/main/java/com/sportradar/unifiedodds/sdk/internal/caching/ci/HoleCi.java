package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableHoleCi;

public class HoleCi {

    private final int number;
    private final int par;

    public HoleCi(SapiHole hole) {
        this.number = hole.getNumber();
        this.par = hole.getPar();
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
