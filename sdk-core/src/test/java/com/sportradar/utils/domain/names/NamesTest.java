/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import static com.sportradar.unifiedodds.sdk.testutil.generic.generationassert.DataGenerationAssert.assertThatGeneratesDistinctAndNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NamesTest {

    @Nested
    public class AnyName {

        @Test
        public void generatedNotEmptyName() {
            String name = Names.any();

            assertThat(name).isNotNull();
            assertThat(name).isNotEmpty();
        }

        @Test
        public void generatesDynamicNonNullIds() {
            assertThatGeneratesDistinctAndNonNull(() -> Names.any());
        }
    }

    @Nested
    public class EnglishName {

        @Test
        public void generatedNameStartingWithLiteralEnglish() {
            String name = Names.anyEnglish();

            assertThat(name).startsWith("English ");
        }

        @Test
        public void generatesDynamicNonNullIds() {
            assertThatGeneratesDistinctAndNonNull(() -> Names.anyEnglish());
        }
    }

    @Nested
    public class FrenchName {

        @Test
        public void generatedNameStartingWithLiteralFrench() {
            String name = Names.anyFrench();

            assertThat(name).startsWith("French ");
        }

        @Test
        public void generatesDynamicNonNullIds() {
            assertThatGeneratesDistinctAndNonNull(() -> Names.anyFrench());
        }
    }
}
