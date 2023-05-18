/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;

/**
 * An implementation of the {@link NamedValue}
 */
public class NamedValueImpl implements NamedValue {

    /**
     * The identifier of the current instance
     */
    private final int id;

    /**
     * The description of the current instance
     */
    private final String description;

    /**
     * Initializes a new instance of {@link NamedValueImpl}
     *
     * @param id - the identifier
     */
    public NamedValueImpl(int id) {
        this(id, null);
    }

    /**
     * Initializes a new instance of {@link NamedValueImpl}
     *
     * @param id - the identifier
     * @param description - a {@link String} describing the new instance
     */
    public NamedValueImpl(int id, String description) {
        Preconditions.checkArgument(id >= 0);

        this.id = id;
        this.description = description;
    }

    /**
     * Returns the identifier of the current instance
     *
     * @return - the identifier of the current instance
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns the description of the current instance
     *
     * @return - the description of the current instance
     */
    @Override
    public String getDescription() {
        return description;
    }
}
