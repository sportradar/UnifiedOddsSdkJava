/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.deserialize;
import static com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer.serialize;

import com.sportradar.unifiedodds.sdk.internal.caching.ci.CourseCi;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCourseCi;
import lombok.val;

public final class CourseCis {

    private CourseCis() {}

    public static CourseCi exportSerializeAndUseConstructorToReimport(CourseCi original) throws Exception {
        val exported = original.export();
        val serialized = serialize(exported);
        val deserialized = deserialize(serialized);
        return new CourseCi((ExportableCourseCi) deserialized);
    }
}
