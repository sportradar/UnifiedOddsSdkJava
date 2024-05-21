/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.domain.language;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.*;

public class Translations implements Serializable {

    private final Map<Locale, String> names = new HashMap<>();

    public Translations(Locale language, String translation) {
        Preconditions.checkNotNull(language);
        names.put(language, translation);
    }

    private Translations(Map<Locale, String> snapshot) {
        snapshot.entrySet().forEach(translation -> add(translation.getKey(), translation.getValue()));
    }

    public static Translations importFrom(Map<Locale, String> snapshot) {
        return new Translations(snapshot);
    }

    public void add(Locale language, String name) {
        Preconditions.checkNotNull(language);
        names.put(language, name);
    }

    public String getFor(Locale locale) {
        return names.get(locale);
    }

    public Map<Locale, String> export() {
        return Collections.unmodifiableMap(names);
    }

    public void addAllWithoutOverriding(Translations inflight) {
        Map<Locale, String> toUpdateWith = new HashMap<>(inflight.export());
        toUpdateWith.putAll(names);
        names.clear();
        names.putAll(toUpdateWith);
    }
}
