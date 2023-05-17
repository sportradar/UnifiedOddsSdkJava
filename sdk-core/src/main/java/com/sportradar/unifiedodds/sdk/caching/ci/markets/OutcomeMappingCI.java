/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class OutcomeMappingCI {

    private final String outcomeId;
    private final String producerOutcomeId;
    private final Map<Locale, String> producerOutcomeName;

    OutcomeMappingCI(Mappings.Mapping.MappingOutcome o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        outcomeId = o.getOutcomeId();
        producerOutcomeId = o.getProductOutcomeId();

        producerOutcomeName = new ConcurrentHashMap<>();
        if (o.getProductOutcomeName() != null) {
            producerOutcomeName.put(locale, o.getProductOutcomeName());
        }
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public String getProducerOutcomeId() {
        return producerOutcomeId;
    }

    public String getProducerOutcomeName(Locale locale) {
        return producerOutcomeName.get(locale);
    }

    public void merge(Mappings.Mapping.MappingOutcome o, Locale locale) {
        Preconditions.checkNotNull(o);
        Preconditions.checkNotNull(locale);

        if (o.getProductOutcomeName() != null) {
            producerOutcomeName.put(locale, o.getProductOutcomeName());
        }
    }
}
