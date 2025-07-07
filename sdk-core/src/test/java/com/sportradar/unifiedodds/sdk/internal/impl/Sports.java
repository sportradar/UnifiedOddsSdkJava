/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static java.util.Collections.emptyList;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.conn.SapiSports;
import com.sportradar.unifiedodds.sdk.entities.Sport;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.SportImpl;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import lombok.val;

public class Sports {

    public static Sport soccer(LanguageHolder language) {
        val soccer = SapiSports.soccer(language);
        return new SportImpl(
            Urn.parse(soccer.getId()),
            ImmutableMap.of(language.get(), soccer.getName()),
            emptyList()
        );
    }
}
