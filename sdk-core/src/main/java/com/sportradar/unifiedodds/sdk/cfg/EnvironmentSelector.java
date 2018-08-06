/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes taking care of the 2nd step when building configuration - selecting the environment.
 */
public interface EnvironmentSelector {
    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access staging environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access staging environment
     */
    ConfigurationBuilder selectStaging();

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     */
    ConfigurationBuilder selectProduction();

    /**
     * Returns a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     *
     * @return a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     */
    ReplayConfigurationBuilder selectReplay();

    /**
     * Returns a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values (usefull for testing with local AMQP)
     *
     * @return a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values
     */
    CustomConfigurationBuilder selectCustom();
}
