/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching;

import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.GrandPrix2024.grandPrix2024RaceStageEndpoint;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.nascarCup2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptionsProviders.nonTimeCriticalRequestOptions;
import static com.sportradar.unifiedodds.sdk.internal.caching.RequestOptionsProviders.timeCriticalRequestOptions;
import static com.sportradar.unifiedodds.sdk.internal.caching.StageSummaryArguments.RaceAndTournamentStageAdapter.raceStage;
import static com.sportradar.unifiedodds.sdk.internal.caching.StageSummaryArguments.RaceAndTournamentStageAdapter.tournamentStage;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;

import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.impl.SummaryDataProviders;
import com.sportradar.utils.Urn;
import java.util.stream.Stream;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

class StageSummaryArguments {

    @SuppressWarnings("unused")
    static Stream<Arguments> raceAndTournamentStagesNonTimeCritical() {
        return raceAndTournamentStagesFor(nonTimeCriticalRequestOptions());
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> raceAndTournamentStagesTimeCritical() {
        return raceAndTournamentStagesFor(timeCriticalRequestOptions());
    }

    private static Stream<Arguments> raceAndTournamentStagesFor(RequestOptions requestOptions) {
        return Stream.of(
            Arguments.of(
                Named.named("RaceStage", raceStage(grandPrix2024RaceStageEndpoint())),
                ENGLISH,
                SummaryDataProviders.providing(
                    in(ENGLISH),
                    with(grandPrix2024RaceStageEndpoint().getSportEvent().getId()),
                    with(requestOptions),
                    grandPrix2024RaceStageEndpoint()
                )
            ),
            Arguments.of(
                Named.named("TournamentStage", tournamentStage(nascarCup2024TournamentInfo())),
                ENGLISH,
                SummaryDataProviders.providing(
                    in(ENGLISH),
                    with(nascarCup2024TournamentInfo().getTournament().getId()),
                    with(requestOptions),
                    nascarCup2024TournamentInfo()
                )
            )
        );
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> raceAndTournamentStagesNonDefaultLanguageNonTimeCritical() {
        return Stream.of(
            Arguments.of(
                Named.named("RaceStage", raceStage(grandPrix2024RaceStageEndpoint())),
                Named.of("default language", ENGLISH),
                Named.of("other language", GERMAN),
                SummaryDataProviders
                    .summaryDataProvider()
                    .providing(
                        in(ENGLISH),
                        with(grandPrix2024RaceStageEndpoint().getSportEvent().getId()),
                        with(nonTimeCriticalRequestOptions()),
                        grandPrix2024RaceStageEndpoint()
                    )
                    .providing(
                        in(GERMAN),
                        with(grandPrix2024RaceStageEndpoint().getSportEvent().getId()),
                        with(nonTimeCriticalRequestOptions()),
                        grandPrix2024RaceStageEndpoint()
                    )
                    .build()
            ),
            Arguments.of(
                Named.named("TournamentStage", tournamentStage(nascarCup2024TournamentInfo())),
                Named.of("default language", ENGLISH),
                Named.of("other language", GERMAN),
                SummaryDataProviders
                    .summaryDataProvider()
                    .providing(
                        in(ENGLISH),
                        with(nascarCup2024TournamentInfo().getTournament().getId()),
                        with(nonTimeCriticalRequestOptions()),
                        nascarCup2024TournamentInfo()
                    )
                    .providing(
                        in(GERMAN),
                        with(nascarCup2024TournamentInfo().getTournament().getId()),
                        with(nonTimeCriticalRequestOptions()),
                        nascarCup2024TournamentInfo()
                    )
                    .build()
            )
        );
    }

    static class RaceAndTournamentStageAdapter {

        private final SapiStageSummaryEndpoint raceStage;
        private final SapiTournamentInfoEndpoint tournamentStage;

        public RaceAndTournamentStageAdapter(SapiStageSummaryEndpoint raceStage) {
            this.raceStage = raceStage;
            this.tournamentStage = null;
        }

        public RaceAndTournamentStageAdapter(SapiTournamentInfoEndpoint tournamentStage) {
            this.tournamentStage = tournamentStage;
            this.raceStage = null;
        }

        static RaceAndTournamentStageAdapter raceStage(SapiStageSummaryEndpoint stage) {
            return new RaceAndTournamentStageAdapter(stage);
        }

        static RaceAndTournamentStageAdapter tournamentStage(SapiTournamentInfoEndpoint stage) {
            return new RaceAndTournamentStageAdapter(stage);
        }

        Urn getId() {
            if (raceStage != null) {
                return Urn.parse(raceStage.getSportEvent().getId());
            } else {
                return Urn.parse(tournamentStage.getTournament().getId());
            }
        }

        String getName() {
            if (raceStage != null) {
                return raceStage.getSportEvent().getName();
            } else {
                return tournamentStage.getTournament().getName();
            }
        }
    }
}
