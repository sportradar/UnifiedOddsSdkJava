/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.UFBetSettlementMarket;
import com.sportradar.uf.datamodel.UFMarket;
import com.sportradar.uf.datamodel.UFOddsChangeMarket;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProviderFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithSettlement;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeSettlement;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketFactoryImpl implements MarketFactory {
    private final static Logger logger = LoggerFactory.getLogger(MarketFactoryImpl.class);

    private final MarketDescriptionProvider marketDescriptionProvider;
    private final NameProviderFactory nameProviderFactory;
    private final NamedValuesProvider namedValuesProvider;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    public MarketFactoryImpl(MarketDescriptionProvider marketDescriptionProvider, NameProviderFactory nameProviderFactory, NamedValuesProvider namedValuesProvider, SDKInternalConfiguration config) {
        Preconditions.checkNotNull(marketDescriptionProvider);
        Preconditions.checkNotNull(nameProviderFactory);
        Preconditions.checkNotNull(namedValuesProvider);
        Preconditions.checkNotNull(config);

        this.marketDescriptionProvider = marketDescriptionProvider;
        this.nameProviderFactory = nameProviderFactory;
        this.namedValuesProvider = namedValuesProvider;
        this.defaultLocale = config.getDefaultLocale();
        this.exceptionHandlingStrategy = config.getExceptionHandlingStrategy();
    }

    @Override
    public Optional<Market> buildMarket(SportEvent sportEvent, UFMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build market with id:{}, reason:", market.getId(), e);
            return Optional.empty();
        }

        return Optional.of(new MarketImpl(
                market.getId(),
                nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId),
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(sportEvent, marketDescriptor, sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                defaultLocale
        ));
    }

    @Override
    public Optional<MarketWithOdds> buildMarketWithOdds(SportEvent sportEvent, UFOddsChangeMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithOdds id={}, reason:", market.getId(), e);
            return Optional.empty();
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId);

        return Optional.of(new MarketWithOddsImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(sportEvent, marketDescriptor, sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                defaultLocale,
                market.getStatus(),
                market.getFavourite(),
                market.getMarketMetadata(),
                buildOddsOutcomes(marketDescriptor, nameProvider, sportEvent, producerId, specifiersMap, market.getOutcome())
        ));
    }

    @Override
    public Optional<MarketWithSettlement> buildMarketWithSettlement(SportEvent sportEvent, UFBetSettlementMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithSettlement id={}, reason:", market.getId(), e);
            return Optional.empty();
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId);

        return Optional.of(new MarketWithSettlementImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(sportEvent, marketDescriptor, sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                defaultLocale,
                market.getVoidReason(),
                buildSettlementOutcomes(marketDescriptor, nameProvider, sportEvent, producerId, specifiersMap, market.getOutcome()),
                namedValuesProvider
        ));
    }

    @Override
    public Optional<MarketWithProbabilities> buildMarketWithProbabilities(SportEvent sportEvent, UFOddsChangeMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithProbabilities id={}, reason:", market.getId(), e);
            return Optional.empty();
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId);

        return Optional.of(new MarketWithProbabilitiesImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(sportEvent, marketDescriptor, sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                defaultLocale,
                market.getStatus(),
                buildProbabilityOutcomes(marketDescriptor, nameProvider, sportEvent, producerId, specifiersMap, market.getOutcome()),
                market.getCashoutStatus()
        ));
    }

    @Override
    public Optional<MarketCancel> buildMarketCancel(SportEvent sportEvent, UFMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build buildMarketCancel id={}, reason:", market.getId(), e);
            return Optional.empty();
        }

        return Optional.of(new MarketCancelImpl(
                market.getId(),
                nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId),
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(sportEvent, marketDescriptor, sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                defaultLocale,
                market.getVoidReason(),
                namedValuesProvider
        ));
    }

    private Map<String, String> parseSpecifiers(String specifiers) {
        if (Strings.isNullOrEmpty(specifiers)) {
            return Collections.emptyMap();
        }

        Map<String, String> specifiersMap = new HashMap<>();

        StringTokenizer st = new StringTokenizer(specifiers, "=" + UnifiedFeedConstants.SPECIFIERS_DELIMITER);

        int tokenisedSpecifiers = st.countTokens();
        if (tokenisedSpecifiers % 2 != 0) {
            logger.warn("Received message market with malformed specifiers: '{}'", specifiers);
            return Collections.emptyMap();
        }

        while (st.hasMoreTokens()) {
            String key = st.nextToken();
            String value = st.nextToken();
            specifiersMap.put(key, value);
        }

        return specifiersMap;
    }

    private MarketDescription getMarketDescription(int marketId, URN sportId, Map<String, String> specifiersMap, int producerId) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkArgument(producerId > 0);

        return marketDescriptionProvider.getMarketDescription(marketId, specifiersMap, Lists.newArrayList(defaultLocale), false);
    }

    private List<OutcomeOdds> buildOddsOutcomes(MarketDescription md, NameProvider nameProvider, SportEvent sportEvent, int producerId, Map<String, String> specifiersMap, List<UFOddsChangeMarket.UFOutcome> outcomes) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        List<OutcomeOdds> builtOutcomes = new ArrayList<>();
        for (UFOddsChangeMarket.UFOutcome o : outcomes) {
            OutcomeOdds outcomeOdds;
            if (isValidPlayerOutcome(sportEvent, md.getId(), o.getId(), o.getTeam())) {
                outcomeOdds = new PlayerOutcomeOddsImpl(
                        o.getId(),
                        nameProvider,
                        new OutcomeDefinitionImpl(md, o.getId(), sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                        defaultLocale,
                        o.getActive(),
                        o.getOdds(),
                        o.getProbabilities(),
                        (Match) sportEvent, // casting gets validated in the #isValidPlayerOutcome(...)
                        o.getTeam()
                );
            } else {
                outcomeOdds = new OutcomeOddsImpl(
                        o.getId(),
                        nameProvider,
                        new OutcomeDefinitionImpl(md, o.getId(), sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                        defaultLocale,
                        o.getActive(),
                        o.getOdds(),
                        o.getProbabilities()
                );
            }


            builtOutcomes.add(outcomeOdds);
        }

        return builtOutcomes;
    }

    private List<OutcomeSettlement> buildSettlementOutcomes(MarketDescription md, NameProvider nameProvider, SportEvent sportEvent, int producerId, Map<String, String> specifiersMap, List<UFBetSettlementMarket.UFOutcome> outcomes) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        List<OutcomeSettlement> builtOutcomes = new ArrayList<>();
        for (UFBetSettlementMarket.UFOutcome o : outcomes) {
            OutcomeSettlement outcomeSettlement = new OutcomeSettlementImpl(
                    o.getId(),
                    nameProvider,
                    new OutcomeDefinitionImpl(md, o.getId(), sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                    defaultLocale,
                    o.getResult(),
                    o.getVoidFactor(),
                    o.getDeadHeatFactor()
            );

            builtOutcomes.add(outcomeSettlement);
        }

        return builtOutcomes;
    }

    private List<OutcomeProbabilities> buildProbabilityOutcomes(MarketDescription md, NameProvider nameProvider, SportEvent sportEvent, int producerId, Map<String, String> specifiersMap, List<UFOddsChangeMarket.UFOutcome> outcomes) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        return outcomes.stream()
                .map(o -> new OutcomeProbabilitiesImpl(
                        o.getId(),
                        nameProvider,
                        new OutcomeDefinitionImpl(md, o.getId(), sportEvent.getSportId(), producerId, specifiersMap, marketDescriptionProvider, defaultLocale, exceptionHandlingStrategy),
                        defaultLocale,
                        o.getActive(),
                        o.getProbabilities()
                )).collect(Collectors.toList());
    }

    private boolean isValidPlayerOutcome(SportEvent sportEvent, int marketId, String outcomeId, Integer outcomeTeamIndication) {
        boolean isMatch = sportEvent instanceof Match;
        if (outcomeTeamIndication == null) {
            return false;
        }

        if (!isMatch) {
            logger.warn("Received invalid player outcome, sport event is not a match. SportEventId:{}, marketId:{}, outcomeId:{}, outcomeTeamIndication:{}",
                    sportEvent.getId(), marketId, outcomeId, outcomeTeamIndication);
            return false;
        }

        return true;
    }
}
