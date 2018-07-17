/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;

import java.io.InputStream;

/**
 * The basic interface representation of a deserializer used to produce valid Java object from a data source
 */
public interface Deserializer {
    Object deserialize(InputStream inStr) throws DeserializationException;
}
