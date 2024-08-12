/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.*;
import static com.sportradar.unifiedodds.sdk.impl.EnvironmentManager.getEnvironmentSettings;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;

public class EnvironmentManagerTest {

    @Test
    public void globalAndNonGlobalStgApisSitUnderSameIpsHoweverReplayShouldPointToNonGlobalAsItIsLongTermStrategy() {
        val nonGlobalApiHost = "stgapi.betradar.com";
        assertThatPointsToApiHost(Replay, nonGlobalApiHost);
        assertThatPointsToApiHost(GlobalReplay, nonGlobalApiHost);
    }

    private void assertThatPointsToApiHost(Environment globalReplay, String nonGlobalApiHost) {
        assertEquals(nonGlobalApiHost, findEnvironmentSetting(globalReplay).getApiHost());
        assertEquals(nonGlobalApiHost, EnvironmentManager.getApiHost(globalReplay));
    }

    @Test
    public void replayShouldPointToNonGlobalMessagingEndpointAsItIsLongTermStrategy() {
        assertThatPointsToMessagingHost(Replay, "replaymq.betradar.com");
    }

    @Test
    public void untilIpConsolidationInTheBackendGlobalReplayShouldPointToNonGlobalMessagingEndpoint() {
        assertThatPointsToMessagingHost(GlobalReplay, "global.replaymq.betradar.com");
    }

    private void assertThatPointsToMessagingHost(Environment replay, String nonGlobalMessagingHost) {
        assertEquals(nonGlobalMessagingHost, findEnvironmentSetting(replay).getMqHost());
        assertEquals(nonGlobalMessagingHost, EnvironmentManager.getMqHost(replay));
    }

    @Test
    public void replayShouldPointToStandardHttpPort() {
        final int standardHttpPort = 80;
        assertThatPointsToApiPost(Replay, standardHttpPort);
        assertThatPointsToApiPost(GlobalReplay, standardHttpPort);
    }

    private void assertThatPointsToApiPost(Environment environment, int apiPort) {
        assertEquals(apiPort, findEnvironmentSetting(environment).getApiPort());
        assertEquals(apiPort, EnvironmentManager.getApiPort(environment));
    }

    @Test
    public void replayEnvironmentsShouldSupportSslOnly() {
        assertTrue(findEnvironmentSetting(Replay).isOnlySsl());
        assertTrue(findEnvironmentSetting(GlobalReplay).isOnlySsl());
    }

    @Test
    public void retryListShouldBeDeprecated_ItIsDeadCode_ExposedThroughStaticContextCanCauseBreakingChange() {
        List<Environment> retryList = asList(Integration, Production);
        assertEquals(retryList, findEnvironmentSetting(Replay).getEnvironmentRetryList());
        assertEquals(retryList, findEnvironmentSetting(GlobalReplay).getEnvironmentRetryList());
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
