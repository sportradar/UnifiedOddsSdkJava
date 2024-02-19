/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.datamodel.UfAlive;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import java.util.List;

public interface RawMessagesQuerier {
    public List<ReceivedRawMessage<UfAlive>> findAlivesOf(MessageInterest interest);
}
