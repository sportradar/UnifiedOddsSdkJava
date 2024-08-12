/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils;

import static com.sportradar.utils.SdkHelper.extractHttpResponseMessage;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class SdkHelperExtractingHttpResponseTest {

    public static final String EMPTY = "";

    @Test
    public void shouldExtractEmptyStringFromNullResponse() {
        assertEquals(EMPTY, extractHttpResponseMessage(null));
    }

    @Test
    public void shouldExtractResponseAsIsWhenItIsEmptyString() {
        assertEquals(EMPTY, extractHttpResponseMessage(EMPTY));
    }

    @Test
    public void shouldExtractResponseAsIs() {
        String response = "givenResponse";

        assertEquals(response, extractHttpResponseMessage(response));
    }

    @Test
    public void shouldExtractContentOfErrorsElementIfSuchIsPresent() {
        String expectedExtract = "<errors>providedErrors</errors>";

        assertEquals(expectedExtract, extractHttpResponseMessage(expectedExtract));
        assertEquals(expectedExtract, extractHttpResponseMessage("preffix" + expectedExtract));
        assertEquals(expectedExtract, extractHttpResponseMessage(expectedExtract + "suffix"));
    }

    @Test
    public void shouldExtractContentOfMessageAndErrorsIfBothArePresent() {
        String errorElement = "<errors>providedErrors</errors>";
        String messageElement = "<message>providedErrors</message>";

        String expectedExtract = errorElement + " (detail: " + messageElement + ")";
        assertEquals(expectedExtract, extractHttpResponseMessage(errorElement + messageElement));
        assertEquals(expectedExtract, extractHttpResponseMessage(messageElement + errorElement));
        assertEquals(
            expectedExtract,
            extractHttpResponseMessage(messageElement + "otherElements" + errorElement)
        );
        assertEquals(
            expectedExtract,
            extractHttpResponseMessage("otherElement" + messageElement + errorElement)
        );
        assertEquals(
            expectedExtract,
            extractHttpResponseMessage(messageElement + errorElement + "oetherElements")
        );
    }

    @Test
    public void shouldKeepMessageAsIsIfErrorElementIsNotPresentEvenIfMessageElementIsPresent() {
        String messageElement = "<message>providedErrors</message>";
        String rawMessage = "some preffix" + messageElement;

        assertEquals(rawMessage, extractHttpResponseMessage(rawMessage));
    }
}
