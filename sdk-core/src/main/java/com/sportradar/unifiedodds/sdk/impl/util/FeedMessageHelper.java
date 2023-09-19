package com.sportradar.unifiedodds.sdk.impl.util;

import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;

@SuppressWarnings({ "HideUtilityClassConstructor", "UnnecessaryParentheses" })
public class FeedMessageHelper {

    /**
     * Provides the id of the message producer
     *
     * @param o - the message from which the producerId should be provided
     * @return - the id of the message producer
     */
    public static int provideProducerIdFromMessage(UnmarshalledMessage o) {
        int producerId;
        if (o instanceof UfOddsChange) {
            producerId = ((UfOddsChange) o).getProduct();
        } else if (o instanceof UfBetStop) {
            producerId = ((UfBetStop) o).getProduct();
        } else if (o instanceof UfBetSettlement) {
            producerId = ((UfBetSettlement) o).getProduct();
        } else if (o instanceof UfRollbackBetSettlement) {
            producerId = ((UfRollbackBetSettlement) o).getProduct();
        } else if (o instanceof UfBetCancel) {
            producerId = ((UfBetCancel) o).getProduct();
        } else if (o instanceof UfFixtureChange) {
            producerId = ((UfFixtureChange) o).getProduct();
        } else if (o instanceof UfRollbackBetCancel) {
            producerId = ((UfRollbackBetCancel) o).getProduct();
        } else if (o instanceof UfSnapshotComplete) {
            producerId = ((UfSnapshotComplete) o).getProduct();
        } else if (o instanceof UfAlive) {
            producerId = ((UfAlive) o).getProduct();
        } else {
            producerId = UnifiedFeedConstants.UNKNOWN_PRODUCER_ID;
        }

        return producerId;
    }

    /**
     * Returns a built cache key for the provided {@link UfFixtureChange}
     *
     * @param fixtureChange the object for which the key is needed
     * @return a built cache key for the provided {@link UfFixtureChange}
     */
    public static String generateFixtureChangeCacheKey(UfFixtureChange fixtureChange) {
        return (
            fixtureChange.getProduct() + "_" + fixtureChange.getEventId() + "_" + fixtureChange.getTimestamp()
        );
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
        if (o instanceof UfOddsChange) {
            eventId = ((UfOddsChange) o).getEventId();
        } else if (o instanceof UfBetStop) {
            eventId = ((UfBetStop) o).getEventId();
        } else if (o instanceof UfBetSettlement) {
            eventId = ((UfBetSettlement) o).getEventId();
        } else if (o instanceof UfRollbackBetSettlement) {
            eventId = ((UfRollbackBetSettlement) o).getEventId();
        } else if (o instanceof UfBetCancel) {
            eventId = ((UfBetCancel) o).getEventId();
        } else if (o instanceof UfFixtureChange) {
            eventId = ((UfFixtureChange) o).getEventId();
        } else if (o instanceof UfRollbackBetCancel) {
            eventId = ((UfRollbackBetCancel) o).getEventId();
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
        if (o instanceof UfOddsChange) {
            timestamp = ((UfOddsChange) o).getTimestamp();
        } else if (o instanceof UfBetStop) {
            timestamp = ((UfBetStop) o).getTimestamp();
        } else if (o instanceof UfAlive) {
            timestamp = ((UfAlive) o).getTimestamp();
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
        if (o instanceof UfOddsChange) {
            timestamp = ((UfOddsChange) o).getTimestamp();
        } else if (o instanceof UfBetStop) {
            timestamp = ((UfBetStop) o).getTimestamp();
        } else if (o instanceof UfBetSettlement) {
            timestamp = ((UfBetSettlement) o).getTimestamp();
        } else if (o instanceof UfRollbackBetSettlement) {
            timestamp = ((UfRollbackBetSettlement) o).getTimestamp();
        } else if (o instanceof UfBetCancel) {
            timestamp = ((UfBetCancel) o).getTimestamp();
        } else if (o instanceof UfFixtureChange) {
            timestamp = ((UfFixtureChange) o).getTimestamp();
        } else if (o instanceof UfRollbackBetCancel) {
            timestamp = ((UfRollbackBetCancel) o).getTimestamp();
        } else if (o instanceof UfSnapshotComplete) {
            timestamp = ((UfSnapshotComplete) o).getTimestamp();
        } else if (o instanceof UfAlive) {
            timestamp = ((UfAlive) o).getTimestamp();
        } else {
            timestamp = 0;
        }

        return timestamp;
    }

    /**
     * Provides the request id of the message (if present)
     *
     * @param o - the message from which the requestId should be provided
     * @return - the id of the recovery request associated with the message
     */
    public static Long provideRequestIdFromMessage(UnmarshalledMessage o) {
        Long requestId;
        if (o instanceof UfOddsChange) {
            requestId = ((UfOddsChange) o).getRequestId();
        } else if (o instanceof UfBetStop) {
            requestId = ((UfBetStop) o).getRequestId();
        } else if (o instanceof UfBetSettlement) {
            requestId = ((UfBetSettlement) o).getRequestId();
        } else if (o instanceof UfRollbackBetSettlement) {
            requestId = ((UfRollbackBetSettlement) o).getRequestId();
        } else if (o instanceof UfBetCancel) {
            requestId = ((UfBetCancel) o).getRequestId();
        } else if (o instanceof UfFixtureChange) {
            requestId = ((UfFixtureChange) o).getRequestId();
        } else if (o instanceof UfRollbackBetCancel) {
            requestId = ((UfRollbackBetCancel) o).getRequestId();
        } else if (o instanceof UfSnapshotComplete) {
            requestId = ((UfSnapshotComplete) o).getRequestId();
            //        } else if (o instanceof UfAlive) {
            //            requestId = ((UfAlive) o).getRequestId();
        } else {
            requestId = null;
        }

        return requestId;
    }
}
