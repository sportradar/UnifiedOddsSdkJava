/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.ReplayWithIntegrationCredentials;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.ReplayWithProductionCredentials;
import static com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager.getEnvironmentSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.utils.OldStyleTest;
import lombok.val;
import org.junit.jupiter.api.Test;

@OldStyleTest
public class EnvironmentManagerTest {

    @Test
    public void replayShouldPointToStandardHttpPort() {
        final int standardHttpPort = 80;
        assertThatPointsToApiPort(ReplayWithIntegrationCredentials, standardHttpPort);
        assertThatPointsToApiPort(ReplayWithProductionCredentials, standardHttpPort);
    }

    private void assertThatPointsToApiPort(Environment environment, int apiPort) {
        assertEquals(apiPort, findEnvironmentSetting(environment).getApiPort());
        assertEquals(apiPort, EnvironmentManager.getApiPort(environment));
    }

    @Test
    public void replayEnvironmentsShouldSupportSslOnly() {
        assertTrue(findEnvironmentSetting(ReplayWithIntegrationCredentials).isOnlySsl());
        assertTrue(findEnvironmentSetting(ReplayWithProductionCredentials).isOnlySsl());
    }

    @Test
    public void retryListShouldBeDeprecated_ItIsDeadCode_ExposedThroughStaticContextCanCauseBreakingChange() {
        assertThat(findEnvironmentSetting(ReplayWithIntegrationCredentials).getEnvironmentRetryList())
            .isEmpty();
        assertThat(findEnvironmentSetting(ReplayWithProductionCredentials).getEnvironmentRetryList())
            .isEmpty();
    }

    private EnvironmentSetting findEnvironmentSetting(Environment environment) {
        val environmentSetting = getEnvironmentSettings()
            .stream()
            .filter(e -> e.getEnvironment() == environment)
            .findFirst();
        assertTrue(environmentSetting.isPresent());
        return environmentSetting.get();
    }
}
