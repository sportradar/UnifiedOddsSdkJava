/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.extended.RoutingKeyInfo;
import com.sportradar.unifiedodds.sdk.internal.impl.CompositeFeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.internal.impl.FeedMessageProcessor;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import java.util.List;
import java.util.UUID;

/**
 * The master {@link FeedMessageProcessor} implementation that handles the flow
 * trough the message processing pipeline
 */
@SuppressWarnings({ "ExplicitInitialization" })
public class CompositeMessageProcessor implements CompositeFeedMessageProcessor {

    /**
     * The processor identifier
     */
    private final String processorId;

    /**
     * The {@link FeedMessageProcessor} implementation that is first invoked in the pipeline
     */
    private final FeedMessageProcessor firstProcessor;

    /**
     * The final {@link FeedMessageProcessor} which invokes the dispatching processor
     */
    private final FeedMessageProcessor finalProcessor;

    /**
     * Indication for the initialization state of the current instance
     */
    private boolean initialized = false;

    /**
     * Constructs a new {@link CompositeMessageProcessor} instance
     *
     * @param processors - a complete{@link List} pipeline processors
     */
    public CompositeMessageProcessor(List<FeedMessageProcessor> processors) {
        Preconditions.checkNotNull(processors);
        Preconditions.checkArgument(!processors.isEmpty());

        this.processorId = UUID.randomUUID().toString();
        this.firstProcessor = processors.get(0);

        FeedMessageProcessor prevProc = firstProcessor;
        for (FeedMessageProcessor nextProc : processors) {
            if (prevProc.getProcessorId().equals(nextProc.getProcessorId())) {
                continue;
            }

            prevProc.setNextMessageProcessor(nextProc);
            prevProc = nextProc;
        }
        this.finalProcessor = prevProc;
    }

    /**
     * Initializes and prepares the composite processor instance
     *
     * @param dispatchingProcessor - the final dispatching message processor
     */
    @Override
    public void init(FeedMessageProcessor dispatchingProcessor) {
        this.finalProcessor.setNextMessageProcessor(dispatchingProcessor);
        this.initialized = true;
    }

    /**
     * Returns the processor identifier
     *
     * @return - the processor identifier
     */
    @Override
    public String getProcessorId() {
        return processorId;
    }

    /**
     * Starts the processing pipeline by calling the {@link #firstProcessor}
     *
     * @param message - the message that should be processed
     * @param body - the raw body of the received message
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     * @param timestamp - all message timestamps
     */
    @Override
    public void processMessage(
        UnmarshalledMessage message,
        byte[] body,
        RoutingKeyInfo routingKeyInfo,
        MessageTimestamp timestamp
    ) {
        if (!initialized) {
            throw new IllegalStateException("The composite message processor needs to be initialized");
        }
        this.firstProcessor.processMessage(message, body, routingKeyInfo, timestamp);
    }

    /**
     * This method should be ignored since the {@link CompositeMessageProcessor} is the trigger/starter
     * of the processing pipeline and can't have a successor processor
     *
     * @param nextMessageProcessor - should be ignored/not used
     */
    @Override
    public void setNextMessageProcessor(FeedMessageProcessor nextMessageProcessor) {
        throw new UnsupportedOperationException(
            "The composite message processor can't have successor processor"
        );
    }
}
