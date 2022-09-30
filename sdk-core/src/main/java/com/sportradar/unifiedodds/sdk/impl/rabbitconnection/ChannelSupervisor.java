/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;

import java.io.IOException;
import java.util.List;

public interface ChannelSupervisor {

    OpeningResult openChannel(List<String> routingKeys, ChannelMessageConsumer channelMessageConsumer, String messageInterest) throws IOException;

    ClosingResult closeChannel() throws IOException;
}
