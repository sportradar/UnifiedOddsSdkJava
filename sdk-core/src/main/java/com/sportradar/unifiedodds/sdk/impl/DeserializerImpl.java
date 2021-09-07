/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;

import javax.xml.bind.*;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * An implementation of the {@link Deserializer} used to deserialize/unmarshall the provided content
 */
public class DeserializerImpl implements Deserializer {
    private final ThreadLocal<Unmarshaller> unmarshaller;
    private final ThreadLocal<Marshaller> marshaller;

    public DeserializerImpl(JAXBContext context) {
        Preconditions.checkNotNull(context);

        this.unmarshaller = ThreadLocal.withInitial(() -> {
            try {
                return context.createUnmarshaller();
            } catch (JAXBException e) {
                throw new IllegalStateException("Failed to create unmarshaller", e);
            }
        });
        this.marshaller = ThreadLocal.withInitial(() -> {
            try {
                return context.createMarshaller();
            } catch (JAXBException e) {
                throw new IllegalStateException("Failed to create marshaller", e);
            }
        });
    }

    @Override
    public Object deserialize(InputStream inStr) throws DeserializationException {
        try {
            return  JAXBIntrospector.getValue(unmarshaller.get().unmarshal(inStr));
        } catch (JAXBException e) {
            throw new DeserializationException("There was a problem unmarshalling the provided data", e);
        }
    }

    @Override
    public String serialize(Object inObj) throws DeserializationException {
        try {
            StringWriter writer = new StringWriter();
            marshaller.get().marshal(inObj, writer);
            return writer.toString();
        } catch (JAXBException e) {
            throw new DeserializationException("There was a problem marshaling the provided data", e);
        }
    }

    @Override
    public void unload() {
        if(unmarshaller != null) {
            unmarshaller.remove();
        }
        if (marshaller != null) {
            marshaller.remove();
        }
    }
}
