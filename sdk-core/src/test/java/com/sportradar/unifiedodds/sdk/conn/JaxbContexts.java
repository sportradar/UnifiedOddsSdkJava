/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public final class JaxbContexts {

    private JaxbContexts() {}

    public static class SportsApi {

        private static final JAXBContext SPORTS_API_JAXB_CONTEXT;

        static {
            try {
                SPORTS_API_JAXB_CONTEXT = JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel");
            } catch (JAXBException e) {
                throw new IllegalStateException("JAXB contexts creation failed, ex: ", e);
            }
        }

        public static String marshall(Object unmarshalled) {
            StringWriter writer = new StringWriter();
            try {
                SPORTS_API_JAXB_CONTEXT.createMarshaller().marshal(unmarshalled, writer);
                return writer.toString();
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
