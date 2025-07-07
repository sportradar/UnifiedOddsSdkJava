/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import static com.sportradar.unifiedodds.sdk.caching.impl.ci.PlayerProfileCis.playerProfileCi;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.Competitor1ToWinBothHalves.competitor1ToWinBothHalvesMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.PenaltyShootoutCompetitor2Total.penaltyShootoutCompetitor2TotalMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.PlayerToScore.playerToScoreMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.TotalHolesWon.totalHolesWonMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.kaiHavertzProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeamCompetitors.germany;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeamCompetitors.scotland;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfCompetitorSpecifier.competitor;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayerSpecifier.player;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfTotalSpecifier.total;
import static com.sportradar.unifiedodds.sdk.entities.Matches.matchWithHomeAndAwayCompetitors;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.anyMatch;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.unifiedodds.sdk.oddsentities.TeamCompetitors.teamCompetitor;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CompetitorCis;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionStub;
import com.sportradar.unifiedodds.sdk.internal.caching.Translations;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("MagicNumber")
class MarketNameTest {

    private static final int PRODUCER_ID = 1;
    private static final String HOME_COMPETITOR_PATTERN = "{$competitor1}";
    private static final String AWAY_COMPETITOR_PATTERN = "{$competitor2}";
    private static final String COMPETITOR_FROM_SPECIFIER_PATTERN = "{%competitor}";
    private static final String PLAYER_FROM_SPECIFIER_PATTERN = "{%player}";
    private static final String TOTAL_SPECIFIER_PATTERN = "{total}";
    private static final String ENGLISH_AND_FRENCH =
        "com.sportradar.unifiedodds.sdk.oddsentities.MarketNameTest#englishAndFrench";

