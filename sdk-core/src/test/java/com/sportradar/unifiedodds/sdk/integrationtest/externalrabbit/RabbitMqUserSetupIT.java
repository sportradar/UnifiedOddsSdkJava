/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit;

import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

import com.rabbitmq.client.*;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation;
import java.io.IOException;
import java.util.concurrent.*;
import lombok.val;
import org.junit.After;
import org.junit.Test;

public class RabbitMqUserSetupIT {

    public static final String DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE = Constants.ADMIN_USERNAME;
    public static final String DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE = Constants.ADMIN_PASSWORD;
    private final Credentials adminCredentials = Credentials.with(
        DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE,
        DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE
    );
    private final Credentials producerCredentials = Credentials.with("producer1", "producer1_P4ssw0rd");

    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, "/testhost");
    private final Client rabbitClient = createRabbitMqClient(
        vhostLocation.getBaseUrl().getHost(),
        adminCredentials,
        Client::new
    );

    private final RabbitMqUserSetup mqUsers = RabbitMqUserSetup.create(vhostLocation, rabbitClient);

    public RabbitMqUserSetupIT() throws Exception {}

    @After
    public void deleteCreatedResources() {
        mqUsers.revertChangesMade();
    }

    @Test
    public void connectionShouldNotBeCreatedForNonExistentUser() {
        assertThatThrownBy(() -> createConnection(producerCredentials))
            .isInstanceOf(AuthenticationFailureException.class);
    }

    @Test
    public void connectionShouldNotBeCreatedForUserWithWrongPassword() {
        mqUsers.setupUser(producerCredentials);

        val wrongPassword = Credentials.with(producerCredentials.getUsername(), "mistypedForAuthToFail");
        assertThatThrownBy(() -> createConnection(wrongPassword))
            .isInstanceOf(AuthenticationFailureException.class);
    }

    @Test
    public void shouldEnableConnectionCreationForSetUpUser() throws Exception {
        mqUsers.setupUser(producerCredentials);

        assertNotNull(createConnection(producerCredentials));
    }

    @Test
    public void settingTheUserTwiceShouldUpdateTheirPassword() {
        val wrongPassword = Credentials.with(producerCredentials.getUsername(), "mistypedForAuthToFail");

        mqUsers.setupUser(wrongPassword);
        mqUsers.setupUser(producerCredentials);

        assertThatThrownBy(() -> createConnection(wrongPassword))
            .isInstanceOf(AuthenticationFailureException.class);
    }

    @Test
    public void shouldDisableConnectionCreationsForRevertedUser() {
        mqUsers.setupUser(producerCredentials);
        mqUsers.revertChangesMade();

        assertThatThrownBy(() -> createConnection(producerCredentials))
            .isInstanceOf(AuthenticationFailureException.class);
    }

    private Connection createConnection(Credentials credentials) throws IOException, TimeoutException {
        final Connection connection;
        val factory = new ConnectionFactory();
        factory.setVirtualHost(vhostLocation.getVirtualHostname());
        factory.setHost(vhostLocation.getBaseUrl().getHost());
        factory.setUsername(credentials.getUsername());
        factory.setPassword(credentials.getPassword());
        connection = factory.newConnection();
        return connection;
    }
}
