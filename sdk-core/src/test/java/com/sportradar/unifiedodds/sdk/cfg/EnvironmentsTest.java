/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.Custom;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalIntegration;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalProduction;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.GlobalReplay;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Integration;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Production;
import static com.sportradar.unifiedodds.sdk.cfg.Environment.Replay;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EnvironmentsTest {

    @Test
    public void getsReplayEnvironments() {
        val replayEnvironments = new HashSet<>();
        replayEnvironments.add(Replay);
        replayEnvironments.add(GlobalReplay);

        assertThat(Environments.getReplayEnvironments()).isEqualTo(replayEnvironments);
    }

    @Test
    public void getsNonReplayEnvironments() {
        val nonReplayEnvironments = new HashSet<>();
        nonReplayEnvironments.add(Integration);
        nonReplayEnvironments.add(Production);
        nonReplayEnvironments.add(Custom);
        nonReplayEnvironments.add(GlobalProduction);
        nonReplayEnvironments.add(GlobalIntegration);

        assertThat(Environments.getNonReplayEnvironments()).isEqualTo(nonReplayEnvironments);
    }

    @Nested
    public class AnyEnvironment {

        private final int sampleSize = 100;
        private List<Environment> environments = Stream
            .generate(() -> Environments.any())
            .limit(sampleSize)
            .distinct()
            .collect(Collectors.toList());

        @Test
        public void generatesNotAlwaysTheSame() {
            assertThat(environments).hasSizeGreaterThan(1);
        }

        @Test
        public void doesNotGenerateNull() {
            assertThat(environments).doesNotContainNull();
        }
    }
}
