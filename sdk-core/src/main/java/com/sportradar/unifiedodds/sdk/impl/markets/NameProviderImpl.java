/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.CompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.PlayerProfileCi;
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
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 15/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "NPathComplexity",
        "NestedIfDepth",
        "ParameterNumber",
        "ReturnCount",
    }
)
public class NameProviderImpl implements NameProvider {

    private static final Logger logger = LoggerFactory.getLogger(NameProviderImpl.class);
    private static final String PLAYER_PROFILE_MARKET_PREFIX = "sr:player:";
    private static final String COMPETITOR_PROFILE_MARKET_PREFIX = "sr:competitor";
    private static final String SIMPLETEAM_PROFILE_MARKET_PREFIX = "sr:simpleteam";
    private static final String COMPOSITE_ID_SEPARATOR = ",";

    private final MarketDescriptionProvider descriptorProvider;
    private final ProfileCache profileCache;
    private final NameExpressionFactory expressionFactory;
    private final SportEvent sportEvent;
    private final int marketId;
    private final Map<String, String> marketSpecifiers;
    private final int producerId;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Supplier<List<Urn>> competitorList;
    private final TimeUtils time;

    private long lastReload;

    NameProviderImpl(
        MarketDescriptionProvider descriptorProvider,
        ProfileCache profileCache,
        NameExpressionFactory expressionFactory,
        SportEvent sportEvent,
        int marketId,
        Map<String, String> marketSpecifiers,
        int producerId,
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        TimeUtils time
    ) {
        this.time = time;
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
    }

    @Override
    public String getMarketName(Locale locale) {
        Map<Locale, String> nameTranslations = getMarketNames(Collections.singletonList(locale));
        if (nameTranslations != null) {
            return nameTranslations.get(locale);
        } else {
            return null;
        }
    }

    @Override
    public Map<Locale, String> getMarketNames(List<Locale> locales) {
        MarketDescription marketDescriptor;
        try {
            marketDescriptor = getMarketDescriptor(locales);
        } catch (ObjectNotFoundException e) {
            return handleErrorCondition("Failed to retrieve market name descriptor", null, null, locales, e);
        }

        List<Locale> missingLocales = SdkHelper.findMissingLocales(marketDescriptor.getLocales(), locales);
        if (!missingLocales.isEmpty()) {
            return handleErrorCondition(
                "Retrieved market descriptor does not contain name descriptor in the specified languages",
                null,
                null,
                missingLocales,
                null
            );
        }

        Map<Locale, String> names = new HashMap<>();
        for (Locale locale : locales) {
            names.put(locale, mapMarketNames(marketDescriptor, locale));
        }
        return names;
    }

    private String mapMarketNames(MarketDescription marketDescriptor, Locale locale) {
        String nameDescriptor = marketDescriptor.getName(locale);
        String nameDescriptorFormat = null;
        List<NameExpression> expressions = null;
        try {
            AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> nameExpressions = getNameExpressions(
                nameDescriptor
            );
            if (nameExpressions != null) {
                nameDescriptorFormat = nameExpressions.getKey();
                expressions = nameExpressions.getValue();
            }
        } catch (IllegalArgumentException | UnsupportedUrnFormatException ex) {
            return handleErrorCondition(
                "The name description parsing failed",
                null,
                nameDescriptor,
                locale,
                ex
            );
        }

        if (expressions == null) {
            return nameDescriptor;
        }

        List<String> collect;
        try {
            collect = expressions.stream().map(e -> e.buildName(locale)).collect(Collectors.toList());
        } catch (IllegalArgumentException | IllegalStateException e) {
            return handleErrorCondition(
                "Error occurred while evaluating the name expression",
                null,
                nameDescriptor,
                locale,
                e
            );
        }

        return String.format(nameDescriptorFormat, collect.toArray());
    }

    @Override
    public String getOutcomeName(String outcomeId, Locale locale) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(locale);

