/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.rabbitmq.client.ConnectionFactory;

class ConfigLimit {

    public static final int HTTP_CLIENT_TIMEOUT_MIN = 1;
    public static final int HTTP_CLIENT_TIMEOUT_DEFAULT = 30;
    public static final int HTTP_CLIENT_TIMEOUT_MAX = 60;
    public static final int HTTP_CLIENT_RECOVERY_TIMEOUT_MIN = 1;
    public static final int HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT = 30;
    public static final int HTTP_CLIENT_RECOVERY_TIMEOUT_MAX = 60;
    public static final int HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN = 1;
    public static final int HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT = 5;
    public static final int HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX = 30;
    public static final int HTTP_CLIENT_MAX_CONN_TOTAL_DEFAULT = 20;
    public static final int HTTP_CLIENT_MAX_CONN_PER_ROUTE_DEFAULT = 15;

    public static final int SPORTEVENTCACHE_TIMEOUT_MIN = 1;
    public static final int SPORTEVENTCACHE_TIMEOUT_DEFAULT = 12;
    public static final int SPORTEVENTCACHE_TIMEOUT_MAX = 48;
    public static final int SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN = 1;
    public static final int SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_DEFAULT = 5;
    public static final int SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX = 60;
    public static final int PROFILECACHE_TIMEOUT_MIN = 1;
    public static final int PROFILECACHE_TIMEOUT_DEFAULT = 24;
    public static final int PROFILECACHE_TIMEOUT_MAX = 48;
    public static final int SINGLEVARIANTMARKET_TIMEOUT_MIN = 1;
    public static final int SINGLEVARIANTMARKET_TIMEOUT_DEFAULT = 3;
    public static final int SINGLEVARIANTMARKET_TIMEOUT_MAX = 24;
    public static final int IGNOREBETPALTIMELINE_TIMEOUT_MIN = 1;
    public static final int IGNOREBETPALTIMELINE_TIMEOUT_DEFAULT = 3;
    public static final int IGNOREBETPALTIMELINE_TIMEOUT_MAX = 24;

    public static final int STATISTICS_INTERVAL_MINUTES_MIN = 0;
    public static final int STATISTICS_INTERVAL_MINUTES_DEFAULT = 10;
    public static final int STATISTICS_INTERVAL_MINUTES_MAX = 60 * 24;

    public static final int INACTIVITY_SECONDS_MIN = 10;
    public static final int INACTIVITY_SECONDS_DEFAULT = 20;
    public static final int INACTIVITY_SECONDS_MAX = 180;
    public static final int INACTIVITY_SECONDS_PREMATCH_MIN = 10;
    public static final int INACTIVITY_SECONDS_PREMATCH_DEFAULT = 20;
    public static final int INACTIVITY_SECONDS_PREMATCH_MAX = 180;
    public static final int MAX_RECOVERY_TIME_MIN = 600;
    public static final int MAX_RECOVERY_TIME_DEFAULT = 1200;
    public static final int MAX_RECOVERY_TIME_MAX = 3600;
    public static final int MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MIN = 20;
    public static final int MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_DEFAULT = 30;
    public static final int MIN_INTERVAL_BETWEEN_RECOVERY_REQUEST_MAX = 180;
    public static final int RABBIT_CONNECTION_TIMEOUT_MIN = 10;
    public static final int RABBIT_CONNECTION_TIMEOUT_DEFAULT =
        ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT / 1000; // default 60s
    public static final int RABBIT_CONNECTION_TIMEOUT_MAX = 120;
    public static final int RABBIT_HEARTBEAT_MIN = 10;
    public static final int RABBIT_HEARTBEAT_DEFAULT = ConnectionFactory.DEFAULT_HEARTBEAT; // default 60s
    public static final int RABBIT_HEARTBEAT_MAX = 180;

    public static final int USAGE_EXPORT_INTERVAL_SEC = 300;
    public static final int USAGE_EXPORT_TIMEOUT_SEC = 20;

    private ConfigLimit() {}
}
