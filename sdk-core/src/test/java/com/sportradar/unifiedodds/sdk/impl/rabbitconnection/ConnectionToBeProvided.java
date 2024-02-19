/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionHealth.HEALTHY;
import static java.util.Arrays.asList;

import com.google.common.collect.Streams;
import com.rabbitmq.client.Channel;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public interface ConnectionToBeProvided {
    @Getter
    @AllArgsConstructor
    public static class ConnectionPresent implements ConnectionToBeProvided {

        private final long createdAt;

        @Setter
        private ConnectionHealth health;

        private final ConnectionCreatingChannels connection;
    }

    public static class ConnectionCreatingChannels extends ConnectionAllOperationsUnsupported {

        private final Iterator<Channel> iteratingAllChannelsAndRepeatingLastOne;

        private ConnectionCreatingChannels(List<Channel> channels) {
            Channel lastChannel = channels.get(channels.size() - 1);
            iteratingAllChannelsAndRepeatingLastOne =
                Streams.concat(channels.stream(), Stream.iterate(lastChannel, i -> i)).iterator();
        }

        @Override
        public Channel createChannel() {
            return iteratingAllChannelsAndRepeatingLastOne.next();
        }
    }

    public static class ConnectionAbsent implements ConnectionToBeProvided {}

    public static enum ConnectionHealth {
        UNHEALTHY_AUTO_RECOVERING,
        HEALTHY,
    }

    public static class Factory {

        private final TimeUtils time;

        public Factory(TimeUtils time) {
            this.time = time;
        }

        public ConnectionPresent whichIs(ChannelsToBeCreated toBeCreating) {
            return new ConnectionPresent(
                time.now(),
                HEALTHY,
                new ConnectionCreatingChannels(toBeCreating.channels)
            );
        }

        public ConnectionPresent whichIs(ConnectionHealth health, ChannelsToBeCreated toBeCreating) {
            return new ConnectionPresent(
                time.now(),
                health,
                new ConnectionCreatingChannels(toBeCreating.channels)
            );
        }

        public ConnectionAbsent absent() {
            return new ConnectionAbsent();
        }
    }

    @AllArgsConstructor
    public static class ChannelsToBeCreated {

        private List<Channel> channels;

        public static ChannelsToBeCreated creating(Channel... channels) {
            return new ChannelsToBeCreated(asList(channels));
        }
    }
}
