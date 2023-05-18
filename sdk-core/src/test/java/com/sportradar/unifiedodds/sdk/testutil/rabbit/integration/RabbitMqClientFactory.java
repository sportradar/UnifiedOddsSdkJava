/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.sportradar.unifiedodds.sdk.testutil.generic.functional.ThrowingFunction;
import lombok.NonNull;
import lombok.val;

public class RabbitMqClientFactory {

    private RabbitMqClientFactory() {}

    public static Client createRabbitMqClient(
        @NonNull final String host,
        @NonNull final Credentials adminCredentials,
        @NonNull final ThrowingFunction<ClientParameters, Client> createClient
    ) throws Exception {
        val parameters = new ClientParameters()
            .url("http://" + host + ":15672/api/")
            .username(adminCredentials.getUsername())
            .password(adminCredentials.getPassword());
        return createClient.apply(parameters);
    }
}
