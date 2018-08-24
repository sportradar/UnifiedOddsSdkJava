/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedMessageInterestCombination;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An utility class that handles session routing key assignment
 */
class OddsFeedRoutingKeyBuilder {
    /**
     * The routing key pattern used to receive snapshot complete messages
     */
    private static final String SNAPSHOT_COMPLETE_ROUTING_KEY_TEMPLATE = "-.-.-.snapshot_complete.-.-.-.%s";

    /**
     * Private constructor, since this is a static utility class
     */
    private OddsFeedRoutingKeyBuilder() {}

    /**
     * Builds a valid list of routing keys for the provided sessions
     * k -> session identifier
     * v -> a {@link List} of routing keys for the associated session
     *
     * @param sessionsData - a {@link Map} describing the feed sessions
     * @param oddsFeedConfiguration - the associated feed SDK configuration instance
     * @return - a collection of session identifiers associated with a valid list of valid routing keys
     */
    static Map<Integer, List<String>> generateKeys(Map<Integer, MessageInterest> sessionsData, SDKInternalConfiguration oddsFeedConfiguration) {
        Preconditions.checkNotNull(sessionsData);
        Preconditions.checkArgument(!sessionsData.isEmpty());
        Preconditions.checkNotNull(oddsFeedConfiguration);

        validateInterestCombination(sessionsData);

        boolean bothLowAndHigh = haveBothLowAndHigh(sessionsData);

        String snapshotRoutingKey = String.format(SNAPSHOT_COMPLETE_ROUTING_KEY_TEMPLATE,
                oddsFeedConfiguration.getSdkNodeId() == null ? "-" : oddsFeedConfiguration.getSdkNodeId());

        Map<Integer, List<String>> result = new HashMap<>(sessionsData.size());
        sessionsData.forEach((k, v) -> {
            List<String> sessionRoutingKeys = new ArrayList<>();

            List<String> basicRoutingKeys = v.getRoutingKeys();
            for (String basicRoutingKey: basicRoutingKeys) {
                if (oddsFeedConfiguration.getSdkNodeId() != null) {
                    sessionRoutingKeys.add(basicRoutingKey + "." + oddsFeedConfiguration.getSdkNodeId() + ".#");
                    basicRoutingKey = basicRoutingKey + ".-.#";
                } else {
                    basicRoutingKey = basicRoutingKey + ".#";
                }

                if (bothLowAndHigh && v == MessageInterest.LoPrioMessagesOnly) {
                    sessionRoutingKeys.add(basicRoutingKey);
                } else {
                    sessionRoutingKeys.add(snapshotRoutingKey);
                    sessionRoutingKeys.add(basicRoutingKey);
                }
            }
            if (v != MessageInterest.SystemAliveMessages) {
                sessionRoutingKeys.add(MessageInterest.SystemAliveMessages.getRoutingKeys().get(0));
            }
            result.put(k, sessionRoutingKeys.stream().distinct().collect(Collectors.toList()));
        });

        return result;
    }

    private static void validateInterestCombination(Map<Integer, MessageInterest> sessionsData) {
        Preconditions.checkNotNull(sessionsData);

        if (sessionsData.isEmpty()) {
            throw new UnsupportedMessageInterestCombination("There must be at least one session per SDK instance");
        }

        if (sessionsData.size() == 1) {
            return;
        }

        Collection<MessageInterest> allUserValues = sessionsData.values();
        Set<MessageInterest> uniqueValues = new HashSet<>(allUserValues);
        if (allUserValues.size() != uniqueValues.size()) {
            throw new UnsupportedMessageInterestCombination("Session message interest must be unique per SDK instance, found duplicates. " + allUserValues);
        }

        if (allUserValues.stream().anyMatch(v -> v == MessageInterest.AllMessages)) {
            throw new UnsupportedMessageInterestCombination("The AllMessages message interest can only be used in a single session setup. " + allUserValues);
        }

        if (sessionsData.size() > 1 && (containsLowOrHigh(sessionsData) && containsMessageTypeInterest(sessionsData))) {
            String errorMsg = "Combination of session message interests not supported." + allUserValues;
            throw new UnsupportedMessageInterestCombination(errorMsg);
        }
    }

    private static boolean containsLowOrHigh(Map<Integer, MessageInterest> sessionsData) {
        Preconditions.checkNotNull(sessionsData);
        return sessionsData.containsValue(MessageInterest.HiPrioMessagesOnly) ||
                sessionsData.containsValue(MessageInterest.LoPrioMessagesOnly);
    }

    private static boolean containsMessageTypeInterest(Map<Integer, MessageInterest> sessionsData) {
        Preconditions.checkNotNull(sessionsData);

        return sessionsData.containsValue(MessageInterest.PrematchMessagesOnly) ||
                sessionsData.containsValue(MessageInterest.LiveMessagesOnly) ||
                 sessionsData.containsValue(MessageInterest.VirtualSports);
    }

    private static boolean haveBothLowAndHigh(Map<Integer, MessageInterest> sessionsData) {
        Preconditions.checkNotNull(sessionsData);
        return sessionsData.containsValue(MessageInterest.LoPrioMessagesOnly) &&
                sessionsData.containsValue(MessageInterest.HiPrioMessagesOnly);
    }
}
