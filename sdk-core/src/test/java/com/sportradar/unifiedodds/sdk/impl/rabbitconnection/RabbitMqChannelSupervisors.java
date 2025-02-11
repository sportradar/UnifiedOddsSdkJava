/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.internal.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.internal.impl.RabbitMqSystemListener;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.AmqpConnectionFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.OnDemandChannelSupervisor;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.RabbitMqChannelImpl;
import com.sportradar.utils.thread.sleep.Sleep;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import lombok.NoArgsConstructor;

public class RabbitMqChannelSupervisors {

    @NoArgsConstructor
    public static class Builder {

        private static final String ANY = "any";

        private Optional<AmqpConnectionFactory> connectionFactory = Optional.empty();
        private Optional<TimeUtils> timeUtils = Optional.empty();
        private Optional<Sleep> sleep = Optional.empty();
        private Optional<ChannelMessageConsumer> messageConsumer = Optional.empty();

        @SuppressWarnings("HiddenField")
        public Builder with(AmqpConnectionFactory connectionFactory) {
            this.connectionFactory = Optional.of(connectionFactory);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public Builder with(TimeUtils timeUtils) {
            this.timeUtils = Optional.of(timeUtils);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public Builder with(Sleep sleep) {
            this.sleep = Optional.of(sleep);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public Builder with(ChannelMessageConsumer messageConsumer) {
            this.messageConsumer = Optional.of(messageConsumer);
            return this;
        }

        public OnDemandChannelSupervisor opened() throws IOException {
            OnDemandChannelSupervisor supervisor = new RabbitMqChannelImpl(
                any(RabbitMqSystemListener.class),
                any(WhoAmIReader.class),
                ANY,
                connectionFactory.orElse(any(AmqpConnectionFactory.class)),
                timeUtils.orElse(any(TimeUtils.class)),
                sleep.orElse(any(Sleep.class))
            );
            supervisor.open(
                Arrays.asList(ANY),
                messageConsumer.orElse(any(ChannelMessageConsumer.class)),
                ANY
            );
            return supervisor;
        }

        private <T> T any(Class<T> clazz) {
            return mock(clazz);
        }
    }
}
