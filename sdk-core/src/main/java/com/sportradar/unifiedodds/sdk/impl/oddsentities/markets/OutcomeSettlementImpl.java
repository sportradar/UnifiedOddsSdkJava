/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UfResult;
import com.sportradar.uf.datamodel.UfVoidFactor;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeResult;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import java.util.Locale;

/**
 * Created on 26/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ParameterNumber" })
class OutcomeSettlementImpl extends OutcomeImpl implements OutcomeSettlement {

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

        switch (result) {
            case WON:
                outcomeResult = OutcomeResult.Won;
                break;
            case LOST:
                outcomeResult = OutcomeResult.Lost;
                break;
            case UNDECIDED_YET:
                outcomeResult = OutcomeResult.UndecidedYet;
                break;
            default:
                outcomeResult = OutcomeResult.Lost;
                break;
        }
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
