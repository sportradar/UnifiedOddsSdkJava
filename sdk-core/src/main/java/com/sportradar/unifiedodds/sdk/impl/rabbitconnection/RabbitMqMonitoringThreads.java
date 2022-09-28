package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqMonitoringThreads {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqMonitoringThreads.class);


    public void startNew(Runnable runnable, String messageInterest, int channelId) {
        Thread monitorThread = new Thread(runnable);
        monitorThread.setName("MqChannelMonitor-" + messageInterest + "-" + channelId);
        monitorThread.setUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable throwable) {
                        logger.error(String.format("Uncaught thread exception monitoring %s", messageInterest), throwable);
                    }
                });

        monitorThread.start();
    }
}
