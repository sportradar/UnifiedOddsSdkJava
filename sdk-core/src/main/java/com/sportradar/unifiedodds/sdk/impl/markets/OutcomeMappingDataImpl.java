/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.OutcomeMappingCI;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class OutcomeMappingDataImpl implements OutcomeMappingData {

    private final OutcomeMappingCI outcomeMapping;
    private final Map<Locale, String> names;
    private final String outcomeId;
    private final String producerOutcomeId;

    OutcomeMappingDataImpl(OutcomeMappingCI m) {
        Preconditions.checkNotNull(m);

        outcomeMapping = m;
        outcomeId = m.getOutcomeId();
        producerOutcomeId = m.getProducerOutcomeId();

        names = null;
    }

    OutcomeMappingDataImpl(String outcomeId, Map<Locale, String> names) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(outcomeId));
        Preconditions.checkNotNull(names);

        this.names = names;
        this.outcomeId = outcomeId;

        this.outcomeMapping = null;
        this.producerOutcomeId = null;
    }

    public String getOutcomeId() {
        return outcomeId;
    }

    public String getProducerOutcomeId() {
        return producerOutcomeId;
    }

    public String getProducerOutcomeName(Locale locale) {
        if (outcomeMapping == null && names != null) {
            return names.get(locale);
        }

        if (outcomeMapping != null) {
            return outcomeMapping.getProducerOutcomeName(locale);
        }

        return null;
    }
}
