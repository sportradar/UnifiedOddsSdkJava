package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.*;
import com.sportradar.utils.time.EpochMillis;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.AllArgsConstructor;

@SuppressWarnings({ "LambdaBodyLength" })
public class ChannelFixture extends NoOpRecoverableChannel {

    private Consumer consumer;
    private int initiatedTimes;
    private int closedTimes;
    private boolean isOpen;

    public ChannelFixture() {}

    void sendMessage() throws IOException {
        consumer.handleDelivery("any", mock(Envelope.class), mock(AMQP.BasicProperties.class), new byte[] {});
    }

    void verifyInitiatedTimes(int expectedTimes) {
        assertThat(initiatedTimes).isEqualTo(expectedTimes);
    }

    void verifyClosedTimes(int expectedTimes) {
        assertThat(closedTimes).isEqualTo(expectedTimes);
    }

    @Override
    @SuppressWarnings("HiddenField")
    public String basicConsume(String s, boolean b, String s1, Consumer consumer) throws IOException {
        verifyChannelIsClosed();
        initiatedTimes++;
        isOpen = true;
        this.consumer = consumer;
        return null;
    }

    @Override
    public AMQP.Queue.DeclareOk queueDeclare() throws IOException {
        return new QueueDeclareOk("anyName");
    }

    @Override
    public void close() throws IOException, TimeoutException {
        verifyChannelIsOpen();
        isOpen = false;
        closedTimes++;
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    private void verifyChannelIsClosed() {
        if (isOpen) {
            throw new IllegalStateException("channel is already open");
        }
    }

    private void verifyChannelIsOpen() {
        if (!isOpen) {
            throw new IllegalStateException("channel is already closed");
        }
    }

    @AllArgsConstructor
    public static class QueueDeclareOk extends DeclareOkAllOperationsUnsupported {

        private final String name;

        @Override
        public String getQueue() {
            return name;
        }
    }
}
