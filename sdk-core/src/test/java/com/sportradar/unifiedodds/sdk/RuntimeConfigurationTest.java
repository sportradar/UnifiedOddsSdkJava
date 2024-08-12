/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RuntimeConfigurationTest {

    public static final String INVALID = "Invalid";

    private RuntimeConfigurationTest() {}

    @Nested
    public class FastHttpClientTimeout {

        @Test
        public void notSetsNull() {
            assertThatThrownBy(() -> RuntimeConfiguration.setFastHttpClientTimeout(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Missing")
                .hasMessageContaining("FastHttpClientTimeout");
        }

        @Test
        public void beEqualOrMoreThanSecond() {
            final int aSecond = 1;
            Duration min = Duration.ofSeconds(aSecond);
            Duration belowMin = min.minusMillis(aSecond);

            RuntimeConfiguration.setFastHttpClientTimeout(min);
            assertThatThrownBy(() -> RuntimeConfiguration.setFastHttpClientTimeout(belowMin))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(INVALID)
                .hasMessageContaining("FastHttpClientTimeout");
        }

        @Test
        public void beEqualOrLessThan30Seconds() {
            final int thirtySeconds = 30;
            Duration max = Duration.ofSeconds(thirtySeconds);
            Duration overMax = max.plusMillis(1);

            RuntimeConfiguration.setFastHttpClientTimeout(max);
            assertThatThrownBy(() -> RuntimeConfiguration.setFastHttpClientTimeout(overMax))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(INVALID)
                .hasMessageContaining("FastHttpClientTimeout");
        }

        @Test
        public void allowToConfigure() {
            final int anyTimeout = 15000;

            RuntimeConfiguration.setFastHttpClientTimeout(Duration.ofMillis(anyTimeout));

            assertThat(RuntimeConfiguration.getFastHttpClientTimeout())
                .isEqualTo(Duration.ofMillis(anyTimeout));
        }
    }

    @Nested
    public class RabbitConnectionTimeout {

        @Nested
        public class MinValue {

            private final int tenSeconds = 10;
            private final int min = tenSeconds;
            private final int belowMin = min - 1;

            @Test
            public void allows10SecondsTimeout() {
                RuntimeConfiguration.setRabbitConnectionTimeout(min);

                assertThat(RuntimeConfiguration.getRabbitConnectionTimeout()).isEqualTo(min);
            }

            @Test
            public void doesNotAllowLowerThan10SecondsTimeout() {
                final int preconfiguredTimeout = 11;
                RuntimeConfiguration.setRabbitConnectionTimeout(preconfiguredTimeout);

                assertThatThrownBy(() -> RuntimeConfiguration.setRabbitConnectionTimeout(belowMin))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(INVALID)
                    .hasMessageContaining("RabbitConnectionTimeout");

                assertThat(RuntimeConfiguration.getRabbitConnectionTimeout()).isEqualTo(preconfiguredTimeout);
            }
        }

        @Nested
        public class MaxValue {

            private final int twoMinutes = 120;
            private final int max = twoMinutes;
            private final int aboveMax = max + 1;

            @Test
            public void allows120SecondsTimeout() {
                RuntimeConfiguration.setRabbitConnectionTimeout(max);

                assertThat(RuntimeConfiguration.getRabbitConnectionTimeout()).isEqualTo(max);
            }

            @Test
            public void doesNotAllowOver120SecondsTimeout() {
                final int preconfiguredTimeout = 11;
                RuntimeConfiguration.setRabbitConnectionTimeout(preconfiguredTimeout);

                assertThatThrownBy(() -> RuntimeConfiguration.setRabbitConnectionTimeout(aboveMax))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(INVALID)
                    .hasMessageContaining("RabbitConnectionTimeout");

                assertThat(RuntimeConfiguration.getRabbitConnectionTimeout()).isEqualTo(preconfiguredTimeout);
            }
        }
    }

    @Nested
    public class RabbitHeartBeat {

        @Nested
        public class MinValue {

            private final int tenSeconds = 10;
            private final int min = tenSeconds;
            private final int belowMin = min - 1;

            @Test
            public void allows10SecondsHeartBeat() {
                RuntimeConfiguration.setRabbitHeartbeat(min);

                assertThat(RuntimeConfiguration.getRabbitHeartbeat()).isEqualTo(min);
            }

            @Test
            public void doesNotAllowLowerThan10SecondsHeartBeat() {
                final int preconfiguredTimeout = 11;
                RuntimeConfiguration.setRabbitHeartbeat(preconfiguredTimeout);

                assertThatThrownBy(() -> RuntimeConfiguration.setRabbitHeartbeat(belowMin))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(INVALID)
                    .hasMessageContaining("RabbitHeartBeat");

                assertThat(RuntimeConfiguration.getRabbitHeartbeat()).isEqualTo(preconfiguredTimeout);
            }
        }

        @Nested
        public class MaxValue {

            private final int twoMinutes = 180;
            private final int max = twoMinutes;
            private final int aboveMax = max + 1;

            @Test
            public void allows180SecondsHeartBeat() {
                RuntimeConfiguration.setRabbitHeartbeat(max);

                assertThat(RuntimeConfiguration.getRabbitHeartbeat()).isEqualTo(max);
            }

            @Test
            public void doesNotAllowHigherThan180SecondsHeartBeat() {
                final int preconfiguredTimeout = 11;
                RuntimeConfiguration.setRabbitHeartbeat(preconfiguredTimeout);

                assertThatThrownBy(() -> RuntimeConfiguration.setRabbitHeartbeat(aboveMax))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(INVALID)
                    .hasMessageContaining("RabbitHeartBeat");

                assertThat(RuntimeConfiguration.getRabbitHeartbeat()).isEqualTo(preconfiguredTimeout);
            }
        }
    }

    @Nested
    public class IgnoreBetPalTimelineSportEventStatus {

        @Test
        public void preservesConfiguredValueToIgnore() {
            RuntimeConfiguration.setIgnoreBetPalTimelineSportEventStatus(true);

            assertThat(RuntimeConfiguration.getIgnoreBetPalTimelineSportEventStatus()).isTrue();
        }

        @Test
        public void preservesConfiguredValueToRespect() {
            RuntimeConfiguration.setIgnoreBetPalTimelineSportEventStatus(false);

            assertThat(RuntimeConfiguration.getIgnoreBetPalTimelineSportEventStatus()).isFalse();
        }
    }
}
