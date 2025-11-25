/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.internal.di.MetricsRegisterer;
import java.util.stream.Stream;
import javax.management.*;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MetricsRegistererTest {

    @ParameterizedTest
    @MethodSource("jmExceptions")
    void failingToRegisterMetricsIsNotCriticalHenceSwallowsException(JMException exception) throws Exception {
        val server = mock(MBeanServer.class);
        when(server.registerMBean(any(), any())).thenThrow(exception);
        try (val metricsRegisterer = new MetricsRegisterer(server)) {
            assertThat(metricsRegisterer.createUnifiedOddsStatistics()).isNotNull();
        }
    }

    @Nested
    class Close {

        @Test
        void closingRemovesSdkBean() throws Exception {
            val sdkStatisticsObjectName = sdkStatisticsObjectName();
            val server = mock(MBeanServer.class);
            when(server.isRegistered(sdkStatisticsObjectName)).thenReturn(true);

            new MetricsRegisterer(server).close();

            verify(server).unregisterMBean(sdkStatisticsObjectName);
        }

        @Test
        void closingDoesNothingIfSdkBeanDidNotExist() throws Exception {
            val sdkStatisticsObjectName = sdkStatisticsObjectName();
            val server = mock(MBeanServer.class);
            when(server.isRegistered(any())).thenReturn(false);

            new MetricsRegisterer(server).close();

            verify(server, never()).unregisterMBean(sdkStatisticsObjectName);
        }

        private ObjectName sdkStatisticsObjectName() throws MalformedObjectNameException {
            return new ObjectName("com.sportradar.unifiedodds.sdk.impl:type=UnifiedOdds");
        }
    }

    private static Stream<JMException> jmExceptions() {
        return Stream.of(
            new NotCompliantMBeanException(),
            new MBeanRegistrationException(new RuntimeException()),
            new InstanceAlreadyExistsException()
        );
    }
}
