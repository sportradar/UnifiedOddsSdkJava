/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes used to set general configuration properties
 * <p>
 *     Types associated with {@link ConfigurationBuilder} represent a re-factored approach to building SDK configuration and
 *     therefore make {@link OddsFeedConfigurationBuilder} related instances obsolete. The {@link OddsFeedConfigurationBuilder}
 *     and related instances cannot be removed in order not to introduce braking changes.
 * </p>
 */
public interface ConfigurationBuilder extends RecoveryConfigurationBuilder<ConfigurationBuilder> {}
