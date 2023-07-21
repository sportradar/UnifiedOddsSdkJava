/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCiToMergeTo.into;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCis.exportSerializeAndUseConstructorToReimport;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.RaceStageCis.usingConstructor;
import static com.sportradar.unifiedodds.sdk.entities.StageType.SprintRace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.testutil.javautil.Languages;
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

    private static SAPIStageSummaryEndpoint stageSummaryOfType(String type) {
        val stageSummary = new SAPIStageSummaryEndpoint();
        SAPISportEvent sportEvent = new SAPISportEvent();
        sportEvent.setStageType(type);
        stageSummary.setSportEvent(sportEvent);
        return stageSummary;
    }

    public static class WhenStageTypeIsProvidedOnConstruction {

        @Test
        public void fromSportEventItPreservesStageType() {
            SAPISportEvent stage = new SAPISportEvent();
            stage.setStageType(SAPI_SPRINT_RACE);
            val raceStage = usingConstructor().constructFrom(stage);

            assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
        }

        @Test
        public void fromFixtureItPreserveStageType() {
            val stage = new SAPIFixture();
            stage.setStageType(SAPI_SPRINT_RACE);
            val raceStage = usingConstructor().constructFrom(stage);

            assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
        }

        @Test
        public void fromStageSummaryItPreserveStageType() {
            val stage = new SAPIStageSummaryEndpoint();
            SAPISportEvent stage1 = new SAPISportEvent();
            stage1.setStageType(SAPI_SPRINT_RACE);
            stage.setSportEvent(stage1);
            val raceStage = usingConstructor().constructFrom(stage);

            assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
        }

        @Test
        public void fromChildSportEventItPreserveStageType() {
            val stage = new SAPISportEventChildren.SAPISportEvent();
            stage.setStageType(SAPI_SPRINT_RACE);
            val raceStage = usingConstructor().constructFrom(stage);

            assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
        }

        @Test
        public void fromParentStageItPreserveStageType() {
            val sprintStage = new SAPIParentStage();
            sprintStage.setStageType(SAPI_SPRINT_RACE);
            val raceStage = usingConstructor().constructFrom(sprintStage);

            assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
        }

        @Test
        public void inFormOfImportingExportedStageCiItPreserveStageType() throws Exception {
            val stage = new SAPIParentStage();
            stage.setStageType(SAPI_SPRINT_RACE);
            val raceStage = usingConstructor().constructFrom(stage);
            val importedRaceStage = exportSerializeAndUseConstructorToReimport(raceStage).construct();

            assertThat(importedRaceStage.getStageType()).isEqualTo(SprintRace);
        }
    }

    @RunWith(Enclosed.class)
    public static class GivenStageTypeIsNotAvailableOnConstruction {

        @RunWith(JUnitParamsRunner.class)
        public static class ButSubsequentMergeContainsStageType {

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsSportEvent(RaceStageCIImpl raceStage) {
                val updatedStage = new SAPISportEvent();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsFixture(RaceStageCIImpl raceStage) {
                val updatedStage = new SAPIFixture();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsStageSummary(RaceStageCIImpl raceStage) {
                val updatedStage = new SAPIStageSummaryEndpoint();
                SAPISportEvent stage = new SAPISportEvent();
                stage.setStageType(SAPI_SPRINT_RACE);
                updatedStage.setSportEvent(stage);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsChildSportEvent(RaceStageCIImpl raceStage) {
                val updatedStage = new SAPISportEventChildren.SAPISportEvent();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            @Test
            @Parameters(method = "raceStagesWithoutStageType")
            public void whenSubsequentMergeObjectIsParentStage(RaceStageCIImpl raceStage) {
                val updatedStage = new SAPIParentStage();
                updatedStage.setStageType(SAPI_SPRINT_RACE);

                raceStage.merge(updatedStage, Languages.any());

                assertThat(raceStage.getStageType()).isEqualTo(SprintRace);
            }

            private Object[] raceStagesWithoutStageType() throws Exception {
                return new Object[][] {
                    { usingConstructor().constructFrom(new SAPISportEvent()) },
                    { usingConstructor().constructFrom(new SAPIFixture()) },
                    { usingConstructor().constructFrom(createEmptySapiStageSummary()) },
                    { usingConstructor().constructFrom(new SAPISportEventChildren.SAPISportEvent()) },
                    { usingConstructor().constructFrom(new SAPIParentStage()) },
                    { createReimportedStage() },
                };
            }

            private static RaceStageCIImpl createReimportedStage() throws Exception {
                SAPIParentStage anySapiObject = new SAPIParentStage();
                val stage = usingConstructor().constructFrom(anySapiObject);
                return exportSerializeAndUseConstructorToReimport(stage).construct();
            }

            private static SAPIStageSummaryEndpoint createEmptySapiStageSummary() {
                val sapiStageSummary = new SAPIStageSummaryEndpoint();
                sapiStageSummary.setSportEvent(new SAPISportEvent());
                return sapiStageSummary;
            }
        }

        public static class FromSummarySourceObject {

            @Test
            public void thenSummaryIsNotReQueriedAsItIsConsideredToBeSourceOfTruthForStageType()
                throws CommunicationException {
                val stageSummary = new SAPIStageSummaryEndpoint();
                stageSummary.setSportEvent(new SAPISportEvent());
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
                        .constructFrom(new SAPISportEvent());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                    val stage = usingConstructor().with(dataRouterManager).constructFrom(new SAPIFixture());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsChildSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SAPISportEventChildren.SAPISportEvent());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void whenSourceObjectForConstructionIsParentStage() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SAPIParentStage());

                    val stageType = stage.getStageType();

                    assertThat(stageType).isNull();
                    verify(dataRouterManager).requestSummaryEndpoint(any(), any(), any());
                }

                @Test
                public void afterReimportingStageCi() throws Exception {
                    val stageWithoutType = usingConstructor().constructFrom(new SAPIParentStage());
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
                        .constructFrom(new SAPISportEvent());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsFixture() throws CommunicationException {
                    val stage = usingConstructor().with(dataRouterManager).constructFrom(new SAPIFixture());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsChildSportEvent() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SAPISportEventChildren.SAPISportEvent());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void whenSourceObjectForConstructionIsParentStage() throws CommunicationException {
                    val stage = usingConstructor()
                        .with(dataRouterManager)
                        .constructFrom(new SAPIParentStage());
                    whenRequestedSummaryThenMerges(stageSummaryOfType(SAPI_SPRINT_RACE), into(stage));

                    assertThat(stage.getStageType()).isEqualTo(SprintRace);
                }

                @Test
                public void afterReimportingStageCi() throws Exception {
                    SAPIParentStage anySourceObject = new SAPIParentStage();
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
                    SAPIStageSummaryEndpoint raceStageSummary,
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
                            .constructFrom(new SAPISportEvent());
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
                            .constructFrom(new SAPIFixture());
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
                            .constructFrom(new SAPISportEventChildren.SAPISportEvent());
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
                            .constructFrom(new SAPIParentStage());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThatThrownBy(() -> stage.getStageType()).hasRootCause(communicationError);
                    }

                    @Test
                    public void afterReimportingStageCi() throws Exception {
                        val anySourceObject = new SAPIParentStage();
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
                            .constructFrom(new SAPISportEvent());
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
                            .constructFrom(new SAPIFixture());
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
                            .constructFrom(new SAPISportEventChildren.SAPISportEvent());
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
                            .constructFrom(new SAPIParentStage());
                        doThrow(communicationError)
                            .when(dataRouterManager)
                            .requestSummaryEndpoint(any(), any(), any());

                        assertThat(stage.getStageType()).isNull();
                    }

                    @Test
                    public void afterReimportingStageCi() throws Exception {
                        val anySapiSourceObject = new SAPIParentStage();
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
