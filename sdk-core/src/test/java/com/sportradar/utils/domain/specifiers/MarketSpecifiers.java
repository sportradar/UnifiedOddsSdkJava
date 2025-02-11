/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.specifiers;

import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.conn.Identifiable;
import com.sportradar.unifiedodds.sdk.internal.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.testutil.generic.collections.Maps;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.val;

public final class MarketSpecifiers {

    private MarketSpecifiers() {}

    public static Map<String, String> anySpecifiers() {
        val noSpecifiers = new HashMap<String, String>();
        val oneSpecifier = Collections.singletonMap("total", "2.5");
        val twoSpecifiers = Maps.of("total", "2.5", "hcp", "-0.5");

        return pickOneRandomlyFrom(noSpecifiers, oneSpecifier, twoSpecifiers);
    }

    public static Map<String, String> variant(Identifiable variant) {
        return ImmutableMap.of(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME, variant.id());
    }
}
