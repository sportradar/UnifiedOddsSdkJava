package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Additional probability attributes for markets which potentially will be (partly) refunded
 * This is valid only for those markets which are sent with x.0, x.25 and x.75 lines and in addition the "no bet" markets (draw no bet, home no bet, ...)
 */
public interface AdditionalProbabilities {
    /**
     * The win probability
     * @return the win probability
     */
    Double getWin();
    /**
     * The lose probability
     * @return the lose probability
     */
    Double getLose();
    /**
     * The half win probability
     * @return the half win probability
     */
    Double getHalfWin();
    /**
     * The half lose probability
     * @return the half lose probability
     */
    Double getHalfLose();
    /**
     * The refund probability
     * @return the refund probability
     */
    Double getRefund();
}
