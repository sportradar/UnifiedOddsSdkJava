/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public final class SportEvents {

    private SportEvents() {}

    public static SportEvent any() {
        return new SportEventStub();
    }

    public static Match anyMatch() {
        return new MatchStub();
    }

    public static Stage anyStage() {
        return new StageStub();
    }
}
