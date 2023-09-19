/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.sportradar.uf.datamodel.UfChangeType;
import com.sportradar.uf.datamodel.UfFixtureChange;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChange;
import com.sportradar.unifiedodds.sdk.oddsentities.FixtureChangeType;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Date;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
class FixtureChangeImpl<T extends SportEvent> extends EventMessageImpl<T> implements FixtureChange<T> {

    private final FixtureChangeType changeType;
    private final Date nextLiveTime;
    private final Date startTime;

    FixtureChangeImpl(
        T sportEvent,
        UfFixtureChange message,
        Producer producer,
        byte[] rawMessage,
        MessageTimestamp timestamp
    ) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        UfChangeType type = message.getChangeType();
        if (type == null) {
            changeType = FixtureChangeType.NotAvailable;
        } else {
            switch (type) {
                case NEW:
                    changeType = FixtureChangeType.New;
                    break;
                case DATETIME:
                    changeType = FixtureChangeType.TimeUpdate;
                    break;
                case CANCELLED:
                    changeType = FixtureChangeType.Cancelled;
                    break;
                case FORMAT:
                    changeType = FixtureChangeType.Format;
                    break;
                case COVERAGE:
                    changeType = FixtureChangeType.Coverage;
                    break;
                case PITCHER:
                    changeType = FixtureChangeType.Pitcher;
                    break;
                default:
                    changeType = FixtureChangeType.OtherChange;
                    break;
            }
        }

        nextLiveTime = message.getNextLiveTime() == null ? null : new Date(message.getNextLiveTime());
        startTime = new Date(message.getStartTime());
    }

    @Override
    public FixtureChangeType getChangeType() {
        return changeType;
    }

    @Override
    public Date getNextLiveTime() {
        return nextLiveTime;
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }
}
