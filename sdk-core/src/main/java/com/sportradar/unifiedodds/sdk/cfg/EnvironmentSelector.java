/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes taking care of the 2nd step when building configuration - selecting the environment.
 */
public interface EnvironmentSelector {
    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     *
     * @deprecated in favour of {{@link #selectIntegration()}} from v2.0.18
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     */
    @Deprecated
    default ConfigurationBuilder selectStaging() { return selectIntegration(); };

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     */
    ConfigurationBuilder selectIntegration();

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

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access specified environment.
     * (for accessing replay or custom server use selectReplay or selectCustom)
     * @param environment a {@link Environment} specifying to which environment to connect
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access specified environment
     */
    ConfigurationBuilder selectEnvironment(Environment environment);
}
