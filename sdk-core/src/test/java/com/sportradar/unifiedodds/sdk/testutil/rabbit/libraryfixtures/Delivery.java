/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

@Getter
public class Delivery {

    private Envelope envelope;
    private AMQP.BasicProperties properties;
    private byte[] body;

    Delivery(
        @NonNull final Envelope envelope,
        @NonNull final AMQP.BasicProperties properties,
        @NonNull final byte[] body
    ) {
        this.envelope = envelope;
        this.properties = properties;
        this.body = body;
    }

    public static Delivery any() {
        final long anyDeliveryTag = 42L;
        val anyRedeliver = false;
        return new Delivery(
            new Envelope(anyDeliveryTag, anyRedeliver, "anyExchange", "anyRoutingKey"),
            new AMQP.BasicProperties(),
            "anyBody".getBytes(UTF_8)
        );
    }
}
