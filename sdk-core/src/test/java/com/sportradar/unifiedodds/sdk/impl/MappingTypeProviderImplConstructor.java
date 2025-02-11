/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.internal.impl.MappingTypeProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.MappingTypeProviderImpl;

public class MappingTypeProviderImplConstructor {

    private MappingTypeProviderImplConstructor() {}

    public static MappingTypeProvider create() {
        return new MappingTypeProviderImpl();
    }
}
