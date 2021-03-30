/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Locale;
import java.util.Map;

/**
 * An interface providing methods to access player details
 */
public interface GoalScorer extends Player {

    /**
     * Returns the method value
     * The attribute can assume values such as 'penalty' and 'own goal'. In case the attribute is not inserted, then the goal is not own goal neither penalty.
     * @return - the method value
     */
    String getMethod();
}
