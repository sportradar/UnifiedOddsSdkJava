/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.unitbuilder.providedvalue;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProvidedValue<T> {

    private T value;

    public T get() {
        return value;
    }
}
