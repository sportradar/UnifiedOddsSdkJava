/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class OutcomeDescriptionsAssert
    extends AbstractAssert<OutcomeDescriptionsAssert, List<OutcomeDescription>> {

    private OutcomeDescriptionsAssert(List<OutcomeDescription> descriptions) {
        super(descriptions, OutcomeDescriptionsAssert.class);
    }

    public static OutcomeDescriptionsAssert assertThat(List<OutcomeDescription> descriptions) {
        return new OutcomeDescriptionsAssert(descriptions);
    }

    public OutcomeDescriptionsAssert haveNamesEqualTo(DescOutcomes sapiOutcomes, LanguageHolder language) {
        isNotNull();
        val actualOutcomeNames = actual.stream().map(o -> o.getName(language.get())).collect(toList());
        val expectedOutcomeNames = sapiOutcomes
            .getOutcome()
            .stream()
            .map(o -> o.getName())
            .collect(Collectors.toList());
        Assertions.assertThat(actualOutcomeNames).isEqualTo(expectedOutcomeNames);
        return this;
    }
}
