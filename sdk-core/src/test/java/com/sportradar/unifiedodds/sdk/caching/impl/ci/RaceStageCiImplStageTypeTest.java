/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCiToMergeTo.into;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCis.exportSerializeAndUseConstructorToReimport;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCis.usingConstructor;
import static com.sportradar.unifiedodds.sdk.entities.StageType.Practice;
import static com.sportradar.unifiedodds.sdk.entities.StageType.SprintRace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.domain.names.Languages;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Stubber;

@RunWith(Enclosed.class)
public class RaceStageCiImplStageTypeTest {

    private static final String SAPI_SPRINT_RACE = "sprint_race";
    private static final String SAPI_PRACTICE_RACE = "practice";

    private static SapiStageSummaryEndpoint stageSummaryOfType(String type) {
        val stageSummary = new SapiStageSummaryEndpoint();
        SapiSportEvent sportEvent = new SapiSportEvent();
        sportEvent.setStageType(type);
        stageSummary.setSportEvent(sportEvent);
        return stageSummary;
    }

    @RunWith(Enclosed.class)
    public static class GivenStageTypeIsProvidedOnConstruction {

        private static SapiParentStage parentStage(String type) {
            SapiParentStage parentStage = new SapiParentStage();
            parentStage.setStageType(type);
            return parentStage;
        }

        private static SapiSportEventChildren.SapiSportEvent childSportEvent(String type) {
            SapiSportEventChildren.SapiSportEvent sportEvent = new SapiSportEventChildren.SapiSportEvent();
            sportEvent.setStageType(type);
            return sportEvent;
        }

        private static SapiFixture fixture(String type) {
            SapiFixture sapiFixture = new SapiFixture();
            sapiFixture.setStageType(type);
            return sapiFixture;
        }

        private static SapiSportEvent sportEvent(String type) {
            SapiSportEvent sportEvent = new SapiSportEvent();
            sportEvent.setStageType(type);
            return sportEvent;
        }

        private static RaceStageCiImpl reimportedStage(String type) throws Exception {
            SapiParentStage parentStage = new SapiParentStage();
            parentStage.setStageType(type);
            val stage = usingConstructor().constructFrom(parentStage);
            return exportSerializeAndUseConstructorToReimport(stage).construct();
        }

        private static SapiStageSummaryEndpoint stageSummary(String type) {
            val sapiStageSummary = new SapiStageSummaryEndpoint();
            SapiSportEvent sportEvent = new SapiSportEvent();
            sportEvent.setStageType(type);
            sapiStageSummary.setSportEvent(sportEvent);
            return sapiStageSummary;
        }

        public static class ThenItIsPreserved {

