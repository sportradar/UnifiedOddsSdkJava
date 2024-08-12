/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.exportAndImportItemsIn;
import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.exportAndImportTheOnlyItemIn;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.FullyPopulatedStage.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedCompetitor.fullyPopulatedFootballCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedCompetitor.fullyPopulatedFormula1CompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.alonsoCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Nascar2024.truexJrCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor.convertToVirtual;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor.virtualStageCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorLocationInTournamentInfo.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.FULLY_POPULATED_TOURNAMENT_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.fullyPopulatedFootballTournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.*;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.failingFirstAndThenProviding;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.failingToProvide;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.Urn.parse;
import static com.sportradar.utils.Urns.CompetitorProfiles.urnForAnyCompetitor;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.FullyPopulatedStage;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedCompetitor;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor;
import com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.utils.Urn;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("ClassFanOutComplexity")
class CompetitorProfileCacheImplTest {

    private static final String PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE =
        "com.sportradar.unifiedodds.sdk.caching.impl.CompetitorProfileCacheImplTest$" +
        "CompetitorEndpointBackedParameterSources" +
        "#propertiesFromFootballCompetitorProfile";
    private static final String PROPERTIES_FROM_FORMULA_1_COMPETITOR_PROFILE =
        "com.sportradar.unifiedodds.sdk.caching.impl.CompetitorProfileCacheImplTest$" +
        "CompetitorEndpointBackedParameterSources" +
        "#propertiesFromFormula1CompetitorProfile";

    private final String anyQualifier = "anyQualifier";
    private final MatchCi anyMatch = mock(MatchCi.class);
    private final int anyDivision = 2;
    private final boolean notVirtual = false;
    private Boolean noInfoAboutVirtual;
    private final DataRouterManagerBuilder dataRouterManagerBuilder = new DataRouterManagerBuilder();

    @Nested
    public class SingleCompetitor {

