/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.uf.sportsapi.datamodel.Attributes;
import com.sportradar.unifiedodds.sdk.entities.markets.*;
import com.sportradar.unifiedodds.sdk.internal.caching.Translations;
import com.sportradar.utils.Urn;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarketDescriptionStub implements MarketDescription {

    private int id;
    private Translations name;
    private List<OutcomeDescription> outcomes;
    private String outcomeType;
    private List<String> groups;
    private List<MarketAttribute> attributes;
    private List<MarketMappingData> mappings;

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

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub withOutcomeType(String outcomeType) {
        this.outcomeType = outcomeType;
        return this;
    }

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub withGroups(List<String> groups) {
        this.groups = groups;
        return this;
    }

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub withAttributes(List<MarketAttribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    @SuppressWarnings("HiddenField")
    public MarketDescriptionStub withMappings(List<MarketMappingData> mappings) {
        this.mappings = mappings;
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
        return mappings;
    }

    @Override
    public List<MarketAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public List<String> getGroups() {
        return groups;
    }

    @Override
    public String getOutcomeType() {
        return outcomeType;
    }

    @RequiredArgsConstructor
    public static class MarketAttributeStub implements MarketAttribute {

        private final String name;
        private final String description;

        public static MarketAttributeStub attributeFrom(Attributes.Attribute descAttribute) {
            return new MarketAttributeStub(descAttribute.getName(), descAttribute.getDescription());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }

    @RequiredArgsConstructor
    public static class MarketMappingDataStub implements MarketMappingData {

        private final String marketId;

        public static MarketMappingDataStub mappingFrom(
            com.sportradar.uf.sportsapi.datamodel.Mappings.Mapping mapping
        ) {
            return new MarketMappingDataStub(mapping.getMarketId());
        }

        @Override
        public Set<Integer> getProducerIds() {
            return null;
        }

        @Override
        public Urn getSportId() {
            return null;
        }

        @Override
        public String getMarketId() {
            return marketId;
        }

        @Override
        public int getMarketTypeId() {
            return 0;
        }

        @Override
        public Integer getMarketSubTypeId() {
            return null;
        }

        @Override
        public String getSovTemplate() {
            return null;
        }

        @Override
        public Map<String, OutcomeMappingData> getOutcomeMappings() {
            return null;
        }

        @Override
        public String getValidFor() {
            return null;
        }

        @Override
        public boolean canMap(int producerId, Urn sportId, Map<String, String> specifiers) {
            return true;
        }
    }
}
