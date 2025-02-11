/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.impl.VariantDescriptionDataProviders.providingList;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.mockito.Mockito.verify;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class VariantDescriptionCacheTest {

    @Nested
    class OnInitialization {

        @Test
        void automaticallyLoadsVariantsForAllLanguages() throws Exception {
            val dataProvider = providingList(
                in(ENGLISH),
                fivePlusVariantDescription(),
                in(FRENCH),
                fivePlusVariantDescription()
            );

            VariantDescriptionCaches
                .stubbingOutDataProvidersAndScheduler()
                .with(dataProvider)
                .withImmediatelyExecutingTaskScheduler()
                .withPrefetchLanguages(asList(ENGLISH, FRENCH))
                .build();

            verify(dataProvider).getData(ENGLISH);
            verify(dataProvider).getData(FRENCH);
        }
    }
}
