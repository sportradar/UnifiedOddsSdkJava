/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches.BuilderStubbingOutDataRouterManager.stubbingOutDataRouterManager;
import static com.sportradar.unifiedodds.sdk.caching.impl.SportEntityFactories.BuilderStubbingOutAllCachesAndStatusFactory.stubbingOutAllCachesAndStatusFactory;
import static com.sportradar.utils.Urns.PlayerProfiles.urnForAnyPlayerProfile;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.val;
import org.junit.jupiter.api.Test;

class PlayerProfileCacheImplTest {

    @Test
    public void profileUrnIsMandatoryToConstructProfile() {
        val profileCache = stubbingOutDataRouterManager().build();
        val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

        assertThatThrownBy(() ->
                profileFactory.buildPlayerProfile(null, asList(ENGLISH), asList(urnForAnyPlayerProfile()))
            )
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void nonNullLanguagesAreMandatoryToConstructProfile() {
        val profileCache = stubbingOutDataRouterManager().build();
        val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

        assertThatThrownBy(() ->
                profileFactory.buildPlayerProfile(
                    urnForAnyPlayerProfile(),
                    null,
                    asList(urnForAnyPlayerProfile())
                )
            )
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void nonEmptyLanguagesAreMandatoryToConstructProfile() {
        val profileCache = stubbingOutDataRouterManager().build();
        val profileFactory = stubbingOutAllCachesAndStatusFactory().with(profileCache).build();

        assertThatThrownBy(() ->
                profileFactory.buildPlayerProfile(
                    urnForAnyPlayerProfile(),
                    emptyList(),
                    asList(urnForAnyPlayerProfile())
                )
            )
            .isInstanceOf(IllegalArgumentException.class);
    }
}
