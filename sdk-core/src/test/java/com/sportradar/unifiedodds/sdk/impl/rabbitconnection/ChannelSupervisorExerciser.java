package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.OpeningResult.NEWLY_OPENED;

class ChannelSupervisorExerciser implements Runnable {

    private final OpeningResult[] openingResults;

    private final ClosingResult[] closingResults;

    private final int numberOrIterations;

    private final ChannelSupervisor supervisor;
    private final CountDownLatch latch;

    private final List<String> routingKeys = Arrays.asList("routingKeys");

    private final ChannelMessageConsumer consumer = getAnyChannelMessageConsumer();

    ChannelSupervisorExerciser(int numberOrIterations, ChannelSupervisor supervisor, CountDownLatch latch) {
        openingResults = new OpeningResult[numberOrIterations];
        closingResults = new ClosingResult[numberOrIterations];
        this.numberOrIterations = numberOrIterations;
        this.supervisor = supervisor;
        this.latch = latch;
    }

    @Override
    public void run() {
        awaitSignal();
        executeNTimes(i -> {
            openingResults[i] = supervisor.openChannel(routingKeys, consumer, "intent");
            closingResults[i] = supervisor.closeChannel();
        });
    }

    private void awaitSignal() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static ChannelMessageConsumer getAnyChannelMessageConsumer() {
        return (routingKey, body, properties, receivedAt) -> {
        };
    }

    private void executeNTimes(Exercisable exercisable) {
        try {
            for (int i = 0; i < numberOrIterations; i++) {
                exercisable.exercise(i);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getCountOfNonDuplicateOpeningCalls() {
        return Arrays.stream(openingResults).filter(r -> r == NEWLY_OPENED).count();
    }

    public long getCountOfNonDuplicateClosingCalls() {
        return Arrays.stream(closingResults).filter(r -> r == ClosingResult.NEWLY_CLOSED).count();
    }

    private interface Exercisable {
        void exercise(int i) throws Exception;
    }
}
