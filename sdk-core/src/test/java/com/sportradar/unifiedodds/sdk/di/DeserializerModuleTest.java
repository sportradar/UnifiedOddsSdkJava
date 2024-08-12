/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class DeserializerModuleTest {

    @Nested
    public class JaxbExceptionIsNotSwallowed {

        private final JAXBException jaxbException = new JAXBException("root jaxb exception");

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

        private void assertThatJaxbExceptionIsNotSwalloedWhenCreatingContextFor(String contextPath) {
            try (MockedStatic<JAXBContext> jaxb = Mockito.mockStatic(JAXBContext.class)) {
                jaxb.when(() -> JAXBContext.newInstance(contextPath)).thenThrow(jaxbException);

                assertThatThrownBy(() -> new DeserializerModule())
                    .isInstanceOf(IllegalStateException.class)
                    .hasRootCause(jaxbException);
            }
        }
    }
}
