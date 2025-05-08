/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.parameterized;

public interface PropertyGetterFrom<T> {
    Object getFrom(T object);
}
