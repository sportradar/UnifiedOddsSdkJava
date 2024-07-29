/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.exportAndImportTheOnlyItemIn;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.failingFirstAndThenProviding;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.failingToProvide;
import static com.sportradar.unifiedodds.sdk.impl.CompetitorDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.Urns.CompetitorProfiles.urnForAnyCompetitor;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.*;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa;
import com.sportradar.unifiedodds.sdk.conn.SapiTeams.VirtualCompetitor;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.utils.Urn;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CompetitorProfileCacheImplTest {

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

        @Test
        public void retrievesNonVirtualCompetitorForMatchPopulatedFromSummary() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            Urn germanyUrn = Urn.parse(Germany2024Uefa.COMPETITOR_ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager =
                CompetitorProfileCacheImplTest.this.dataRouterManagerBuilder.withSummaries(
                        providing(in(ENGLISH), with(germanyUrn.toString()), summary)
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
            dataRouterManager.requestSummaryEndpoint(ENGLISH, germanyUrn, anyMatch);

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
        public void retrievesVirtualCompetitorForMatchPopulatedFromSummary() throws Exception {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyVsVirtual2024();
            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            DataRouterManager dataRouterManager = dataRouterManagerBuilder
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
            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

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
        public void retrievesVirtualCompetitor() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);

            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

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
            Urn germanyUrn = Urn.parse(Germany2024Uefa.COMPETITOR_ID);
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
            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);
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

        @Test
        public void competitorProfileApiEndpointDoesNotContainVirtualFlagCurrently() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();

            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

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

            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

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

            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

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
        public void exportsImportsCompetitorWithoutVirtualInformationYet() throws Exception {
            SapiCompetitorProfileEndpoint virtual = SapiTeams.VirtualCompetitor.profile();
            virtual.getCompetitor().setVirtual(true);

            Urn virtualUrn = Urn.parse(VirtualCompetitor.ID);

            DataRouterImpl dataRouter = new DataRouterImpl();
            val competitorProvider = failingFirstAndThenProviding(
                in(ENGLISH),
                with(virtualUrn.toString()),
                virtual
            );
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
}
