/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.utils.Urn;

/**
 * A sport entity representation used by caching components
 */
class SportEntityCi {

    /**
     * The id of the represented sport entity
     */
    private final Urn id;

    /**
     * Initializes a new instance of the {@link SportEntityCi} class
     *
     * @param id - The {@link Urn} of the represented sport entity
     */
    protected SportEntityCi(Urn id) {
        Preconditions.checkNotNull(id);

        this.id = id;
    }

    /**
     * Returns the {@link Urn} of the represented sport entity
     *
     * @return - the {@link Urn} of the represented sport entity
     */
    public Urn getId() {
        return id;
    }

    /**
     * Determines whether the specified object is equal to the current object
     *
     * @param obj - the object to compare with the current object
     * @return - true if the specified object is equal to the current object. otherwise, false
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof SportEntityCi) {
            SportEntityCi other = (SportEntityCi) obj;

            return id.equals(other.getId());
        }

        return false;
    }

    /**
     * Serves as the default hash function
     *
     * @return - a hash code for the current object
     */
    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
