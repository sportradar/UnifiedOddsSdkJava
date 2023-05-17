/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static java.util.Objects.nonNull;
import static org.junit.Assert.fail;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.read.ListAppender;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogsMock {

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    public static LogsMock createCapturingFor(Class<?> targetClass) {
        final val logsMock = new LogsMock();
        logsMock.addTestLogAppender(targetClass);
        return logsMock;
    }

    private void addTestLogAppender(Class<?> target) {
        logger = LoggerFactory.getLogger(target);
        ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
        appender = new ListAppender();
        logbackLogger.addAppender(appender);
        appender.start();
    }

    public void verifyLoggedLineContaining(final String text) {
        for (ILoggingEvent loggingEvent : appender.list) {
            if (loggingEvent.getFormattedMessage().contains(text)) {
                return;
            }
        }
        fail("Could not find log line that matches: " + text);
    }

    public void verifyNotLoggedLineContaining(final String text) {
        for (ILoggingEvent loggingEvent : appender.list) {
            if (loggingEvent.getFormattedMessage().contains(text)) {
                fail("Could find log line that matches: " + text);
            }
        }
    }

    public void verifyLoggedExceptionMessageContaining(final String text) {
        for (ILoggingEvent loggingEvent : appender.list) {
            final val exceptionProxy = loggingEvent.getThrowableProxy();
            if (nonNull(exceptionProxy) && exceptionProxy.getMessage().contains(text)) {
                return;
            }
        }
        fail("Could not find log line that matches: " + text);
    }
}
