/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.APIPageNotFound;
import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import java.io.InputStream;

public class MessageAndActionExtractor {

    private Deserializer apiDeserializer;

    @Inject
    public MessageAndActionExtractor(@Named("SportsApiJaxbDeserializer") Deserializer apiDeserializer) {
        Preconditions.checkNotNull(apiDeserializer);

        this.apiDeserializer = apiDeserializer;
    }

    public String parse(InputStream httpResponseContent) {
        Preconditions.checkNotNull(apiDeserializer);
        Preconditions.checkNotNull(httpResponseContent);

        String errMsg;
        try {
            Object deserializedResponse = apiDeserializer.deserialize(httpResponseContent);
            if (deserializedResponse instanceof APIPageNotFound) {
                errMsg = ((APIPageNotFound) deserializedResponse).getMessage();
            } else if (deserializedResponse instanceof Response) {
                Response response = (Response) deserializedResponse;
                errMsg =
                    Concatenator
                        .separatingWith(", ")
                        .appendIfNotNull(response.getMessage())
                        .appendIfNotNull(response.getAction())
                        .retrieve();
            } else {
                errMsg = "Unknown response format, " + deserializedResponse.getClass().getName();
            }
        } catch (DeserializationException e) {
            errMsg = "No specific message";
        }
        return errMsg;
    }
}
