package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.AdditionalProbabilities;

public class AdditionalProbabilitiesImpl implements AdditionalProbabilities {

    private Double win;
    private Double lose;
    private Double half_win;
    private Double half_lose;
    private Double refund;

    AdditionalProbabilitiesImpl(Double win, Double lose, Double half_win, Double half_lose, Double refund) {
        this.win = win;
        this.lose = lose;
        this.half_win = half_win;
        this.half_lose = half_lose;
        this.refund = refund;
    }

    @Override
    public Double getWin() {
        return win;
    }

    @Override
    public Double getLose() {
        return lose;
    }

    @Override
    public Double getHalfWin() {
        return half_win;
    }

    @Override
    public Double getHalfLose() {
        return half_lose;
    }

    @Override
    public Double getRefund() {
        return refund;
    }
}
