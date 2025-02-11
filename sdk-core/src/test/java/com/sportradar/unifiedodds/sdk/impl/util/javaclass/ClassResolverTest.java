/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.util.javaclass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.internal.impl.util.javaclass.ClassResolver;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;

public class ClassResolverTest {

    @Test
    public void resolvesSnakeYamlClass() {
        assertThat(new ClassResolver().resolveByName("org.yaml.snakeyaml.Yaml")).isEqualTo(Yaml.class);
    }

    @ParameterizedTest
    @MethodSource("ensuresStubThrowsSameException")
    public void doesNotResolveClassWhichIsNotInTheClassPath(ClassResolver classResolver) {
        assertThatThrownBy(() -> classResolver.resolveByName("com.invented.ClassName"))
            .isInstanceOf(IllegalStateException.class);
    }

    private static Stream<ClassResolver> ensuresStubThrowsSameException() {
        return Stream.of(new ClassResolver(), ClassResolverStubs.notFindingClass());
    }
}
