/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.utils.URN;

/**
 * A sport entity representation used by caching components
 */
class SportEntityCI {
    /**
     * The id of the represented sport entity
     */
    private final URN id;

    /**
     * Initializes a new instance of the {@link SportEntityCI} class
     *
     * @param id - The {@link URN} of the represented sport entity
     */
    protected SportEntityCI(URN id) {
        Preconditions.checkNotNull(id);

        this.id = id;
    }

    /**
     * Returns the {@link URN} of the represented sport entity
     *
     * @return - the {@link URN} of the represented sport entity
     */
    public URN getId() {
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

        if (obj instanceof SportEntityCI) {
            SportEntityCI other = (SportEntityCI) obj;

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
