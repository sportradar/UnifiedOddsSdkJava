/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import com.sportradar.unifiedodds.sdk.impl.UnifiedOddsStatistics;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsRegisterer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxModule.class);

    private final MBeanServer mbeanServer;

    public MetricsRegisterer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

    public UnifiedOddsStatistics createUnifiedOddsStatistics() {
        UnifiedOddsStatistics statsBean = null;
        try {
            ObjectName name = new ObjectName("com.sportradar.unifiedodds.sdk.impl:type=UnifiedOdds");
            statsBean = new UnifiedOddsStatistics();
            if (!mbeanServer.isRegistered(name)) {
                mbeanServer.registerMBean(statsBean, name);
            }
        } catch (
            MalformedObjectNameException
            | NotCompliantMBeanException
            | MBeanRegistrationException
            | InstanceAlreadyExistsException e
        ) {
            LOGGER.warn("UnifiedOddsStatistics initialization failed w/ ex.:", e);
        }

        return statsBean;
    }
}
