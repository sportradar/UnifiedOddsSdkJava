/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.internal.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.internal.impl.DeserializerImpl;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class DeserializerModule implements Module {

    private final JAXBContext messagesJaxbContext;

    private final JAXBContext sportsApiJaxbContext;

    private final JAXBContext customBetApiJaxbContext;

    public DeserializerModule() {
        try {
            messagesJaxbContext = JAXBContext.newInstance("com.sportradar.uf.datamodel");
            sportsApiJaxbContext = JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel");
            customBetApiJaxbContext = JAXBContext.newInstance("com.sportradar.uf.custombet.datamodel");
        } catch (JAXBException e) {
            throw new IllegalStateException("JAXB contexts creation failed, ex: ", e);
        }
    }

    @Override
    public void configure(Binder binder) {}

    @Provides
    @Named("MessageJAXBContext")
    private JAXBContext provideMessageJaxbContext() {
        return messagesJaxbContext;
    }

    @Provides
    @Named("SportsApiJaxbDeserializer")
    private Deserializer provideSportsApiJaxbDeserializer() {
        return new DeserializerImpl(sportsApiJaxbContext);
    }

    @Provides
    @Named("CustomBetApiJaxbDeserializer")
    private Deserializer provideCustomBetApiJaxbDeserializer() {
        return new DeserializerImpl(customBetApiJaxbContext);
    }

    @Provides
    @Named("MessageDeserializer")
    private Deserializer provideMessageDeserializer() {
        return new DeserializerImpl(messagesJaxbContext);
    }
}
