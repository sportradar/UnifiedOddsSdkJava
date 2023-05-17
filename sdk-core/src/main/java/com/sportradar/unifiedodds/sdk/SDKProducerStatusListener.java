/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;

/**
 * Defines a contract used to observe instances which provide information about a producer ({@link ProducerUp}
 * and {@link ProducerDown} messages)
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength" })
public interface SDKProducerStatusListener {
    /**
     * Invoked when a producer serving messages via the feed is down
     * (the SDK detected an invalid producer state)
     *
     * @param producerDown A {@link ProducerDown} instance
     *        specifying the associated producer and reason
     *
     * @deprecated from v2.0.8 in favour of {@link #onProducerStatusChange(ProducerStatus)}
     */
    @Deprecated
    void onProducerDown(ProducerDown producerDown);

    /**
     * Invoked when a producer gets reawaken and the SDK gets up in sync with it
     *
     * @param producerUp the object containing information about the producer status update
     *
     * @deprecated from v2.0.8 in favour of {@link #onProducerStatusChange(ProducerStatus)}
     */
    @Deprecated
    void onProducerUp(ProducerUp producerUp);

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
    default void onProducerStatusChange(ProducerStatus producerStatus) {
        // roll out default behaviour - it is advised to update the code to use this method instead of the onProducerDown/onProducerUp
    }

    /**
     * Invoked when the recovery is initiated
     *
     * @param recoveryInitiated the new {@link RecoveryInitiated}
     * @since v2.0.51
     */
    default void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {
        // roll out default behaviour - it is advised to update the code to use this method instead of the onProducerDown/onProducerUp
    }
}
