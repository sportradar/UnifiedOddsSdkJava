/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerScope;
import java.util.*;
import java.util.stream.Collectors;

// Constant names should comply with a naming convention
@SuppressWarnings({ "java:S115", "EqualsAvoidNull", "ReturnCount" })
public enum MessageInterest {
    // hi.-.live.odds_change.5.sr:match.12329150
    /**
     * Interested in live-match messages only
     */
    LiveMessagesOnly(true, "*.*.live.*.*.*.*"),

    /**
     * Interested in pre-match messages only
     */
    PrematchMessagesOnly(true, "*.pre.*.*.*.*.*"),

    /**
     * Interested in virtual sports messages only
     */
    VirtualSports(true, Arrays.asList("*.virt.*.*.*.*.*", "*.*.virt.*.*.*.*")),

    /**
     * Interested in hi priority messages only
     */
    HiPrioMessagesOnly(true, "hi.*.*.*.*.*.*"),

    /**
     * Interested in lo priority messages only
     */
    LoPrioMessagesOnly(true, "lo.*.*.*.*.*.*"),

    /**
     * Interested only in messages for specific matches
     */
    SpecifiedMatchesOnly(false, ""),

    /**
     * Interested in all messages
     */
    AllMessages(true, "*.*.*.*.*.*.*"),

    /**
     * Interest for Alive messages (messages that indicate producers health status),
     * sessions with this message interest do not dispatch any events.
     */
    SystemAliveMessages(true, "-.-.-.alive.#");

    /**
     * Indicates whether the routing key for current {@link MessageInterest} instance is static
     * (does not change)
     */
    private final boolean routingKeyStatic;

    /**
     * The routing keys for the current {@link MessageInterest} instance or a null reference if
     * routing key is not static
     */
    private final List<String> routingKeys;

    /**
     * Initializes a new member of the {@link MessageInterest} enum
     *
     * @param routingKeyStatic value indicating whether the routing key for current
     *        {@link MessageInterest} instance is static (does not change)
     * @param routingKey The routing key for the current {@link MessageInterest} instance or a null
     *        reference if routing key is not static
     */
    MessageInterest(boolean routingKeyStatic, String routingKey) {
        this.routingKeyStatic = routingKeyStatic;
        if (!routingKey.equals("")) {
            this.routingKeys = Collections.singletonList(routingKey);
        } else {
            this.routingKeys = null;
        }
    }

    /**
     * Initializes a new member of the {@link MessageInterest} enum
     *
     * @param routingKeyStatic value indicating whether the routing key for current
     *        {@link MessageInterest} instance is static (does not change)
     * @param routingKeys The routing key for the current {@link MessageInterest} instance or a null
     *        reference if routing key is not static
     */
    MessageInterest(boolean routingKeyStatic, List<String> routingKeys) {
        this.routingKeyStatic = routingKeyStatic;
        this.routingKeys = routingKeys;
    }

    /**
     * Gets a value indicating whether the routing key for current {@link MessageInterest} instance
     * is static (does not change)
     *
     * @return a value indicating whether the routing key for current {@link MessageInterest}
     *         instance is static (does not change)
     */
    public boolean isRoutingKeyStatic() {
        return routingKeyStatic;
    }

    /**
     * Gets the routing key(s) for the current {@link MessageInterest} instance or a null reference if
     * routing key is not static
     *
     * @return the routing key(s) for the current {@link MessageInterest} instance or a null reference
     *         if routing key is not static
     */
    public List<String> getRoutingKeys() {
        return routingKeys;
    }

    /**
     * Returns the producer ids that generate this specific message interest
     *
     * @return a lis of possible producers
     * @param availableProducers a list of available producers
     */
    public Set<Integer> getPossibleSourceProducers(Map<Integer, Producer> availableProducers) {
        Preconditions.checkNotNull(availableProducers);

        Set<Integer> possibleProducers = new HashSet<>();
        switch (this) {
            case LiveMessagesOnly:
                possibleProducers.addAll(
                    availableProducers
                        .values()
                        .stream()
                        .filter(p -> p.getProducerScopes().contains(ProducerScope.Live))
                        .map(Producer::getId)
                        .collect(Collectors.toSet())
                );
                break;
            case PrematchMessagesOnly:
                possibleProducers.addAll(
                    availableProducers
                        .values()
                        .stream()
                        .filter(p -> p.getProducerScopes().contains(ProducerScope.Prematch))
                        .map(Producer::getId)
                        .collect(Collectors.toSet())
                );
                break;
            case VirtualSports:
                possibleProducers.addAll(
                    availableProducers
                        .values()
                        .stream()
                        .filter(p -> p.getProducerScopes().contains(ProducerScope.Virtuals))
                        .map(Producer::getId)
                        .collect(Collectors.toSet())
                );
                break;
            case AllMessages:
            case HiPrioMessagesOnly:
            case LoPrioMessagesOnly:
            case SpecifiedMatchesOnly:
            default:
                possibleProducers.addAll(
                    availableProducers.values().stream().map(Producer::getId).collect(Collectors.toSet())
                );
                break;
        }

        return possibleProducers;
    }

    /**
     * Returns an indication if the provided {@link Producer} is in the message interest scope
     *
     * @param producer the {@link Producer} that needs to be checked for the scope
     * @return <code>true</code> if the producer is in scope, otherwise <code>false</code>
     */
    public boolean isProducerInScope(Producer producer) {
        Preconditions.checkNotNull(producer);

        switch (this) {
            case LiveMessagesOnly:
                return producer.getProducerScopes().contains(ProducerScope.Live);
            case PrematchMessagesOnly:
                return producer.getProducerScopes().contains(ProducerScope.Prematch);
            case VirtualSports:
                return producer.getProducerScopes().contains(ProducerScope.Virtuals);
            case AllMessages:
            case HiPrioMessagesOnly:
            case LoPrioMessagesOnly:
            case SpecifiedMatchesOnly:
            default:
                return true;
        }
    }

    public String toShortString() {
        switch (this) {
            case LiveMessagesOnly:
                return "live";
            case PrematchMessagesOnly:
                return "prematch";
            case VirtualSports:
                return "virtuals";
            case AllMessages:
                return "all";
            case HiPrioMessagesOnly:
                return "hi";
            case LoPrioMessagesOnly:
                return "lo";
            case SpecifiedMatchesOnly:
                return "specified";
            default:
                return "";
        }
    }
}
