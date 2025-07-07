/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import static com.sportradar.unifiedodds.sdk.caching.impl.ci.PlayerProfileCis.playerProfileCi;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.BatterHead2Head.batterHead2HeadMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.Cricket.EnglandNationalTeam2025.joeRootProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.Cricket.IndiaNationalTeam2025.viratKohliProfile;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.batterHead2HeadMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMaxoversSpecifier.maxovers;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayer1Specifier.player1;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayer2Specifier.player2;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.anyMatch;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeOddsAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.Urn.parse;
import static com.sportradar.utils.domain.names.LanguageHolder.in;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionStub;
import com.sportradar.unifiedodds.sdk.caching.markets.NameFocusedOutcomeDescriptionStub;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.Translations;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class OutcomeNameTest {

    private static final int PRODUCER_ID = 1;
    private static final int ANY_INT = 10;

    @Nested
    class PlayerExpressions {

        private Locale language = Locale.ENGLISH;

        @Test
        void generatesPlayerNamesAsOutcomeNamesFromPlayerIdsInSpecifiers() throws Exception {
            val sapiMarketDescription = batterHead2HeadMarketDescription(language);
            val batterHead2HeadMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val specifiers = ImmutableMap.of(
                "player1",
                joeRootProfile().getId(),
                "player2",
                viratKohliProfile().getId(),
                "maxovers",
                ANY_INT + ""
            );
            val marketDescriptionProvider = providing(
                in(language),
                batterHead2HeadMarketDescription,
                with(specifiers)
            );

            val joeRoot = playerProfileCi()
                .withId(parse(joeRootProfile().getId()))
                .withName(in(language), joeRootProfile().getName())
                .build();
            val viratKohli = playerProfileCi()
                .withId(parse(viratKohliProfile().getId()))
                .withName(in(language), viratKohliProfile().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), joeRoot, viratKohli);

            val factory = stubbingOutSportEventAndCaches() //SPORT EVENT
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            val market = factory
                .buildMarketWithOdds(
                    anyMatch(),
                    batterHead2HeadMarket(
                        player1(parse(joeRootProfile().getId())),
                        player2(parse(viratKohliProfile().getId())),
                        maxovers(ANY_INT)
                    ),
                    PRODUCER_ID
                )
                .get();
            val outcomeOdds = market.getOutcomeOdds();

            assertThat(outcomeOdds.get(0)).hasNameForDefaultLanguage(language, joeRootProfile().getName());
            String draw = batterHead2HeadMarketDescription.getOutcomes().get(1).getName(language);
            assertThat(outcomeOdds.get(1)).hasNameForDefaultLanguage(language, draw);
            assertThat(outcomeOdds.get(2)).hasNameForDefaultLanguage(language, viratKohliProfile().getName());
        }
    }

    public MarketDescriptionStub stubWithMarketAndOutcomeIdsAndNamesFrom(
        DescMarket sapiDescMarket,
        Locale language
    ) {
        return new MarketDescriptionStub()
            .with(new Translations(language, sapiDescMarket.getName()))
            .with(outcomeIdAndName(in(language), from(sapiDescMarket)))
            .withId(sapiDescMarket.getId());
    }

    private List<OutcomeDescription> outcomeIdAndName(LanguageHolder lang, DescMarket sapiMarketDescription) {
        return sapiMarketDescription
            .getOutcomes()
            .getOutcome()
            .stream()
            .map(o ->
                new NameFocusedOutcomeDescriptionStub(o.getId(), new Translations(lang.get(), o.getName()))
            )
            .collect(Collectors.toList());
    }
}