        Map<Locale, String> outcomeNames = getOutcomeNames(outcomeId, Collections.singletonList(locale));
        if (outcomeNames != null) {
            return outcomeNames.get(locale);
        } else {
            return null;
        }
    }

    @Override
    public Map<Locale, String> getOutcomeNames(String outcomeId, List<Locale> locales) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(locales);

        if (
            outcomeId.startsWith(PLAYER_PROFILE_MARKET_PREFIX) ||
            outcomeId.startsWith(COMPETITOR_PROFILE_MARKET_PREFIX)
        ) {
            try {
                return getOutcomeNamesFromProfile(outcomeId, locales);
            } catch (
                UnsupportedUrnFormatException | CacheItemNotFoundException | IllegalCacheStateException ex
            ) {
                return handleErrorCondition(
                    "Failed to generate outcome name for profile",
                    outcomeId,
                    null,
                    locales,
                    ex
                );
            }
        }

        MarketDescription marketDescription = getMarketDescriptionForOutcome(outcomeId, locales, true);
        if (marketDescription == null) {
            return null;
        }

        Optional<OutcomeDescription> optDesc = marketDescription
            .getOutcomes()
            .stream()
            .filter(o -> o.getId().equals(outcomeId))
            .findFirst();

        if (
            !optDesc.isPresent() ||
            !SdkHelper.findMissingLocales(optDesc.get().getLocales(), locales).isEmpty()
        ) {
            return handleErrorCondition(
                "Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language",
                outcomeId,
                null,
                locales,
                null
            );
        }

        OutcomeDescription outcomeDescription = optDesc.get();
        if (
            marketDescription.getAttributes() != null &&
            marketDescription
                .getAttributes()
                .stream()
                .anyMatch(a -> a.getName().equals(UnifiedFeedConstants.FLEX_SCORE_MARKET_ATTRIBUTE_NAME))
        ) {
            try {
                return FlexMarketHelper.getNames(outcomeDescription, marketSpecifiers);
            } catch (IllegalArgumentException e) {
                return handleErrorCondition(
                    "The generation of name for flex score market outcome failed",
                    outcomeId,
                    outcomeDescription.getName(locales.get(0)),
                    locales,
                    e
                );
            }
        }

        Map<Locale, String> names = new HashMap<>();
        for (Locale locale : locales) {
            String nameDescription = outcomeDescription.getName(locale);
            String nameDescriptionFormat = null;
            List<NameExpression> expressions = null;
            try {
                AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> nameExpressions = getNameExpressions(
                    nameDescription
                );
                if (nameExpressions != null) {
                    nameDescriptionFormat = nameExpressions.getKey();
                    expressions = nameExpressions.getValue();
                }
            } catch (IllegalArgumentException e) {
                return handleErrorCondition(
                    "The name description parsing failed",
                    outcomeId,
                    nameDescription,
                    locales,
                    e
                );
            }

            if (expressions == null || nameDescriptionFormat == null) {
                names.put(locale, nameDescription);
                continue;
            }

            try {
                names.put(
                    locale,
                    String.format(
                        nameDescriptionFormat,
                        expressions.stream().map(e -> e.buildName(locale)).toArray()
                    )
                );
            } catch (IllegalStateException | IllegalArgumentException | UnsupportedUrnFormatException e) {
                return handleErrorCondition(
                    "Error occurred while evaluating the name expression",
                    outcomeId,
                    nameDescription,
                    locales,
                    e
                );
            }
        }
        return names;
    }

    private Map<Locale, String> getOutcomeNamesFromProfile(String outcomeId, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(locales);

        String[] idParts = outcomeId.split(COMPOSITE_ID_SEPARATOR);
        Map<Locale, List<String>> names = locales
            .stream()
            .collect(Collectors.toMap(l -> l, l -> new ArrayList<>(idParts.length)));

        for (String idPart : idParts) {
            Urn profileId;
            try {
                profileId = Urn.parse(idPart);
            } catch (UnsupportedUrnFormatException ex) {
                throw new UnsupportedUrnFormatException("OutcomeId=" + idPart + " is not a valid URN", ex);
            }

            if (idPart.startsWith(PLAYER_PROFILE_MARKET_PREFIX)) {
                PlayerProfileCi playerProfile = profileCache.getPlayerProfile(
                    profileId,
                    locales,
                    competitorList.get()
                );
                for (Locale locale : locales) {
                    names.get(locale).add(playerProfile.getNames(locales).get(locale));
                }
            } else if (
                idPart.startsWith(COMPETITOR_PROFILE_MARKET_PREFIX) ||
                idPart.startsWith(SIMPLETEAM_PROFILE_MARKET_PREFIX)
            ) {
                CompetitorCi competitorProfile = profileCache.getCompetitorProfile(profileId, locales);
                for (Locale locale : locales) {
                    names.get(locale).add(competitorProfile.getNames(locales).get(locale));
                }
            }
        }

        return names
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(Map.Entry::getKey, n -> String.join(COMPOSITE_ID_SEPARATOR, n.getValue()))
            );
    }

    private MarketDescription getMarketDescriptor(Locale locale) throws ObjectNotFoundException {
        Preconditions.checkNotNull(locale);
        return getMarketDescriptor(Collections.singletonList(locale));
    }

    private MarketDescription getMarketDescriptor(List<Locale> locales) throws ObjectNotFoundException {
        Preconditions.checkNotNull(locales);

        try {
            MarketDescription marketDescription = descriptorProvider.getMarketDescription(
                marketId,
                marketSpecifiers,
                locales,
                true
            );
            return marketDescription;
        } catch (CacheItemNotFoundException e) {
            throw new ObjectNotFoundException("The requested market[" + marketId + "] was not found", e);
        }
    }

    private AbstractMap.SimpleImmutableEntry<String, List<NameExpression>> getNameExpressions(
        String nameDescriptor
    ) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nameDescriptor));

        AbstractMap.SimpleImmutableEntry<String, List<String>> descriptorData = NameExpressionHelper.parseDescriptor(
            nameDescriptor
        );
        if (descriptorData == null || descriptorData.getValue() == null) {
            return null;
        }

        List<NameExpression> expressions = new ArrayList<>(descriptorData.getValue().size());
        for (String expr : descriptorData.getValue()) {
            AbstractMap.SimpleImmutableEntry<String, String> expressionParse = NameExpressionHelper.parseExpression(
                expr
            );
            expressions.add(
                expressionFactory.buildExpression(
                    sportEvent,
                    marketSpecifiers,
                    expressionParse.getValue(),
                    expressionParse.getKey()
                )
            );
        }

        return new AbstractMap.SimpleImmutableEntry<>(descriptorData.getKey(), expressions);
    }

    private String handleErrorCondition(
        String message,
        String outcomeId,
        String nameDescriptor,
        Locale locale,
        Exception ex
    ) {
        return handleErrorCondition(message, outcomeId, nameDescriptor, Collections.singletonList(locale), ex)
            .get(locale);
    }

    private Map<Locale, String> handleErrorCondition(
        String message,
        String outcomeId,
        String nameDescriptor,
        List<Locale> locales,
        Exception ex
    ) {
        String statusMessage = new StatusMessage(
            sportEvent,
            marketId,
            marketSpecifiers,
            message,
            outcomeId,
            nameDescriptor,
            locales
        )
            .toString();
        if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
            if (ex != null) {
                throw new NameGenerationException(statusMessage, ex);
            } else {
                throw new NameGenerationException(statusMessage);
            }
        } else {
            if (ex != null) {
                logger.warn(statusMessage, ex);
            } else {
                logger.warn(statusMessage);
            }
            return null;
        }
    }

    private static List<Urn> provideSportEventCompetitorIds(SportEvent sportEvent) {
        Preconditions.checkNotNull(sportEvent);

        if (sportEvent instanceof Competition) {
            return ((Competition) sportEvent).getCompetitors() == null
                ? Collections.emptyList()
                : ((Competition) sportEvent).getCompetitors()
                    .stream()
                    .map(Competitor::getId)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private MarketDescription getMarketDescriptionForOutcome(
        String outcomeId,
        List<Locale> locales,
        boolean firstTime
    ) {
        MarketDescription marketDescription = null;
        try {
            marketDescription = getMarketDescriptor(locales);
        } catch (ObjectNotFoundException ex) {
            handleErrorCondition("Failed to retrieve market name description", outcomeId, null, locales, ex);
            return marketDescription;
        }

        if (marketDescription == null) {
            handleErrorCondition(
                "Failed to retrieve market name description",
                outcomeId,
                null,
                locales,
                null
            );
            return null;
        }

        if (marketDescription.getOutcomes() == null || marketDescription.getOutcomes().isEmpty()) {
            if (firstTime) {
                logger.warn(
                    new StatusMessage(
                        sportEvent,
                        marketId,
                        marketSpecifiers,
                        "Retrieved market descriptor is lacking outcomes",
                        outcomeId,
                        null,
                        locales
                    )
                        .toString()
                );
                if (canReload()) {
                    logger.warn("Reloading market description");
                    descriptorProvider.reloadMarketDescription(marketId, marketSpecifiers);
                    return getMarketDescriptionForOutcome(outcomeId, locales, false);
                } else {
                    logger.debug("Throttling down market reloading");
                }
            }
            handleErrorCondition(
                "Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language",
                outcomeId,
                null,
                locales,
                null
            );
            return null;
        }

        Optional<OutcomeDescription> optDesc = marketDescription
            .getOutcomes()
            .stream()
            .filter(o -> o.getId().equals(outcomeId))
            .findFirst();
        if (
            !optDesc.isPresent() ||
            !SdkHelper.findMissingLocales(optDesc.get().getLocales(), locales).isEmpty()
        ) {
            if (firstTime) {
                logger.warn(
                    new StatusMessage(
                        sportEvent,
                        marketId,
                        marketSpecifiers,
                        "Retrieved market descriptor is missing outcome",
                        outcomeId,
                        null,
                        locales
                    )
                        .toString()
                );
                if (canReload()) {
                    logger.warn("Reloading market description");
                    descriptorProvider.reloadMarketDescription(marketId, marketSpecifiers);
                    return getMarketDescriptionForOutcome(outcomeId, locales, false);
                } else {
                    logger.debug("Throttling down market reloading");
                }
            }
            handleErrorCondition(
                "Retrieved market descriptor does not contain name descriptor for associated outcome in the specified language",
                outcomeId,
                null,
                locales,
                null
            );
            return null;
        }

        return marketDescription;
    }

    private boolean canReload() {
        long currentTimestamp = time.now();
        boolean allow =
            Math.abs(currentTimestamp - lastReload) / 1000 > SdkHelper.MarketDescriptionMinFetchInterval;
        if (allow) {
            lastReload = currentTimestamp;
        }
        return allow;
    }

    static class StatusMessage {

        private final SportEvent sportEvent;
        private final int marketId;
        private final Map<String, String> marketSpecifiers;
        private final String message;
        private final String outcomeId;
        private final String outcomeName;
        private final List<Locale> locales;

        StatusMessage(
            SportEvent sportEvent,
            int marketId,
            Map<String, String> marketSpecifiers,
            String message,
            String outcomeId,
            String outcomeName,
            List<Locale> locales
        ) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(message));
            Preconditions.checkNotNull(sportEvent);
            Preconditions.checkNotNull(locales);
            Preconditions.checkArgument(marketId > 0);
            this.sportEvent = sportEvent;
            this.marketId = marketId;
            this.marketSpecifiers = marketSpecifiers;
            this.message = message;
            this.outcomeId = outcomeId;
            this.outcomeName = outcomeName;
            this.locales = locales;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("An error occurred while generating the name for event=[")
                .append(sportEvent)
                .append("], market=[");
            String specifierString = marketSpecifiers == null
                ? "null"
                : marketSpecifiers
                    .entrySet()
                    .stream()
                    .map(e -> "{" + e.getKey() + "}={" + e.getValue() + "}")
                    .collect(Collectors.joining("|"));
            sb.append(" MarketId=").append(marketId);
            sb.append(" Specifiers=[").append(specifierString).append("]");

            if (outcomeId != null) {
                sb.append(" OutcomeId=").append(outcomeId);
            }

            sb.append("]");

            sb.append(" Locale=").append(locales);

            if (outcomeName != null) {
                sb.append(" Retrieved nameDescriptor=[").append(outcomeName).append("]");
            }

            sb.append("]. Additional message: ").append(message);

            return sb.toString();
        }
    }
}
