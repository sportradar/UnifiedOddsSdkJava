/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.guava.libraryfixtures;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class NoOpCache implements Cache {

    @Override
    public Object getIfPresent(Object o) {
        return null;
    }

    @Override
    public Object get(Object o, Callable callable) throws ExecutionException {
        return null;
    }

    @Override
    public void put(Object o, Object o2) {}

    @Override
    public void putAll(Map map) {}

    @Override
    public void invalidate(Object o) {}

    @Override
    public long size() {
        return 0;
    }

    @Override
    public CacheStats stats() {
        return null;
    }

    @Override
    public ConcurrentMap asMap() {
        return null;
    }

    @Override
    public void cleanUp() {}

    @Override
    public void invalidateAll() {}

    @Override
    public void invalidateAll(Iterable iterable) {}

    @Override
    public ImmutableMap getAllPresent(Iterable iterable) {
        return null;
    }
}
