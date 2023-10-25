/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusChange;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;

/**
 * Defines a contract used to observe instances which provide information about a producer ({@link ProducerStatusChange}
 * messages)
 */
@SuppressWarnings({ "LineLength" })
public interface SdkProducerStatusListener {
    /**
     * Invoked when the producer status changes, some examples:
     * <ul>
     *     <li>Producer up to producer down</li>
     *     <li>Producer down(processing queue delay) to producer down(alive interval violation)</li>
     *     <li>Producer down to producer up</li>
     *     <li>...</li>
     * </ul>
     *
     * @param producerStatus the new {@link ProducerStatus}
     * @since v2.0.8
     */
    void onProducerStatusChange(ProducerStatus producerStatus);

    /**
     * Invoked when the recovery is initiated
     *
     * @param recoveryInitiated the new {@link RecoveryInitiated}
     * @since v2.0.51
     */
    void onRecoveryInitiated(RecoveryInitiated recoveryInitiated);
}
