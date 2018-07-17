/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * An implementation of the {@link Deserializer} used to deserialize/unmarshall the provided content
 */
public class DeserializerImpl implements Deserializer {
    private final Unmarshaller unmarshaller;

    public DeserializerImpl(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    @Override
    public synchronized Object deserialize(InputStream inStr) throws DeserializationException {
        try {
            return  JAXBIntrospector.getValue(unmarshaller.unmarshal(inStr));
        } catch (JAXBException e) {
            throw new DeserializationException("There was a problem unmarshalling the provided data", e);
        }
    }
}
