/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.util.Optional;
import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MessageAndActionExtractor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageAndActionExtractor.class);

    public String parse(InputStream httpResponseContent) {
        Preconditions.checkNotNull(httpResponseContent);
        try {
            Element deserializedResponse = (Element) JAXB.unmarshal(httpResponseContent, Object.class);
            return extractMessageFromXml(deserializedResponse);
        } catch (DataBindingException | ClassCastException e) {
            LOGGER.warn("Failed to parse the response as XML", e);
            return "No specific message";
        }
    }

    private static String extractMessageFromXml(Element element) {
        Optional<String> message = getTextContentFromFirstChildElement(element, "message");
        Optional<String> action = getTextContentFromFirstChildElement(element, "action");
        return Concatenator
            .separatingWith(", ")
            .appendIfNotNull(message.orElse(null))
            .appendIfNotNull(action.orElse(null))
            .retrieve();
    }

    private static Optional<String> getTextContentFromFirstChildElement(Element element, String tagName) {
        NodeList children = element.getElementsByTagName(tagName);
        return Optional
            .of(children)
            .filter(ch -> ch.getLength() > 0)
            .map(ch -> ch.item(0))
            .map(Node::getTextContent);
    }
}
