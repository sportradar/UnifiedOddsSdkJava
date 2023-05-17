package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.mockito.Mockito.*;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import java.io.IOException;
import org.mockito.ArgumentCaptor;
import org.mockito.verification.VerificationMode;

@SuppressWarnings({ "LambdaBodyLength" })
public interface ChannelFixture extends RecoverableChannel {
    void sendMessageAt(long epochMillis);

    void verifyInitiated(VerificationMode times);

    class Holder {

        private ChannelFixture channel;

        Holder(TimeUtils timeUtils) throws IOException {
            stubInitiationBehaviour();
            setupControlMethods(timeUtils);
        }

        private void setupControlMethods(TimeUtils timeUtils) {
            sendMessageAt(timeUtils);

            verifyInitiated();
        }

        private void stubInitiationBehaviour() throws IOException {
            channel = mock(ChannelFixture.class);
            AMQP.Queue.DeclareOk declareQueue = mock(AMQP.Queue.DeclareOk.class);
            when(channel.queueDeclare()).thenReturn(declareQueue);
            when(declareQueue.getQueue()).thenReturn("any");
        }

        private void sendMessageAt(TimeUtils timeUtils) {
            doAnswer(invocationOnMock -> {
                    ArgumentCaptor<Consumer> messageConsumer = ArgumentCaptor.forClass(Consumer.class);
                    verify(channel)
                        .basicConsume(anyString(), anyBoolean(), anyString(), messageConsumer.capture());
                    when(timeUtils.now()).thenReturn(invocationOnMock.getArgument(0));
                    messageConsumer
                        .getValue()
                        .handleDelivery(
                            "any",
                            mock(Envelope.class),
                            mock(AMQP.BasicProperties.class),
                            new byte[] {}
                        );
                    return null;
                })
                .when(channel)
                .sendMessageAt(anyLong());
        }

        private void verifyInitiated() {
            doAnswer(invocationOnMock -> {
                    verify(channel, invocationOnMock.getArgument(0))
                        .basicConsume(any(), anyBoolean(), anyString(), any());
                    return null;
                })
                .when(channel)
                .verifyInitiated(any());
        }

        ChannelFixture get() {
            return channel;
        }
    }
}
