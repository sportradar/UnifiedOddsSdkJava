/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.unifiedodds.sdk.internal.exceptions.DeserializationException;
import java.io.InputStream;

/**
 * The basic interface representation of a deserializer used to produce valid Java object from a data source
 */
public interface Deserializer {
    Object deserialize(InputStream inStr) throws DeserializationException;
    String serialize(Object inObj) throws DeserializationException;
    void unload();
}
