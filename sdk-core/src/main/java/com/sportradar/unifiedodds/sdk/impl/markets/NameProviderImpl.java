/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCI;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCI;
import com.sportradar.unifiedodds.sdk.caching.ProfileCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.Competition;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
public class NameProviderImpl implements NameProvider {
    private static final Logger logger = LoggerFactory.getLogger(NameProviderImpl.class);
    private final static String PLAYER_PROFILE_MARKET_PREFIX = "sr:player:";
    private final static String COMPETITOR_PROFILE_MARKET_PREFIX = "sr:competitor";
    private final static String SIMPLETEAM_PROFILE_MARKET_PREFIX = "sr:simpleteam";
    private final static String COMPOSITE_ID_SEPARATOR = ",";

    private final MarketDescriptionProvider descriptorProvider;
    private final ProfileCache profileCache;
    private final NameExpressionFactory expressionFactory;
    private final SportEvent sportEvent;
    private final int marketId;
    private final Map<String, String> marketSpecifiers;
    private final int producerId;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Supplier<List<URN>> competitorList;

    private final Map<Locale, MarketDescription> marketDescriptionCache;

    NameProviderImpl(MarketDescriptionProvider descriptorProvider,
                            ProfileCache profileCache,
                            NameExpressionFactory expressionFactory,
                            SportEvent sportEvent,
                            int marketId,
                            Map<String, String> marketSpecifiers,
                            int producerId,
                            ExceptionHandlingStrategy exceptionHandlingStrategy) {
        Preconditions.checkNotNull(descriptorProvider);
        Preconditions.checkNotNull(profileCache);
        Preconditions.checkNotNull(expressionFactory);
        Preconditions.checkNotNull(sportEvent);
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkArgument(producerId > 0);
        Preconditions.checkNotNull(exceptionHandlingStrategy);


        this.descriptorProvider = descriptorProvider;
        this.profileCache = profileCache;
        this.expressionFactory = expressionFactory;
        this.sportEvent = sportEvent;
        this.marketId = marketId;
        this.marketSpecifiers = marketSpecifiers;
        this.producerId = producerId;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.competitorList = () -> provideSportEventCompetitorIds(sportEvent);

        this.marketDescriptionCache = Maps.newConcurrentMap();
    }

    @Override
    public String getMarketName(Locale locale) {
        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescriptor(locale);
        } catch (ObjectNotFoundException e) {
            return handleErrorCondition("Failed to retrieve market name descriptor", null, null, locale, e);
        }

        String nameDescriptor = marketDescriptor.getName(locale);
        if (nameDescriptor == null) {
            return handleErrorCondition("Retrieved market descriptor does not contain name descriptor in the specified language",
                    null, null, locale, null);
        }

        String nameDescriptorFormat = null;
        List<NameExpression> expressions = null;
        try {
            AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> nameExpressions = getNameExpressions(nameDescriptor);
            if (nameExpressions != null) {
                nameDescriptorFormat = nameExpressions.getKey();
                expressions = nameExpressions.getValue();
            }
        } catch (IllegalArgumentException | UnsupportedUrnFormatException ex) {
            return handleErrorCondition("The name description parsing failed",null, nameDescriptor, locale, ex);
        }

        if (expressions == null) {
            return nameDescriptor;
        }

        List<String> collect;
        try {
            collect = expressions.stream().map(e -> e.buildName(locale)).collect(Collectors.toList());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return handleErrorCondition("Error occurred while evaluating the name expression", null, nameDescriptor, locale, e);
        }

