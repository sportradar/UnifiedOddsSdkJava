/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sportradar.uf.datamodel.UfBetSettlementMarket;
import com.sportradar.uf.datamodel.UfMarket;
import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.markets.NameProviderFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    {
        "BooleanExpressionComplexity",
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "ConstantName",
        "LambdaBodyLength",
        "LineLength",
        "MethodLength",
    }
)
public class MarketFactoryImpl implements MarketFactory {

    private static final Logger logger = LoggerFactory.getLogger(MarketFactoryImpl.class);

    private final MarketDescriptionProvider marketDescriptionProvider;
    private final NameProviderFactory nameProviderFactory;
    private final NamedValuesProvider namedValuesProvider;
    private final Locale defaultLocale;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    public MarketFactoryImpl(
        MarketDescriptionProvider marketDescriptionProvider,
        NameProviderFactory nameProviderFactory,
        NamedValuesProvider namedValuesProvider,
        SdkInternalConfiguration config
    ) {
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
    public Optional<Market> buildMarket(SportEvent sportEvent, UfMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor = null;
        try {
            marketDescriptor =
                getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build market with id:{}, reason:", market.getId(), e);
        }

        return Optional.of(
            new MarketImpl(
                market.getId(),
                nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId),
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(
                    market.getId(),
                    sportEvent,
                    marketDescriptor,
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale
            )
        );
    }

    @Override
    public Optional<MarketWithOdds> buildMarketWithOdds(
        SportEvent sportEvent,
        UfOddsChangeMarket market,
        int producerId
    ) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor = null;
        try {
            marketDescriptor =
                getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithOdds id={}, reason:", market.getId(), e);
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(
            sportEvent,
            market.getId(),
            specifiersMap,
            producerId
        );

        return Optional.of(
            new MarketWithOddsImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(
                    market.getId(),
                    sportEvent,
                    marketDescriptor,
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale,
                market.getStatus(),
                market.getFavourite(),
                market.getMarketMetadata(),
                buildOddsOutcomes(
                    market.getId(),
                    marketDescriptor,
                    nameProvider,
                    sportEvent,
                    producerId,
                    specifiersMap,
                    market.getOutcome()
                )
            )
        );
    }

    @Override
    public Optional<MarketWithSettlement> buildMarketWithSettlement(
        SportEvent sportEvent,
        UfBetSettlementMarket market,
        int producerId
    ) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor = null;
        try {
            marketDescriptor =
                getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithSettlement id={}, reason:", market.getId(), e);
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(
            sportEvent,
            market.getId(),
            specifiersMap,
            producerId
        );

        return Optional.of(
            new MarketWithSettlementImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(
                    market.getId(),
                    sportEvent,
                    marketDescriptor,
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale,
                market.getVoidReason(),
                buildSettlementOutcomes(
                    market.getId(),
                    nameProvider,
                    sportEvent,
                    producerId,
                    specifiersMap,
                    market.getOutcome()
                ),
                namedValuesProvider
            )
        );
    }

    @Override
    public Optional<MarketWithProbabilities> buildMarketWithProbabilities(
        SportEvent sportEvent,
        UfOddsChangeMarket market,
        int producerId
    ) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor = null;
        try {
            marketDescriptor =
                getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build marketWithProbabilities id={}, reason:", market.getId(), e);
        }

        NameProvider nameProvider = nameProviderFactory.buildNameProvider(
            sportEvent,
            market.getId(),
            specifiersMap,
            producerId
        );

        return Optional.of(
            new MarketWithProbabilitiesImpl(
                market.getId(),
                nameProvider,
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(
                    market.getId(),
                    sportEvent,
                    marketDescriptor,
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale,
                market.getStatus(),
                buildProbabilityOutcomes(
                    market.getId(),
                    nameProvider,
                    sportEvent,
                    producerId,
                    specifiersMap,
                    market.getOutcome()
                ),
                market.getCashoutStatus(),
                market.getMarketMetadata()
            )
        );
    }

    @Override
    public Optional<MarketCancel> buildMarketCancel(SportEvent sportEvent, UfMarket market, int producerId) {
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkNotNull(market);

        Map<String, String> specifiersMap = parseSpecifiers(market.getSpecifiers());

        MarketDescription marketDescriptor = null;
        try {
            marketDescriptor =
                getMarketDescription(market.getId(), sportEvent.getSportId(), specifiersMap, producerId);
        } catch (CacheItemNotFoundException e) {
            logger.warn("Failed to build buildMarketCancel id={}, reason:", market.getId(), e);
        }

        return Optional.of(
            new MarketCancelImpl(
                market.getId(),
                nameProviderFactory.buildNameProvider(sportEvent, market.getId(), specifiersMap, producerId),
                specifiersMap,
                parseSpecifiers(market.getExtendedSpecifiers()),
                new MarketDefinitionImpl(
                    market.getId(),
                    sportEvent,
                    marketDescriptor,
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale,
                market.getVoidReason(),
                namedValuesProvider
            )
        );
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

    private MarketDescription getMarketDescription(
        int marketId,
        Urn sportId,
        Map<String, String> specifiersMap,
        int producerId
    ) throws CacheItemNotFoundException {
        Preconditions.checkNotNull(sportId);
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkArgument(producerId > 0);

        return marketDescriptionProvider.getMarketDescription(
            marketId,
            specifiersMap,
            Lists.newArrayList(defaultLocale),
            false
        );
    }

    @SuppressWarnings("ParameterNumber")
    private List<OutcomeOdds> buildOddsOutcomes(
        int marketId,
        MarketDescription md,
        NameProvider nameProvider,
        SportEvent sportEvent,
        int producerId,
        Map<String, String> specifiersMap,
        List<UfOddsChangeMarket.UfOutcome> outcomes
    ) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        List<OutcomeOdds> builtOutcomes = new ArrayList<>();
        for (UfOddsChangeMarket.UfOutcome o : outcomes) {
            OutcomeOdds outcomeOdds;

            if (isValidPlayerOutcome(sportEvent, md, o.getId(), o.getTeam())) {
                outcomeOdds =
                    new PlayerOutcomeOddsImpl(
                        o.getId(),
                        nameProvider,
                        new OutcomeDefinitionImpl(
                            marketId,
                            o.getId(),
                            sportEvent.getSportId(),
                            producerId,
                            specifiersMap,
                            marketDescriptionProvider,
                            defaultLocale,
                            exceptionHandlingStrategy
                        ),
                        defaultLocale,
                        o.getActive(),
                        o.getOdds(),
                        o.getProbabilities(),
                        (Match) sportEvent, // casting gets validated in the #isValidPlayerOutcome(...)
                        o.getTeam(),
                        buildAdditionalProbabilities(o)
                    );
            } else {
                outcomeOdds =
                    new OutcomeOddsImpl(
                        o.getId(),
                        nameProvider,
                        new OutcomeDefinitionImpl(
                            marketId,
                            o.getId(),
                            sportEvent.getSportId(),
                            producerId,
                            specifiersMap,
                            marketDescriptionProvider,
                            defaultLocale,
                            exceptionHandlingStrategy
                        ),
                        defaultLocale,
                        o.getActive(),
                        o.getOdds(),
                        o.getProbabilities(),
                        buildAdditionalProbabilities(o)
                    );
            }

            builtOutcomes.add(outcomeOdds);
        }

        return builtOutcomes;
    }

    private List<OutcomeSettlement> buildSettlementOutcomes(
        int marketId,
        NameProvider nameProvider,
        SportEvent sportEvent,
        int producerId,
        Map<String, String> specifiersMap,
        List<UfBetSettlementMarket.UfOutcome> outcomes
    ) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        List<OutcomeSettlement> builtOutcomes = new ArrayList<>();
        for (UfBetSettlementMarket.UfOutcome o : outcomes) {
            OutcomeSettlement outcomeSettlement = new OutcomeSettlementImpl(
                o.getId(),
                nameProvider,
                new OutcomeDefinitionImpl(
                    marketId,
                    o.getId(),
                    sportEvent.getSportId(),
                    producerId,
                    specifiersMap,
                    marketDescriptionProvider,
                    defaultLocale,
                    exceptionHandlingStrategy
                ),
                defaultLocale,
                o.getResult(),
                o.getVoidFactor(),
                o.getDeadHeatFactor()
            );

            builtOutcomes.add(outcomeSettlement);
        }

        return builtOutcomes;
    }

    private List<OutcomeProbabilities> buildProbabilityOutcomes(
        int marketId,
        NameProvider nameProvider,
        SportEvent sportEvent,
        int producerId,
        Map<String, String> specifiersMap,
        List<UfOddsChangeMarket.UfOutcome> outcomes
    ) {
        if (outcomes == null || outcomes.isEmpty()) {
            return Collections.emptyList();
        }

        return outcomes
            .stream()
            .map(o ->
                new OutcomeProbabilitiesImpl(
                    o.getId(),
                    nameProvider,
                    new OutcomeDefinitionImpl(
                        marketId,
                        o.getId(),
                        sportEvent.getSportId(),
                        producerId,
                        specifiersMap,
                        marketDescriptionProvider,
                        defaultLocale,
                        exceptionHandlingStrategy
                    ),
                    defaultLocale,
                    o.getActive(),
                    o.getProbabilities(),
                    buildAdditionalProbabilities(o)
                )
            )
            .collect(Collectors.toList());
    }

    private boolean isValidPlayerOutcome(
        SportEvent sportEvent,
        MarketDescription marketDescription,
        String outcomeId,
        Integer outcomeTeamIndication
    ) {
        boolean isMatch = sportEvent instanceof Match;
        if (outcomeTeamIndication == null) {
            return false;
        }

        if (!isMatch) {
            logger.warn(
                "Received invalid player outcome, sport event is not a match. SportEventId:{}, marketId:{}, outcomeId:{}, outcomeTeamIndication:{}",
                sportEvent.getId(),
                marketDescription != null ? marketDescription.getId() : null,
                outcomeId,
                outcomeTeamIndication
            );
            return false;
        }

        return true;
    }

    private AdditionalProbabilities buildAdditionalProbabilities(UfOddsChangeMarket.UfOutcome outcome) {
        return (
                outcome.getWinProbabilities() != null ||
                outcome.getLoseProbabilities() != null ||
                outcome.getHalfWinProbabilities() != null ||
                outcome.getHalfLoseProbabilities() != null ||
                outcome.getRefundProbabilities() != null
            )
            ? new AdditionalProbabilitiesImpl(
                outcome.getWinProbabilities(),
                outcome.getLoseProbabilities(),
                outcome.getHalfWinProbabilities(),
                outcome.getHalfLoseProbabilities(),
                outcome.getRefundProbabilities()
            )
            : null;
    }
}
