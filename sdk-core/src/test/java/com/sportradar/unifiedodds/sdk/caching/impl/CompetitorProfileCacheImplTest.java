/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.*;
import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiSimpleTeams.FullyPopulatedCollegeBasketballTeam.fullyPopulatedCollegeBasketballTeam;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.FullyPopulatedStage.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedFootballCompetitor.fullyPopulatedFootballCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedFormula1Competitor.fullyPopulatedFormula1CompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.alonsoCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Nascar2024.truexJrCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor.convertToVirtual;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor.virtualStageCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorLocationInTournamentInfo.COMPETITORS_AT_ROOT_LEVEL;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.CompetitorLocationInTournamentInfo.COMPETITORS_IN_GROUP;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.FULLY_POPULATED_TOURNAMENT_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FullyPopulatedTournament.fullyPopulatedFootballTournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.nascarCup2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.replaceFirstCompetitorWithVirtual;
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

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.MatchCi;
import com.sportradar.unifiedodds.sdk.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.caching.impl.CompetitorProfileCacheImplTest.CompetitorEndpointBackedParameterSources.PropertySetterToSapiCompetitorProfileEndpoint;
import com.sportradar.unifiedodds.sdk.conn.SapiSimpleTeams.FullyPopulatedCollegeBasketballTeam;
import com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.FullyPopulatedStage;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.FullyPopulatedFootballCompetitor;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor;
import com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.CompetitorPlayer;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.utils.Urn;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber" })
class CompetitorProfileCacheImplTest {

    private static final String PROPERTIES_PACKAGE_CLASS =
        "com.sportradar.unifiedodds.sdk.caching.impl.CompetitorProfileCacheImplTest$" +
        "CompetitorEndpointBackedParameterSources";
    private static final String ALL_PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE =
        PROPERTIES_PACKAGE_CLASS + "#propertiesFromFootballCompetitorProfile";
    private static final String ALL_PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE_TEAM =
        PROPERTIES_PACKAGE_CLASS + "#propertiesFromFootballCompetitorProfileTeam";
    private static final String ALL_PROPERTIES_FROM_FOOTBALL_EXPORTABLE_COMPETITOR =
        PROPERTIES_PACKAGE_CLASS + "#propertiesFromFootballExportableCompetitor";

