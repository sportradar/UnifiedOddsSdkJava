/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import static java.util.stream.Collectors.toSet;

import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportData;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class SportDataListAssert extends AbstractAssert<SportDataListAssert, List<SportData>> {

    private SportDataListAssert(List<SportData> sport) {
        super(sport, SportDataListAssert.class);
    }

    public static SportDataListAssert assertThat(List<SportData> sport) {
        return new SportDataListAssert(sport);
    }

    public SportDataListAssert containsExactlyAllElementsInAnyOrder(List<SportData> expected) {
        isNotNull();

        Assertions.assertThat(stringified(actual)).isEqualTo(stringified(expected));

        return this;
    }

    private Set<SportDataHolder> stringified(List<SportData> expected) {
        return expected.stream().map(SportDataHolder::new).collect(toSet());
    }
}
