/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusCaches.BuilderStubbingOutSportEventCache.stubbingOutSportEventCache;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEventStatusFactories.BuilderStubbingOutStatusValueCache.stubbingOutStatusValueCacheWith;
import static com.sportradar.unifiedodds.sdk.caching.impl.StatusCachePopulator.populate;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfSportEventStatuses.*;
import static com.sportradar.unifiedodds.sdk.entities.EventStatus.NotStarted;
import static com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static com.sportradar.utils.Urn.parse;
import static com.sportradar.utils.Urns.SportEvents.getForAnyMatch;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.TEN;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.datamodel.UfSportEventStatus;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.caching.SportEventCaches;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerBuilder;
import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.math.BigDecimal;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SportEventStatusCacheTest {

    @Nested
    public class NotPopulated {

        private Urn matchUrn = getForAnyMatch();
        private final boolean noRefreshViaApi = false;
        private final boolean apiCallOnNotFound = true;

        @Test
        public void failsToBuildStatusForNullMatchUrn() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            assertThatThrownBy(() ->
                    statusFactory.buildSportEventStatus(null, MatchStatus.class, noRefreshViaApi)
                )
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void buildsNullStatusFromEmptyCacheWhenInstructedToDoNoApiCalls() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noRefreshViaApi
            );

            assertThat(matchStatus).isNull();
        }

        @Test
        public void notFindingSportEventResultsInStatusNotStarted() {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(SportEventCaches.notFindingSportEvent())
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                apiCallOnNotFound
            );

            assertThat(matchStatus.getStatus()).isEqualTo(NotStarted);
        }

        @Test
        public void findingNonCompetitionSportEventResultsInStatusNotStarted() {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            String matchId = summary.getSportEvent().getId();
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(SportEventCaches.everyItemIsTournament())
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                apiCallOnNotFound
            );

            assertThat(matchStatus.getStatus()).isEqualTo(NotStarted);
        }
    }

    @Nested
    public class PopulatedFromMessage {

        private Urn matchUrn = getForAnyMatch();
        private final boolean apiCallOnNotFound = true;

        @Test
        public void doesNoApiCallAfterCachingStatusFromMessage() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            SportEventStatusDto statusDto = new SportEventStatusDto(
                withScore(soccerMatchFeedStatus(), home(TEN), away(ONE))
            );
            populate(statusCache).fromMessage(with(matchUrn), statusDto);

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                apiCallOnNotFound
            );

            assertThat(matchStatus.getHomeScore()).isEqualByComparingTo(TEN);
            assertThat(matchStatus.getAwayScore()).isEqualByComparingTo(ONE);
        }

        @Test
        public void requestingUnknownTypeWillMakeMatchStatusPropertiesInaccessible() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            SportEventStatusDto statusDto = new SportEventStatusDto(
                withScore(soccerMatchFeedStatus(), home(TEN), away(ONE))
            );
            populate(statusCache).fromMessage(with(matchUrn), statusDto);

            CompetitionStatus status = statusFactory.buildSportEventStatus(
                matchUrn,
                UnknownType.class,
                apiCallOnNotFound
            );

            assertThat(status).isNotNull();
            assertThat(status).isNotInstanceOf(MatchStatus.class);
        }
    }

    @Nested
    public class PopulatedFromSummary {

        private Urn matchUrn = getForAnyMatch();
        private final boolean noApiCalls = false;
        private final boolean allowApiCalls = true;

        private final DataRouterManagerBuilder dataRouterManager = new DataRouterManagerBuilder();

        @Test
        public void cacheScoreFetchedBySummaryWhichIsNotIssuedByStatusCacheItself() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();
            SportEventStatusDto statusDto = new SportEventStatusDto(
                withScore(soccerMatchFeedStatus(), home(TEN), away(ONE))
            );
            populate(statusCache).fromSummary(with(matchUrn), statusDto);

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noApiCalls
            );

            assertThat(matchStatus.getHomeScore()).isEqualByComparingTo(TEN);
            assertThat(matchStatus.getAwayScore()).isEqualByComparingTo(ONE);
        }

        @Test
        public void notFetchesScoreWhenInstructedNotToDoApiCalls() {
            val statusCache = stubbingOutSportEventCache().build();
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                matchUrn,
                MatchStatus.class,
                noApiCalls
            );

            assertThat(matchStatus).isNull();
        }

        @Test
        public void fetchesScoreFromSummary() {
            SapiMatchSummaryEndpoint summary = soccerMatchGermanyScotlandEuro2024();
            String matchId = summary.getSportEvent().getId();
            summary.getSportEventStatus().setHomeScore("1");
            summary.getSportEventStatus().setAwayScore("10");
            DataRouterImpl dataRouter = new DataRouterImpl();
            val statusCache = stubbingOutSportEventCache()
                .with(
                    SportEventCaches.everyCompetitionRequestsSummaryToFetchStatus(
                        via(
                            dataRouterManager
                                .withSummaries(providing(in(ENGLISH), with(matchId), summary))
                                .with(dataRouter)
                                .build()
                        ),
                        in(ENGLISH)
                    )
                )
                .build();
            dataRouter.setDataListeners(singletonList(statusCache));
            val statusFactory = stubbingOutStatusValueCacheWith(statusCache).build();

            MatchStatus matchStatus = statusFactory.buildSportEventStatus(
                parse(matchId),
                MatchStatus.class,
                allowApiCalls
            );

            assertThat(matchStatus.getHomeScore()).isEqualByComparingTo(ONE);
            assertThat(matchStatus.getAwayScore()).isEqualByComparingTo(TEN);
        }
    }

    private UfSportEventStatus withScore(
        UfSportEventStatus ufSportEventStatus,
        BigDecimal home,
        BigDecimal away
    ) {
        ufSportEventStatus.setHomeScore(home);
        ufSportEventStatus.setAwayScore(away);
        return ufSportEventStatus;
    }

    public BigDecimal home(BigDecimal score) {
        return score;
    }

    public BigDecimal away(BigDecimal score) {
        return score;
    }

    public static String withUrn(String id) {
        return id;
    }

    public static interface UnknownType extends CompetitionStatus {}
}
