package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.impl.ProducerData;

public class ProducerDataBuilder {

    private boolean active;

    static ProducerDataBuilder producerData() {
        return new ProducerDataBuilder();
    }

    public ProducerData withId(int id) {
        return new ProducerData(id, "any", "any", active, "any", "", -1);
    }

    public ProducerDataBuilder active() {
        active = true;
        return this;
    }
}
