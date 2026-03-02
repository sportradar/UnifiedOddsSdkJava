/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.oddsentities.OutcomeResult.UnsupportedBySdk;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.datamodel.UfResult;
import com.sportradar.uf.datamodel.UfVoidFactor;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeResult;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 26/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ParameterNumber" })
class OutcomeSettlementImpl extends OutcomeImpl implements OutcomeSettlement {

    private static final Map<UfResult, OutcomeResult> OUTCOME_RESULTS = ImmutableMap
        .<UfResult, OutcomeResult>builder()
        .put(UfResult.WON, OutcomeResult.Won)
        .put(UfResult.LOST, OutcomeResult.Lost)
        .put(UfResult.UNDECIDED_YET, OutcomeResult.UndecidedYet)
        .build();

    private final double voidFactor;
    private final double deadHeatFactor;
    private final OutcomeResult outcomeResult;

    OutcomeSettlementImpl(
        String id,
        NameProvider nameProvider,
        OutcomeDefinition outcomeDefinition,
        Locale defaultLocale,
        UfResult result,
        UfVoidFactor voidFactor,
        Double deadHeatFactor
    ) {
        super(id, nameProvider, outcomeDefinition, defaultLocale);
        this.voidFactor = voidFactor == null ? 0.0 : voidFactor.value();
        this.deadHeatFactor = deadHeatFactor == null ? 1 : deadHeatFactor;
        this.outcomeResult = OUTCOME_RESULTS.getOrDefault(result, UnsupportedBySdk);
    }

    /**
     * Under certain circumstances the whole bet is refunded or half the bet is refunded
     *
     * @return 1 if the whole bet is refunded (regardless of win or loss), 0.5 if half the bet is
     * refunded (the other half is payed out if it is a win otherwise lost), 0 no refund
     */
    @Override
    public double getVoidFactor() {
        return voidFactor;
    }

    /**
     * Dead-heat Factor (A dead-heat factor may be returned for markets where a bet has be placed on
     * a particular team/player to place and this particular player has placed but the place is
     * shared with multiple players, reducing the payout)
     *
     * @return deadheat factor or 1 if none.
     */
    @Override
    public double getDeadHeatFactor() {
        return deadHeatFactor;
    }

    /**
     * Returns an indication of the outcome result state
     *
     * @return an indication of the outcome result state
     */
    @Override
    public OutcomeResult getOutcomeResult() {
        return outcomeResult;
    }
}
