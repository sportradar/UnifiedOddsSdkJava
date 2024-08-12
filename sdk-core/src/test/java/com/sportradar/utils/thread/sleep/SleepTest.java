/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.thread.sleep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.LogsMock;
import java.util.Optional;
import lombok.val;
import org.junit.jupiter.api.Test;

public class SleepTest {

    public static final int MILLIS_IN_SECOND = 1000;
    public static final int UNDER_A_SECOND = 800;
    public static final int OVER_A_SECOND = 1500;
    public static final int FOREVER = 30000;

    private ExceptionPreserver exceptionPreserver = new ExceptionPreserver();

    @Test
    public void sleepsMillis() {
        assertThatTakesAroundOneSecond(() -> new Sleep().millis(MILLIS_IN_SECOND));
    }

    @Test
    public void swallowsInterruptedExceptionAndLogsIt() {
        final val logsMock = LogsMock.createCapturingFor(Sleep.class);
        Thread thread = new Thread(() -> new Sleep().millis(FOREVER));

        thread.setUncaughtExceptionHandler(exceptionPreserver);
        thread.start();
        await().until(thread::getState, equalTo(Thread.State.TIMED_WAITING));

        thread.interrupt();
        await().until(() -> logsMock.loggedLineContains("Interrupted"));
        await().until(thread::getState, equalTo(Thread.State.TERMINATED));
        assertThat(exceptionPreserver.exception).isEmpty();
    }

    private void assertThatTakesAroundOneSecond(Runnable task) {
        long before = System.currentTimeMillis();
        task.run();
        long after = System.currentTimeMillis();
        assertThat(after - before).isGreaterThan(UNDER_A_SECOND).isLessThan(OVER_A_SECOND);
    }

    private static class ExceptionPreserver implements Thread.UncaughtExceptionHandler {

        private volatile Optional<Throwable> exception = Optional.empty();

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            exception = Optional.of(e);
        }
    }
}
