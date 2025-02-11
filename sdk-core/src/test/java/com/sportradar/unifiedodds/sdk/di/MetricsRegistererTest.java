/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.di.MetricsRegisterer;
import java.util.stream.Stream;
import javax.management.InstanceAlreadyExistsException;
import javax.management.JMException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MetricsRegistererTest {

    @ParameterizedTest
    @MethodSource("jmExceptions")
    public void failingToRegisterMetricsIsNotCriticalHenceSwallowsException(JMException exception)
        throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        MBeanServer server = mock(MBeanServer.class);
        when(server.registerMBean(any(), any())).thenThrow(exception);
        val metricsRegisterer = new MetricsRegisterer(server);

        assertThat(metricsRegisterer.createUnifiedOddsStatistics()).isNotNull();
    }

    private static Stream<JMException> jmExceptions() {
        return Stream.of(
            //new MalformedObjectNameException(),
            new NotCompliantMBeanException(),
            new MBeanRegistrationException(new RuntimeException()),
            new InstanceAlreadyExistsException()
        );
    }
}
