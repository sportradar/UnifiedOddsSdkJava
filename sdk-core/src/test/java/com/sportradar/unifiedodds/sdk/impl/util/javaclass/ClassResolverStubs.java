/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.util.javaclass;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassResolverStubs {

    private ClassResolverStubs() {}

    public static ClassResolver notFindingClass() {
        ClassResolver resolver = mock(ClassResolver.class);
        when(resolver.resolveByName(any())).thenThrow(IllegalStateException.class);
        return resolver;
    }
}
