/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.stream.Collectors;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class LogsAssert extends AbstractAssert<LogsAssert, ListAppender<ILoggingEvent>> {

    private LogsAssert(ListAppender<ILoggingEvent> logs) {
        super(logs, LogsAssert.class);
    }

    public static LogsAssert assertThat(ListAppender<ILoggingEvent> logs) {
        return new LogsAssert(logs);
    }

    public LogsAssert hasLogLineContaining(String logLine) {
        val logs =
            this.actual().list.stream().map(ILoggingEvent::getFormattedMessage).collect(Collectors.toList());

        Assertions
            .assertThat(logs)
            .anyMatch(s -> s.contains(logLine), "Logs are expected to contain " + logLine);

        return this;
    }
}
