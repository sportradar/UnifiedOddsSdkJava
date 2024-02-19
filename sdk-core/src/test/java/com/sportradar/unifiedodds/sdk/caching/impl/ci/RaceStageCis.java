/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.deserialize;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.serialize;
import static com.sportradar.utils.Urns.SportEvents.urnForAnyStage;
import static java.util.Optional.empty;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableRaceStageCi;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagers;
import com.sportradar.unifiedodds.sdk.testutil.guava.libraryfixtures.Caches;
import com.sportradar.utils.domain.names.Languages;
import java.util.*;
import lombok.val;

public class RaceStageCis {

    private static Random random = new Random();

    private RaceStageCis() {}

    public static BuilderForConstructionOnly usingConstructor() {
        return new BuilderForConstructionOnly();
    }

    public static BuilderForConstructionFromExportedOnly usingConstructorToReimport(
        ExportableRaceStageCi exported
    ) {
        return new BuilderForConstructionFromExportedOnly(exported);
    }

    public static BuilderForConstructionFromExportedOnly exportSerializeAndUseConstructorToReimport(
        RaceStageCiImpl original
    ) throws Exception {
        val exportedRaceStage = original.export();
        val serialized = serialize(exportedRaceStage);
        val deserialized = deserialize(serialized);
        return usingConstructorToReimport((ExportableRaceStageCi) deserialized);
    }

    public static class BuilderForConstructionOnly {

        private Optional<DataRouterManager> providedDataRouterManager = empty();
        private Optional<ExceptionHandlingStrategy> providedErrorHandlingStrategy = empty();

        private BuilderForConstructionOnly() {}

        public BuilderForConstructionOnly with(DataRouterManager dataRouterManager) {
            this.providedDataRouterManager = Optional.of(dataRouterManager);
            return this;
        }

        public BuilderForConstructionOnly with(ExceptionHandlingStrategy exceptionHandlingStrategy) {
            this.providedErrorHandlingStrategy = Optional.of(exceptionHandlingStrategy);
            return this;
        }

        public RaceStageCiImpl constructFrom(SapiSportEvent sapiSourceObject) {
            return new RaceStageCiImpl(
                urnForAnyStage(),
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                Languages.any(),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                sapiSourceObject,
                Languages.any(),
                Caches.any()
            );
        }

        public RaceStageCiImpl constructFrom(SapiFixture sapiSourceObject) {
            return new RaceStageCiImpl(
                urnForAnyStage(),
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                Languages.any(),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                sapiSourceObject,
                Languages.any(),
                Caches.any()
            );
        }

        public RaceStageCiImpl constructFrom(SapiStageSummaryEndpoint sapiSourceObject) {
            return new RaceStageCiImpl(
                urnForAnyStage(),
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                Languages.any(),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                sapiSourceObject,
                Languages.any(),
                Caches.any()
            );
        }

        public RaceStageCiImpl constructFrom(SapiSportEventChildren.SapiSportEvent sapiSourceObject) {
            return new RaceStageCiImpl(
                urnForAnyStage(),
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                Languages.any(),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                sapiSourceObject,
                Languages.any(),
                Caches.any()
            );
        }

        public RaceStageCiImpl constructFrom(SapiParentStage sapiSourceObject) {
            return new RaceStageCiImpl(
                urnForAnyStage(),
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                Languages.any(),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                sapiSourceObject,
                Languages.any(),
                Caches.any()
            );
        }
    }

    public static class BuilderForConstructionFromExportedOnly {

        private ExportableRaceStageCi exportableToImport;
        private Optional<DataRouterManager> providedDataRouterManager = empty();
        private Optional<ExceptionHandlingStrategy> providedErrorHandlingStrategy = empty();

        private BuilderForConstructionFromExportedOnly(ExportableRaceStageCi exportableToImport) {
            this.exportableToImport = exportableToImport;
        }

        public BuilderForConstructionFromExportedOnly with(DataRouterManager dataRouterManager) {
            this.providedDataRouterManager = Optional.of(dataRouterManager);
            return this;
        }

        public BuilderForConstructionFromExportedOnly with(
            ExceptionHandlingStrategy exceptionHandlingStrategy
        ) {
            this.providedErrorHandlingStrategy = Optional.of(exceptionHandlingStrategy);
            return this;
        }

        public RaceStageCiImpl construct() {
            return new RaceStageCiImpl(
                exportableToImport,
                providedDataRouterManager.orElse(DataRouterManagers.any()),
                providedErrorHandlingStrategy.orElse(anyErrorHandlingStrategy()),
                Caches.any()
            );
        }
    }
}
