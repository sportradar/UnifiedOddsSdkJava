package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCarCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableHoleCI;

public class HoleCI {
    private final int number;
    private final int par;

    public HoleCI(int number, int par) {
        this.number = number;
        this.par = par;
    }

    HoleCI(ExportableHoleCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.number = exportable.getNumber();
        this.par = exportable.getPar();
    }

    public int getNumber(){ return number; }

    public int getPar(){ return par; }

    public ExportableHoleCI export() { return new ExportableHoleCI(number, par); }

}
