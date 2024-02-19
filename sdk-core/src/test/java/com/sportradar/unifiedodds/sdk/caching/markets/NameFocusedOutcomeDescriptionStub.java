/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.domain.language.Translations;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import java.util.Collection;
import java.util.Locale;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NameFocusedOutcomeDescriptionStub implements OutcomeDescription {

    private final String id;
    private final Translations name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName(Locale language) {
        return name.getFor(language);
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }

    @Override
    public Collection<Locale> getLocales() {
        return name.export().keySet();
    }
}
