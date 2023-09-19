package com.sportradar.unifiedodds.sdk;

import static org.junit.Assert.*;

import com.sportradar.unifiedodds.sdk.entities.ResourceTypeGroup;
import com.sportradar.utils.Urn;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class UrnTests {

    @Test
    public void defaultUrnTest() {
        Urn urn = new Urn("sr", "match", 12345);
        assertNotNull(urn);
        assertEquals("sr", urn.getPrefix());
        assertEquals("match", urn.getType());
        assertEquals(ResourceTypeGroup.MATCH, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }

    @Test
    public void customEventUrnTest() {
        Urn urn = new Urn("ccc", "match", 12345);
        assertNotNull(urn);
        assertEquals("ccc", urn.getPrefix());
        assertEquals("match", urn.getType());
        assertEquals(ResourceTypeGroup.MATCH, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }

    @Test
    public void customSimpleTournamentEventUrnTest() {
        Urn urn = new Urn("ccc", "simple_tournament", 12345);
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
        Urn urn = Urn.parse(urnStr);
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
        Urn urn = Urn.parse(urnStr);
        assertNotNull(urn);
        assertEquals("sr", urn.getPrefix());
        assertEquals("abcde", urn.getType());
        assertEquals(ResourceTypeGroup.OTHER, urn.getGroup());
        assertFalse(urn.isSimpleTeam());
        assertEquals(12345, urn.getId());
    }
}
