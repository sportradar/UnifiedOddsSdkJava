/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.ClientParameters;
import com.rabbitmq.http.client.domain.UserPermissions;
import com.sportradar.unifiedodds.sdk.testutil.generic.functional.ThrowingFunction;
import java.util.*;
import lombok.NonNull;
import lombok.val;

public class RabbitMqUserSetup {

    private final VhostLocation vhostLocation;
    private final Client rabbitClient;
    private final Set<String> createdUsernames = new HashSet<>();

    private RabbitMqUserSetup(final VhostLocation vhostLocation, final Client rabbitClient) {
        this.vhostLocation = vhostLocation;
        this.rabbitClient = rabbitClient;
    }

    public static RabbitMqUserSetup create(
        @NonNull final VhostLocation vhostLocation,
        @NonNull Client rabbitClient
    ) throws Exception {
        return new RabbitMqUserSetup(vhostLocation, rabbitClient);
    }

    public void revertChangesMade() {
        if (createdUsernames.size() > 0) {
            deleteModifiedResources();
        }
        createdUsernames.clear();
    }

    private void deleteModifiedResources() {
        createdUsernames.stream().forEach(u -> rabbitClient.deleteUser(u));
        rabbitClient.deleteVhost(vhostLocation.getVirtualHostname());
    }

    public void setupUser(Credentials credentialsToSetup) {
        createdUsernames.add(credentialsToSetup.getUsername());

        upsertUser(credentialsToSetup);
        rabbitClient.createVhost(vhostLocation.getVirtualHostname());
        grantAdminPermissions(credentialsToSetup.getUsername());
    }

    private void grantAdminPermissions(String username) {
        val permissions = new UserPermissions();
        permissions.setUser(username);
        permissions.setVhost(vhostLocation.getVirtualHostname());
        permissions.setConfigure(".*");
        permissions.setRead(".*");
        permissions.setWrite(".*");
        rabbitClient.updatePermissions(vhostLocation.getVirtualHostname(), username, permissions);
    }

    private void upsertUser(final Credentials userToSetup) {
        val username = userToSetup.getUsername();
        val password = userToSetup.getPassword();
        if (rabbitClient.getUsers().stream().noneMatch(a -> a.getName().equals(username))) {
            rabbitClient.createUser(username, password.toCharArray(), Collections.singletonList("user"));
        } else {
            rabbitClient.updateUser(username, password.toCharArray(), Collections.singletonList("user"));
        }
    }
}
