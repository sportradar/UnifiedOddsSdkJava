/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.sportradar.unifiedodds.sdk.testutil.generic.functional.ThrowingFunction;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RabbitMqClientFactoryTest {

    private final String any = "any";

    private final Client rabbit = mock(Client.class);
    private final ThrowingFunction<ClientParameters, Client> createClient = parameters -> rabbit;

    @Test
    public void shouldNotBeCreatedWithNullHostLocation() {
        assertThatThrownBy(() -> createRabbitMqClient(null, Credentials.any(), createClient))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("host");
    }

    @Test
    public void shouldNotBeCreatedWithNullAdminCredentials() {
        assertThatThrownBy(() -> createRabbitMqClient(any, null, createClient))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("adminCredentials");
    }

    @Test
    public void shouldNotBeCreatedWithNullClientCreation() {
        assertThatThrownBy(() -> createRabbitMqClient(any, Credentials.any(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("createClient");
    }

    @Test
    public void shouldCreateAdminConnection() throws Exception {
        val adminCredentials = with("adminUsername", "adminPassword");
        val host = "specifiedHost";

        createRabbitMqClient(
            host,
            adminCredentials,
            params -> {
                assertEquals(adminCredentials.getUsername(), params.getUsername());
                assertEquals(adminCredentials.getPassword(), params.getPassword());
                assertEquals(host, params.getUrl().getHost());
                return rabbit;
            }
        );
    }

    @Test
    public void shouldUseCreationFunctionToCreate() throws Exception {
        val adminCredentials = with("adminUsername", "adminPassword");
        val host = "specifiedHost";

        val actualClient = createRabbitMqClient(host, adminCredentials, params -> rabbit);

        assertEquals(rabbit, actualClient);
    }
}
