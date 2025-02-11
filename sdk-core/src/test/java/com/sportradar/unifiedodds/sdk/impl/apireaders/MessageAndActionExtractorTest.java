/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Charsets;
import com.sportradar.uf.custombet.datamodel.CapiResponse;
import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.bind.JAXB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class MessageAndActionExtractorTest {

    private final MessageAndActionExtractor messageExtractor = new MessageAndActionExtractor();

    @ParameterizedTest
    @ValueSource(
        strings = {
            "No data for the event: sr:match:123:prematch",
            "No data for the event: sr:match:123:prematch, bookmakerId: 16281",
        }
    )
    public void extractsMessageFromCustomBetResponse(String message) {
        String parsedMessage = messageExtractor.parse(customBetResponse(message));

        assertThat(parsedMessage).isEqualTo(message);
    }

    @ParameterizedTest
    @ValueSource(strings = { "Content not found: null", "some other message" })
    public void extractsMessageFromSportsApiResponse(String message) {
        String parsedMessage = messageExtractor.parse(sportsApiResponse(message));

        assertThat(parsedMessage).isEqualTo(message);
    }

    @ParameterizedTest
    @ValueSource(strings = { "missing info", "no such player" })
    public void concatenateMessageAndActionFromResponseIfBothAvailable(String action) {
        String parsedMessage = messageExtractor.parse(sportsApiResponse("msg for " + action, action));

        assertThat(parsedMessage).isEqualTo("msg for " + action + ", " + action);
    }

    @Test
    public void extractsEmptyMessageIfNoMessageReceivedForSportsApi() {
        String parsedMessage = messageExtractor.parse(sportsApiResponse(null));

        assertThat(parsedMessage).isEmpty();
    }

    @Test
    public void extractsEmptyMessageIfNoMessageReceivedForCustomBetApi() {
        String parsedMessage = messageExtractor.parse(customBetResponse(null));

        assertThat(parsedMessage).isEmpty();
    }

    @Test
    public void failedParsingIsIndicatedWithMessage() {
        String parsedMessage = messageExtractor.parse(jsonResponse());

        assertThat(parsedMessage).isEqualTo("No specific message");
    }

    private InputStream customBetResponse(String message) {
        CapiResponse response = new CapiResponse();
        response.setMessage(message);
        return asInputStream(response);
    }

    private InputStream sportsApiResponse(String message) {
        Response response = new Response();
        response.setMessage(message);
        return asInputStream(response);
    }

    private InputStream sportsApiResponse(String message, String action) {
        Response response = new Response();
        response.setMessage(message);
        response.setAction(action);
        return asInputStream(response);
    }

    private static ByteArrayInputStream asInputStream(Object response) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JAXB.marshal(response, output);
        return new ByteArrayInputStream(output.toByteArray());
    }

    private InputStream jsonResponse() {
        String response = "{\"a\": 1111}";
        return new ByteArrayInputStream(response.getBytes(Charsets.UTF_8));
    }
}
