/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import java.util.Optional;

public class GlobalVariables {

    private Optional<ProducerId> producer = Optional.empty();
    private Optional<SportEvent> sportEvent = Optional.empty();

    public ProducerId getProducer() {
        return producer.get();
    }

    public void setProducer(ProducerId producerId) {
        this.producer = Optional.of(producerId);
    }

    public SportEvent getSportEvent() {
        return sportEvent.get();
    }

    public void setSportEvent(SportEvent sportEvent) {
        this.sportEvent = Optional.of(sportEvent);
    }
}