        return String.format(nameDescriptorFormat, collect.toArray());
    }

    @Override
    public String getOutcomeName(String outcomeId, Locale locale) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(locale);

        if (outcomeId.startsWith(PLAYER_PROFILE_MARKET_PREFIX) || outcomeId.startsWith(COMPETITOR_PROFILE_MARKET_PREFIX)) {
            try {
                return getOutcomeNameFromProfile(outcomeId, locale);
            } catch (UnsupportedUrnFormatException | CacheItemNotFoundException | IllegalCacheStateException ex) {
                return handleErrorCondition("Failed to generate outcome name for profile", outcomeId, null, locale, ex);
            }
        }

        MarketDescription marketDescription = getMarketDescriptionForOutcome(outcomeId, locale, true);
        if(marketDescription == null){
            return null;
        }

        Optional<OutcomeDescription> optDesc = marketDescription.getOutcomes().stream().filter(o -> o.getId().equals(outcomeId)).findFirst();

        if (!optDesc.isPresent() || optDesc.get().getName(locale) == null) {
            return handleErrorCondition("Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language", outcomeId, null, locale, null);
        }

        String nameDescription = optDesc.get().getName(locale);
        if (marketDescription.getAttributes() != null &&
                marketDescription.getAttributes().stream().anyMatch(a -> a.getName().equals(UnifiedFeedConstants.FLEX_SCORE_MARKET_ATTRIBUTE_NAME))) {
            try {
                return FlexMarketHelper.getName(nameDescription, marketSpecifiers);
            } catch (IllegalArgumentException e) {
                return handleErrorCondition("The generation of name for flex score market outcome failed",
                        outcomeId, nameDescription, locale, e);
            }
        }

        String nameDescriptionFormat = null;
        List<NameExpression> expressions = null;
        try {
            AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> nameExpressions =
                    getNameExpressions(nameDescription);
            if (nameExpressions != null) {
                nameDescriptionFormat = nameExpressions.getKey();
                expressions = nameExpressions.getValue();
            }
        } catch (IllegalArgumentException e) {
            return handleErrorCondition("The name description parsing failed",
                    outcomeId, nameDescription, locale, e);
        }

        if (expressions == null || nameDescriptionFormat == null) {
            return nameDescription;
        }

        try {
            return String.format(nameDescriptionFormat, expressions.stream()
                    .map(e -> e.buildName(locale)).toArray());
        } catch (IllegalStateException | IllegalArgumentException | UnsupportedUrnFormatException e) {
            return handleErrorCondition("Error occurred while evaluating the name expression",
                    outcomeId, nameDescription, locale, e);
        }
    }

    private String getOutcomeNameFromProfile(String outcomeId, Locale locale) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(locale);

        String[] idParts = outcomeId.split(COMPOSITE_ID_SEPARATOR);
        List<String> names = new ArrayList<>(idParts.length);

        for (String idPart : idParts) {
            URN profileId;
            try {
                profileId = URN.parse(idPart);
            } catch (UnsupportedUrnFormatException ex) {
                throw new UnsupportedUrnFormatException("OutcomeId=" + idPart + " is not a valid URN", ex);
            }

            if (idPart.startsWith(PLAYER_PROFILE_MARKET_PREFIX)) {
                PlayerProfileCI playerProfile = profileCache.getPlayerProfile(profileId, Lists.newArrayList(locale), competitorList.get());
                names.add(playerProfile.getNames(Collections.singletonList(locale)).get(locale));
            } else if (idPart.startsWith(COMPETITOR_PROFILE_MARKET_PREFIX) || idPart.startsWith(SIMPLETEAM_PROFILE_MARKET_PREFIX)) {
                CompetitorCI competitorProfile = profileCache.getCompetitorProfile(profileId, Lists.newArrayList(locale));
                names.add(competitorProfile.getNames(Collections.singletonList(locale)).get(locale));
            }
        }

        return String.join(COMPOSITE_ID_SEPARATOR, names);
    }

    private MarketDescription getMarketDescriptor(Locale locale) throws ObjectNotFoundException {
        Preconditions.checkNotNull(locale);

        if (marketDescriptionCache.containsKey(locale)) {
            return marketDescriptionCache.get(locale);
        }

        try {
            MarketDescription marketDescription = descriptorProvider.getMarketDescription(marketId, marketSpecifiers, Lists.newArrayList(locale), true);

            marketDescriptionCache.put(locale, marketDescription);

            return marketDescription;
        } catch (CacheItemNotFoundException e) {
            throw new ObjectNotFoundException("The requested market[" + marketId + "] was not found", e);
        }
    }

    private AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> getNameExpressions(String nameDescriptor) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nameDescriptor));

        AbstractMap.SimpleImmutableEntry<String, List<String>> descriptorData = NameExpressionHelper.parseDescriptor(nameDescriptor);
        if (descriptorData == null || descriptorData.getValue() == null) {
            return null;
        }

        List<NameExpression> expressions = new ArrayList<>(descriptorData.getValue().size());
        for (String expr : descriptorData.getValue()) {
            AbstractMap.SimpleImmutableEntry<String, String> expressionParse = NameExpressionHelper.parseExpression(expr);
            expressions.add(expressionFactory.buildExpression(
                    sportEvent,
                    marketSpecifiers,
                    expressionParse.getValue(),
                    expressionParse.getKey()));
        }

        return new AbstractMap.SimpleImmutableEntry<>(
                descriptorData.getKey(),
                expressions
        );
    }

    private String handleErrorCondition(String message, String outcomeId, String nameDescriptor, Locale locale, Exception ex) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(message));
        Preconditions.checkNotNull(locale);

        StringBuilder sb = new StringBuilder("An error occurred while generating the name for event=[")
                .append(sportEvent)
                .append("], market=[");
        String specifierString = marketSpecifiers == null ? "null" :
                marketSpecifiers.entrySet().stream()
                        .map(e -> "{" + e.getKey() + "}={" + e.getValue() + "}")
                        .collect(Collectors.joining("|"));
        sb.append(" MarketId=").append(marketId);
        sb.append(" Specifiers=[").append(specifierString).append("]");

        if (outcomeId != null) {
            sb.append(" OutcomeId=").append(outcomeId);
        }

        sb.append("]");

        sb.append(" Locale=").append(locale);

        if (nameDescriptor != null) {
            sb.append(" Retrieved nameDescriptor=[").append(nameDescriptor).append("]");
        }

        sb.append("]. Additional message: ").append(message);

        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (ex != null) {
                throw new NameGenerationException(sb.toString(), ex);
            } else {
                throw new NameGenerationException(sb.toString());
            }
        } else {
            if (ex != null) {
                logger.warn(sb.toString(), ex);
            } else {
                logger.warn(sb.toString());
            }
            return null;
        }
    }

    private static List<URN> provideSportEventCompetitorIds(SportEvent sportEvent) {
        Preconditions.checkNotNull(sportEvent);

        if (sportEvent instanceof Competition) {
            return ((Competition) sportEvent).getCompetitors() == null ? Collections.emptyList() :
                    ((Competition) sportEvent).getCompetitors().stream()
                            .map(Competitor::getId)
                            .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private MarketDescription getMarketDescriptionForOutcome(String outcomeId, Locale locale, boolean firstTime)
    {
        MarketDescription marketDescription = null;
        try {
            marketDescription = getMarketDescriptor(locale);
        } catch (ObjectNotFoundException ex) {
            handleErrorCondition("Failed to retrieve market name description", outcomeId, null, locale, ex);
            return marketDescription;
        }

        if(marketDescription == null){
            handleErrorCondition("Failed to retrieve market name description", outcomeId, null, locale, null);
            return null;
        }

        if (marketDescription.getOutcomes() == null || marketDescription.getOutcomes().isEmpty()) {
            if(firstTime){
                handleErrorCondition("Retrieved market descriptor is lacking outcomes", outcomeId, null, locale, null);
                if (((MarketDescriptionImpl) marketDescription).canBeFetched()) {
                    handleErrorCondition("Reloading market description", outcomeId, null, locale, null);
                    descriptorProvider.reloadMarketDescription(marketId, marketSpecifiers);
                    marketDescriptionCache.clear();
                    return getMarketDescriptionForOutcome(outcomeId, locale, false);
                } else {
                    logger.debug("Throttling down market reloading");
                }
            }
            handleErrorCondition("Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language", outcomeId, null, locale, null);
            return null;
        }

        Optional<OutcomeDescription> optDesc = marketDescription.getOutcomes().stream().filter(o -> o.getId().equals(outcomeId)).findFirst();
        if (!optDesc.isPresent() || optDesc.get().getName(locale) == null) {
            if(firstTime){
                handleErrorCondition("Retrieved market descriptor is missing outcome", outcomeId, null, locale, null);
                if (((MarketDescriptionImpl) marketDescription).canBeFetched()) {
                    handleErrorCondition("Reloading market description", outcomeId, null, locale, null);
                    descriptorProvider.reloadMarketDescription(marketId, marketSpecifiers);
                    marketDescriptionCache.clear();
                    return getMarketDescriptionForOutcome(outcomeId, locale, false);
                } else {
                    logger.debug("Throttling down market reloading");
                }
            }
            handleErrorCondition("Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language", outcomeId, null, locale, null);
            return null;
        }

        return marketDescription;
    }
}