    @Nested
    class HomeAndAwayCompetitorExpressions {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_FRENCH)
        void generatesMarketNameWithHomeCompetitor(Locale language) throws Exception {
            val sapiMarketDescription = competitor1ToWinBothHalvesMarketDescription(language);
            val marketDescription = new MarketDescriptionStub()
                .with(new Translations(language, sapiMarketDescription.getName()))
                .withId(sapiMarketDescription.getId());

            val marketDescriptionProvider = providing(
                in(language),
                marketDescription,
                with(ImmutableMap.of())
            );

            val factory = stubbingOutSportEventAndCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .withDefaultLanguage(language)
                .build();

            val home = teamCompetitor().withName(in(language), germany().getName()).build();
            val away = teamCompetitor().withName(in(language), scotland().getName()).build();

            val market = factory
                .buildMarketWithOdds(
                    matchWithHomeAndAwayCompetitors(home, away),
                    competitor1ToWinBothHalvesMarket(),
                    PRODUCER_ID
                )
                .get();

            assertThat(market.getName(language))
                .isEqualTo(
                    nameOf(
                        sapiMarketDescription,
                        with(HOME_COMPETITOR_PATTERN),
                        replacedWith(home.getName(language))
                    )
                );
        }

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_FRENCH)
        void generatesMarketNameWithAwayCompetitor(Locale language) throws Exception {
            val sapiMarketDescription = penaltyShootoutCompetitor2TotalMarketDescription(language);
            val marketDescription = new MarketDescriptionStub()
                .with(new Translations(language, sapiMarketDescription.getName()))
                .withId(sapiMarketDescription.getId());

            val totalSpecifier = ImmutableMap.of("total", "1");
            val marketDescriptionProvider = providing(in(language), marketDescription, with(totalSpecifier));

            val factory = stubbingOutSportEventAndCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .withDefaultLanguage(language)
                .build();

            val home = teamCompetitor().withName(in(language), germany().getName()).build();
            val away = teamCompetitor().withName(in(language), scotland().getName()).build();

            val market = factory
                .buildMarketWithOdds(
                    matchWithHomeAndAwayCompetitors(home, away),
                    penaltyShootoutCompetitor2TotalMarket(total(1)),
                    PRODUCER_ID
                )
                .get();

            assertThat(market.getName(language))
                .isEqualTo(
                    nameOf(
                        penaltyShootoutCompetitor2TotalMarketDescription(language),
                        with(AWAY_COMPETITOR_PATTERN),
                        replacedWith(away.getName(language))
                    )
                );
        }
    }

    @Nested
    class CompetitorFromSpecifierExpressions {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_FRENCH)
        void generatesMarketNameWithCompetitorPatternReplacedWithCompetitorNameFetchedFromCompetitorProfile(
            Locale language
        ) throws Exception {
            val sapiMarketDescription = totalHolesWonMarketDescription(language);
            val marketDescription = new MarketDescriptionStub()
                .with(new Translations(language, sapiMarketDescription.getName()))
                .withId(sapiMarketDescription.getId());

            val total = 10;
            val competitorId = "sr:competitor:12";
            val specifiers = ImmutableMap.of("total", String.valueOf(total), "competitor", competitorId);
            val marketDescriptionProvider = providing(in(language), marketDescription, with(specifiers));

            val competitorProfile = CompetitorCis
                .competitorCi()
                .withName(in(language), germany().getName())
                .withId(Urn.parse(competitorId))
                .build();
            val profileCache = ProfileCaches.providing(in(language), competitorProfile);

            val factory = stubbingOutSportEventAndCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            val market = factory
                .buildMarketWithOdds(
                    anyMatch(),
                    totalHolesWonMarket(competitor(Urn.parse(competitorId)), total(total)),
                    PRODUCER_ID
                )
                .get();

            assertThat(market.getName(language))
                .isEqualTo(
                    nameOf(
                        totalHolesWonMarketDescription(language),
                        with(COMPETITOR_FROM_SPECIFIER_PATTERN),
                        replacedWith(germany().getName()),
                        with(TOTAL_SPECIFIER_PATTERN),
                        replacedWith(String.valueOf(total))
                    )
                );
        }
    }

    @Nested
    class PlayerExpressions {

        @ParameterizedTest
        @MethodSource(ENGLISH_AND_FRENCH)
        void generatesMarketNameWithPlayerPatternReplacedWithPlayerNameFetchedFromPlayerProfile(
            Locale language
        ) throws Exception {
            val sapiMarketDescription = playerToScoreMarketDescription(language);
            val marketDescription = new MarketDescriptionStub()
                .with(new Translations(language, sapiMarketDescription.getName()))
                .withId(sapiMarketDescription.getId());

            val playerId = Urn.parse("sr:player:9324");
            val specifiers = ImmutableMap.of("player", playerId.toString());
            val marketDescriptionProvider = providing(in(language), marketDescription, with(specifiers));

            val playerProfile = playerProfileCi()
                .withId(playerId)
                .withName(in(language), kaiHavertzProfile().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), playerProfile);

            val factory = stubbingOutSportEventAndCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            val market = factory
                .buildMarketWithOdds(anyMatch(), playerToScoreMarket(player(playerId)), PRODUCER_ID)
                .get();

            assertThat(market.getName(language))
                .isEqualTo(
                    nameOf(
                        playerToScoreMarketDescription(language),
                        with(PLAYER_FROM_SPECIFIER_PATTERN),
                        replacedWith(kaiHavertzProfile().getName())
                    )
                );
        }
    }

    private String nameOf(DescMarket descMarket, String stringToReplace, String replacement) {
        val name = descMarket.getName();
        return name.replace(stringToReplace, replacement);
    }

    private String nameOf(
        DescMarket descMarket,
        String firstPattern,
        String firstReplacement,
        String secondPattern,
        String secondReplacement
    ) {
        val name = descMarket.getName();
        return name.replace(firstPattern, firstReplacement).replace(secondPattern, secondReplacement);
    }

    private String replacedWith(String name) {
        return name;
    }

    @SuppressWarnings("unused")
    static Stream<Locale> englishAndFrench() {
        return Stream.of(ENGLISH, Locale.FRENCH);
    }
}
