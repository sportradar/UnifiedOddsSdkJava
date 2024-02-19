/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.thread.sleep;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public final class SleepMock {

    private SleepMock() {}

    public static Sleep onSleepDo(VoidCallables.ThrowingRunnable runnable) {
        Sleep sleep = mock(Sleep.class);
        doAnswer(i -> {
                runnable.run();
                return null;
            })
            .when(sleep)
            .millis(anyInt());
        return sleep;
    }
}
