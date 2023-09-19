/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UfOddsChange;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.entities.OddsGenerationImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MethodLength", "ParameterNumber" })
class OddsChangeImpl<T extends SportEvent> extends EventMessageImpl<T> implements OddsChange<T> {

    private static final Logger logger = LoggerFactory.getLogger(OddsChangeImpl.class);
    private final OddsChangeReason oddsChangeReason;
    private final Integer betstopReason;
    private final Integer bettingStatus;
    private final List<MarketWithOdds> affectedMarkets;
    private final NamedValuesProvider namedValuesProvider;
    private final OddsGeneration oddsGeneration;

    OddsChangeImpl(
        T sportEvent,
        UfOddsChange message,
        Producer producer,
        byte[] rawMessage,
        final MarketFactory marketFactory,
        final NamedValuesProvider namedValuesProvider,
        MessageTimestamp timestamp
    ) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        Preconditions.checkNotNull(marketFactory, "marketFactory");
        Preconditions.checkNotNull(namedValuesProvider, "namedValuesProvider");
        this.namedValuesProvider = namedValuesProvider;

        // TODO update schemas to get more odds change reasons?
        if (message.getOddsChangeReason() == null) {
            oddsChangeReason = OddsChangeReason.Normal;
        } else {
            switch (message.getOddsChangeReason()) {
                case RISKADJUSTMENT_UPDATE:
                    oddsChangeReason = OddsChangeReason.RiskAdjustment;
                    break;
                default:
                    oddsChangeReason = OddsChangeReason.Normal;
            }
        }

        if (message.getOdds() != null) {
            betstopReason = message.getOdds().getBetstopReason();
            bettingStatus = message.getOdds().getBettingStatus();

            if (message.getOdds().getMarket() != null) {
                affectedMarkets =
                    message
                        .getOdds()
                        .getMarket()
                        .stream()
                        .map(m -> marketFactory.buildMarketWithOdds(sportEvent, m, message.getProduct()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            } else {
                logger.info(
                    "Processing oddsChange message with empty odds list. sportEvent:{}, producer:{}",
                    sportEvent.getId(),
                    producer
                );
                affectedMarkets = Collections.emptyList();
            }
        } else {
            betstopReason = null;
            bettingStatus = null;
            affectedMarkets = Collections.emptyList();
            logger.info(
                "Processing oddsChange message without odds info. sportEvent:{}, producer:{}",
                sportEvent.getId(),
                producer
            );
        }

        oddsGeneration =
            message.getOddsGenerationProperties() == null
                ? null
                : new OddsGenerationImpl(message.getOddsGenerationProperties());
    }

    /**
     * Get the reason why the odds changed
     *
     * @return if RiskAdjustment this means the user changed some configuration forcing an odds
     * change, otherwise it is a normal update based on changed conditions (i.e. something
     * happened in the game or enough time has passed)
     */
    @Override
    public OddsChangeReason getChangeReason() {
        return oddsChangeReason;
    }

    /**
     * Returns the betstop reason value descriptor
     *
     * @return betstop reason value descriptor
     */
    @Override
    public NamedValue getBetstopReasonValue() {
        if (betstopReason == null) {
            return null;
        }

        return namedValuesProvider.getBetStopReasons().getNamedValue(betstopReason);
    }

    /**
     * Returns the reason for the most recently sent betstop if the betstop is still active
     *
     * @return the reason for the most recently sent betstop if the betstop is still active
     * (otherwise null)
     */
    @Override
    public String getBetstopReason() {
        if (betstopReason == null) {
            return null;
        }

        return namedValuesProvider.getBetStopReasons().getNamedValue(betstopReason).getDescription();
    }

    /**
     * Returns the betting status value descriptor
     *
     * @return the betting status value descriptor
     */
    @Override
    public NamedValue getBettingStatusValue() {
        if (bettingStatus == null) {
            return null;
        }

        return namedValuesProvider.getBettingStatuses().getNamedValue(bettingStatus);
    }

    /**
     * If this field is set, it reports that a previous betstop was sent but the markets have now
     * been reopened. A conservative bookmaker could keep the markets suspended. Previously, we call
     * it that the markets are current in early betstart.
     *
     * @return the betting status - if set the affected markets are in early betstart otherwise
     * null.
     */
    @Override
    public String getBettingStatus() {
        if (bettingStatus == null) {
            return null;
        }

        return namedValuesProvider.getBettingStatuses().getNamedValue(bettingStatus).getDescription();
    }

    /**
     * Returns a list of {@link MarketWithOdds} associated with the message
     * @return a list of {@link MarketWithOdds} associated with the message
     */
    @Override
    public List<MarketWithOdds> getMarkets() {
        return affectedMarkets;
    }

    /**
     * Gets the odds generation properties (contains a few key-parameters
     * that can be used in a client’s own special odds model,
     * or even offer spread betting bets based on it)
     * @return the odds generation properties
     */
    @Override
    public OddsGeneration getOddsGenerationProperties() {
        return oddsGeneration;
    }
}
