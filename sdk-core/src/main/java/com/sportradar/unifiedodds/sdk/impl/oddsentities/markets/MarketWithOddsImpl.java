/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UFFavourite;
import com.sportradar.uf.datamodel.UFMarketMetadata;
import com.sportradar.uf.datamodel.UFMarketStatus;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 24/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName", "ParameterNumber" })
class MarketWithOddsImpl extends MarketImpl implements MarketWithOdds {

    private static final Logger logger = LoggerFactory.getLogger(MarketWithOddsImpl.class);
    private final MarketStatus status;
    private final List<OutcomeOdds> outcomeOdds;
    private final boolean favourite;
    private final MarketMetadata marketMetadata;

    MarketWithOddsImpl(
        int id,
        NameProvider nameProvider,
        Map<String, String> specifiersMap,
        Map<String, String> extendedSpecifiers,
        MarketDefinition marketDefinition,
        Locale defaultLocale,
        UFMarketStatus status,
        UFFavourite favourite,
        UFMarketMetadata marketMetadata,
        List<OutcomeOdds> outcomes
    ) {
        super(id, nameProvider, specifiersMap, extendedSpecifiers, marketDefinition, defaultLocale);
        MarketStatus stat = MarketStatus.fromFeedValue(status);
        if (stat == null) {
            logger.warn("Defaulting market status to deactivated, id:{}", id);
            stat = MarketStatus.Deactivated;
        }
        this.status = stat;

        this.favourite = favourite != null && favourite == UFFavourite.YES;
        this.marketMetadata = marketMetadata == null ? null : new MarketMetadataImpl(marketMetadata);
        this.outcomeOdds = outcomes;
    }

    /**
     * Are odds for this market active
     *
     * @return true if odds for this market are active
     */
    @Override
    public MarketStatus getStatus() {
        return status;
    }

    /**
     * @return a list of odds for the different outcomes for this market or null if this market is
     * inactive
     */
    @Override
    public List<OutcomeOdds> getOutcomeOdds() {
        return outcomeOdds;
    }

    /**
     * Only applicable if multiple market lines of the same market type is provided in the odds
     * update
     *
     * @return in case we provide multiple lines for the same market - this reports if this line is
     * the recommended one (this often but not always means the most balanced)
     */
    @Override
    public boolean isFavourite() {
        return favourite;
    }

    /**
     * Returns a {@link MarketMetadata} which contains additional market information
     *
     * @return a {@link MarketMetadata} which contains additional market information
     */
    @Override
    public MarketMetadata getMarketMetadata() {
        return marketMetadata;
    }
}
