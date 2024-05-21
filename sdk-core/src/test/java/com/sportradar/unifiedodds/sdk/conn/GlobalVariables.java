/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.utils.generic.testing.RandomInteger;
import java.util.Optional;

public class GlobalVariables {

    private Optional<ProducerId> producer = Optional.empty();
    private Optional<SportEvent> sportEventUrn = Optional.empty();
    private Optional<Sport> sportUrn = Optional.empty();
    private final int nodeId = 1;

    public ProducerId getProducer() {
        return producer.get();
    }

    public void setProducer(ProducerId producerId) {
        this.producer = Optional.of(producerId);
    }

    public SportEvent getSportEventUrn() {
        return sportEventUrn.get();
    }

    public void setSportEventUrn(SportEvent sportEventUrn) {
        this.sportEventUrn = Optional.of(sportEventUrn);
    }

    public Sport getSportUrn() {
        return sportUrn.get();
    }

    public void setSportUrn(Sport sportUrn) {
        this.sportUrn = Optional.of(sportUrn);
    }

    public int getNodeId() {
        return nodeId;
    }
}
