/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;

/**
 * These are all the messages you can receive relating to odds. You implement this interface to
 * handle received messages.
 *
 */
public interface OddsFeedListener extends GenericOddsFeedListener<SportEvent> {}
