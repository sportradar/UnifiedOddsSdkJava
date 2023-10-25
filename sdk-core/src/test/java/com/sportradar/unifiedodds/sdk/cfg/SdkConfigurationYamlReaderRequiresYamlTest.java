/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReaderStubs.anyResourceReader;
import static com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolverStubs.notFindingClass;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.impl.util.javaclass.ClassResolver;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;

public class SdkConfigurationYamlReaderRequiresYamlTest {

    @ParameterizedTest
    @MethodSource
    public void failsIfSnakeYamlDependencyIsNotInClassPath(SdkConfigurationYamlReader reader) {
        assertThatThrownBy(() -> reader.readConfiguration())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Yaml configuration reader dependency missing");
    }

    public static Stream<SdkConfigurationYamlReader> failsIfSnakeYamlDependencyIsNotInClassPath() {
        ClassResolver notResolvingClasses = notFindingClass();
        return Stream.of(
            new SdkConfigurationYamlReader(notResolvingClasses, new Yaml(), anyResourceReader()),
            new SdkConfigurationYamlReader(
                notResolvingClasses,
                new Yaml(),
                anyResourceReader(),
                "anyFileName"
            )
        );
    }
}
