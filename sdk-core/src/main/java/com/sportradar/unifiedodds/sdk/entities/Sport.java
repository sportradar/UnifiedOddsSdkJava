/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods implemented by classes representing a sport
 */
public interface Sport extends SportSummary {
    /**
     * Returns an unmodifiable {@link List} representing categories
     * which belong to the sport represented by the current instance
     *
     * @return  - an unmodifiable {@link List} representing categories
     * which belong to the sport represented by the current instance
     */
    List<Category> getCategories();
}
