/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;

public class LogsMockTest {

    private static final String SPECIFIED = "specified";
    private static final String LOG = "Log";
    private static final String LINE = "Line";

    private static final String SPECIFIED_LOG_LINE = SPECIFIED + LOG + LINE;
    private static final String UNRELATED_LOG_LINE = "unrelatedLogLine";
    private static final String ANY = "any";
    private static final String COULD_NOT_FIND_LOG_LINE_THAT_MATCHES = "Could not find log line that matches";

    private LogsMockTest() {}

    @Nested
    public class VerifyLoggedLineContaining {

        @Test
        public void shouldStartCaptureLogsForGivenClass() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            logsMock.verifyLoggedLineContaining(SPECIFIED_LOG_LINE);
            assertThat(logsMock.loggedLineContains(SPECIFIED_LOG_LINE)).isTrue();
        }

        @Test
        public void shouldNotCaptureLogsForUnrelatedClass() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(UnrelatedClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() -> logsMock.verifyLoggedLineContaining(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
            assertThat(logsMock.loggedLineContains(SPECIFIED_LOG_LINE)).isFalse();
        }

        @Test
        public void shouldFailToValidateIfRequestedLogNotPresent() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() -> logsMock.verifyLoggedLineContaining("requestedText"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
            assertThat(logsMock.loggedLineContains("requestedText")).isFalse();
        }

        @ParameterizedTest
        @CsvSource(
            { SPECIFIED_LOG_LINE + ", " + UNRELATED_LOG_LINE, UNRELATED_LOG_LINE + ", " + SPECIFIED_LOG_LINE }
        )
        public void shouldVerifyPresenceOfLogsInQuestionInMultilineLogStream(
            final String line1,
            final String line2
        ) {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(line1);
            LoggerFactory.getLogger(TargetClass.class).info(line2);

            logsMock.verifyLoggedLineContaining(SPECIFIED_LOG_LINE);
            assertThat(logsMock.loggedLineContains(SPECIFIED_LOG_LINE)).isTrue();
        }
    }

    @Nested
    public class VerifyLoggedLineContainingAll {

        @Test
        public void shouldStartCaptureLogsForGivenClass() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            logsMock.verifyLoggedLineContainingAll(SPECIFIED_LOG_LINE);
        }

        @Test
        public void shouldNotCaptureLogsForUnrelatedClass() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(UnrelatedClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() -> logsMock.verifyLoggedLineContainingAll(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
        }

        @Test
        public void shouldFailtToValidateIfRequestedLogNotPresent() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() -> logsMock.verifyLoggedLineContainingAll("requestedText"))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
        }

        @Test
        public void shouldFailtToValidateIfOnlyPartOrRequestedLogLineIsPresent() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() ->
                    logsMock.verifyLoggedLineContainingAll(SPECIFIED, LOG, LINE, "somethingElse")
                )
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
        }

        @ParameterizedTest
        @CsvSource(
            { SPECIFIED_LOG_LINE + ", " + UNRELATED_LOG_LINE, UNRELATED_LOG_LINE + ", " + SPECIFIED_LOG_LINE }
        )
        public void shouldVerifyPresenceOfLogsInQuestionInMultilineLog(
            final String line1,
            final String line2
        ) {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(line1);
            LoggerFactory.getLogger(TargetClass.class).info(line2);

            logsMock.verifyLoggedLineContainingAll(SPECIFIED_LOG_LINE);
        }

        @Test
        public void shouldAlwaysVerifySuccessfullyIfNoRequestedTextIsProvided() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            logsMock.verifyLoggedLineContainingAll();
        }
    }

    @Nested
    public class VerifyLoggedExceptionMessageContaining {

        @Test
        public void shouldVerifyExceptionMessageLoggedContainsSpecifiedText() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(ANY, new RuntimeException(SPECIFIED_LOG_LINE));

            logsMock.verifyLoggedExceptionMessageContaining(SPECIFIED_LOG_LINE);
        }

        @ParameterizedTest
        @CsvSource(
            { SPECIFIED_LOG_LINE + ", " + UNRELATED_LOG_LINE, UNRELATED_LOG_LINE + ", " + SPECIFIED_LOG_LINE }
        )
        public void shouldVerifyExceptionMessageLoggedContainsSpecifiedTextInMultilineLogStream(
            final String line1,
            final String line2
        ) {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(ANY, new RuntimeException(line1));
            LoggerFactory.getLogger(TargetClass.class).info(ANY, new RuntimeException(line2));

            logsMock.verifyLoggedExceptionMessageContaining(SPECIFIED_LOG_LINE);
        }

        @Test
        public void shouldFailToVerifyExceptionMessageLoggedContainsSpecifiedTextIfItDoesNot() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(ANY, new RuntimeException(UNRELATED_LOG_LINE));

            assertThatThrownBy(() -> logsMock.verifyLoggedExceptionMessageContaining(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
        }

        @Test
        public void failsToVerifyExceptionMessageLoggedContainsSpecifiedTextIfSomeLogLinesDoesNotContainException() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(ANY);

            assertThatThrownBy(() -> logsMock.verifyLoggedExceptionMessageContaining(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining(COULD_NOT_FIND_LOG_LINE_THAT_MATCHES);
        }
    }

    @Nested
    public class VerifyNotLoggedLineContaining {

        @Test
        public void shouldVerifyAbsenceOfLogLineInQuestion() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(UNRELATED_LOG_LINE);

            logsMock.verifyNotLoggedLineContaining(SPECIFIED_LOG_LINE);
        }

        @Test
        public void shouldFailToVerifyAbsenceOfLogLineInQuestionIfItWasPresent() {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(SPECIFIED_LOG_LINE);

            assertThatThrownBy(() -> logsMock.verifyNotLoggedLineContaining(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Could find log line that matches");
        }

        @ParameterizedTest
        @CsvSource(
            { SPECIFIED_LOG_LINE + ", " + UNRELATED_LOG_LINE, UNRELATED_LOG_LINE + ", " + SPECIFIED_LOG_LINE }
        )
        public void shouldFailToVerifyAbsenceOfLogLineInQuestionIfItWasPresentInMultiLineLogStream(
            final String line1,
            final String line2
        ) {
            final val logsMock = LogsMock.createCapturingFor(TargetClass.class);

            LoggerFactory.getLogger(TargetClass.class).info(line1);
            LoggerFactory.getLogger(TargetClass.class).info(line2);

            assertThatThrownBy(() -> logsMock.verifyNotLoggedLineContaining(SPECIFIED_LOG_LINE))
                .isInstanceOf(AssertionError.class)
                .hasMessageContaining("Could find log line that matches");
        }
    }

    public class TargetClass {}

    public class UnrelatedClass {}
}
