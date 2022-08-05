package com.sportradar.unifiedodds.sdk.integration.fixtures.sdk;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AliveHeartbeatExpectations {

    private final AliveHeartbeatFixture fixture;

    public AliveHeartbeatExpectations withFrequencySecs(int frequencySecs) {
        fixture.withFrequencySecs(frequencySecs);
        return this;
    }

    public AliveHeartbeatExpectations forBookmakerID(int bookmakerID) {
        fixture.forBookmakerID(bookmakerID);
        return this;
    }

    public void isRunning() {
        fixture.isRunning();
    }
}
