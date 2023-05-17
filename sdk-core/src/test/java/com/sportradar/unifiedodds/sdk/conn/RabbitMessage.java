package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import java.util.Date;

/**
 * Class RabbitMessage
 */
@SuppressWarnings({ "MemberName", "VisibilityModifier" })
public class RabbitMessage {

    /**
     * Gets the message to be sent
     */
    public UnmarshalledMessage Message;

    /**
     * Gets the value when last message was sent (used only for period messages)
     */
    public Date LastSent;

    /**
     * Gets the period on which message should be sent (if set)
     */
    public java.time.Period Period;

    /**
     * Gets a value indicating whether id of the message should be randomized
     */
    public boolean RandomizeId;
}
