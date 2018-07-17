/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

import java.util.Arrays;
import java.util.Set;

/**
 * Created on 03/07/2017.
 * // TODO @eti: Javadoc
 */
public class ProducerImpl implements Producer {
    private final ProducerData producerData;
    private final int id;
    private final String name;
    private final String description;
    private final boolean active;
    private final boolean enabled;
    private final String apiUrl;
    private final Set<ProducerScope> producerScopes;

    ProducerImpl(ProducerData pData) {
        Preconditions.checkNotNull(pData);

        producerData = pData;
        id = producerData.getId();
        name = producerData.getName();
        description = producerData.getDescription();
        active = producerData.isActive();
        enabled = producerData.isEnabled();
        apiUrl = producerData.getApiUrl();
        producerScopes = producerData.getProducerScopes();
    }

    private ProducerImpl(int unknownProducerId, SDKInternalConfiguration configuration) {
        Preconditions.checkArgument(unknownProducerId > 0);
        Preconditions.checkNotNull(configuration);

        id = unknownProducerId;
        name = "Unknown";
        description = "Unknown producer";
        active = true;
        enabled = true;
        apiUrl = configuration.getAPIHost();
        producerScopes = ImmutableSet.<ProducerScope>builder()
                .addAll(Arrays.asList(ProducerScope.values()))
                .build();
        producerData = null;
    }

    static Producer buildUnknownProducer(int unknownProducerId, SDKInternalConfiguration configuration) {
        return new ProducerImpl(unknownProducerId, configuration);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isAvailable() {
        return active;
    }

    @Override
    public long getLastMessageTimestamp() {
        return producerData == null ? 0 : producerData.getLastMessageTimestamp();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isFlaggedDown() {
        // unknown producer always flagged up
        return producerData != null && producerData.isFlaggedDown();
    }

    @Override
    public String getApiUrl() {
        return apiUrl;
    }

    @Override
    public Set<ProducerScope> getProducerScopes() {
        return producerScopes;
    }

    @Override
    public long getLastProcessedMessageGenTimestamp() {
        return producerData == null ? 0 : producerData.getLastProcessedMessageGenTimestamp();
    }

    @Override
    public long getProcessingQueDelay() {
        return System.currentTimeMillis() - getLastProcessedMessageGenTimestamp();
    }

    @Override
    public long getTimestampForRecovery() {
        return producerData == null ? 0 : producerData.getTimestampForRecovery();
    }

    @Override
    public String toString() {
        return "ProducerImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", lastMessageTimestamp=" + getLastMessageTimestamp() +
                ", lastProcessedMessageGenTimestamp=" + getLastProcessedMessageGenTimestamp() +
                ", timestampForRecovery=" + getTimestampForRecovery() +
                ", processingQueDelay=" + getProcessingQueDelay() +
                ", enabled=" + enabled +
                ", flaggedDown=" + isFlaggedDown() +
                ", producerScopes=" + producerScopes +
                '}';
    }
}
