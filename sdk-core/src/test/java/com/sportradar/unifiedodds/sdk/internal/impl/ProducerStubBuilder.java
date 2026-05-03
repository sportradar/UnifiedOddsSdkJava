/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerScope;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInfo;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("HiddenField")
public class ProducerStubBuilder {

    private int id;
    private String name;
    private String description;
    private boolean active;
    private boolean enabled;
    private String apiUrl;
    private Set<ProducerScope> producerScopes;
    private int statefulRecoveryWindowInMinutes;
    private long lastMessageTimestamp;
    private boolean flaggedDown;
    private long lastProcessedMessageGenTimestamp;
    private long timestampForRecovery;
    private long processingQueDelay;
    private RecoveryInfo recoveryInfo;

    public static ProducerStubBuilder withLiveId() {
        return new ProducerStubBuilder().withId(1);
    }

    public ProducerStubBuilder withId(int id) {
        this.id = id;
        return this;
    }

    public ProducerStubBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProducerStubBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ProducerStubBuilder active() {
        this.active = true;
        return this;
    }

    public ProducerStubBuilder enabled() {
        this.enabled = true;
        return this;
    }

    public ProducerStubBuilder withApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
        return this;
    }

    public ProducerStubBuilder withProducerScopes(Set<ProducerScope> producerScopes) {
        this.producerScopes = producerScopes;
        return this;
    }

    public ProducerStubBuilder withStatefulRecoveryWindowInMinutes(int statefulRecoveryWindowInMinutes) {
        this.statefulRecoveryWindowInMinutes = statefulRecoveryWindowInMinutes;
        return this;
    }

    public ProducerStubBuilder withLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
        return this;
    }

    public ProducerStubBuilder withFlaggedDown(boolean flaggedDown) {
        this.flaggedDown = flaggedDown;
        return this;
    }

    public ProducerStubBuilder withLastProcessedMessageGenTimestamp(long lastProcessedMessageGenTimestamp) {
        this.lastProcessedMessageGenTimestamp = lastProcessedMessageGenTimestamp;
        return this;
    }

    public ProducerStubBuilder withTimestampForRecovery(long timestampForRecovery) {
        this.timestampForRecovery = timestampForRecovery;
        return this;
    }

    public ProducerStubBuilder withProcessingQueDelay(long processingQueDelay) {
        this.processingQueDelay = processingQueDelay;
        return this;
    }

    public ProducerStubBuilder withRecoveryInfo(RecoveryInfo recoveryInfo) {
        this.recoveryInfo = recoveryInfo;
        return this;
    }

    public Producer build() {
        return new ProducerStub();
    }

    public static ProducerStubBuilder create() {
        return new ProducerStubBuilder();
    }

    private class ProducerStub implements Producer {

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
        public long getLastMessageTimestamp() {
            return lastMessageTimestamp;
        }

        @Override
        public boolean isAvailable() {
            return active;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public boolean isFlaggedDown() {
            return flaggedDown;
        }

        @Override
        public String getApiUrl() {
            return apiUrl;
        }

        @Override
        public Set<ProducerScope> getProducerScopes() {
            return producerScopes != null ? producerScopes : Collections.emptySet();
        }

        @Override
        public long getLastProcessedMessageGenTimestamp() {
            return lastProcessedMessageGenTimestamp;
        }

        @Override
        public long getProcessingQueDelay() {
            return processingQueDelay;
        }

        @Override
        public long getTimestampForRecovery() {
            return timestampForRecovery;
        }

        @Override
        public int getStatefulRecoveryWindowInMinutes() {
            return statefulRecoveryWindowInMinutes;
        }

        @Override
        public RecoveryInfo getRecoveryInfo() {
            return recoveryInfo;
        }
    }
}
