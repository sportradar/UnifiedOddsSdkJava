/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationBuilderLimitTests extends ConfigurationBuilderSetup {

    @Test
    public void httpClientTimeoutBelowMin() {
        verify(() -> intBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN - 1));
        verify(() -> prodBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN - 1));
        verify(() -> replayBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN - 1));
        verify(() -> custBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN - 1));
    }

    @Test
    public void httpClientTimeoutAboveMax() {
        verify(() -> intBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX + 1));
        verify(() -> prodBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX + 1));
        verify(() -> replayBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX + 1));
        verify(() -> custBuilder().setHttpClientTimeout(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX + 1));
    }

    @Test
    public void httpClientRecoveryTimeoutBelowMin() {
        verify(() ->
            intBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN - 1)
        );
        verify(() ->
            prodBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN - 1)
        );
        verify(() ->
            replayBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN - 1)
        );
        verify(() ->
            custBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN - 1)
        );
    }

    @Test
    public void httpClientRecoveryTimeoutAboveMax() {
        verify(() ->
            intBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX + 1)
        );
        verify(() ->
            prodBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX + 1)
        );
        verify(() ->
            replayBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX + 1)
        );
        verify(() ->
            custBuilder().setHttpClientRecoveryTimeout(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX + 1)
        );
    }

    @Test
    public void httpClientFastFailingTimeoutBelowMin() {
        verify(() ->
            intBuilder().setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN - 1)
        );
        verify(() ->
            prodBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN - 1)
        );
        verify(() ->
            replayBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN - 1)
        );
        verify(() ->
            custBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN - 1)
        );
    }

    @Test
    public void httpClientFastFailingTimeoutAboveMax() {
        verify(() ->
            intBuilder().setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX + 1)
        );
        verify(() ->
            prodBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX + 1)
        );
        verify(() ->
            replayBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX + 1)
        );
        verify(() ->
            custBuilder()
                .setHttpClientFastFailingTimeout(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX + 1)
        );
    }

    @Test
    public void httpClientMaxConnTotalIsZero() {
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            intBuilder().setHttpClientMaxConnTotal(0).build().getApi().getHttpClientMaxConnTotal()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            prodBuilder().setHttpClientMaxConnTotal(0).build().getApi().getHttpClientMaxConnTotal()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            replayBuilder().setHttpClientMaxConnTotal(0).build().getApi().getHttpClientMaxConnTotal()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT,
            custBuilder().setHttpClientMaxConnTotal(0).build().getApi().getHttpClientMaxConnTotal()
        );
    }

    @Test
    public void httpClientMaxConnPerRouteIsZero() {
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            intBuilder().setHttpClientMaxConnPerRoute(0).build().getApi().getHttpClientMaxConnPerRoute()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            prodBuilder().setHttpClientMaxConnPerRoute(0).build().getApi().getHttpClientMaxConnPerRoute()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            replayBuilder().setHttpClientMaxConnPerRoute(0).build().getApi().getHttpClientMaxConnPerRoute()
        );
        Assert.assertEquals(
            ConfigLimit.HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT,
            custBuilder().setHttpClientMaxConnPerRoute(0).build().getApi().getHttpClientMaxConnPerRoute()
        );
    }

    @Test
    public void inactivitySecondsBelowMin() {
        verify(() -> intBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN - 1));
        verify(() -> prodBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN - 1));
        verify(() -> replayBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN - 1));
        verify(() -> custBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MIN - 1));
    }

    @Test
    public void inactivitySecondsAboveMax() {
        verify(() -> intBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX + 1));
        verify(() -> prodBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX + 1));
        verify(() -> replayBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX + 1));
        verify(() -> custBuilder().setInactivitySeconds(ConfigLimit.INACTIVITY_SECONDS_MAX + 1));
    }

    @Test
    public void inactivitySecondsPrematchBelowMin() {
        verify(() ->
            intBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN - 1)
        );
        verify(() ->
            prodBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN - 1)
        );
        verify(() ->
            replayBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN - 1)
        );
        verify(() ->
            custBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN - 1)
        );
    }

    @Test
    public void inactivitySecondsPrematchAboveMax() {
        verify(() ->
            intBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX + 1)
        );
        verify(() ->
            prodBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX + 1)
        );
        verify(() ->
            replayBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX + 1)
        );
        verify(() ->
            custBuilder().setInactivitySecondsPrematch(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX + 1)
        );
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusCacheTimeoutBelowMin() {
        verify(() ->
            intBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN - 1
                )
        );
        verify(() ->
            prodBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN - 1
                )
        );
        verify(() ->
            replayBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN - 1
                )
        );
        verify(() ->
            custBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN - 1
                )
        );
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusCacheTimeoutAboveMax() {
        verify(() ->
            intBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX + 1
                )
        );
        verify(() ->
            prodBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX + 1
                )
        );
        verify(() ->
            replayBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX + 1
                )
        );
        verify(() ->
            custBuilder()
                .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
                    ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX + 1
                )
        );
    }

    @Test
    public void profileCacheTimeoutBelowMin() {
        verify(() -> intBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN - 1));
        verify(() -> prodBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN - 1));
        verify(() -> replayBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN - 1));
        verify(() -> custBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN - 1));
    }

    @Test
    public void profileCacheTimeoutAboveMax() {
        verify(() -> intBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX + 1));
        verify(() -> prodBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX + 1));
        verify(() -> replayBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX + 1));
        verify(() -> custBuilder().setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX + 1));
    }

    @Test
    public void sportEventCacheTimeoutBelowMin() {
        verify(() -> intBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN - 1));
        verify(() -> prodBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN - 1));
        verify(() -> replayBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN - 1));
        verify(() -> custBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN - 1));
    }

    @Test
    public void sportEventCacheTimeoutAboveMax() {
        verify(() -> intBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX + 1));
        verify(() -> prodBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX + 1));
        verify(() -> replayBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX + 1));
        verify(() -> custBuilder().setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX + 1));
    }

    @Test
    public void sportEventStatusCacheTimeoutBelowMin() {
        verify(() ->
            intBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN - 1)
        );
        verify(() ->
            prodBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN - 1)
        );
        verify(() ->
            replayBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN - 1)
        );
        verify(() ->
            custBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN - 1)
        );
    }

    @Test
    public void sportEventStatusCacheTimeoutAboveMax() {
        verify(() ->
            intBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX + 1)
        );
        verify(() ->
            prodBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX + 1)
        );
        verify(() ->
            replayBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX + 1)
        );
        verify(() ->
            custBuilder()
                .setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX + 1)
        );
    }

    @Test
    public void variantMarketDescriptionCacheTimeoutBelowMin() {
        verify(() ->
            intBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN - 1)
        );
        verify(() ->
            prodBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN - 1)
        );
        verify(() ->
            replayBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN - 1)
        );
        verify(() ->
            custBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN - 1)
        );
    }

    @Test
    public void variantMarketDescriptionCacheTimeoutAboveMax() {
        verify(() ->
            intBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX + 1)
        );
        verify(() ->
            prodBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX + 1)
        );
        verify(() ->
            replayBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX + 1)
        );
        verify(() ->
            custBuilder()
                .setVariantMarketDescriptionCacheTimeout(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX + 1)
        );
    }

    @Test
    public void rabbitConnectionTimeoutBelowMin() {
        verify(() -> intBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN - 1));
        verify(() -> prodBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN - 1));
        verify(() -> replayBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN - 1)
        );
        verify(() -> custBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN - 1));
    }

    @Test
    public void rabbitConnectionTimeoutAboveMax() {
        verify(() -> intBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX + 1));
        verify(() -> prodBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX + 1));
        verify(() -> replayBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX + 1)
        );
        verify(() -> custBuilder().setRabbitConnectionTimeout(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX + 1));
    }

    @Test
    public void rabbitHeartbeatBelowMin() {
        verify(() -> intBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MIN - 1));
        verify(() -> prodBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MIN - 1));
        verify(() -> replayBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MIN - 1));
        verify(() -> custBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MIN - 1));
    }

    @Test
    public void rabbitHeartbeatAboveMax() {
        verify(() -> intBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MAX + 1));
        verify(() -> prodBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MAX + 1));
        verify(() -> replayBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MAX + 1));
        verify(() -> custBuilder().setRabbitHeartbeat(ConfigLimit.RABBIT_HEARTBEAT_MAX + 1));
    }

    @Test
    public void statisticsIntervalBelowMin() {
        verify(() -> intBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN - 1));
        verify(() -> prodBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN - 1));
        verify(() -> replayBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN - 1));
        verify(() -> custBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN - 1));
    }

    @Test
    public void statisticsIntervalAboveMax() {
        verify(() -> intBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX + 1));
        verify(() -> prodBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX + 1));
        verify(() -> replayBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX + 1));
        verify(() -> custBuilder().setStatisticsInterval(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX + 1));
    }

    private ConfigurationBuilder intBuilder() {
        return buildConfig("i");
    }

    private ConfigurationBuilder prodBuilder() {
        return buildConfig("p");
    }

    private ConfigurationBuilder replayBuilder() {
        return buildConfig("r");
    }

    private CustomConfigurationBuilder custBuilder() {
        return buildCustomConfig();
    }

    private void verify(ThrowableAssert.ThrowingCallable settingOperation) {
        assertThatThrownBy(settingOperation).isInstanceOf(IllegalArgumentException.class);
    }
}
