/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import static com.sportradar.unifiedodds.sdk.testutil.generic.generationassert.DataGenerationAssert.assertThatGeneratesDistinctAndNonNull;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class NamesTest {

    public static class AnyName {

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

    public static class EnglishName {

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

    public static class FrenchName {

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
