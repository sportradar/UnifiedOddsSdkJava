package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;

public class FeedMessage {

    public long Timestamp;

    public SportEvent Event;

    public String MsgType;

    public FeedMessage(long timestamp, SportEvent sportEvent, String msgType) {
        Timestamp = timestamp;
        Event = sportEvent;
        MsgType = msgType;
    }
}