    private static final String OLD_JERSEY_PROPERTIES_IN_PRE_V3_3_3 =
        PROPERTIES_PACKAGE_CLASS + "#jerseyPropertiesPreV3_3_3";
    private static final String ABSENCE_OF_NEW_JERSEY_PROPERTIES_IN_VERSIONS_PRE_V3_3_3 =
        PROPERTIES_PACKAGE_CLASS + "#absenceOfNewJerseyPropertiesInVersionsPreV3_3_3";
    private static final String ALL_PROPERTIES_FROM_FORMULA_1_COMPETITOR_PROFILE =
        PROPERTIES_PACKAGE_CLASS + "#propertiesFromFormula1CompetitorProfile";
    private static final String ALL_PROPERTIES_FROM_COLLEGE_BASKETBALL_TEAM_COMPETITOR_PROFILE =
        "com.sportradar.unifiedodds.sdk.caching.impl.CompetitorProfileCacheImplTest" +
        "$SimpleTeamProfileEndpointBackedParameterSources" +
        "#propertiesFromCollegeBasketballCompetitorProfile";

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
        @MethodSource(
            {
                ALL_PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE,
                ALL_PROPERTIES_FROM_FOOTBALL_EXPORTABLE_COMPETITOR,
            }
        )
        void preservesPropertiesAfterExportingAndReimporting(
            PropertyGetterFromCompetitor property,
            PropertySetterToSapiCompetitorProfileEndpoint sapiProperty,
            Object expected
        ) throws Exception {
            Urn competitorUrn = parse(FullyPopulatedFootballCompetitor.URN);
            val sapiCompetitor = fullyPopulatedFootballCompetitorProfile();
            sapiProperty.setOn(sapiCompetitor);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), sapiCompetitor);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            profileCache.getCompetitorProfile(competitorUrn, asList(ENGLISH)).getGender();

            exportAndImportItemsIn(profileCache);

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                competitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(property.getFrom(competitor)).isEqualTo(expected);
            verify(competitorProvider, times(1)).getData(ENGLISH, with(competitorUrn.toString()));
        }

        @ParameterizedTest
        @MethodSource(
            { OLD_JERSEY_PROPERTIES_IN_PRE_V3_3_3, ABSENCE_OF_NEW_JERSEY_PROPERTIES_IN_VERSIONS_PRE_V3_3_3 }
        )
        void legacyPreV3_3_3ExportIntegrationPreservesOnlyPreV3_3_3ExistingPropertiesAfterExportingAndReimporting(
            PropertyGetterFromCompetitor property,
            PropertySetterToSapiCompetitorProfileEndpoint sapiProperty,
            Object expected
        ) throws Exception {
            Urn competitorUrn = parse(FullyPopulatedFootballCompetitor.URN);
            val sapiCompetitor = fullyPopulatedFootballCompetitorProfile();
            sapiProperty.setOn(sapiCompetitor);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), sapiCompetitor);
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withCompetitors(competitorProvider)
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));

            profileCache.getCompetitorProfile(competitorUrn, asList(ENGLISH)).getGender();

            exportAndImportItemsUsingLegacyPreV3_3_3ImportIntegration(profileCache);

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                competitorUrn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(property.getFrom(competitor)).isEqualTo(expected);
        }

        @ParameterizedTest
        @MethodSource(
            {
                ALL_PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE,
                ALL_PROPERTIES_FROM_FOOTBALL_COMPETITOR_PROFILE_TEAM,
            }
        )
        void cachesPropertiesFromFootballCompetitorProfile(
            PropertyGetterFromCompetitor property,
            PropertySetterToSapiCompetitorProfileEndpoint sapiProperty,
            Object expected
        ) throws Exception {
            Urn competitorUrn = parse(FullyPopulatedFootballCompetitor.URN);
            SapiCompetitorProfileEndpoint sapiCompetitor = fullyPopulatedFootballCompetitorProfile();
            sapiProperty.setOn(sapiCompetitor);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), sapiCompetitor);
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

            assertThat(firstCall).isEqualTo(expected);
            assertThat(secondCall).isEqualTo(expected);
            verify(competitorProvider, times(1)).getData(any(), any());
        }

        @ParameterizedTest
        @MethodSource(ALL_PROPERTIES_FROM_FORMULA_1_COMPETITOR_PROFILE)
        public void cachesPropertiesFromFormula1CompetitorProfile(
            PropertyGetterFromCompetitor property,
            PropertySetterToSapiCompetitorProfileEndpoint sapiProperty,
            Object expected
        ) throws Exception {
            Urn competitorUrn = parse(FullyPopulatedFootballCompetitor.URN);
            SapiCompetitorProfileEndpoint sapiCompetitor = fullyPopulatedFormula1CompetitorProfile();
            sapiProperty.setOn(sapiCompetitor);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = providing(in(ENGLISH), with(competitorUrn.toString()), sapiCompetitor);
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
        void retrievesVirtualCompetitorForMatchPopulatedFromMatchSummary() throws Exception {
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

        @ParameterizedTest
        @MethodSource(ALL_PROPERTIES_FROM_COLLEGE_BASKETBALL_TEAM_COMPETITOR_PROFILE)
        public void retrievesCompetitorPopulatedFromSimpleTeamEndpoint(
            PropertyGetterFromCompetitor property,
            SimpleTeamProfileEndpointBackedParameterSources.PropertySetterToSapiSimpleTeamProfileEndpoint sapiProperty,
            Object expected
        ) throws Exception {
            Urn urn = parse(FullyPopulatedCollegeBasketballTeam.URN);
            val sapiSimpleTeam = fullyPopulatedCollegeBasketballTeam();
            sapiProperty.setOn(sapiSimpleTeam);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
                .withSimpleTeams(providing(in(ENGLISH), with(urn.toString()), sapiSimpleTeam))
                .with(dataRouter)
                .build();
            val profileCache = stubbingOutDataRouterManager()
                .withDefaultLanguage(ENGLISH)
                .with(dataRouterManager)
                .build();
            dataRouter.setDataListeners(asList(profileCache));
            dataRouterManager.requestSimpleTeamEndpoint(ENGLISH, urn, anyMatch);

            val profileFactory = stubbingOutAllCachesAndStatusFactory()
                .withDefaultLanguage(ENGLISH)
                .with(profileCache)
                .build();

            val competitor = profileFactory.buildCompetitor(
                urn,
                anyQualifier,
                anyDivision,
                noInfoAboutVirtual,
                anyMatch,
                asList(ENGLISH)
            );

            assertThat(property.getFrom(competitor)).isEqualTo(expected);
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

    @SuppressWarnings("MultipleStringLiterals")
    public static class CompetitorEndpointBackedParameterSources {

        static Stream<Arguments> propertiesFromFootballCompetitorProfile() {
            return Stream
                .of(
                    propertiesAtRootLevelForFootball(),
                    divisionProperties(),
                    jerseyProperties(),
                    playerProperties(),
                    managerProperties(),
                    venueProperties()
                )
                .flatMap(s -> s);
        }

        static Stream<Arguments> propertiesFromFootballCompetitorProfileTeam() {
            return Stream.of(competitorPlayerProperties()).flatMap(s -> s);
        }

        static Stream<Arguments> propertiesFromFootballExportableCompetitor() {
            return Stream.of(exportableCompetitorPlayerProperties()).flatMap(s -> s);
        }

        static Stream<Arguments> propertiesFromFormula1CompetitorProfile() {
            return Stream
                .of(propertiesAtRootLevelForF1(), venueProperties(), raceDriverProperties())
                .flatMap(s -> s);
        }

        private static Stream<Arguments> propertiesAtRootLevelForFootball() {
            return Stream
                .of(
                    nameInFootball(),
                    genderInFootball(),
                    ageGroupInFootball(),
                    stateInFootball(),
                    virtual(),
                    shortNameInFootball(),
                    countryCode(),
                    countryInFootball(),
                    abbreviations()
                )
                .flatMap(s -> s);
        }

        private static Stream<Arguments> propertiesAtRootLevelForF1() {
            return Stream
                .of(
                    nameInF1(),
                    genderInF1(),
                    ageGroupInF1(),
                    stateInF1(),
                    virtual(),
                    shortNameInF1(),
                    countryCode(),
                    countryInF1(),
                    abbreviations()
                )
                .flatMap(s -> s);
        }

        private static Stream<Arguments> jerseyProperties() {
            return Stream.of(jerseyPropertiesPreV3_3_3(), newJerseyPropertiesAddedInV3_3_3()).flatMap(s -> s);
        }

        private static Stream<Arguments> playerProperties() {
            return Stream.of(getPlayerProperties()).flatMap(s -> s);
        }

        private static Stream<Arguments> competitorPlayerProperties() {
            return Stream.of(getCompetitorPlayerProperties()).flatMap(s -> s);
        }

        private static Stream<Arguments> exportableCompetitorPlayerProperties() {
            return Stream.of(getExportableCompetitorPlayerProperties()).flatMap(s -> s);
        }

        private static Stream<Arguments> nameInF1() {
            return Stream.of(
                arguments(
                    "individual name translation",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName1"),
                    "someName1"
                ),
                arguments(
                    "name translation from names",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName2"),
                    "someName2"
                ),
                arguments(
                    "null name results in null individual name translation if driver and team names are also null",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setName(null);
                        p.getRaceDriverProfile().getRaceDriver().setName(null);
                        p.getRaceDriverProfile().getRaceTeam().setName(null);
                    },
                    null
                ),
                arguments(
                    "null name results in null name translation from names if driver name and team names are also null",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setName(null);
                        p.getRaceDriverProfile().getRaceDriver().setName(null);
                        p.getRaceDriverProfile().getRaceTeam().setName(null);
                    },
                    null
                ),
                arguments(
                    "name translation defaults to driver name if competitor and teams are unnamed",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setName(null);
                        p.getRaceDriverProfile().getRaceDriver().setName("some driver name");
                        p.getRaceDriverProfile().getRaceTeam().setName(null);
                    },
                    "some driver name"
                ),
                arguments(
                    "name translation defaults to team name if competitor and driver are unnamed",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setName(null);
                        p.getRaceDriverProfile().getRaceDriver().setName(null);
                        p.getRaceDriverProfile().getRaceTeam().setName("some team name");
                    },
                    "some team name"
                ),
                arguments(
                    "empty name",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> nameInFootball() {
            return Stream.of(
                arguments(
                    "individual name translation",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName1"),
                    "someName1"
                ),
                arguments(
                    "name translation from names",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName2"),
                    "someName2"
                ),
                arguments(
                    "null individual name translation",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(null),
                    null
                ),
                arguments(
                    "null name translation from names",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(null),
                    null
                ),
                arguments(
                    "empty name",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> genderInF1() {
            return Stream.of(
                arguments(
                    "gender",
                    Competitor::getGender,
                    p -> p.getCompetitor().setGender("gender"),
                    "gender"
                ),
                arguments(
                    "null gender results in null if driver and team gender are also null",
                    Competitor::getGender,
                    p -> {
                        p.getCompetitor().setGender(null);
                        p.getRaceDriverProfile().getRaceTeam().setGender(null);
                        p.getRaceDriverProfile().getRaceDriver().setGender(null);
                    },
                    null
                ),
                arguments(
                    "gender defaults to driver gender if competitor and team genders are not provided",
                    Competitor::getGender,
                    p -> {
                        p.getCompetitor().setGender(null);
                        p.getRaceDriverProfile().getRaceTeam().setGender(null);
                        p.getRaceDriverProfile().getRaceDriver().setGender("female");
                    },
                    "female"
                ),
                arguments(
                    "gender defaults to team gender if competitor and driver genders are not provided",
                    Competitor::getGender,
                    p -> {
                        p.getCompetitor().setGender(null);
                        p.getRaceDriverProfile().getRaceTeam().setGender("female");
                        p.getRaceDriverProfile().getRaceDriver().setGender(null);
                    },
                    "female"
                ),
                arguments("empty gender", Competitor::getGender, p -> p.getCompetitor().setGender(""), "")
            );
        }

        private static Stream<Arguments> genderInFootball() {
            return Stream.of(
                arguments(
                    "gender",
                    Competitor::getGender,
                    p -> p.getCompetitor().setGender("gender"),
                    "gender"
                ),
                arguments("null gender", Competitor::getGender, p -> p.getCompetitor().setGender(null), null),
                arguments("empty gender", Competitor::getGender, p -> p.getCompetitor().setGender(""), "")
            );
        }

        private static Stream<Arguments> ageGroupInF1() {
            return Stream.of(
                arguments(
                    "age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup("U20"),
                    "U20"
                ),
                arguments(
                    "null age group results in null if driver and team age groups are also null",
                    Competitor::getAgeGroup,
                    p -> {
                        p.getCompetitor().setAgeGroup(null);
                        p.getRaceDriverProfile().getRaceTeam().setAgeGroup(null);
                        p.getRaceDriverProfile().getRaceDriver().setAgeGroup(null);
                    },
                    null
                ),
                arguments(
                    "age group defaults to driver age group if competitor and team age groups are not provided",
                    Competitor::getAgeGroup,
                    p -> {
                        p.getCompetitor().setAgeGroup(null);
                        p.getRaceDriverProfile().getRaceTeam().setAgeGroup(null);
                        p.getRaceDriverProfile().getRaceDriver().setAgeGroup("U20");
                    },
                    "U20"
                ),
                arguments(
                    "age group defaults to team age group if competitor and driver age groups are not provided",
                    Competitor::getAgeGroup,
                    p -> {
                        p.getCompetitor().setAgeGroup(null);
                        p.getRaceDriverProfile().getRaceTeam().setAgeGroup("U20");
                        p.getRaceDriverProfile().getRaceDriver().setAgeGroup(null);
                    },
                    "U20"
                ),
                arguments(
                    "empty age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> ageGroupInFootball() {
            return Stream.of(
                arguments(
                    "age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup("U20"),
                    "U20"
                ),
                arguments(
                    "null age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup(null),
                    null
                ),
                arguments(
                    "empty age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> stateInF1() {
            return Stream.of(
                arguments(
                    "state",
                    Competitor::getState,
                    p -> p.getCompetitor().setState("Virginia"),
                    "Virginia"
                ),
                arguments(
                    "null state results in null if driver and team states are also null",
                    Competitor::getState,
                    p -> {
                        p.getCompetitor().setState(null);
                        p.getRaceDriverProfile().getRaceTeam().setState(null);
                        p.getRaceDriverProfile().getRaceDriver().setState(null);
                    },
                    null
                ),
                arguments(
                    "state defaults to driver state if competitor and team states are not provided",
                    Competitor::getState,
                    p -> {
                        p.getCompetitor().setState(null);
                        p.getRaceDriverProfile().getRaceTeam().setState(null);
                        p.getRaceDriverProfile().getRaceDriver().setState("Virginia");
                    },
                    "Virginia"
                ),
                arguments(
                    "state defaults to team state if competitor and driver states are not provided",
                    Competitor::getState,
                    p -> {
                        p.getCompetitor().setState(null);
                        p.getRaceDriverProfile().getRaceTeam().setState("Virginia");
                        p.getRaceDriverProfile().getRaceDriver().setState(null);
                    },
                    "Virginia"
                ),
                arguments("empty state", Competitor::getState, p -> p.getCompetitor().setState(""), "")
            );
        }

        private static Stream<Arguments> stateInFootball() {
            return Stream.of(
                arguments(
                    "state",
                    Competitor::getState,
                    p -> p.getCompetitor().setState("Virginia"),
                    "Virginia"
                ),
                arguments("null state", Competitor::getState, p -> p.getCompetitor().setState(null), null),
                arguments("empty state", Competitor::getState, p -> p.getCompetitor().setState(""), "")
            );
        }

        private static Stream<Arguments> virtual() {
            return Stream.of(
                arguments("virtual", Competitor::isVirtual, p -> p.getCompetitor().setVirtual(true), true),
                arguments(
                    "not virtual",
                    Competitor::isVirtual,
                    p -> p.getCompetitor().setVirtual(false),
                    false
                ),
                arguments(
                    "null virtual is treated not virtual",
                    Competitor::isVirtual,
                    p -> p.getCompetitor().setVirtual(null),
                    false
                )
            );
        }

        private static Stream<Arguments> shortNameInF1() {
            return Stream.of(
                arguments(
                    "shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName("some short name"),
                    "some short name"
                ),
                arguments(
                    "null state results in null if driver and team states are also null",
                    Competitor::getShortName,
                    p -> {
                        p.getCompetitor().setShortName(null);
                        p.getRaceDriverProfile().getRaceDriver().setShortName(null);
                        p.getRaceDriverProfile().getRaceTeam().setShortName(null);
                    },
                    null
                ),
                arguments(
                    "short name defaults to driver short name if competitor and team short names are not provided",
                    Competitor::getShortName,
                    p -> {
                        p.getCompetitor().setShortName(null);
                        p.getRaceDriverProfile().getRaceDriver().setShortName("some driver short name");
                        p.getRaceDriverProfile().getRaceTeam().setShortName(null);
                    },
                    "some driver short name"
                ),
                arguments(
                    "short name defaults to team short name if competitor and driver short names are not provided",
                    Competitor::getShortName,
                    p -> {
                        p.getCompetitor().setShortName(null);
                        p.getRaceDriverProfile().getRaceDriver().setShortName(null);
                        p.getRaceDriverProfile().getRaceTeam().setShortName("some team short name");
                    },
                    "some team short name"
                ),
                arguments(
                    "empty shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> shortNameInFootball() {
            return Stream.of(
                arguments(
                    "shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName("some short name"),
                    "some short name"
                ),
                arguments(
                    "null state",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName(null),
                    null
                ),
                arguments(
                    "empty shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> countryCode() {
            return Stream.of(
                arguments(
                    "country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode("LT"),
                    "LT"
                ),
                arguments(
                    "null country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode(null),
                    null
                ),
                arguments(
                    "empty country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> countryInF1() {
            return Stream.of(
                arguments(
                    "individual country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Lithuania"),
                    "Lithuania"
                ),
                arguments(
                    "country translation from countries",
                    c -> c.getCountries().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Estonia"),
                    "Estonia"
                ),
                arguments(
                    "country results in null if driver and team country is also null",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setCountry(null);
                        p.getRaceDriverProfile().setRaceDriver(null);
                        p.getRaceDriverProfile().setRaceTeam(null);
                    },
                    null
                ),
                arguments(
                    "country defaults to driver country if competitor and team country is not provided",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setCountry(null);
                        p.getRaceDriverProfile().getRaceDriver().setCountry("Mexico");
                        p.getRaceDriverProfile().getRaceTeam().setCountry(null);
                    },
                    "Mexico"
                ),
                arguments(
                    "country defaults to team country if competitor and driver country is not provided",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setCountry(null);
                        p.getRaceDriverProfile().getRaceDriver().setCountry(null);
                        p.getRaceDriverProfile().getRaceTeam().setCountry("Mexico");
                    },
                    "Mexico"
                ),
                arguments(
                    "empty country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> countryInFootball() {
            return Stream.of(
                arguments(
                    "individual country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Lithuania"),
                    "Lithuania"
                ),
                arguments(
                    "country translation from countries",
                    c -> c.getCountries().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Estonia"),
                    "Estonia"
                ),
                arguments(
                    "null individual country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(null),
                    null
                ),
                arguments(
                    "null country translation from countries",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(null),
                    null
                ),
                arguments(
                    "empty country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> abbreviations() {
            return Stream.of(
                arguments(
                    "individual abbreviation translation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setAbbreviation("some abbreviation1"),
                    "some abbreviation1"
                ),
                arguments(
                    "abbreviation translation from abbreviations",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setAbbreviation("some abbreviation2"),
                    "some abbreviation2"
                ),
                arguments(
                    "absent individual abbreviation truncates in truncated name",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("GERMANY");
                    },
                    "GER"
                ),
                arguments(
                    "absent abbreviation translation from abbreviations results in truncated name",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("GERMANY");
                    },
                    "GER"
                ),
                arguments(
                    "absent name and individual abbreviation results in empty string",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName(null);
                    },
                    ""
                ),
                arguments(
                    "absent name and abbreviation translation from abbreviations results in empty string",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName(null);
                    },
                    ""
                ),
                arguments(
                    "empty name and absent abbreviation results in empty string",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("");
                    },
                    ""
                ),
                arguments(
                    "1 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("A");
                    },
                    "A"
                ),
                arguments(
                    "2 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("AB");
                    },
                    "AB"
                ),
                arguments(
                    "3 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("ABC");
                    },
                    "ABC"
                ),
                arguments(
                    "short name and absent abbreviation results in uppercase name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("abc");
                    },
                    "ABC"
                ),
                arguments(
                    "present name and absent abbreviation results in uppercase truncated name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("Germany");
                    },
                    "GER"
                )
            );
        }

        private static Stream<Arguments> divisionProperties() {
            return Stream.of(
                arguments(
                    "division name",
                    c -> c.getDivision().getDivisionName(),
                    p -> p.getCompetitor().setDivisionName("Division 1"),
                    "Division 1"
                ),
                arguments(
                    "division number",
                    c -> c.getDivision().getDivision(),
                    p -> p.getCompetitor().setDivision(2),
                    2
                )
            );
        }

        private static Stream<Arguments> jerseyPropertiesPreV3_3_3() {
            return Stream.of(
                arguments(
                    "jersey type",
                    c -> c.getJerseys().get(0).getType(),
                    p -> p.getJerseys().getJersey().get(0).setType("some type"),
                    "some type"
                ),
                arguments(
                    "jersey base",
                    c -> c.getJerseys().get(0).getBase(),
                    p -> p.getJerseys().getJersey().get(0).setBase("some base"),
                    "some base"
                ),
                arguments(
                    "jersey sleeve",
                    c -> c.getJerseys().get(0).getSleeve(),
                    p -> p.getJerseys().getJersey().get(0).setSleeve("some sleeve"),
                    "some sleeve"
                ),
                arguments(
                    "jersey number",
                    c -> c.getJerseys().get(0).getNumber(),
                    p -> p.getJerseys().getJersey().get(0).setNumber("45"),
                    "45"
                ),
                arguments(
                    "jersey stripes",
                    c -> c.getJerseys().get(0).getStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setStripesColor("#135246"),
                    "#135246"
                ),
                arguments(
                    "jersey split color",
                    c -> c.getJerseys().get(0).getSplitColor(),
                    p -> p.getJerseys().getJersey().get(0).setSplitColor("#246135"),
                    "#246135"
                ),
                arguments(
                    "jersey shirt type",
                    c -> c.getJerseys().get(0).getShirtType(),
                    p -> p.getJerseys().getJersey().get(0).setShirtType("#357468"),
                    "#357468"
                ),
                arguments(
                    "jersey sleeve detail",
                    c -> c.getJerseys().get(0).getSleeveDetail(),
                    p -> p.getJerseys().getJersey().get(0).setSleeveDetail("#468357"),
                    "#468357"
                )
            );
        }

        private static Stream<Arguments> newJerseyPropertiesAddedInV3_3_3() {
            return Stream.of(
                arguments(
                    "jersey has stripes",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(true),
                    true
                ),
                arguments(
                    "jersey does not have stripes",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(false),
                    false
                ),
                arguments(
                    "jersey stripes is not configured",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(null),
                    null
                ),
                arguments(
                    "jersey has horizontal stripes",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(true),
                    true
                ),
                arguments(
                    "jersey does not have horizontal stripes",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(false),
                    false
                ),
                arguments(
                    "jersey horizontal stripes is not configured",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(null),
                    null
                ),
                arguments(
                    "jersey horizontal stripes color is not configured",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor(null),
                    null
                ),
                arguments(
                    "jersey horizontal stripes color is an empty string",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor(""),
                    ""
                ),
                arguments(
                    "jersey horizontal stripes color has value",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor("aaafff"),
                    "aaafff"
                ),
                arguments(
                    "jersey squares are not configured",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(null),
                    null
                ),
                arguments(
                    "jersey has squares",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(true),
                    true
                ),
                arguments(
                    "jersey does not have squares",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(false),
                    false
                ),
                arguments(
                    "jersey squares color is not configured",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor(null),
                    null
                ),
                arguments(
                    "jersey squares color is empty",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor(""),
                    ""
                ),
                arguments(
                    "jersey squares color is configured",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor("ff22ff"),
                    "ff22ff"
                ),
                arguments(
                    "jersey split is not configured",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(null),
                    null
                ),
                arguments(
                    "jersey has split",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(true),
                    true
                ),
                arguments(
                    "jersey does not have split",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(false),
                    false
                )
            );
        }

        private static Stream<Arguments> getCompetitorPlayerProperties() {
            return Stream.of(
                arguments(
                    "Competitor players list is unavailable if competitor profile players and " +
                    "competitor profile competitor (Team) players are null.",
                    c -> c.getPlayers(),
                    p -> {
                        p.setPlayers(null);
                        p.getCompetitor().setPlayers(null);
                    },
                    null
                )
            );
        }

        private static Stream<Arguments> getExportableCompetitorPlayerProperties() {
            return Stream.of(
                arguments(
                    "Exported competitor players list is empty if competitor profile players " +
                    "and competitor profile competitor (Team) players are null.",
                    c -> c.getPlayers().isEmpty(),
                    p -> {
                        p.setPlayers(null);
                        p.getCompetitor().setPlayers(null);
                    },
                    true
                )
            );
        }

        private static Stream<Arguments> getPlayerProperties() {
            return Stream.of(
                arguments(
                    "Competitor gets players from competitor profile competitor (Team) if competitor " +
                    "profile players are not available.",
                    c -> c.getPlayers().isEmpty(),
                    p -> p.setPlayers(null),
                    false
                ),
                arguments(
                    "Competitor player jersey numbers are unavailable when competitor profile players" +
                    " are not populated and competitor profile competitor players are configured.",
                    c ->
                        c
                            .getPlayers()
                            .stream()
                            .filter(p -> ((CompetitorPlayer) p).getJerseyNumber() != null)
                            .count(),
                    p -> p.setPlayers(null),
                    0L
                ),
                arguments(
                    "Player jersey number is not available",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getJerseyNumber(),
                    p -> p.getPlayers().getPlayer().get(0).setJerseyNumber(null),
                    null
                ),
                arguments(
                    "Player jersey number is empty (0)",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getJerseyNumber(),
                    p -> p.getPlayers().getPlayer().get(0).setJerseyNumber(0),
                    0
                ),
                arguments(
                    "Player jersey number is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getJerseyNumber(),
                    p -> p.getPlayers().getPlayer().get(0).setJerseyNumber(5),
                    5
                ),
                arguments(
                    "Player type is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getType(),
                    p -> p.getPlayers().getPlayer().get(0).setType(null),
                    null
                ),
                arguments(
                    "Player type is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getType(),
                    p -> p.getPlayers().getPlayer().get(0).setType(""),
                    ""
                ),
                arguments(
                    "Player type is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getType(),
                    p -> p.getPlayers().getPlayer().get(0).setType("forward"),
                    "forward"
                ),
                arguments(
                    "Player date of birth is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getDateOfBirth(),
                    p -> p.getPlayers().getPlayer().get(0).setDateOfBirth(null),
                    null
                ),
                arguments(
                    "Player date of birth is null when the input data is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getDateOfBirth(),
                    p -> p.getPlayers().getPlayer().get(0).setDateOfBirth(""),
                    null
                ),
                arguments(
                    "Player date of birth is null when the input data is not a correct Date",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getDateOfBirth(),
                    p -> p.getPlayers().getPlayer().get(0).setDateOfBirth("Incorrect_Data_String"),
                    null
                ),
                arguments(
                    "Player date of birth is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getDateOfBirth(),
                    p -> p.getPlayers().getPlayer().get(0).setDateOfBirth("2004-01-01"),
                    Date.from(LocalDate.of(2004, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC))
                ),
                arguments(
                    "Player nationality is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNationality(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setNationality(null),
                    null
                ),
                arguments(
                    "Player nationality is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNationality(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setNationality(""),
                    ""
                ),
                arguments(
                    "Player nationality is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNationality(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setNationality("Brazilian"),
                    "Brazilian"
                ),
                arguments(
                    "Player country code is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getCountryCode(),
                    p -> p.getPlayers().getPlayer().get(0).setCountryCode(null),
                    null
                ),
                arguments(
                    "Player country code is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getCountryCode(),
                    p -> p.getPlayers().getPlayer().get(0).setCountryCode(""),
                    ""
                ),
                arguments(
                    "Player country code is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getCountryCode(),
                    p -> p.getPlayers().getPlayer().get(0).setCountryCode("BR"),
                    "BR"
                ),
                arguments(
                    "Player height is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getHeight(),
                    p -> p.getPlayers().getPlayer().get(0).setHeight(null),
                    null
                ),
                arguments(
                    "Player height is empty (0)",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getHeight(),
                    p -> p.getPlayers().getPlayer().get(0).setHeight(0),
                    0
                ),
                arguments(
                    "Player height is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getHeight(),
                    p -> p.getPlayers().getPlayer().get(0).setHeight(180),
                    180
                ),
                arguments(
                    "Player weight is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getWeight(),
                    p -> p.getPlayers().getPlayer().get(0).setWeight(null),
                    null
                ),
                arguments(
                    "Player weight is empty (0)",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getWeight(),
                    p -> p.getPlayers().getPlayer().get(0).setWeight(0),
                    0
                ),
                arguments(
                    "Player weight is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getWeight(),
                    p -> p.getPlayers().getPlayer().get(0).setWeight(75),
                    75
                ),
                arguments(
                    "Player full name is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getFullName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setFullName(null),
                    null
                ),
                arguments(
                    "Player full name is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getFullName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setFullName(""),
                    ""
                ),
                arguments(
                    "Player full name is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getFullName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setFullName("John Doe"),
                    "John Doe"
                ),
                arguments(
                    "Player nickname is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNickname(),
                    p -> p.getPlayers().getPlayer().get(0).setNickname(null),
                    null
                ),
                arguments(
                    "Player nickname is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNickname(),
                    p -> p.getPlayers().getPlayer().get(0).setNickname(""),
                    ""
                ),
                arguments(
                    "Player nickname is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getNickname(),
                    p -> p.getPlayers().getPlayer().get(0).setNickname("Johnny"),
                    "Johnny"
                ),
                arguments(
                    "Player gender is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getGender(),
                    p -> p.getPlayers().getPlayer().get(0).setGender(null),
                    null
                ),
                arguments(
                    "Player gender is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getGender(),
                    p -> p.getPlayers().getPlayer().get(0).setGender(""),
                    ""
                ),
                arguments(
                    "Player gender is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getGender(),
                    p -> p.getPlayers().getPlayer().get(0).setGender("Male"),
                    "Male"
                ),
                arguments(
                    "Player ID is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getId(),
                    p -> p.getPlayers().getPlayer().get(0).setId("sr:player:12345"),
                    "sr:player:12345"
                ),
                arguments(
                    "Player name is null",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setName(null),
                    null
                ),
                arguments(
                    "Player name is empty",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setName(""),
                    ""
                ),
                arguments(
                    "Player name is configured",
                    c -> ((CompetitorPlayer) c.getPlayers().get(0)).getName(ENGLISH),
                    p -> p.getPlayers().getPlayer().get(0).setName("John"),
                    "John"
                )
            );
        }

        private static Stream<Arguments> absenceOfNewJerseyPropertiesInVersionsPreV3_3_3() {
            return Stream.of(
                arguments(
                    "jersey has stripes",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(true),
                    null
                ),
                arguments(
                    "jersey does not have stripes",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(false),
                    null
                ),
                arguments(
                    "jersey stripes is not configured",
                    c -> c.getJerseys().get(0).getStripes(),
                    p -> p.getJerseys().getJersey().get(0).setStripes(null),
                    null
                ),
                arguments(
                    "jersey has horizontal stripes",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(true),
                    null
                ),
                arguments(
                    "jersey does not have horizontal stripes",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(false),
                    null
                ),
                arguments(
                    "jersey horizontal stripes is not configured",
                    c -> c.getJerseys().get(0).getHorizontalStripes(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripes(null),
                    null
                ),
                arguments(
                    "jersey horizontal stripes color is not configured",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor(null),
                    null
                ),
                arguments(
                    "jersey horizontal stripes color is an empty string",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor(""),
                    null
                ),
                arguments(
                    "jersey horizontal stripes color has value",
                    c -> c.getJerseys().get(0).getHorizontalStripesColor(),
                    p -> p.getJerseys().getJersey().get(0).setHorizontalStripesColor("aaafff"),
                    null
                ),
                arguments(
                    "jersey squares are not configured",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(null),
                    null
                ),
                arguments(
                    "jersey has squares",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(true),
                    null
                ),
                arguments(
                    "jersey does not have squares",
                    c -> c.getJerseys().get(0).getSquares(),
                    p -> p.getJerseys().getJersey().get(0).setSquares(false),
                    null
                ),
                arguments(
                    "jersey squares color is not configured",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor(null),
                    null
                ),
                arguments(
                    "jersey squares color is empty",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor(""),
                    null
                ),
                arguments(
                    "jersey squares color is configured",
                    c -> c.getJerseys().get(0).getSquaresColor(),
                    p -> p.getJerseys().getJersey().get(0).setSquaresColor("ff22ff"),
                    null
                ),
                arguments(
                    "jersey split is not configured",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(null),
                    null
                ),
                arguments(
                    "jersey has split",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(true),
                    null
                ),
                arguments(
                    "jersey does not have split",
                    c -> c.getJerseys().get(0).getSplit(),
                    p -> p.getJerseys().getJersey().get(0).setSplit(false),
                    null
                )
            );
        }

        private static Stream<Arguments> managerProperties() {
            return Stream.of(
                arguments(
                    "manager id",
                    c -> c.getManager().getId(),
                    p -> p.getManager().setId("sr:player:345123"),
                    Urn.parse("sr:player:345123")
                ),
                arguments(
                    "manager name translation from names",
                    c -> c.getManager().getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getManager().setName("managers name1"),
                    "managers name1"
                ),
                arguments(
                    "manager individual name translation",
                    c -> c.getManager().getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getManager().setName("managers name2"),
                    "managers name2"
                ),
                arguments(
                    "manager nationality",
                    c -> c.getManager().getNationality(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getManager().setNationality("Lithuanian"),
                    "Lithuanian"
                ),
                arguments(
                    "manager country code",
                    c -> c.getManager().getCountryCode(),
                    p -> p.getManager().setCountryCode("IT"),
                    "IT"
                )
            );
        }

        @SuppressWarnings("MagicNumber")
        private static Stream<Arguments> venueProperties() {
            return Stream.concat(
                Stream.of(
                    arguments(
                        "venue id",
                        c -> c.getVenue().getId(),
                        p -> p.getVenue().setId("sr:venue:259488"),
                        "sr:venue:259488"
                    ),
                    arguments(
                        "venue name translation from names",
                        c -> c.getVenue().getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setName("Sport Arena1"),
                        "Sport Arena1"
                    ),
                    arguments(
                        "venue individual name translation",
                        c -> c.getVenue().getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setName("Sport Arena2"),
                        "Sport Arena2"
                    ),
                    arguments(
                        "venue city translation from cities",
                        c -> c.getVenue().getCities().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setCityName("Riga"),
                        "Riga"
                    ),
                    arguments(
                        "venue individual city translation",
                        c -> c.getVenue().getCity(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setCityName("Warsaw"),
                        "Warsaw"
                    ),
                    arguments(
                        "venue country translation from countries",
                        c -> c.getVenue().getCountries().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setCountryName("Latvia"),
                        "Latvia"
                    ),
                    arguments(
                        "venue individual country translation",
                        c -> c.getVenue().getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                        p -> p.getVenue().setCountryName("Greece"),
                        "Greece"
                    ),
                    arguments(
                        "venue capacity",
                        c -> c.getVenue().getCapacity(),
                        p -> p.getVenue().setCapacity(10002),
                        10002
                    ),
                    arguments(
                        "venue coordinates",
                        c -> c.getVenue().getCoordinates(),
                        p -> p.getVenue().setMapCoordinates("56.9496, 24.1052"),
                        "56.9496, 24.1052"
                    ),
                    arguments(
                        "venue country code",
                        c -> c.getVenue().getCountryCode(),
                        p -> p.getVenue().setCountryCode("GB"),
                        "GB"
                    ),
                    arguments(
                        "venue state",
                        c -> c.getVenue().getState(),
                        p -> p.getVenue().setState("California"),
                        "California"
                    )
                ),
                courseProperties()
            );
        }

        @SuppressWarnings("MagicNumber")
        private static Stream<Arguments> courseProperties() {
            return Stream.of(
                arguments(
                    "venue course name translation from names ",
                    c ->
                        c
                            .getVenue()
                            .getCourses()
                            .get(0)
                            .getNames()
                            .get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getVenue().getCourse().get(0).setName("course 1 name"),
                    "course 1 name"
                ),
                arguments(
                    "venue course individual name translation",
                    c -> c.getVenue().getCourses().get(0).getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getVenue().getCourse().get(0).setName("course 2 name"),
                    "course 2 name"
                ),
                arguments(
                    "venue course id",
                    c -> c.getVenue().getCourses().get(0).getId(),
                    p -> p.getVenue().getCourse().get(0).setId("sr:venue:911829"),
                    "sr:venue:911829"
                ),
                arguments(
                    "venue course hole number",
                    c -> c.getVenue().getCourses().get(0).getHoles().get(0).getNumber(),
                    p -> p.getVenue().getCourse().get(0).getHole().get(0).setNumber(7),
                    7
                ),
                arguments(
                    "venue course hole par",
                    c -> c.getVenue().getCourses().get(0).getHoles().get(0).getPar(),
                    p -> p.getVenue().getCourse().get(0).getHole().get(0).setPar(5),
                    5
                )
            );
        }

        private static Stream<Arguments> raceDriverProperties() {
            return Stream.concat(
                Stream.of(
                    arguments(
                        "race driver id",
                        c -> c.getRaceDriver().getRaceDriverId(),
                        p -> p.getRaceDriverProfile().getRaceDriver().setId("sr:player:998111"),
                        "sr:player:998111"
                    ),
                    arguments(
                        "race driver team id",
                        c -> c.getRaceDriver().getRaceTeamId(),
                        p -> p.getRaceDriverProfile().getRaceTeam().setId("sr:team:998112"),
                        "sr:team:998112"
                    )
                ),
                carProperties()
            );
        }

        private static Stream<Arguments> carProperties() {
            return Stream.of(
                arguments(
                    "race driver car name",
                    c -> c.getRaceDriver().getCar().getName(),
                    p -> p.getRaceDriverProfile().getCar().setName("Ferrari"),
                    "Ferrari"
                ),
                arguments(
                    "race driver car chassis",
                    c -> c.getRaceDriver().getCar().getChassis(),
                    p -> p.getRaceDriverProfile().getCar().setChassis("FF-2021"),
                    "FF-2021"
                ),
                arguments(
                    "race driver car engine name",
                    c -> c.getRaceDriver().getCar().getEngineName(),
                    p -> p.getRaceDriverProfile().getCar().setEngineName("V12"),
                    "V12"
                )
            );
        }

        public interface PropertySetterToSapiCompetitorProfileEndpoint {
            void setOn(SapiCompetitorProfileEndpoint sapiCompetitor);
        }

        public static Arguments arguments(
            String propertyName,
            PropertyGetterFromCompetitor propertyGetter,
            PropertySetterToSapiCompetitorProfileEndpoint propertySetter,
            Object expected
        ) {
            return Arguments.of(Named.of(propertyName, propertyGetter), propertySetter, expected);
        }
    }

    @SuppressWarnings("MultipleStringLiterals")
    public static class SimpleTeamProfileEndpointBackedParameterSources {

        static Stream<Arguments> propertiesFromCollegeBasketballCompetitorProfile() {
            return Stream.of(propertiesAtRootLevel(), divisionProperties()).flatMap(s -> s);
        }

        private static Stream<Arguments> propertiesAtRootLevel() {
            return Stream
                .of(
                    name(),
                    gender(),
                    ageGroup(),
                    state(),
                    virtual(),
                    shortName(),
                    countryCode(),
                    country(),
                    abbreviations()
                )
                .flatMap(s -> s);
        }

        private static Stream<Arguments> name() {
            return Stream.of(
                arguments(
                    "individual name translation",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName1"),
                    "someName1"
                ),
                arguments(
                    "name translation from names",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName("someName2"),
                    "someName2"
                ),
                arguments(
                    "null name results in null individual name translation",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(null),
                    null
                ),
                arguments(
                    "null name results in null name translation from names",
                    c -> c.getNames().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(null),
                    null
                ),
                arguments(
                    "empty name",
                    c -> c.getName(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> gender() {
            return Stream.of(
                arguments(
                    "gender",
                    Competitor::getGender,
                    p -> p.getCompetitor().setGender("gender"),
                    "gender"
                ),
                arguments("null gender", Competitor::getGender, p -> p.getCompetitor().setGender(null), null),
                arguments("empty gender", Competitor::getGender, p -> p.getCompetitor().setGender(""), "")
            );
        }

        private static Stream<Arguments> ageGroup() {
            return Stream.of(
                arguments(
                    "age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup("U20"),
                    "U20"
                ),
                arguments(
                    "null age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup(null),
                    null
                ),
                arguments(
                    "empty age group",
                    Competitor::getAgeGroup,
                    p -> p.getCompetitor().setAgeGroup(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> state() {
            return Stream.of(
                arguments(
                    "state",
                    Competitor::getState,
                    p -> p.getCompetitor().setState("Virginia"),
                    "Virginia"
                ),
                arguments("null state", Competitor::getState, p -> p.getCompetitor().setState(null), null),
                arguments("empty state", Competitor::getState, p -> p.getCompetitor().setState(""), "")
            );
        }

        private static Stream<Arguments> virtual() {
            return Stream.of(
                arguments("virtual", Competitor::isVirtual, p -> p.getCompetitor().setVirtual(true), true),
                arguments(
                    "not virtual",
                    Competitor::isVirtual,
                    p -> p.getCompetitor().setVirtual(false),
                    false
                ),
                arguments(
                    "null virtual is treated not virtual",
                    Competitor::isVirtual,
                    p -> p.getCompetitor().setVirtual(null),
                    false
                )
            );
        }

        private static Stream<Arguments> shortName() {
            return Stream.of(
                arguments(
                    "shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName("some short name"),
                    "some short name"
                ),
                arguments(
                    "null shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName(null),
                    null
                ),
                arguments(
                    "empty shortName",
                    Competitor::getShortName,
                    p -> p.getCompetitor().setShortName(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> countryCode() {
            return Stream.of(
                arguments(
                    "country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode("LT"),
                    "LT"
                ),
                arguments(
                    "null country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode(null),
                    null
                ),
                arguments(
                    "empty country code",
                    Competitor::getCountryCode,
                    p -> p.getCompetitor().setCountryCode(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> country() {
            return Stream.of(
                arguments(
                    "individual country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Lithuania"),
                    "Lithuania"
                ),
                arguments(
                    "country translation from countries",
                    c -> c.getCountries().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry("Estonia"),
                    "Estonia"
                ),
                arguments(
                    "absent country results in null individual country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(null),
                    null
                ),
                arguments(
                    "absent country results in null country translation from countries",
                    c -> c.getCountries().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(null),
                    null
                ),
                arguments(
                    "empty country translation",
                    c -> c.getCountry(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setCountry(""),
                    ""
                )
            );
        }

        private static Stream<Arguments> abbreviations() {
            return Stream.of(
                arguments(
                    "individual abbreviation translation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setAbbreviation("some abbreviation1"),
                    "some abbreviation1"
                ),
                arguments(
                    "abbreviation translation from abbreviations",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> p.getCompetitor().setAbbreviation("some abbreviation2"),
                    "some abbreviation2"
                ),
                arguments(
                    "absent individual abbreviation truncates in truncated name",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("GERMANY");
                    },
                    "GER"
                ),
                arguments(
                    "absent abbreviation translation from abbreviations results in truncated name",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("GERMANY");
                    },
                    "GER"
                ),
                arguments(
                    "absent name and individual abbreviation results in empty string",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName(null);
                    },
                    ""
                ),
                arguments(
                    "absent name and abbreviation translation from abbreviations results in empty string",
                    c -> c.getAbbreviations().get(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName(null);
                    },
                    ""
                ),
                arguments(
                    "empty name and absent abbreviation results in empty string",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("");
                    },
                    ""
                ),
                arguments(
                    "1 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("A");
                    },
                    "A"
                ),
                arguments(
                    "2 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("AB");
                    },
                    "AB"
                ),
                arguments(
                    "3 letter name and absent abbreviation results in name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("ABC");
                    },
                    "ABC"
                ),
                arguments(
                    "short name and absent abbreviation results in uppercase name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("abc");
                    },
                    "ABC"
                ),
                arguments(
                    "present name and absent abbreviation results in uppercase truncated name as abbreviation",
                    c -> c.getAbbreviation(FullyPopulatedFootballCompetitor.LANGUAGE),
                    p -> {
                        p.getCompetitor().setAbbreviation(null);
                        p.getCompetitor().setName("Germany");
                    },
                    "GER"
                )
            );
        }

        private static Stream<Arguments> divisionProperties() {
            return Stream.of(
                arguments(
                    "division name",
                    c -> c.getDivision().getDivisionName(),
                    p -> p.getCompetitor().setDivisionName("Division 1"),
                    "Division 1"
                ),
                arguments(
                    "division number",
                    c -> c.getDivision().getDivision(),
                    p -> p.getCompetitor().setDivision(2),
                    2
                )
            );
        }

        public interface PropertySetterToSapiSimpleTeamProfileEndpoint {
            void setOn(SapiSimpleTeamProfileEndpoint sapiTeam);
        }

        public static Arguments arguments(
            String propertyName,
            PropertyGetterFromCompetitor propertyGetter,
            PropertySetterToSapiSimpleTeamProfileEndpoint propertySetter,
            Object expected
        ) {
            return Arguments.of(Named.of(propertyName, propertyGetter), propertySetter, expected);
        }
    }

    public interface PropertyGetterFromCompetitor {
        Object getFrom(Competitor competitor);
    }
}
