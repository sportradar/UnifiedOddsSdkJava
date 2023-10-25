/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.entities.Assist;
import com.sportradar.unifiedodds.sdk.entities.Player;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Represents an assists on a sport event
 *
 * @see PlayerImpl
 * @see Assist
 */
public class AssistImpl extends PlayerImpl implements Assist {

    /**
     *  A {@link String} specifying the type of the assist
     */
    private final String type;

    /**
     * Initializes a new instance of the {@link AssistImpl} class
     *
     * @param id    - the unique {@link Urn} identifier representing the current {@link Player} instance
     * @param names - a {@link Map} containing the available player name translations
     * @param type  - a {@link String} specifying the type of the assist
     */
    public AssistImpl(Urn id, Map<Locale, String> names, String type) {
        super(id, names);
        this.type = type;
    }

    /**
     * Returns a {@link String} specifying the type of the assist
     *
     * @return - a {@link String} specifying the type of the assist
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Returns a {@link String} describing the current {@link Assist} instance
     *
     * @return - a {@link String} describing the current {@link Assist} instance
     */
    @Override
    public String toString() {
        return "AssistImpl{" + "type='" + type + '\'' + "} " + super.toString();
    }
}
