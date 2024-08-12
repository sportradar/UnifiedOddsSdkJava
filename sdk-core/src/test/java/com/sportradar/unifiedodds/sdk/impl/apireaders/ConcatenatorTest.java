/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ConcatenatorTest {

    @Nested
    public class ConcatenatingInts {

        public static final int ELEVEN = 11;
        public static final int TWENTY_TWO = 22;
        public static final int THIRTY_THREE = 33;
        private Concatenator concatenator = Concatenator.separatingWith(",");

        @Test
        public void startWithEmptyString() {
            assertThat(concatenator.retrieve()).isEmpty();
        }

        @Test
        public void appendsSingleString() {
            Integer singleInt = 1;
            assertThat(concatenator.appendIfNotNull(singleInt).retrieve()).isEqualTo(singleInt + "");
        }

        @Test
        public void staysAtEmptyStringAfterAttemptingToAppendNull() {
            assertThat(concatenator.appendIfNotNull((Integer) null).retrieve()).isEmpty();
        }

        @Test
        public void separateMultipleStringsWithCommas() {
            Integer first = ELEVEN;
            Integer second = TWENTY_TWO;
            assertThat(concatenator.appendIfNotNull(first).appendIfNotNull(second).retrieve())
                .isEqualTo("11,22");
        }

        @Test
        public void staysAtFirstStringAfterAttemptingToAppendNull() {
            Integer first = ELEVEN;
            assertThat(concatenator.appendIfNotNull(first).appendIfNotNull((Integer) null).retrieve())
                .isEqualTo(first + "");
        }

        @Test
        public void keepsOnlySecondStringIfFirstIsNull() {
            Integer second = TWENTY_TWO;
            assertThat(concatenator.appendIfNotNull((Integer) null).appendIfNotNull(second).retrieve())
                .isEqualTo(second + "");
        }

        @Test
        public void doesNotAppendThirdSentenceIfItIsNull() {
            Integer first = ELEVEN;
            Integer second = TWENTY_TWO;
            assertThat(
                concatenator
                    .appendIfNotNull(first)
                    .appendIfNotNull(second)
                    .appendIfNotNull((Integer) null)
                    .retrieve()
            )
                .isEqualTo("11,22");
        }

        @Test
        public void contunuesAppendingEvenSomeInTheMiddleAreNull() {
            Integer first = ELEVEN;
            Integer third = THIRTY_THREE;
            assertThat(
                concatenator
                    .appendIfNotNull(first)
                    .appendIfNotNull((Integer) null)
                    .appendIfNotNull(third)
                    .retrieve()
            )
                .isEqualTo("11,33");
        }
    }

    @Nested
    public class ConcatenatingStrings {

        private Concatenator concatenator = Concatenator.separatingWith(",");

        @Test
        public void startWithEmptyString() {
            assertThat(concatenator.retrieve()).isEmpty();
        }

        @Test
        public void appendsSingleString() {
            String singleString = "some message";
            assertThat(concatenator.appendIfNotNull(singleString).retrieve()).isEqualTo(singleString);
        }

        @Test
        public void staysAtEmptyStringAfterAttemptingToAppendNull() {
            assertThat(concatenator.appendIfNotNull((String) null).retrieve()).isEmpty();
        }

        @Test
        public void separateMultipleStringsWithCommas() {
            String first = "sentence 1";
            String second = "sentence 2";
            assertThat(concatenator.appendIfNotNull(first).appendIfNotNull(second).retrieve())
                .isEqualTo("sentence 1,sentence 2");
        }

        @Test
        public void staysAtFirstStringAfterAttemptingToAppendNull() {
            String first = "phrase 1";
            assertThat(concatenator.appendIfNotNull(first).appendIfNotNull((String) null).retrieve())
                .isEqualTo(first);
        }

        @Test
        public void keepsOnlySecondStringIfFirstIsNull() {
            String second = "phrase 2";
            assertThat(concatenator.appendIfNotNull((String) null).appendIfNotNull(second).retrieve())
                .isEqualTo(second);
        }

        @Test
        public void doesNotAppendThirdSentenceIfItIsNull() {
            String first = "word 1";
            String second = "word 2";
            assertThat(
                concatenator
                    .appendIfNotNull(first)
                    .appendIfNotNull(second)
                    .appendIfNotNull((String) null)
                    .retrieve()
            )
                .isEqualTo("word 1,word 2");
        }

        @Test
        public void contunuesAppendingEvenSomeInTheMiddleAreNull() {
            String first = "snippet 1";
            String third = "snippet 3";
            assertThat(
                concatenator
                    .appendIfNotNull(first)
                    .appendIfNotNull((String) null)
                    .appendIfNotNull(third)
                    .retrieve()
            )
                .isEqualTo("snippet 1,snippet 3");
        }
    }

    @Nested
    public class Separator {

        @Test
        public void separateWithDash() {
            String first = "snippet 1";
            String second = "snippet 2";
            assertThat(
                Concatenator.separatingWith("-").appendIfNotNull(first).appendIfNotNull(second).retrieve()
            )
                .isEqualTo("snippet 1-snippet 2");
        }

        @Test
        public void separateWithCommaSpace() {
            String first = "snippet 1";
            String second = "snippet 2";
            assertThat(
                Concatenator.separatingWith(", ").appendIfNotNull(first).appendIfNotNull(second).retrieve()
            )
                .isEqualTo("snippet 1, snippet 2");
        }
    }
}
