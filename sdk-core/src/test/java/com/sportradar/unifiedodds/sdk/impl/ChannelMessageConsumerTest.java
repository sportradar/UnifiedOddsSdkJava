package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.impl.Constants.ODDS_CHANGE_KEY;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.di.MockedMasterModule;
import com.sportradar.unifiedodds.sdk.di.TestingModule;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.xml.bind.JAXBContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@SuppressWarnings({ "MagicNumber", "VisibilityModifier" })
public class ChannelMessageConsumerTest {

    private static final String ROUTING_KEY = "routing_key";

    final Injector injector = Guice.createInjector(
        Modules.override(new MockedMasterModule()).with(new TestingModule())
    );

    ChannelMessageConsumer chanMsgConsumer;
    private JAXBContext messagingJaxbContext;

    @BeforeEach
    public void setup() {
        messagingJaxbContext =
            spy(injector.getInstance(Key.get(JAXBContext.class, Names.named("MessageJAXBContext"))));
        chanMsgConsumer =
            new ChannelMessageConsumerImpl(
                injector.getInstance(RoutingKeyParser.class),
                injector.getInstance(SdkInternalConfiguration.class),
                injector.getInstance(SdkProducerManager.class),
                messagingJaxbContext
            );
    }

    @Test
    public void nullDataRaisesDeserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] nullData = null;

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, nullData, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.any(), Mockito.any());
    }

    @Test
    public void emptyDataRaisesDeserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] emptyData = new byte[0];

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, emptyData, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.any(), Mockito.any());
    }

    @Test
    public void throwsOnReceiveEventsBeforeReceiverIsOpened() throws Exception {
        byte[] data = oddsChangeBytes();

        assertThatThrownBy(() -> chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void invokesOnMsgReceivedAfterReceiverIsOpened() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        //Execute
        chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L);

        //Verify
        Mockito
            .verify(msgConsumer)
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test
    public void throwsForNullRoutingKey() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        assertThatThrownBy(() -> chanMsgConsumer.onMessageReceived(null, data, null, 0L))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void invokesOnMsgReceivedForUnparsableRoutingKey() throws Exception {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = oddsChangeBytes();

        //Execute
        chanMsgConsumer.onMessageReceived(ROUTING_KEY, data, null, 0L);

        //Verify
        Mockito
            .verify(msgConsumer)
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test
    public void deserializationFailedEvent() {
        //Prepare
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = new byte[] { 1, 2, 3, 4 }; //this can't be deserialized

        //Execute
        chanMsgConsumer.onMessageReceived(ROUTING_KEY, data, null, 0L);

        //Verify
        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.eq(data), Mockito.any());
    }

    @Test
    public void testUnmarshallerThreadSafe() throws Exception {
        final int totalThreads = 20;
        final int invocationsPerThread = 1000;
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);
        byte[] data = oddsChangeBytes();

        CountDownLatch finished = new CountDownLatch(totalThreads);
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);

        List<Callable<Void>> callableTasks = new ArrayList<>();
        for (int i = 0; i < totalThreads; i++) {
            callableTasks.add(() -> {
                int count = 0;
                while (count < invocationsPerThread) {
                    chanMsgConsumer.onMessageReceived(ODDS_CHANGE_KEY, data, null, 0L);
                    count++;
                }
                finished.countDown();
                return null;
            });
        }

        executor.invokeAll(callableTasks);
        finished.await(30, TimeUnit.SECONDS);

        executor.shutdownNow();

        //Verify
        Mockito
            .verify(msgConsumer, times(invocationsPerThread * totalThreads))
            .onMessageReceived(Mockito.any(), Mockito.eq(data), Mockito.any(), Mockito.any());
    }

    @Test
    public void getsConsumerDescription() {
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = new byte[] { 1, 2, 3, 4 }; //this can't be deserialized

        chanMsgConsumer.onMessageReceived(ROUTING_KEY, data, null, 0L);

        Mockito.verify(msgConsumer).onMessageDeserializationFailed(Mockito.eq(data), Mockito.any());
    }

    @Test
    public void unmarshallerCleanedUpOnClose() throws Exception {
        MessageConsumer msgConsumer = Mockito.mock(MessageConsumer.class);
        chanMsgConsumer.open(msgConsumer);

        byte[] data = new byte[] { 1, 2, 3, 4 }; //this can't be deserialized
        chanMsgConsumer.onMessageReceived(ROUTING_KEY, data, null, 0L);

        chanMsgConsumer.close();

        chanMsgConsumer.onMessageReceived(ROUTING_KEY, data, null, 0L);

        verify(messagingJaxbContext, times(2)).createUnmarshaller();
    }

    private byte[] oddsChangeBytes() throws Exception {
        return Files.readAllBytes(
            Paths.get(this.getClass().getClassLoader().getResource(Constants.ODDS_CHANGE_MSG_URI).toURI())
        );
    }
}
