/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiSportEvent;
import com.sportradar.utils.Urn;
import java.util.Optional;

public class GlobalVariables {

    private Optional<ProducerId> producer = Optional.empty();
    private Optional<Urn> sportEventUrn = Optional.empty();
    private Optional<Sport> sportUrn = Optional.empty();
    private final int nodeId = 1;

    public ProducerId getProducer() {
        return producer.get();
    }

    public GlobalVariables setProducer(ProducerId producerId) {
        this.producer = Optional.of(producerId);
        return this;
    }

    public Urn getSportEventUrn() {
        return sportEventUrn.get();
    }

    public GlobalVariables setSportEventUrn(SportEvent sportEventUrn) {
        this.sportEventUrn = Optional.of(sportEventUrn.getUrn());
        return this;
    }

    public GlobalVariables setSportEventUrn(SapiSportEvent sportEvent) {
        this.sportEventUrn = Optional.of(Urn.parse(sportEvent.getId()));
        return this;
    }

    public GlobalVariables setSportEventUrn(Urn urn) {
        this.sportEventUrn = Optional.of(urn);
        return this;
    }

    public Sport getSportUrn() {
        return sportUrn.get();
    }

    public GlobalVariables setSportUrn(Sport sportUrn) {
        this.sportUrn = Optional.of(sportUrn);
        return this;
    }

    public int getNodeId() {
        return nodeId;
    }
}
