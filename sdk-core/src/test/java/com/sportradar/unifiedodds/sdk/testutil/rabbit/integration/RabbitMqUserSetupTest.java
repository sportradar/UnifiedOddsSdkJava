/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqUserSetup.create;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.VhostLocation.at;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.UserInfo;
import com.rabbitmq.http.client.domain.UserPermissions;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;

public class RabbitMqUserSetupTest {

    private final String any = "any";
    private final String username = "John";
    private final String password = "P4ssw0rd";
    private final String virtualHost = "specifiedVirtualHost";
    private final Client rabbitClient = mock(Client.class);

    @Test
    public void shouldNotBeCreatedWithNullVhostLocation() {
        assertThatThrownBy(() -> create(null, rabbitClient))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("vhostLocation");
    }

    @Test
    public void shouldNotBeCreatedWithNullClientCreation() {
        assertThatThrownBy(() -> create(VhostLocation.any(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("rabbitClient");
    }

    @Test
    public void shouldCreateUserIfItDoesNotExist() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);

        userSetup.setupUser(with(username, password));

        verify(rabbitClient).createUser(eq(username), eq(password.toCharArray()), any());
    }

    @Test
    public void shouldUpdateUserIfItDoesExist() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);
        val existingUsers = asList(namedUser(username));
        when(rabbitClient.getUsers()).thenReturn(existingUsers);

        userSetup.setupUser(with(username, password));

        verify(rabbitClient).updateUser(eq(username), eq(password.toCharArray()), any());
    }

    @Test
    public void shouldSetPermissionsForUserToSetUp() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);
        val permissionsCaptor = ArgumentCaptor.forClass(UserPermissions.class);

        userSetup.setupUser(with(username, any));

        verify(rabbitClient).updatePermissions(anyString(), eq(username), permissionsCaptor.capture());
        val permissions = permissionsCaptor.getValue();
        assertThat(permissions.getUser()).isEqualTo(username);
    }

    @Test
    public void shouldCreateVhost() throws Exception {
        val userSetup = create(at(BaseUrl.any(), virtualHost), rabbitClient);

        userSetup.setupUser(with(username, password));

        verify(rabbitClient).createVhost(virtualHost);
    }

    @Test
    public void shouldSetPermissionsOnGivenVirtualHost() throws Exception {
        val userSetup = create(at(BaseUrl.any(), virtualHost), rabbitClient);
        val permissionsCaptor = ArgumentCaptor.forClass(UserPermissions.class);

        userSetup.setupUser(Credentials.any());

        verify(rabbitClient).updatePermissions(eq(virtualHost), any(), permissionsCaptor.capture());
        val permissions = permissionsCaptor.getValue();
        assertThat(permissions.getVhost()).isEqualTo(virtualHost);
    }

    @Test
    public void shouldDeleteCreatedUser() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);

        userSetup.setupUser(with(username, any));
        userSetup.revertChangesMade();

        verify(rabbitClient).deleteUser(eq(username));
    }

    @Test
    public void shouldDeleteUpdatedUserEvenIfItHadExistedBeforeSettingItInitiallyUp() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);
        val existingUsers = asList(namedUser(username));
        when(rabbitClient.getUsers()).thenReturn(existingUsers);

        userSetup.setupUser(with(username, any));
        userSetup.revertChangesMade();

        verify(rabbitClient).deleteUser(eq(username));
    }

    @Test
    public void shouldNotDeleteDeletedUserWhenRevertingTwice() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);

        userSetup.setupUser(with(username, any));
        userSetup.revertChangesMade();
        userSetup.revertChangesMade();

        verify(rabbitClient, times(1)).deleteUser(eq(username));
    }

    @Test
    public void shouldNotDeleteAnyUserIfNoneWasEverSetUp() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);

        userSetup.revertChangesMade();

        verify(rabbitClient, times(0)).deleteUser(nullable(String.class));
    }

    @ParameterizedTest
    @CsvSource({ "user1, user2" })
    public void shouldDelete2CreatedUsers(final String username1, final String username2) throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);
        userSetup.setupUser(with(username1, any));
        userSetup.setupUser(with(username2, any));

        userSetup.revertChangesMade();

        verify(rabbitClient).deleteUser(eq(username1));
        verify(rabbitClient).deleteUser(eq(username2));
    }

    @Test
    public void shouldDeleteTheUserOnceEvenIfItWasSetUpMultipleTimes() throws Exception {
        val userSetup = create(VhostLocation.any(), rabbitClient);
        userSetup.setupUser(with(username, any));
        userSetup.setupUser(with(username, any));

        userSetup.revertChangesMade();

        verify(rabbitClient, times(1)).deleteUser(eq(username));
    }

    @Test
    public void shouldDeleteCreatedVhost() throws Exception {
        val userSetup = create(VhostLocation.at(BaseUrl.any(), virtualHost), rabbitClient);

        userSetup.setupUser(with(username, password));
        userSetup.revertChangesMade();

        verify(rabbitClient).deleteVhost(virtualHost);
    }

    @ParameterizedTest
    @CsvSource({ "user1, user2" })
    public void shouldDeleteVhostOnceEvenIfMultipleUsersAreDeleted(final String user1, final String user2)
        throws Exception {
        val userSetup = create(VhostLocation.at(BaseUrl.any(), virtualHost), rabbitClient);

        userSetup.setupUser(with(user1, password));
        userSetup.setupUser(with(user2, password));
        userSetup.revertChangesMade();

        verify(rabbitClient, times(1)).deleteVhost(virtualHost);
    }

    @Test
    public void shouldNotDeleteDeletedVhostWhenRevertingMultipleTimes() throws Exception {
        val userSetup = create(VhostLocation.at(BaseUrl.any(), virtualHost), rabbitClient);

        userSetup.setupUser(with(username, password));
        userSetup.revertChangesMade();
        userSetup.revertChangesMade();

        verify(rabbitClient, times(1)).deleteVhost(virtualHost);
    }

    private UserInfo namedUser(final String name) {
        val user = mock(UserInfo.class);
        when(user.getName()).thenReturn(name);
        return user;
    }
}
