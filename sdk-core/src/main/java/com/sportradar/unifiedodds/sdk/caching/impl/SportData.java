/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Contains basic information about a sport (soccer, basketball, ...)
 */
public class SportData extends SportEntityData {
    /**
     * A {@link List} representing the categories, which belong to the sport
     * represented by the current instance
     */
    private final List<CategoryData> categories;

    /**
     * Initializes a new instance of the {@link SportData} class
     *
     * @param id - The {@link URN} representing the id of the item
     * @param names - A {@link Map} containing translated sport name
     * @param categories - A {@link List} representing the categories, which belong to the sport
     */
    SportData(URN id, Map<Locale, String> names, List<CategoryData> categories) {
        super(id, names);

        Preconditions.checkNotNull(categories);

        this.categories = ImmutableList.copyOf(categories);
    }

    /**
     * Returns a {@link List} representing the categories, which belong to the sport
     * represented by the current instance
     *
     * @return - A {@link List} representing the categories, which belong to the sport
     * represented by the current instance
     */
    public List<CategoryData> getCategories() {
        return categories;
    }
}
