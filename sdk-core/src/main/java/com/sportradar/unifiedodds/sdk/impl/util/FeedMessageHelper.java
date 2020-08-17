package com.sportradar.unifiedodds.sdk.impl.util;

import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;

public class FeedMessageHelper {

    /**
     * Provides the id of the message producer
     *
     * @param o - the message from which the producerId should be provided
     * @return - the id of the message producer
     */
    public static int provideProducerIdFromMessage(UnmarshalledMessage o) {
        int producerId;
        if (o instanceof UFOddsChange) {
            producerId = ((UFOddsChange) o).getProduct();
        } else if (o instanceof UFBetStop) {
            producerId = ((UFBetStop) o).getProduct();
        } else if (o instanceof UFBetSettlement) {
            producerId = ((UFBetSettlement) o).getProduct();
        } else if (o instanceof UFRollbackBetSettlement) {
            producerId = ((UFRollbackBetSettlement) o).getProduct();
        } else if (o instanceof UFBetCancel) {
            producerId = ((UFBetCancel) o).getProduct();
        } else if (o instanceof UFFixtureChange) {
            producerId = ((UFFixtureChange) o).getProduct();
        } else if (o instanceof UFRollbackBetCancel) {
            producerId = ((UFRollbackBetCancel) o).getProduct();
        } else if (o instanceof UFSnapshotComplete) {
            producerId = ((UFSnapshotComplete) o).getProduct();
        } else if (o instanceof UFAlive) {
            producerId = ((UFAlive) o).getProduct();
        } else {
            producerId = UnifiedFeedConstants.UNKNOWN_PRODUCER_ID;
        }

        return producerId;
    }

    /**
     * Returns a built cache key for the provided {@link UFFixtureChange}
     *
     * @param fixtureChange the object for which the key is needed
     * @return a built cache key for the provided {@link UFFixtureChange}
     */
    public static String generateFixtureChangeCacheKey(UFFixtureChange fixtureChange) {
        return fixtureChange.getProduct() + "_" + fixtureChange.getEventId() + "_" + fixtureChange.getTimestamp();
    }

    /**
     * Provides the id of the associated event if available, otherwise an explanation why
     * the eventId is not available get(ex: for a snapshot complete -> system message)
     *
     * @param o - the message from which the eventIdd should be provided
     * @return - the associated eventId or an explanation why the eventId is not available
     * (ex: for a snapshot complete -> system message)
     */
    public static String provideEventIdFromMessage(UnmarshalledMessage o) {
        String eventId;
        if (o instanceof UFOddsChange) {
            eventId = ((UFOddsChange) o).getEventId();
        } else if (o instanceof UFBetStop) {
            eventId = ((UFBetStop) o).getEventId();
        } else if (o instanceof UFBetSettlement) {
            eventId = ((UFBetSettlement) o).getEventId();
        } else if (o instanceof UFRollbackBetSettlement) {
            eventId = ((UFRollbackBetSettlement) o).getEventId();
        } else if (o instanceof UFBetCancel) {
            eventId = ((UFBetCancel) o).getEventId();
        } else if (o instanceof UFFixtureChange) {
            eventId = ((UFFixtureChange) o).getEventId();
        } else if (o instanceof UFRollbackBetCancel) {
            eventId = ((UFRollbackBetCancel) o).getEventId();
        } else {
            return "System message";
        }

        return eventId;
    }

    /**
     * Provides the message generation timestamp,
     * the generation timestamp is extracted only from the betstop and oddschange message
     *
     * @param o the message from which the timestamp should be provided
     * @return the message generation timestamp if available; otherwise null
     */
    public static Long provideMessageGenTimestampFromMessage(UnmarshalledMessage o) {
        Long timestamp = null;
        if (o instanceof UFOddsChange) {
            timestamp = ((UFOddsChange) o).getTimestamp();
        } else if (o instanceof UFBetStop) {
            timestamp = ((UFBetStop) o).getTimestamp();
        } else if (o instanceof UFAlive) {
            timestamp = ((UFAlive) o).getTimestamp();
        }

        return timestamp;
    }

    /**
     * Provides the message timestamp
     *
     * @param o the message from which the timestamp should be provided
     * @return the message timestamp
     */
    public static long provideGenTimestampFromMessage(UnmarshalledMessage o) {
        long timestamp;
        if (o instanceof UFOddsChange) {
            timestamp = ((UFOddsChange) o).getTimestamp();
        } else if (o instanceof UFBetStop) {
            timestamp = ((UFBetStop) o).getTimestamp();
        } else if (o instanceof UFBetSettlement) {
            timestamp = ((UFBetSettlement) o).getTimestamp();
        } else if (o instanceof UFRollbackBetSettlement) {
            timestamp = ((UFRollbackBetSettlement) o).getTimestamp();
        } else if (o instanceof UFBetCancel) {
            timestamp = ((UFBetCancel) o).getTimestamp();
        } else if (o instanceof UFFixtureChange) {
            timestamp = ((UFFixtureChange) o).getTimestamp();
        } else if (o instanceof UFRollbackBetCancel) {
            timestamp = ((UFRollbackBetCancel) o).getTimestamp();
        } else if (o instanceof UFSnapshotComplete) {
            timestamp = ((UFSnapshotComplete) o).getTimestamp();
        } else if (o instanceof UFAlive) {
            timestamp = ((UFAlive) o).getTimestamp();
        } else {
            timestamp = 0;
        }

        return timestamp;
    }
}