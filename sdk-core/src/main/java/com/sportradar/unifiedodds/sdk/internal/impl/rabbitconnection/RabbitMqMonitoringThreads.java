/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ConstantName" })
public class RabbitMqMonitoringThreads {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqMonitoringThreads.class);

    // todo: should use Scheduler without thread.sleep
    public void startNew(Runnable runnable, String messageInterest, int channelId) {
        Thread monitorThread = new Thread(runnable);
        monitorThread.setName("MqChannelMonitor-" + messageInterest + "-" + channelId);
        monitorThread.setUncaughtExceptionHandler(
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable throwable) {
                    logger.error(
                        String.format("Uncaught thread exception monitoring %s", messageInterest),
                        throwable
                    );
                }
            }
        );

        monitorThread.start();
    }
}
