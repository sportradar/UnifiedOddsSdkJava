/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import com.sportradar.utils.URN;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link RoutingKeyParser} implementation that uses regex for key parsing
 */
public class RegexRoutingKeyParser implements RoutingKeyParser {
    /**
     * The group name used by the {@link #REGEX_PATTERN} to store the sport id
     */
    private static final String SPORT_GROUP_NAME = "sportId";

    /**
     * The group name used by the {@link #REGEX_PATTERN} to store the event type(match, tournament,...)
     */
    private static final String EVENT_TYPE_GROUP_NAME = "eventType";

    /**
     * The group name used by the {@link #REGEX_PATTERN} to store the event id
     */
    private static final String EVENT_ID_GROUP_NAME = "eventId";

    /**
     * The group name that matches the SDK nodeId routing key part
     */
    private static final String SDK_FEED_NODE_ID = "nodeId";

    /**
     * The regex pattern used to parse the routing key
     */
    private static final String REGEX_STRING =
            "\\A([^.]+)" + // hi-lo
            "\\.([^.]+)" + // -
            "\\.([^.]+)" + // live/prematch
            "\\.([^.]+)" + // message type (alive, odds change,...)
            "\\.(?<" + SPORT_GROUP_NAME + ">((\\d+)|(-)))" +
            "\\.(?<" + EVENT_TYPE_GROUP_NAME + ">((([a-z]+):([a-zA-Z_2]+))|(-)))" +
            "\\.(?<" + EVENT_ID_GROUP_NAME + ">((\\d+)|(-)))" +
            "(\\.(?<" + SDK_FEED_NODE_ID + ">((-?\\d+)|(-))))?" +
            "(\\z)";

    /**
     * A {@link Pattern} instance compiled with the {@link #REGEX_STRING}
     */
    private static final Pattern REGEX_PATTERN = Pattern.compile(REGEX_STRING);

    /**
     * The prefix which is used to build sport {@link URN} identifiers
     */
    private static final String SPORT_ID_PREFIX = "sr:sport:";


    /**
     * Returns a {@link RoutingKeyInfo} containing the parsed routing key data
     *
     * @param routingKey - a complete RabbitMq routing key
     * @return - a {@link RoutingKeyInfo} containing the parsed routing key data
     */
    @Override
    public RoutingKeyInfo getRoutingKeyInfo(String routingKey) {
        Preconditions.checkNotNull(routingKey);

        Matcher matcher = REGEX_PATTERN.matcher(routingKey);

        if (!matcher.find() || (matcher.group(SPORT_GROUP_NAME).equals("-") && matcher.group(EVENT_ID_GROUP_NAME).equals("-"))) {
            return new RoutingKeyInfo(routingKey, true);
        }

        URN sportId;
        if (!matcher.group(SPORT_GROUP_NAME).equals("-")) {
            sportId = URN.parse(SPORT_ID_PREFIX + matcher.group(SPORT_GROUP_NAME));
        } else {
            sportId = null;
        }

        String eventType = matcher.group(EVENT_TYPE_GROUP_NAME);
        String eventIdKey = matcher.group(EVENT_ID_GROUP_NAME);

        URN eventId = null;
        if (!eventType.equals("-") && !eventIdKey.equals("-")) {
            try {
                eventId = URN.parse(eventType + ":" + eventIdKey);
            } catch (UnsupportedUrnFormatException e) {
                // ignore the exception, not a valid sport event routing key
            }
        }

        return new RoutingKeyInfo(routingKey, sportId, eventId);
    }
}
