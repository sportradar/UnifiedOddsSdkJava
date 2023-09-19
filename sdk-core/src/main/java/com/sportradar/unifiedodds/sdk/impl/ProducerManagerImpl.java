/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInfo;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 03/07/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName", "IllegalCatch", "LineLength" })
public class ProducerManagerImpl implements SdkProducerManager {

    private static final Logger logger = LoggerFactory.getLogger(ProducerManagerImpl.class);
    private final SdkInternalConfiguration configuration;
    private final Map<Integer, ProducerData> producers;
    private Set<Integer> unknownProducersWarning = new HashSet<>();
    private boolean feedOpened;

    @Inject
    public ProducerManagerImpl(
        SdkInternalConfiguration configuration,
        ProducerDataProvider producerDataProvider
    ) {
        Preconditions.checkNotNull(configuration);
        Preconditions.checkNotNull(producerDataProvider);

        this.configuration = configuration;

        logger.info("Fetching producer list");
        List<ProducerData> availableProducers = producerDataProvider.getAvailableProducers();
        availableProducers.forEach(pd ->
            logger.info(
                "Producers -> id: '{}', name: '{}', description: '{}', STATUS: [{}]",
                pd.getId(),
                pd.getName(),
                pd.getDescription(),
                pd.isActive() ? "ACTIVE" : "INACTIVE"
            )
        );

        this.producers =
            availableProducers.stream().collect(Collectors.toConcurrentMap(ProducerData::getId, v -> v));

        logger.info("Automatically disabling producers: {}", configuration.getDisabledProducers());
        configuration.getDisabledProducers().forEach(this::disableProducer);
    }

    @Override
    public Map<Integer, Producer> getAvailableProducers() {
        return producers
            .entrySet()
            .stream()
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, v -> new ProducerImpl(v.getValue())));
    }

    @Override
    public Map<Integer, Producer> getActiveProducers() {
        return producers
            .entrySet()
            .stream()
            .filter(p -> p.getValue().isActive())
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, v -> new ProducerImpl(v.getValue())));
    }

    @Override
    public Producer getProducer(int id) {
        if (producers.containsKey(id)) {
            return new ProducerImpl(producers.get(id));
        } else {
            return generateUnknownProducer(id);
        }
    }

    private Producer generateUnknownProducer(int id) {
        if (!unknownProducersWarning.contains(id)) {
            logger.warn("Generating Unknown producer: " + id);
            unknownProducersWarning.add(id);
        }

        return ProducerImpl.buildUnknownProducer(id, configuration);
    }

    @Override
    public void enableProducer(int producerId) {
        if (feedOpened) {
            throw new UnsupportedOperationException(
                "Can not enable producers once the feed instance is opened"
            );
        }

        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setEnabled(true);
        }
    }

    @Override
    public void disableProducer(int producerId) {
        if (feedOpened) {
            throw new UnsupportedOperationException(
                "Can not disable producers once the feed instance is opened"
            );
        }

        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setEnabled(false);
        }
    }

    @Override
    public void setProducerRecoveryFromTimestamp(int producerId, long lastMessageTimestamp) {
        Preconditions.checkArgument(lastMessageTimestamp >= 0);

        if (feedOpened) {
            throw new IllegalStateException(
                "Can not update last message timestamps for producers once the feed instance is opened"
            );
        }

        if (!producers.containsKey(producerId)) {
            logger.warn(
                "Received request to set a recovery timestamp for an unknown producer, id: {} - ignoring request",
                producerId
            );
            return;
        }

        if (lastMessageTimestamp != 0) {
            int maxRequestMinutes = producers.get(producerId).getStatefulRecoveryWindowInMinutes();
            long maxRecoveryInterval = TimeUnit.MILLISECONDS.convert(maxRequestMinutes, TimeUnit.MINUTES);
            long requestedRecoveryInterval = System.currentTimeMillis() - lastMessageTimestamp;
            if (requestedRecoveryInterval > maxRecoveryInterval) {
                throw new IllegalArgumentException(
                    String.format(
                        "Last received message timestamp can not be more than '%s' minutes ago, producerId:%s timestamp:%s (max recovery = '%s' minutes ago)",
                        maxRequestMinutes,
                        producerId,
                        lastMessageTimestamp,
                        maxRequestMinutes
                    )
                );
            }
        }

        ProducerData producerData = producers.get(producerId);
        producerData.setRecoveryFromTimestamp(lastMessageTimestamp);
    }

    @Override
    public boolean isProducerEnabled(int producerId) {
        return producers
            .values()
            .stream()
            .filter(p -> p.getId() == producerId)
            .findFirst()
            .map(ProducerData::isEnabled)
            .orElse(false);
    }

    @Override
    public boolean isProducerDown(int producerId) {
        return producers
            .values()
            .stream()
            .filter(p -> p.getId() == producerId)
            .findFirst()
            .map(ProducerData::isFlaggedDown)
            .orElse(false);
    }

    @Override
    public void open() {
        this.feedOpened = true;
    }

    @Override
    public void setProducerDown(int producerId, boolean flaggedDown) {
        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setFlaggedDown(flaggedDown);
        }
    }

    @Override
    public void internalSetProducerLastMessageTimestamp(int producerId, long lastMessageTimestamp) {
        Preconditions.checkArgument(lastMessageTimestamp > 0);

        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setLastMessageTimestamp(lastMessageTimestamp);
        }
    }

    @Override
    public void internalSetProducerLastRecoveryMessageTimestamp(
        int producerId,
        long lastRecoveryMessageTimestamp
    ) {
        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setLastRecoveryMessageReceivedTimestamp(lastRecoveryMessageTimestamp);
        }
    }

    @Override
    public void setLastProcessedMessageGenTimestamp(int producerId, long lastProcessedMessageGenTimestamp) {
        Preconditions.checkArgument(lastProcessedMessageGenTimestamp > 0);

        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setLastProcessedMessageGenTimestamp(lastProcessedMessageGenTimestamp);
        }
    }

    @Override
    public void setLastAliveReceivedGenTimestamp(int producerId, long aliveReceivedGenTimestamp) {
        Preconditions.checkArgument(aliveReceivedGenTimestamp > 0);

        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setLastAliveReceivedGenTimestamp(aliveReceivedGenTimestamp);
        }
    }

    @Override
    public void setProducerRecoveryInfo(int producerId, RecoveryInfo recoveryInfo) {
        try {
            if (producers.containsKey(producerId)) {
                ProducerData producer = producers.get(producerId);
                if (producer != null && recoveryInfo != null) {
                    producer.setRecoveryInfo(recoveryInfo);
                }
            } else {
                logger.warn(
                    "Error saving recovery info to the producer " + producerId + ". Producer is missing."
                );
            }
        } catch (Exception ex) {
            logger.warn("Error saving recovery info to the producer " + producerId, ex);
        }
    }

    @Override
    public long getProducerLastRecoveryMessageTimestamp(int producerId) {
        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            return producerData.getLastRecoveryMessageTimestamp();
        }
        return 0;
    }

    @Override
    public void internalSetProducerLastRecoveryAttemptTimestamp(
        int producerId,
        long lastRecoveryAttemptTimestamp
    ) {
        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            producerData.setLastRecoveryAttemptTimestamp(lastRecoveryAttemptTimestamp);
        }
    }

    @Override
    public long getProducerLastRecoveryAttemptTimestamp(int producerId) {
        if (producers.containsKey(producerId)) {
            ProducerData producerData = producers.get(producerId);
            return producerData.getLastRecoveryAttemptTimestamp();
        }
        return 0;
    }
}
