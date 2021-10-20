package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.ResourceTypeGroup;
import com.sportradar.utils.URN;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class UrnTests {

    @Test
    public void defaultUrnTest() {
        URN urn = new URN("sr", "match", 12345);
        assertNotNull(urn);
        assertEquals("sr", urn.getPrefix());
        assertEquals("match", urn.getType());
        assertEquals(ResourceTypeGroup.MATCH, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }

    @Test
    public void customEventUrnTest() {
        URN urn = new URN("ccc", "match", 12345);
        assertNotNull(urn);
        assertEquals("ccc", urn.getPrefix());
        assertEquals("match", urn.getType());
        assertEquals(ResourceTypeGroup.MATCH, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }
}
