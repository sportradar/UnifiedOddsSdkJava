/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created on 03/07/2017.
 * // TODO @eti: Javadoc
 */
public class ProducerData {
    final static int DEFAULT_STATEFUL_RECOVERY_WINDOW_IN_MINUTES = 4320;

    private final static Logger logger = LoggerFactory.getLogger(ProducerData.class);
    private final static Map<String, ProducerScope> scopeMappings =
            ImmutableMap.<String, ProducerScope>builder()
                    .put("prematch", ProducerScope.Prematch)
                    .put("live", ProducerScope.Live)
                    .put("virtual", ProducerScope.Virtuals)
                    .build();

    private final int id;
    private final String name;
    private final String description;
    private final boolean active;
    private final String apiUrl;
    private final Set<ProducerScope> producerScopes;
    private final int statefulRecoveryWindowInMinutes;
    private long lastMessageTimestamp = 0;
    private boolean enabled;
    private boolean flaggedDown = true;
    private long lastProcessedMessageGenTimestamp;
    private long lastAliveReceivedGenTimestamp = 0;
    private long recoveryFromTimestamp;

    ProducerData(int id, String name, String description, boolean active, String apiUrl, String producerScopes, Integer statefulRecoveryWindowInMinutes) {
        Preconditions.checkArgument(id > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(description));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(apiUrl));

        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.apiUrl = apiUrl;
        this.enabled = active;

        ImmutableSet.Builder<ProducerScope> builder = ImmutableSet.builder();
        if (!Strings.isNullOrEmpty(producerScopes)) {
            String[] split = producerScopes.split("\\|");
            long count = Stream.of(split).filter(scopeMappings::containsKey).count();

            if (count == split.length) {
                for (String s : split) {
                    builder.add(scopeMappings.get(s));
                }
            } else {
                logger.warn("Handling producer[{}] with unknown ProducerScope values: '{}'", id, producerScopes);
                builder.addAll(Arrays.stream(ProducerScope.values()).collect(Collectors.toList()));
            }
        } else {
            logger.warn("Handling producer[{}] with 'null' ProducerScope values", id);
            builder.addAll(Arrays.stream(ProducerScope.values()).collect(Collectors.toList()));
        }
        this.producerScopes = builder.build();
        this.statefulRecoveryWindowInMinutes = statefulRecoveryWindowInMinutes == null ?
                DEFAULT_STATEFUL_RECOVERY_WINDOW_IN_MINUTES :
                statefulRecoveryWindowInMinutes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return active;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public long getLastProcessedMessageGenTimestamp() {
        return lastProcessedMessageGenTimestamp;
    }

    public long getTimestampForRecovery() {
        if (lastAliveReceivedGenTimestamp == 0) {
            return recoveryFromTimestamp;
        }

        return lastAliveReceivedGenTimestamp;
    }

    public int getStatefulRecoveryWindowInMinutes() {
        return statefulRecoveryWindowInMinutes;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        Preconditions.checkArgument(lastMessageTimestamp > 0);

        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public boolean isFlaggedDown() {
        return flaggedDown;
    }

    public void setFlaggedDown(boolean flaggedDown) {
        this.flaggedDown = flaggedDown;
    }

    public Set<ProducerScope> getProducerScopes() {
        return producerScopes;
    }

    public void setLastProcessedMessageGenTimestamp(long lastProcessedMessageGenTimestamp) {
        Preconditions.checkArgument(lastProcessedMessageGenTimestamp > 0);

        this.lastProcessedMessageGenTimestamp = lastProcessedMessageGenTimestamp;
    }

    public void setLastAliveReceivedGenTimestamp(long lastAliveReceivedGenTimestamp) {
        this.lastAliveReceivedGenTimestamp = lastAliveReceivedGenTimestamp;
    }

    public void setRecoveryFromTimestamp(long recoveryFromTimestamp) {
        this.recoveryFromTimestamp = recoveryFromTimestamp;
    }
}