        @Test
        public void profileUrnIsMandatoryToConstructProfile() {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            assertThatThrownBy(() ->
                    profileFactory.buildCompetitor(
                        null,
                        anyQualifier,
                        anyDivision,
                        notVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void nonNullLanguagesAreMandatoryToConstructProfile() {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            assertThatThrownBy(() ->
                    profileFactory.buildCompetitor(
                        urnForAnyCompetitor(),
                        anyQualifier,
                        anyDivision,
                        notVirtual,
                        anyMatch,
                        null
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest
        @ValueSource(booleans = { true, false })
        @Disabled
        public void competitorVirtualAtBuildTimeIsDeadArgument(boolean isVirtual) throws Exception {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            val competitor = profileFactory.buildCompetitor(
                urnForAnyCompetitor(),
                anyQualifier,
                anyDivision,
                isVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @ParameterizedTest
        @MethodSource(PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE)
        @Disabled
        public void cachesPropertiesFromFootballCompetitorProfile(
            Urn competitorUrn,
            CompetitorEndpointBackedParameterSources.PropertyGetterFromCompetitor property,
            Object expected
        ) throws Exception {
            SapiCompetitorProfileEndpoint competitor = fullyPopulatedFootballCompetitorProfile();

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), competitor);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            Callable<Competitor> competitorCreator = () ->
                profileFactory.buildCompetitor(
                    competitorUrn,
                    anyQualifier,
                    anyDivision,
                    noInfoAboutVirtual,
                    anyMatch,
                    asList(ENGLISH)
                );

            val firstCall = property.getFrom(competitorCreator.call());
            val secondCall = property.getFrom(competitorCreator.call());

            assertThat(expected).isNotNull();
            assertThat(firstCall).isEqualTo(expected);
            assertThat(secondCall).isEqualTo(expected);
            verify(competitorProvider, times(1)).getData(any(), any());
        }

        @ParameterizedTest
        @MethodSource(PROPERTIES_FROM_FORMULA_1_COMPETITOR_PROFILE)
        @Disabled
        public void cachesPropertiesFromFormula1CompetitorProfile(
            Urn competitorUrn,
            CompetitorEndpointBackedParameterSources.PropertyGetterFromCompetitor property,
            Object expected
        ) throws Exception {
            SapiCompetitorProfileEndpoint competitor = fullyPopulatedFormula1CompetitorProfile();

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), competitor);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            Callable<Competitor> competitorCreator = () ->
                profileFactory.buildCompetitor(
                    competitorUrn,
                    anyQualifier,
                    anyDivision,
                    noInfoAboutVirtual,
                    anyMatch,
                    asList(ENGLISH)
                );

            val firstCall = property.getFrom(competitorCreator.call());
            val secondCall = property.getFrom(competitorCreator.call());

            assertThat(expected).isNotNull();
            assertThat(firstCall).isEqualTo(expected);
            assertThat(secondCall).isEqualTo(expected);
            verify(competitorProvider, times(1)).getData(any(), any());
        }

        @Test
        public void retrievesNonVirtualCompetitorForMatchPopulatedFromStageSummary() throws Exception {
            SapiStageSummaryEndpoint summary = fullyPopulatedStageSummary();
            replace1stCompetitorWith(GrandPrix2024.fernandoAlonso(), summary);
            Urn competitorUrn = parse(GrandPrix2024.ALONSO_COMPETITOR_URN);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FullyPopulatedStage.URN), summary)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(competitorUrn.toString()),
                            withoutVirtualFlag(alonsoCompetitorProfile())
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(FullyPopulatedStage.URN), anyMatch);

            val stage = profileFactory.buildCompetitor(
                competitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(stage.isVirtual()).isFalse();
        }

        @Test
        public void retrievesNonVirtualCompetitorPopulatedFromTournamentPropertyInTournamentInfo()
            throws Exception {
            SapiTournamentInfoEndpoint tournament = nascarCup2024TournamentInfo();
            Urn truexJrUrn = parse(SapiTeams.Nascar2024.TRUEX_JR_COMPETITOR_URN);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(Nascar2024.TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(truexJrUrn.toString()),
                            withoutVirtualFlag(truexJrCompetitorProfile())
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(Nascar2024.TOURNAMENT_URN), anyMatch);

            val competitor = profileFactory.buildCompetitor(
                truexJrUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorPopulatedFromTournamentPropertyInTournamentInfo()
            throws Exception {
            SapiTournamentInfoEndpoint tournament = nascarCup2024TournamentInfo();
            tournament.getTournament().getCompetitors().getCompetitor().add(virtualStageCompetitor());
            Urn virtualCompetitorUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(Nascar2024.TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(virtualCompetitorUrn.toString()),
                            withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(Nascar2024.TOURNAMENT_URN), anyMatch);

            val competitor = profileFactory.buildCompetitor(
                virtualCompetitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        public void retrievesNonVirtualCompetitorPopulatedFromTheRootOfTournamentInfo() throws Exception {
            SapiTournamentInfoEndpoint tournament = fullyPopulatedFootballTournamentInfo(
                COMPETITORS_AT_ROOT_LEVEL
            );
            val competitorId = tournament.getCompetitors().getCompetitor().get(0).getId();

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FULLY_POPULATED_TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(competitorId),
                            withoutVirtualFlag(fullyPopulatedFootballCompetitorProfile())
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(
                ENGLISH,
                parse(FULLY_POPULATED_TOURNAMENT_URN),
                anyMatch
            );

            val competitor = profileFactory.buildCompetitor(
                parse(competitorId),
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorPopulatedFromTheRootOfTournamentInfo() throws Exception {
            SapiTournamentInfoEndpoint tournament = fullyPopulatedFootballTournamentInfo(
                COMPETITORS_AT_ROOT_LEVEL
            );
            tournament.getCompetitors().getCompetitor().add(virtualStageCompetitor());
            Urn virtualCompetitorUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FULLY_POPULATED_TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(virtualCompetitorUrn.toString()),
                            withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(
                ENGLISH,
                parse(FULLY_POPULATED_TOURNAMENT_URN),
                anyMatch
            );

            val competitor = profileFactory.buildCompetitor(
                virtualCompetitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        public void retrievesNonVirtualCompetitorPopulatedFromGroupsOfTournamentInfo() throws Exception {
            SapiTournamentInfoEndpoint tournament = fullyPopulatedFootballTournamentInfo(
                COMPETITORS_IN_GROUP
            );
            Urn competitorUrn = parse(
                tournament.getGroups().getGroup().get(0).getCompetitor().get(0).getId()
            );

            DataRouterImpl dataRouter = new DataRouterImpl();

            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FULLY_POPULATED_TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(competitorUrn.toString()),
                            withoutVirtualFlag(fullyPopulatedFootballCompetitorProfile())
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(
                ENGLISH,
                parse(FULLY_POPULATED_TOURNAMENT_URN),
                anyMatch
            );

            val competitor = profileFactory.buildCompetitor(
                competitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorPopulatedFromGroupsOfTournamentInfo() throws Exception {
            SapiTournamentInfoEndpoint tournament = fullyPopulatedFootballTournamentInfo(
                COMPETITORS_IN_GROUP
            );
            tournament.getGroups().getGroup().get(0).getCompetitor().add(virtualStageCompetitor());
            Urn virtualCompetitorUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FULLY_POPULATED_TOURNAMENT_URN), tournament)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(virtualCompetitorUrn.toString()),
                            withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(
                ENGLISH,
                parse(FULLY_POPULATED_TOURNAMENT_URN),
                anyMatch
            );

            val competitor = profileFactory.buildCompetitor(
                virtualCompetitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        public void retrievesNonVirtualCompetitorForMatchPopulatedFromSummary() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            Urn germanyUrn = parse(Germany2024Uefa.COMPETITOR_ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(GERMANY_SCOTLAND_MATCH_URN), summary)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(germanyUrn.toString()),
                            withoutVirtualFlag(fullyPopulatedFootballCompetitorProfile())
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(GERMANY_SCOTLAND_MATCH_URN), anyMatch);

            val germany = profileFactory.buildCompetitor(
                germanyUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(germany.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorForMatchPopulatedFromStageSummary() throws Exception {
            SapiStageSummaryEndpoint summary = fullyPopulatedStageSummary();
            replace1stCompetitorWithVirtual(summary);
            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(FullyPopulatedStage.URN), summary)
                    )
                    .withCompetitors(
                        providing(
                            in(ENGLISH),
                            with(virtualUrn.toString()),
                            withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                        )
                    )
                    .with(dataRouter)
                    .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(FullyPopulatedStage.URN), anyMatch);

            val stage = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(stage.isVirtual()).isTrue();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorForMatchPopulatedFromSummary() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withSummaries(providing(in(ENGLISH), with(virtualUrn.toString()), summary))
                .withCompetitors(
                    providing(
                        in(ENGLISH),
                        with(virtualUrn.toString()),
                        withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                    )
                )
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, virtualUrn, anyMatch);

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        public void retrievesNonVirtualCompetitor() throws Exception {
            SapiCompetitorProfileEndpoint germany = Germany2024Uefa.germanyCompetitorProfile();
            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), germany))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitorForMatchPopulatedFromTournamentSummary() throws Exception {
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            val virtualStageCompetitor = nascarCupWithVirtual
                .getTournament()
                .getCompetitors()
                .getCompetitor()
                .get(0);
            val virtualCompetitorUrn = virtualStageCompetitor.getId();

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withSummaries(providing(in(ENGLISH), with(virtualCompetitorUrn), nascarCupWithVirtual))
                .withCompetitors(
                    providing(
                        in(ENGLISH),
                        with(virtualCompetitorUrn.toString()),
                        withoutVirtualFlag(convertToVirtual(fullyPopulatedFootballCompetitorProfile()))
                    )
                )
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(virtualCompetitorUrn), anyMatch);

            val competitor = profileFactory.buildCompetitor(
                parse(virtualCompetitorUrn),
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        @Disabled
        public void retrievesVirtualCompetitor() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);

            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), virtual))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        @Disabled
        public void absenceOfVirtualFlagInSummaryDoesNotOverrideVirtualFlagFromCompetitor() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            Urn germanyUrn = parse(Germany2024Uefa.COMPETITOR_ID);
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);
            virtual.getCompetitor().setId(germanyUrn.toString());

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(germanyUrn.toString()), virtual))
                .withSummaries(providing(in(ENGLISH), with(germanyUrn.toString()), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                germanyUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isTrue();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, germanyUrn, anyMatch);
            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        @Disabled
        public void virtualFlagInSummaryOverridesLackOfVirtualFlagFromCompetitor() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            Urn virtualUrn = parse(VirtualCompetitor.ID);
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(null);
            virtual.getCompetitor().setId(virtualUrn.toString());

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), virtual))
                .withSummaries(providing(in(ENGLISH), with(virtualUrn.toString()), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, virtualUrn, anyMatch);
            assertThat(competitor.isVirtual()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(booleans = false)
        public void nonVirtualProfileInDifferentLanguageDoesOverrideVirtualFlagFromSummary()
            throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            Urn virtualUrn = parse(VirtualCompetitor.ID);
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(false);
            virtual.getCompetitor().setId(virtualUrn.toString());

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), virtual))
                .withSummaries(providing(in(FRENCH), with(virtualUrn.toString()), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            dataRouterManager.requestSummaryEndpoint(FRENCH, virtualUrn, anyMatch);
            competitor.getName(ENGLISH);
            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void undefinedVirtualProfileInDifferentLanguageDoesNotOverrideVirtualFlagFromSummary()
            throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            Urn virtualUrn = parse(VirtualCompetitor.ID);
            SapiCompetitorProfileEndpoint undefinedVirtual = SapiTeams.VirtualCompetitor.profile();
            undefinedVirtual.getCompetitor().setVirtual(null);
            undefinedVirtual.getCompetitor().setId(virtualUrn.toString());

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), undefinedVirtual))
                .withSummaries(providing(in(FRENCH), with(virtualUrn.toString()), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            dataRouterManager.requestSummaryEndpoint(FRENCH, virtualUrn, anyMatch);
            competitor.getName(ENGLISH);
            assertThat(competitor.isVirtual()).isTrue();
        }

        @Test
        public void competitorProfileApiEndpointDoesNotContainVirtualFlagCurrently() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();

            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(providing(in(ENGLISH), with(virtualUrn.toString()), virtual))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void competitorProfileApiEndpointInvokedOnceEvenIfVirtualFlagIsAbsent() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(null);

            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(virtualUrn.toString()), virtual);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                virtualUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            competitor.isVirtual();
            competitor.isVirtual();
            verify(competitorProvider, times(1)).getData(any(), any());
        }

        @Test
        @Disabled
        public void throwsExceptionOnFailedRetrieval() throws Exception {
            DataRouterImpl dataRouter = new DataRouterImpl();

            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(failingToProvide(in(ENGLISH), with(urnForAnyCompetitor().toString())))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .with(dataRouterManager)
                .withDefaultLanguage(ENGLISH)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(ExceptionHandlingStrategy.Throw)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                urnForAnyCompetitor(),
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThatThrownBy(() -> competitor.isVirtual()).isInstanceOf(ObjectNotFoundException.class);
        }

        @Test
        public void returnsNotVirtualOnFailedRetrievalWhenExceptionsAreSupressed() throws Exception {
            DataRouterImpl dataRouter = new DataRouterImpl();

            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(failingToProvide(in(ENGLISH), with(urnForAnyCompetitor().toString())))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .with(dataRouterManager)
                .withDefaultLanguage(ENGLISH)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(ExceptionHandlingStrategy.Catch)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                urnForAnyCompetitor(),
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(competitor.isVirtual()).isFalse();
        }

        @Test
        @Disabled
        public void exportsImportsVirtualCompetitor() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);

            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(virtualUrn.toString()), virtual);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            assertThat(
                profileFactory
                    .buildCompetitor(
                        virtualUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isTrue();

            exportAndImportTheOnlyItemIn(profileCache);

            assertThat(
                profileFactory
                    .buildCompetitor(
                        virtualUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isTrue();
            verify(competitorProvider, times(1)).getData(ENGLISH, with(virtualUrn.toString()));
        }

        @Test
        @Disabled
        public void exportsImportsNonVirtualCompetitor() throws Exception {
            val competitor = alonsoCompetitorProfile();
            competitor.getCompetitor().setVirtual(false);

            Urn competitorUrn = parse(competitor.getCompetitor().getId());

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), competitor);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            assertThat(
                profileFactory
                    .buildCompetitor(
                        competitorUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isFalse();

            exportAndImportItemsIn(profileCache);

            assertThat(
                profileFactory
                    .buildCompetitor(
                        competitorUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isFalse();
            verify(competitorProvider, times(1)).getData(ENGLISH, with(competitorUrn.toString()));
        }

        @Test
        @SneakyThrows
        @Disabled
        public void workaroundBreakingChangeAfterReimportMakesRedundantCallIfVirtualSourcedNotFromProfile() {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);
            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(virtualUrn.toString()), virtual);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .withSummaries(providing(in(ENGLISH), with(GERMANY_SCOTLAND_MATCH_URN), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .with(ExceptionHandlingStrategy.Throw)
                .build();
            dataRouterManager.requestSummaryEndpoint(ENGLISH, parse(GERMANY_SCOTLAND_MATCH_URN), anyMatch);

            exportAndImportItemsIn(profileCache);

            assertThat(
                profileFactory
                    .buildCompetitor(
                        virtualUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isTrue();
        }

        @Test
        @Disabled
        public void fetchesProfileAfterReimportIfVirtualWasNotKnownBeforeExport() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);
            Urn virtualUrn = parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = failingFirstAndThenProviding(
                in(ENGLISH),
                with(virtualUrn.toString()),
                virtual
            );
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .withSummaries(providing(in(ENGLISH), with(GERMANY_SCOTLAND_MATCH_URN), summary))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .with(ExceptionHandlingStrategy.Throw)
                .build();

            assertThatThrownBy(() ->
                profileFactory
                    .buildCompetitor(
                        virtualUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            );

            exportAndImportItemsIn(profileCache);

            assertThat(
                profileFactory
                    .buildCompetitor(
                        virtualUrn,
                        anyQualifier,
                        anyDivision,
                        noInfoAboutVirtual,
                        anyMatch,
                        asList(ENGLISH)
                    )
                    .isVirtual()
            )
                .isTrue();
        }

        private SapiCompetitorProfileEndpoint withoutVirtualFlag(SapiCompetitorProfileEndpoint profile) {
            profile.getCompetitor().setVirtual(null);
            return profile;
        }
    }

    @Nested
    public class MultipleCompetitors {

        @Test
        public void nonNullProfileUrnsIsMandatoryToConstructProfile() {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            assertThatThrownBy(() ->
                    profileFactory.buildStreamCompetitors(null, mock(SportEventCi.class), asList(ENGLISH))
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void nonNullProfileUrnIsMandatoryToConstructProfile() {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            assertThatThrownBy(() ->
                    profileFactory.buildStreamCompetitors(
                        asList(null),
                        mock(SportEventCi.class),
                        asList(ENGLISH)
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void nonNullLanguagesAreMandatoryToConstructProfile() {
            val profileCache = stubbingOutDataRouterManager().build();
            val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

            assertThatThrownBy(() ->
                    profileFactory.buildStreamCompetitors(
                        asList(urnForAnyCompetitor()),
                        mock(SportEventCi.class),
                        null
                    )
                )
                .isInstanceOf(NullPointerException.class);
        }
    }

    public static class CompetitorEndpointBackedParameterSources {

        public static Stream<Arguments> propertiesFromFootballCompetitorProfile() {
            val argFactory = new CompetitorPropertyArguments(
                parse(FullyPopulatedCompetitor.URN),
                fullyPopulatedFootballCompetitorProfile()
            );
            return Stream
                .of(
                    propertiesAtRootLevel(argFactory),
                    divisionProperties(argFactory),
                    jerseyProperties(argFactory),
                    managerProperties(argFactory),
                    venueProperties(argFactory)
                )
                .flatMap(s -> s);
        }

        public static Stream<Arguments> propertiesFromFormula1CompetitorProfile() {
            val argFactory = new CompetitorPropertyArguments(
                parse(FullyPopulatedCompetitor.URN),
                fullyPopulatedFormula1CompetitorProfile()
            );
            return Stream
                .of(
                    propertiesAtRootLevel(argFactory),
                    venueProperties(argFactory),
                    raceDriverProperties(argFactory)
                )
                .flatMap(s -> s);
        }

        private static Stream<Arguments> propertiesAtRootLevel(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor(
                    "individual name translation",
                    c -> c.getName(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getName()
                ),
                argFactory.getFor(
                    "name translation from names",
                    c -> c.getNames().get(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getName()
                ),
                argFactory.getFor("gender", Competitor::getGender, p -> p.getCompetitor().getGender()),
                argFactory.getFor("age group", Competitor::getAgeGroup, p -> p.getCompetitor().getAgeGroup()),
                argFactory.getFor("age state", Competitor::getState, p -> p.getCompetitor().getState()),
                argFactory.getFor("isVirtual", Competitor::isVirtual, p -> p.getCompetitor().isVirtual()),
                argFactory.getFor(
                    "shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().getShortName()
                ),
                argFactory.getFor(
                    "country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().getCountryCode()
                ),
                argFactory.getFor(
                    "individual country translation",
                    c -> c.getCountry(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getCountry()
                ),
                argFactory.getFor(
                    "country translation from countries",
                    c -> c.getCountries().get(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getCountry()
                ),
                argFactory.getFor(
                    "individual abbreviation translation",
                    c -> c.getAbbreviation(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getAbbreviation()
                ),
                argFactory.getFor(
                    "abbreviation translation from abbreviations",
                    c -> c.getAbbreviations().get(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getCompetitor().getAbbreviation()
                )
            );
        }

        private static Stream<Arguments> divisionProperties(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor(
                    "division name",
                    c -> c.getDivision().getDivisionName(),
                    p -> p.getCompetitor().getDivisionName()
                ),
                argFactory.getFor(
                    "division number",
                    c -> c.getDivision().getDivision(),
                    p -> p.getCompetitor().getDivision()
                )
            );
        }

        private static Stream<Arguments> jerseyProperties(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor(
                    "jersey type",
                    c -> c.getJerseys().get(0).getType(),
                    p -> p.getJerseys().getJersey().get(0).getType()
                ),
                argFactory.getFor(
                    "jersey base",
                    c -> c.getJerseys().get(0).getBase(),
                    p -> p.getJerseys().getJersey().get(0).getBase()
                ),
                argFactory.getFor(
                    "jersey sleeve",
                    c -> c.getJerseys().get(0).getSleeve(),
                    p -> p.getJerseys().getJersey().get(0).getSleeve()
                ),
                argFactory.getFor(
                    "jersey number",
                    c -> c.getJerseys().get(0).getNumber(),
                    p -> p.getJerseys().getJersey().get(0).getNumber()
                ),
                argFactory.getFor(
                    "jersey stripes",
                    c -> c.getJerseys().get(0).getStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).getStripesColor()
                ),
                argFactory.getFor(
                    "jersey split color",
                    c -> c.getJerseys().get(0).getSplitColor(),
                    p -> p.getJerseys().getJersey().get(0).getSplitColor()
                ),
                argFactory.getFor(
                    "jersey shirt type",
                    c -> c.getJerseys().get(0).getShirtType(),
                    p -> p.getJerseys().getJersey().get(0).getShirtType()
                ),
                argFactory.getFor(
                    "jersey sleeve detail",
                    c -> c.getJerseys().get(0).getSleeveDetail(),
                    p -> p.getJerseys().getJersey().get(0).getSleeveDetail()
                )
            );
        }

        private static Stream<Arguments> managerProperties(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor("manager id", c -> c.getManager().getId(), p -> p.getManager().getId()),
                argFactory.getFor(
                    "manager name translation from names",
                    c -> c.getManager().getNames().get(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getManager().getName()
                ),
                argFactory.getFor(
                    "manager individual name translation",
                    c -> c.getManager().getName(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getManager().getName()
                ),
                argFactory.getFor(
                    "manager nationality",
                    c -> c.getManager().getNationality(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getManager().getNationality()
                ),
                argFactory.getFor(
                    "manager country code",
                    c -> c.getManager().getCountryCode(),
                    p -> p.getManager().getCountryCode()
                )
            );
        }

        private static Stream<Arguments> venueProperties(CompetitorPropertyArguments argFactory) {
            return Stream.concat(
                Stream.of(
                    argFactory.getFor("venue id", c -> c.getVenue().getId(), p -> p.getVenue().getId()),
                    argFactory.getFor(
                        "venue name translation from names",
                        c -> c.getVenue().getNames().get(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getName()
                    ),
                    argFactory.getFor(
                        "venue individual name translation",
                        c -> c.getVenue().getName(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getName()
                    ),
                    argFactory.getFor(
                        "venue city translation from cities",
                        c -> c.getVenue().getCities().get(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getCityName()
                    ),
                    argFactory.getFor(
                        "venue individual city translation",
                        c -> c.getVenue().getCity(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getCityName()
                    ),
                    argFactory.getFor(
                        "venue country translation from countries",
                        c -> c.getVenue().getCountries().get(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getCountryName()
                    ),
                    argFactory.getFor(
                        "venue individual country translation",
                        c -> c.getVenue().getCountry(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getCountryName()
                    ),
                    argFactory.getFor(
                        "venue country translation from countries",
                        c -> c.getVenue().getCountry(FullyPopulatedCompetitor.LANGUAGE),
                        p -> p.getVenue().getCountryName()
                    ),
                    argFactory.getFor(
                        "venue capacity",
                        c -> c.getVenue().getCapacity(),
                        p -> p.getVenue().getCapacity()
                    ),
                    argFactory.getFor(
                        "venue coordinates",
                        c -> c.getVenue().getCoordinates(),
                        p -> p.getVenue().getMapCoordinates()
                    ),
                    argFactory.getFor(
                        "venue country code",
                        c -> c.getVenue().getCountryCode(),
                        p -> p.getVenue().getCountryCode()
                    ),
                    argFactory.getFor(
                        "venue state",
                        c -> c.getVenue().getState(),
                        p -> p.getVenue().getState()
                    )
                ),
                courseProperties(argFactory)
            );
        }

        private static Stream<Arguments> courseProperties(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor(
                    "venue course name translation from names ",
                    c -> c.getVenue().getCourses().get(0).getNames().get(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getVenue().getCourse().get(0).getName()
                ),
                argFactory.getFor(
                    "venue course individual name translation",
                    c -> c.getVenue().getCourses().get(0).getName(FullyPopulatedCompetitor.LANGUAGE),
                    p -> p.getVenue().getCourse().get(0).getName()
                ),
                argFactory.getFor(
                    "venue course id",
                    c -> c.getVenue().getCourses().get(0).getId(),
                    p -> p.getVenue().getCourse().get(0).getId()
                ),
                argFactory.getFor(
                    "venue course hole number",
                    c -> c.getVenue().getCourses().get(0).getHoles().get(0).getNumber(),
                    p -> p.getVenue().getCourse().get(0).getHole().get(0).getNumber()
                ),
                argFactory.getFor(
                    "venue course hole par",
                    c -> c.getVenue().getCourses().get(0).getHoles().get(0).getPar(),
                    p -> p.getVenue().getCourse().get(0).getHole().get(0).getPar()
                )
            );
        }

        private static Stream<Arguments> raceDriverProperties(CompetitorPropertyArguments argFactory) {
            return Stream.concat(
                Stream.of(
                    argFactory.getFor(
                        "race driver id",
                        c -> c.getRaceDriver().getRaceDriverId(),
                        p -> p.getRaceDriverProfile().getRaceDriver().getId()
                    ),
                    argFactory.getFor(
                        "race driver team id",
                        c -> c.getRaceDriver().getRaceTeamId(),
                        p -> p.getRaceDriverProfile().getRaceTeam().getId()
                    )
                ),
                carProperties(argFactory)
            );
        }

        private static Stream<Arguments> carProperties(CompetitorPropertyArguments argFactory) {
            return Stream.of(
                argFactory.getFor(
                    "race driver car name",
                    c -> c.getRaceDriver().getCar().getName(),
                    p -> p.getRaceDriverProfile().getCar().getName()
                ),
                argFactory.getFor(
                    "race driver car chassis",
                    c -> c.getRaceDriver().getCar().getChassis(),
                    p -> p.getRaceDriverProfile().getCar().getChassis()
                ),
                argFactory.getFor(
                    "race driver car engine name",
                    c -> c.getRaceDriver().getCar().getEngineName(),
                    p -> p.getRaceDriverProfile().getCar().getEngineName()
                )
            );
        }

        public interface PropertyGetterFromCompetitor {
            Object getFrom(Competitor market);
        }

        @AllArgsConstructor
        public static class CompetitorPropertyArguments {

            private Urn competitorId;
            private SapiCompetitorProfileEndpoint profile;

            public Arguments getFor(
                String propertyName,
                PropertyGetterFromCompetitor propertyGetter,
                Function<SapiCompetitorProfileEndpoint, Object> expected
            ) {
                return Arguments.of(
                    competitorId,
                    Named.of(propertyName, propertyGetter),
                    expected.apply(profile)
                );
            }
        }
    }
}