            @Test
            public void whenConstructedFromSapSportEvent() {
                val raceStage = usingConstructor().constructFrom(sportEvent(SAPI_SPRINT_RACE));

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            public void whenConstructedFromSapFixture() {
                val raceStage = usingConstructor().constructFrom(fixture(SAPI_SPRINT_RACE));

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            public void whenConstructedFromSapiStageSummary() {
                val raceStage = usingConstructor().constructFrom(stageSummary(SAPI_SPRINT_RACE));

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            public void whenConstructedFromSapiChildSportEvent() {
                val raceStage = usingConstructor().constructFrom(childSportEvent(SAPI_SPRINT_RACE));

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            public void whenConstructedFromSapiParentStage() {
                val raceStage = usingConstructor().constructFrom(parentStage(SAPI_SPRINT_RACE));

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            public void whenImportingExportedStageCi() throws Exception {
                val sprint = fixture(SAPI_SPRINT_RACE);
                val raceStage = usingConstructor().constructFrom(sprint);
                val importedRaceStage = exportSerializeAndUseConstructorToReimport(raceStage).construct();

                assertThat(importedRaceStage.getStageType()).isEqualTo(SprintRace);
            }
        }

        @RunWith(JUnitParamsRunner.class)
        public static class ThenItIsNotRemovedOnSubsequentMerge {

            @Test
            @Parameters(method = "sprintStages")
            public void ofSapiSportEvent(RaceStageCiImpl raceStage) {
                raceStage.merge(new SapiSportEvent(), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void ofSapiFixture(RaceStageCiImpl raceStage) {
                raceStage.merge(new SapiFixture(), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void ofSapiStageSummary(RaceStageCiImpl raceStage) {
                val stage = new SapiStageSummaryEndpoint();
                stage.setSportEvent(new SapiSportEvent());
                raceStage.merge(stage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void ofSapiChildSportEvent(RaceStageCiImpl raceStage) {
                raceStage.merge(new SapiSportEventChildren.SapiSportEvent(), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void whenConstructedFromSapiParentStage(RaceStageCiImpl raceStage) {
                raceStage.merge(new SapiParentStage(), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            private Object[] sprintStages() throws Exception {
                return new Object[][] {
                    { usingConstructor().constructFrom(sportEvent(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(fixture(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(stageSummary(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(childSportEvent(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(parentStage(SAPI_SPRINT_RACE)) },
                    { reimportedStage(SAPI_SPRINT_RACE) },
                };
            }
        }

        @RunWith(JUnitParamsRunner.class)
        public static class ThenItIsChangedOnSubsequentMerge {

            @Test
            @Parameters(method = "sprintStages")
            public void fromSapiSportEvent(RaceStageCiImpl sprintRace) {
                sprintRace.merge(sportEvent(SAPI_PRACTICE_RACE), Languages.any());

                assertThat(sprintRace.getStageType()).isEqualTo(Practice);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void fromSapiFixture(RaceStageCiImpl sprintRace) {
                sprintRace.merge(fixture(SAPI_PRACTICE_RACE), Languages.any());

                assertThat(sprintRace.getStageType()).isEqualTo(Practice);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void fromSapiStageSummary(RaceStageCiImpl sprintRace) {
                sprintRace.merge(stageSummary(SAPI_PRACTICE_RACE), Languages.any());

                assertThat(sprintRace.getStageType()).isEqualTo(Practice);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void fromSapiChildSportEvent(RaceStageCiImpl raceStage) {
                raceStage.merge(childSportEvent(SAPI_PRACTICE_RACE), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(Practice);
            }

            @Test
            @Parameters(method = "sprintStages")
            public void fromSapiParentStage(RaceStageCiImpl raceStage) {
                raceStage.merge(childSportEvent(SAPI_PRACTICE_RACE), Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(Practice);
            }

            private Object[] sprintStages() throws Exception {
                return new Object[][] {
                    { usingConstructor().constructFrom(sportEvent(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(fixture(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(stageSummary(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(childSportEvent(SAPI_SPRINT_RACE)) },
                    { usingConstructor().constructFrom(parentStage(SAPI_SPRINT_RACE)) },
                    { reimportedStage(SAPI_SPRINT_RACE) },
                };
            }
        }
    }

    @RunWith(Enclosed.class)
    public static class GivenStageTypeIsNotAvailableOnConstruction {

        @RunWith(JUnitParamsRunner.class)
        public static class ButSubsequentMergeContainsStageType {

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsSportEvent(RaceStageCiImpl raceStage) {
                val updatedStage = new SapiSportEvent();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsFixture(RaceStageCiImpl raceStage) {
                val updatedStage = new SapiFixture();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsStageSummary(RaceStageCiImpl raceStage) {
                val updatedStage = new SapiStageSummaryEndpoint();
                SapiSportEvent stage = new SapiSportEvent();
                stage.setStageType(SAPI_SPRINT_RACE);
                updatedStage.setSportEvent(stage);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsChildSportEvent(RaceStageCiImpl raceStage) {
                val updatedStage = new SapiSportEventChildren.SapiSportEvent();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsParentStage(RaceStageCiImpl raceStage) {
                val updatedStage = new SapiParentStage();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            private Object[] raceStagesWithoutStageType() throws Exception {
                return new Object[][] {
                    { usingConstructor().constructFrom(new SapiSportEvent()) },
                    { usingConstructor().constructFrom(new SapiFixture()) },
                    { usingConstructor().constructFrom(createEmptySapiStageSummary()) },
                    { usingConstructor().constructFrom(new SapiSportEventChildren.SapiSportEvent()) },
                    { usingConstructor().constructFrom(new SapiParentStage()) },
                    { createReimportedStage() },
                };
            }

            private static RaceStageCiImpl createReimportedStage() throws Exception {
                SapiParentStage anySapiObject = new SapiParentStage();
                val stage = usingConstructor().constructFrom(anySapiObject);
                return exportSerializeAndUseConstructorToReimport(stage).construct();
            }

            private static SapiStageSummaryEndpoint createEmptySapiStageSummary() {
                val sapiStageSummary = new SapiStageSummaryEndpoint();
                sapiStageSummary.setSportEvent(new SapiSportEvent());
                return sapiStageSummary;
            }
        }

        public static class FromSummarySourceObject {

            @Test
            public void thenSummaryIsNotReQueriedAsItIsConsideredToBeSourceOfTruthForStageType()
                throws CommunicationException {
                val stageSummary = new SapiStageSummaryEndpoint();
                stageSummary.setSportEvent(new SapiSportEvent());
                DataRouterManager dataRouterManager = mock(DataRouterManager.class);
                val stage = usingConstructor().with(dataRouterManager).constructFrom(stageSummary);

                stage.getStageType();

                verify(dataRouterManager, never()).requestSummaryEndpoint(any(), any(), any());
            }
        }

        @RunWith(Enclosed.class)
        public static class FromNonSummarySourceObject {

            public static class ThenSummaryIsQueriedButDueToSomeEdgeCaseDownstreamItIsNotMergedIntoCi {

                private DataRouterManager dataRouterManager = mock(DataRouterManager.class);

                @Test
                public void whenSourceObjectForConstructionIsSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiSportEvent());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                    val stage = usingConstructor().with(dataRouterManager).constructFrom(new SapiFixture());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsChildSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiSportEventChildren.SapiSportEvent());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsParentStage() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiParentStage());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void afterReimportingStageCi() throws Exception {
                    val stageWithoutType = usingConstructor().constructFrom(new SapiParentStage());
                    val importedRaceStage = exportSerializeAndUseConstructorToReimport(stageWithoutType)
                        .with(dataRouterManager)
                        .construct();

                    val stageType = importedRaceStage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }
            }

            public static class ThenSummaryIsQueriedAndMergedIntoCiWithStageTypeUpdated {

                private DataRouterManager dataRouterManager = mock(DataRouterManager.class);

                @Test
                public void whenSourceObjectForConstructionIsSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiSportEvent());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                    val stage = usingConstructor().with(dataRouterManager).constructFrom(new SapiFixture());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsChildSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiSportEventChildren.SapiSportEvent());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsParentStage() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SapiParentStage());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void afterReimportingStageCi() throws Exception {
                    SapiParentStage anySourceObject = new SapiParentStage();
                    val stage = usingConstructor().constructFrom(anySourceObject);
                    val importedRaceStage = exportSerializeAndUseConstructorToReimport(stage)
                        .with(dataRouterManager)
                        .construct();
                    whenRequestedSummaryThenMerges(
                        stageSummaryOfType(SAPI_SPRINT_RACE),
                        into(importedRaceStage)
                    );

                    assertThat(importedRaceStage.getStageType()).isEqualTo(SprintRace);
                }

                private void whenRequestedSummaryThenMerges(
                    SapiStageSummaryEndpoint raceStageSummary,
                    RaceStageCiToMergeTo stage
                ) throws CommunicationException {
                    does(() -> stage.getValue().merge(raceStageSummary, Languages.any()))
                        .when(dataRouterManager)
                        .requestSummaryEndpoint(any(), any(), any());
                }

                private static Stubber does(Runnable action) {
                    return doAnswer(args -> {
                        action.run();
                        return null;
                    });
                }
            }

            @RunWith(Enclosed.class)
            public static class AndSummaryIsQueriedButItFailsDueToCommunicationErrorAndSdkIsConfiguredToThrowErrors {

                public static class ThenErrorIsWrappedAndRethrown {

                    private DataRouterManager dataRouterManager = mock(DataRouterManager.class);
                    private CommunicationException communicationError = new CommunicationException(
                        "any error message",
                        "anyUrl"
                    );
                    private ExceptionHandlingStrategy sdkThrowsErrors = Throw;

                    @Test
                    public void whenSourceObjectForConstructionIsSportEvent() throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkThrowsErrors)
                            .constructFrom(new SapiSportEvent());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> stage.getStageType()).hasRootCause(communicationError);
                    }

                    @Test
                    public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkThrowsErrors)
                            .constructFrom(new SapiFixture());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> stage.getStageType()).hasRootCause(communicationError);
                    }

                    @Test
                    public void whenSourceObjectForConstructionIsChildSportEvent()
                        throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkThrowsErrors)
                            .constructFrom(new SapiSportEventChildren.SapiSportEvent());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> stage.getStageType()).hasRootCause(communicationError);
                    }

                    @Test
                    public void whenSourceObjectForConstructionIsParentStage() throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkThrowsErrors)
                            .constructFrom(new SapiParentStage());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> stage.getStageType()).hasRootCause(communicationError);
                    }

                    @Test
                    public void afterReimportingStageCi() throws Exception {
                        val anySourceObject = new SapiParentStage();
                        val stage = usingConstructor().constructFrom(anySourceObject);
                        val importedStage = exportSerializeAndUseConstructorToReimport(stage)
                            .with(dataRouterManager)
                            .with(sdkThrowsErrors)
                            .construct();
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> importedStage.getStageType())
                            .hasRootCause(communicationError);
                    }
                }
            }

            @RunWith(Enclosed.class)
            public static class AndSummaryIsQueriedButItFailsDueToCommunicationErrorAndSdkIsConfiguredToCatchErrors {

                public static class ThenSdkDoesNotProvideStageType {

                    private DataRouterManager dataRouterManager = mock(DataRouterManager.class);
                    private CommunicationException communicationError = new CommunicationException(
                        "any error message",
                        "anyUrl"
                    );
                    private ExceptionHandlingStrategy sdkCatchesErrors = Catch;

                    @Test
                    public void whenSourceObjectForConstructionIsSportEvent() throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkCatchesErrors)
                            .constructFrom(new SapiSportEvent());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(stage.getStageType()).isNull();
                    }

                    @Test
                    public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkCatchesErrors)
                            .constructFrom(new SapiFixture());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(stage.getStageType()).isNull();
                    }

                    @Test
                    public void whenSourceObjectForConstructionIsChildSportEvent()
                        throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkCatchesErrors)
                            .constructFrom(new SapiSportEventChildren.SapiSportEvent());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(stage.getStageType()).isNull();
                    }

                    @Test
                    public void whenSourceObjectUserForConstructionIsParentStage()
                        throws CommunicationException {
                        val stage = usingConstructor()
                            .with(dataRouterManager)
                            .with(sdkCatchesErrors)
                            .constructFrom(new SapiParentStage());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(stage.getStageType()).isNull();
                    }

                    @Test
                    public void afterReimportingStageCi() throws Exception {
                        val anySapiSourceObject = new SapiParentStage();
                        val stage = usingConstructor().constructFrom(anySapiSourceObject);
                        val importedStage = exportSerializeAndUseConstructorToReimport(stage)
                            .with(dataRouterManager)
                            .with(sdkCatchesErrors)
                            .construct();
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(importedStage.getStageType()).isNull();
                    }
                }
            }
        }
    }
}
