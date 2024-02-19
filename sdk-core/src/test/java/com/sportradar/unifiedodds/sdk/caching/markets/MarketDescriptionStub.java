/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.domain.language.Translations;
import com.sportradar.unifiedodds.sdk.entities.markets.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;

@RequiredArgsConstructor
public class MarketDescriptionStub implements MarketDescription {

    private int id;
    private Translations name;
    private List<OutcomeDescription> outcomes;

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub withId(int id) {
        this.id = id;
        return this;
    }

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub with(Translations name) {
        this.name = name;
        return this;
    }

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub with(List<OutcomeDescription> outcomes) {
        this.outcomes = outcomes;
        return this;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName(Locale language) {
        return name.getFor(language);
    }

    @Override
    public Collection<Locale> getLocales() {
        return name.export().keySet();
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }

    @Override
    public List<OutcomeDescription> getOutcomes() {
        return outcomes;
    }

    @Override
    public List<Specifier> getSpecifiers() {
        return null;
    }

    @Override
    public List<MarketMappingData> getMappings() {
        return null;
    }

    @Override
    public List<MarketAttribute> getAttributes() {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public String getOutcomeType() {
        return null;
    }
}
