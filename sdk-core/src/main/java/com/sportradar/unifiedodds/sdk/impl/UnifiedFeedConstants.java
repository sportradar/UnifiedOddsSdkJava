/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

/**
 * Created on 28/06/2017.
 * // TODO @eti: Javadoc
 */
public final class UnifiedFeedConstants {

    public static final int UNKNOWN_PRODUCER_ID = 99;
    public static final String SPECIFIERS_DELIMITER = "\\|";
    public static final String MARKET_GROUPS_DELIMITER = "\\|";
    public static final String MARKET_MAPPING_PRODUCTS_DELIMITER = "\\|";
    public static final String VARIANT_DESCRIPTION_NAME = "variant";
    public static final String OUTCOMETEXT_VARIANT_VALUE = "pre:outcometext"; // "playerprops" tag
    public static final String FREETEXT_VARIANT_VALUE = "free_text";
    public static final String PLAYER_PROPS_MARKET_GROUP = "player_props";
    public static final String FLEX_SCORE_MARKET_ATTRIBUTE_NAME = "is_flex_score";
    public static final String PLAYER_URN_TYPE = "player";

    //public static final String SIMPLETEAM_URN_TYPE = "simpleteam";

    private UnifiedFeedConstants() {}
}
