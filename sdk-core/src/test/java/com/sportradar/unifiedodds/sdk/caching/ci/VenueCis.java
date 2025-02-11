/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.deserialize;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.serialize;

import com.sportradar.unifiedodds.sdk.internal.caching.ci.VenueCi;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableVenueCi;
import lombok.val;

public final class VenueCis {

    private VenueCis() {}

    public static VenueCi exportSerializeAndUseConstructorToReimport(VenueCi original) throws Exception {
        val exportedRaceStage = original.export();
        val serialized = serialize(exportedRaceStage);
        val deserialized = deserialize(serialized);
        return new VenueCi((ExportableVenueCi) deserialized);
    }
}
