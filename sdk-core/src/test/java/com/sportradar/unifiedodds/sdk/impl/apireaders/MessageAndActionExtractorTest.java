/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.APIPageNotFound;
import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import java.io.InputStream;
import lombok.val;
import org.junit.Test;

public class MessageAndActionExtractorTest {

    private final Deserializer apiDeserializer = mock(Deserializer.class);

    private final MessageAndActionExtractor messageExtractor = new MessageAndActionExtractor(apiDeserializer);

    @Test
    public void extractsMessageFromResponse() throws DeserializationException {
        val message = "some message";
        when(apiDeserializer.deserialize(any())).thenReturn(responseWithMessage(message));

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEqualTo(message);
    }

    @Test
    public void extractsActionAsMessageFromResponse() throws DeserializationException {
        val action = "some action";
        when(apiDeserializer.deserialize(any())).thenReturn(responseWithAction(action));

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEqualTo(action);
    }

    @Test
    public void concatenateMessageAndActionFromResponseIfBothAvailable() throws DeserializationException {
        when(apiDeserializer.deserialize(any())).thenReturn(responseWithMessageAndAction("msg", "action"));

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEqualTo("msg, action");
    }

    @Test
    public void extractsEmptyMessageIfNoMessageReceived() throws DeserializationException {
        when(apiDeserializer.deserialize(any())).thenReturn(new Response());

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEmpty();
    }

    @Test
    public void parsesMessageWhenPageIsNotFound() throws DeserializationException {
        val message = "some message";
        when(apiDeserializer.deserialize(any())).thenReturn(pageNotFoundWithMessage(message));

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEqualTo(message);
    }

    @Test
    public void onParsingUnexpectedClassReturnsItsNameAsPartOfMessage() throws DeserializationException {
        Object unexpectedParsedType = new Object();
        when(apiDeserializer.deserialize(any())).thenReturn(unexpectedParsedType);

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).contains("Unknown").contains("Object");
    }

    @Test
    public void failedParsingIsIndicatedWithMessage() throws DeserializationException {
        when(apiDeserializer.deserialize(any())).thenThrow(DeserializationException.class);

        String parsedMessage = messageExtractor.parse(mock(InputStream.class));

        assertThat(parsedMessage).isEqualTo("No specific message");
    }

    private APIPageNotFound pageNotFoundWithMessage(String message) {
        APIPageNotFound response = new APIPageNotFound();
        response.setMessage(message);
        return response;
    }

    private Response responseWithMessage(String message) {
        Response response = new Response();
        response.setMessage(message);
        return response;
    }

    private Response responseWithAction(String action) {
        Response response = new Response();
        response.setAction(action);
        return response;
    }

    private Response responseWithMessageAndAction(String message, String action) {
        Response response = new Response();
        response.setMessage(message);
        response.setAction(action);
        return response;
    }
}
