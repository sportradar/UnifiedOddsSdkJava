/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UfOutcomeActive;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.AdditionalProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsDisplayType;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Locale;

/**
 * Created on 26/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "MagicNumber", "OverloadMethodsDeclarationOrder", "ParameterNumber" })
class OutcomeOddsImpl extends OutcomeProbabilitiesImpl implements OutcomeOdds {

    private final double odds;

    OutcomeOddsImpl(
        String id,
        NameProvider nameProvider,
        OutcomeDefinition outcomeDefinition,
        Locale defaultLocale,
        UfOutcomeActive active,
        Double odds,
        Double probability,
        AdditionalProbabilities additionalProbabilities
    ) {
        super(
            id,
            nameProvider,
            outcomeDefinition,
            defaultLocale,
            active,
            probability,
            additionalProbabilities
        );
        this.odds = odds == null ? Double.NaN : odds;
    }

    /**
     * Indicates if the outcome is {@link com.sportradar.unifiedodds.sdk.oddsentities.PlayerOutcomeOdds} instance
     *
     * @return <code>true</code> if the current outcome is a player outcome, otherwise <code>false</code>
     */
    @Override
    public boolean isPlayerOutcome() {
        return false;
    }

    @Override
    public Double getOdds(OddsDisplayType oddsDisplayType) {
        if (oddsDisplayType == null || oddsDisplayType == OddsDisplayType.Decimal) {
            return this.odds;
        }

        return convertEuOddsToUs(this.odds);
    }

    /**
     * Convert decimal EU odds to decimal American odds
     *
     * <p>if EU_odds >= 2 then US_odds=(EU_odds - 1)</p>
     * <p>100 if EU_odds <2 then US_odds=(-100)/(EU_odds-1)</p>
     *
     * @param oddsEuDouble EU odds not null
     */
    private static Double convertEuOddsToUs(Double oddsEuDouble) {
        if (oddsEuDouble == null || oddsEuDouble.isNaN()) {
            return oddsEuDouble;
        }
        BigDecimal oddsEu = BigDecimal.valueOf(oddsEuDouble);
        Double oddUs;
        if (oddsEu.doubleValue() == 1) {
            oddUs = null;
        } else if (oddsEu.doubleValue() >= 2) {
            oddUs = oddsEu.subtract(BigDecimal.valueOf(1d)).multiply(BigDecimal.valueOf(100)).doubleValue();
        } else {
            oddUs =
                (BigDecimal.valueOf(-100d)).divide(
                        oddsEu.subtract(BigDecimal.valueOf(1)),
                        MathContext.DECIMAL128
                    )
                    .doubleValue();
        }

        return oddUs;
    }
}
