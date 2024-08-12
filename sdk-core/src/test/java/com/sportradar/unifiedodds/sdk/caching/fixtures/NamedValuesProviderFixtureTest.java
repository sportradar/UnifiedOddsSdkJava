/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import lombok.val;
import org.junit.jupiter.api.Test;

public class NamedValuesProviderFixtureTest {

    private final int id = 43;
    private final String description = "description";

    private NamedValuesProviderFixture fixture = new NamedValuesProviderFixture();

    @Test
    public void shouldNotStubNullBetstopReason() {
        assertThatThrownBy(() -> fixture.stubBetstopReason(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("betstopReason");
    }

    @Test
    public void stubbedBetstopReasonShouldPreserveId() {
        val betstopReason = new NamedValueStub(id, "any");

        fixture.stubBetstopReason(betstopReason);

        assertThat(fixture.getBetStopReasons().getNamedValue(id).getId()).isEqualTo(id);
    }

    @Test
    public void stubbedBetstopReasonShouldPreserveDescription() {
        val betstopReason = new NamedValueStub(id, description);

        fixture.stubBetstopReason(betstopReason);

        assertThat(fixture.getBetStopReasons().getNamedValue(id).getDescription()).isEqualTo(description);
    }

    @Test
    public void shouldPreserveIdWhenProvidingUnstubbedBetstopReason() {
        assertThat(fixture.getBetStopReasons().getNamedValue(id).getId()).isEqualTo(id);
    }

    @Test
    public void whenNotStubbedShouldProvideBetstopReasonWithoutDescription() {
        assertThat(fixture.getBetStopReasons().getNamedValue(id).getDescription()).isNull();
    }

    @Test
    public void shouldPreserveIdWhenProvidingUnrelatedBetstopReasonToOneWhichIsStubbed() {
        final int unrelatedId = 67;
        val betstopReason = new NamedValueStub(id, description);

        fixture.stubBetstopReason(betstopReason);

        assertThat(fixture.getBetStopReasons().getNamedValue(unrelatedId).getId()).isEqualTo(unrelatedId);
    }

    @Test
    public void providingUnrelatedBetstopReasonToOneWhichIsStubbedShouldContainNoDescription() {
        final int unrelatedId = 67;
        val betstopReason = new NamedValueStub(id, description);

        fixture.stubBetstopReason(betstopReason);

        assertThat(fixture.getBetStopReasons().getNamedValue(unrelatedId).getDescription()).isNull();
    }

    @Test
    public void shouldNotStubNullBettingStatus() {
        assertThatThrownBy(() -> fixture.stubBettingStatus(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("bettingStatus");
    }

    @Test
    public void stubbedBettingStatusShouldPreserveId() {
        val betstopReason = new NamedValueStub(id, "any");

        fixture.stubBettingStatus(betstopReason);

        assertThat(fixture.getBettingStatuses().getNamedValue(id).getId()).isEqualTo(id);
    }

    @Test
    public void stubbedBettinsStatusShouldPreserveDescription() {
        val betstopReason = new NamedValueStub(id, description);

        fixture.stubBettingStatus(betstopReason);

        assertThat(fixture.getBettingStatuses().getNamedValue(id).getDescription()).isEqualTo(description);
    }

    @Test
    public void shouldPreserveIdWhenProvidingUnstubbedBettingStatus() {
        assertThat(fixture.getBettingStatuses().getNamedValue(id).getId()).isEqualTo(id);
    }

    @Test
    public void whenNotStubbedShouldProvideBettingStatusWithoutDescription() {
        assertThat(fixture.getBettingStatuses().getNamedValue(id).getDescription()).isNull();
    }

    @Test
    public void shouldPreserveIdWhenProvidingUnrelatedBettingStatusToOneWhichIsStubbed() {
        final int unrelatedId = 67;
        val bettingStatus = new NamedValueStub(id, description);

        fixture.stubBettingStatus(bettingStatus);

        assertThat(fixture.getBettingStatuses().getNamedValue(unrelatedId).getId()).isEqualTo(unrelatedId);
    }

    @Test
    public void providingUnrelatedBettingStatusToOneWhichIsStubbedShouldContainNoDescription() {
        final int unrelatedId = 67;
        val bettingStatus = new NamedValueStub(id, description);

        fixture.stubBettingStatus(bettingStatus);

        assertThat(fixture.getBettingStatuses().getNamedValue(unrelatedId).getDescription()).isNull();
    }
}
