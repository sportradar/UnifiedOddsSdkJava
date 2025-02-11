/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import java.util.List;

/**
 * Created on 03/07/2017.
 * // TODO @eti: Javadoc
 */
public interface ProducerDataProvider {
    List<ProducerData> getAvailableProducers();
}
