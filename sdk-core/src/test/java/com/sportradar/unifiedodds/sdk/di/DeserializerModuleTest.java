/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.name.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@RunWith(Enclosed.class)
public class DeserializerModuleTest {

    public static class JaxbExceptionIsNotSwallowed {

        public static final JAXBException JAXB_EXCEPTION = new JAXBException("root jaxb exception");

        @Test
        public void whenCreatingFeedMessagesJaxbContext() {
            assertThatJaxbExceptionIsNotSwalloedWhenCreatingContextFor("com.sportradar.uf.datamodel");
        }

        @Test
        public void whenCreatingSportsApiJaxbContext() {
            assertThatJaxbExceptionIsNotSwalloedWhenCreatingContextFor(
                "com.sportradar.uf.sportsapi.datamodel"
            );
        }

        @Test
        public void whenCreatingCustomBetApiJaxbContext() {
            assertThatJaxbExceptionIsNotSwalloedWhenCreatingContextFor(
                "com.sportradar.uf.custombet.datamodel"
            );
        }

        private static void assertThatJaxbExceptionIsNotSwalloedWhenCreatingContextFor(String contextPath) {
            try (MockedStatic<JAXBContext> jaxb = Mockito.mockStatic(JAXBContext.class)) {
                jaxb.when(() -> JAXBContext.newInstance(contextPath)).thenThrow(JAXB_EXCEPTION);

                assertThatThrownBy(() -> new DeserializerModule())
                    .isInstanceOf(IllegalStateException.class)
                    .hasRootCause(JAXB_EXCEPTION);
            }
        }
    }
}
