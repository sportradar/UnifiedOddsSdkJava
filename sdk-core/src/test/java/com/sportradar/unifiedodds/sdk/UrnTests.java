package com.sportradar.unifiedodds.sdk;

import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.entities.ResourceTypeGroup;
import com.sportradar.utils.URN;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
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

    @Test
    public void customSimpleTournamentEventUrnTest() {
        URN urn = new URN("ccc", "simple_tournament", 12345);
        assertNotNull(urn);
        assertEquals("ccc", urn.getPrefix());
        assertEquals("simple_tournament", urn.getType());
        assertEquals(ResourceTypeGroup.TOURNAMENT, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }

    @Test
    public void tournamentRoundWithGroupUrnType() {
        String urnStr = "sr:group:12345";
        URN urn = URN.parse(urnStr);
        assertNotNull(urn);
        assertEquals("sr", urn.getPrefix());
        assertEquals("group", urn.getType());
        assertEquals(ResourceTypeGroup.OTHER, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }

    @Test
    public void parseCustomUrnType() {
        String urnStr = "sr:abcde:12345";
        URN urn = URN.parse(urnStr);
        assertNotNull(urn);
        assertEquals("sr", urn.getPrefix());
        assertEquals("abcde", urn.getType());
        assertEquals(ResourceTypeGroup.OTHER, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }
}
