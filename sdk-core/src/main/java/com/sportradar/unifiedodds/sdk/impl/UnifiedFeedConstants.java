/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

/**
 * Created on 28/06/2017.
 * // TODO @eti: Javadoc
 */
public final class UnifiedFeedConstants {
    public static final String PRODUCTION_MESSAGING_HOST = "mq.betradar.com";
    public static final String PRODUCTION_API_HOST = "api.betradar.com";

    public static final String INTEGRATION_MESSAGING_HOST = "stgmq.betradar.com";
    public static final String INTEGRATION_API_HOST = "stgapi.betradar.com";

    public static final String REPLAY_MESSAGING_HOST = "replaymq.betradar.com";
    public static final String REPLAY_API_HOST = PRODUCTION_API_HOST;

    public static final int UNKNOWN_PRODUCER_ID = 99;
    public static final String SPECIFIERS_DELIMITER = "\\|";
    public static final String MARKET_GROUPS_DELIMITER = "\\|";
    public static final String MARKET_MAPPING_PRODUCTS_DELIMITER = "\\|";
    public static final String VARIANT_DESCRIPTION_NAME = "variant";
    public static final String OUTCOMETEXT_VARIANT_VALUE = "pre:outcometext"; // "playerprops" tag
    public static final String PLAYER_PROPS_MARKET_GROUP = "player_props";
    public static final String FLEX_SCORE_MARKET_ATTRIBUTE_NAME = "is_flex_score";
    public static final String SIMPLETEAM_URN_TYPE = "simpleteam";

    private UnifiedFeedConstants() {}
}
