package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.datamodel.UFAlive;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.uf.datamodel.UFSnapshotComplete;

import java.util.Date;
import java.util.Random;

/**
 * Class for building test feed messages
 */
public class FeedMessageBuilder
{
    private final int producerId;

    public FeedMessageBuilder(int producerId) {
        this.producerId = producerId;
    }

    /**
     * Builds UFOddsChange message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFOddsChange message
     */
    public UFOddsChange buildOddsChange(Long eventId, Integer productId, Long requestId, Date timestamp)
    {
        if (eventId != null && eventId.equals(-1L))
        {
            eventId = generateEventId();
        }

        UFOddsChange message = new UFOddsChange();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    /**
     * Builds UFBetStop message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFBetStop message
     */
    public UFBetStop buildBetStop(Long eventId, Integer productId, Long requestId, Date timestamp)
    {
        if (eventId != null && eventId.equals(-1L))
        {
            eventId = generateEventId();
        }

        UFBetStop message = new UFBetStop();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @param subscribed if subscribed attributed is 1 or 0
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId, Date timestamp, boolean subscribed)
    {
        UFAlive message = new UFAlive();
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setSubscribed(subscribed ? 1 : 0);

        return message;
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId, Date timestamp)
    {
        return buildAlive(productId, timestamp, true);
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId)
    {
        return buildAlive(productId, new Date(), true);
    }

    /**
     * Builds UFSnapshotComplete message
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFSnapshotComplete message
     */
    public UFSnapshotComplete buildSnapshotComplete(Integer productId, long requestId, Date timestamp)
    {
        UFSnapshotComplete message = new UFSnapshotComplete();
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    private static Long generateEventId(){
        return (long) Math.abs(new Random().nextInt(999999));
    }
}
