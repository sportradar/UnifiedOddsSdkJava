package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.oddsentities.AdditionalProbabilities;

@SuppressWarnings({ "MemberName", "ParameterName" })
public class AdditionalProbabilitiesImpl implements AdditionalProbabilities {

    private final Double win;
    private final Double lose;
    private final Double half_win;
    private final Double half_lose;
    private final Double refund;

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
