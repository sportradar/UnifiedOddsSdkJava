/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.BlockedCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.UnblockedCallback;
import lombok.val;
import org.junit.jupiter.api.Test;

public class SdkConnectionTest {

    private final Connection underlying = mock(Connection.class);
    private final Connection connection = new SdkConnection(underlying);

    @Test
    public void delegatesClientProvidedName() {
        val name = "underlying connection name";
        when(underlying.getClientProvidedName()).thenReturn(name);

        assertThat(connection.getClientProvidedName()).isEqualTo(name);
    }

    @Test
    public void delegatesBlockedCallbackListener() {
        val blockedCallback = mock(BlockedCallback.class);
        val unBlockedCallback = mock(UnblockedCallback.class);

        connection.addBlockedListener(blockedCallback, unBlockedCallback);

        verify(underlying).addBlockedListener(blockedCallback, unBlockedCallback);
    }

    @Test
    public void delegatesSettingConnectionId() {
        val id = "connection id";
        connection.setId(id);

        verify(underlying).setId(id);
    }

    @Test
    public void getsIdOfUnderlyingConnection() {
        val id = "connection id";
        when(underlying.getId()).thenReturn(id);

        assertThat(connection.getId()).isEqualTo(id);
    }
}
