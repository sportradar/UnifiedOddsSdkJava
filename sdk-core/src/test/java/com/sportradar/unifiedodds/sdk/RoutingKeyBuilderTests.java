/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedMessageInterestCombination;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

/**
 * Created on 03/07/2018.
 * // TODO @eti: Javadoc
 */
public class RoutingKeyBuilderTests {
    private static final String SNAPSHOT_COMPLETE_ROUTING_KEY = "-.-.-.snapshot_complete.#";

    private Map<Integer, MessageInterest> createdSessions = new HashMap<>();
    private Map<Integer, List<String>> validationMap = new HashMap<>();

    @Before
    public void beforeAll() {
        createdSessions.clear();
    }

    @Test
    public void validMsgInterestsCombination_Test1() {
        createdSessions.put(1, MessageInterest.AllMessages);

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test1_nodeId() {
        createdSessions.put(1, MessageInterest.AllMessages);

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfgWithNodeId());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_1() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_2_1() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);
        createdSessions.put(2, MessageInterest.LiveMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_2_2() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);
        createdSessions.put(3, MessageInterest.VirtualSports);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_3() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);
        createdSessions.put(2, MessageInterest.LiveMessagesOnly);
        createdSessions.put(3, MessageInterest.VirtualSports);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_3_nodeId() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);
        createdSessions.put(2, MessageInterest.LiveMessagesOnly);
        createdSessions.put(3, MessageInterest.VirtualSports);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".46.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfgWithNodeId());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_1_1() {
        createdSessions.put(1, MessageInterest.HiPrioMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_1_2() {
        createdSessions.put(2, MessageInterest.LoPrioMessagesOnly);

        validationMap.put(2, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_2() {
        createdSessions.put(1, MessageInterest.HiPrioMessagesOnly);
        createdSessions.put(2, MessageInterest.LoPrioMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0)
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_2_nodeId() {
        createdSessions.put(1, MessageInterest.HiPrioMessagesOnly);
        createdSessions.put(2, MessageInterest.LoPrioMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0)
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfgWithNodeId());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test1() {
        createdSessions.put(1, MessageInterest.PrematchMessagesOnly);
        createdSessions.put(3, MessageInterest.HiPrioMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test2() {
        createdSessions.put(1, MessageInterest.AllMessages);
        createdSessions.put(3, MessageInterest.HiPrioMessagesOnly);

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test3() {
        createdSessions.put(1, MessageInterest.AllMessages);
        createdSessions.put(3, MessageInterest.VirtualSports);

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test4() {
        createdSessions.put(1, MessageInterest.LoPrioMessagesOnly);
        createdSessions.put(3, MessageInterest.VirtualSports);

        validationMap.put(1, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    private static void compareResults(Map<Integer, List<String>> result, Map<Integer, List<String>> validationMap) {
        Assert.assertEquals("Result map size doesn't match validation map size", result.size(), validationMap.size());

        validationMap.forEach((sessionHash, expectedRoutingKeys) -> {
            List<String> calculatedRoutingKeys = result.get(sessionHash);

            Assert.assertNotNull("Calculated routing keys per session are missing routing keys for session with hash: " + sessionHash, calculatedRoutingKeys);
            Assert.assertTrue("Calculated session routing keys are missing some routing keys", listEqualsIgnoreOrder(calculatedRoutingKeys, expectedRoutingKeys));
        });
    }

    private static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }

    private static SDKInternalConfiguration getMockedCfg() {
        SDKInternalConfiguration c = Mockito.mock(SDKInternalConfiguration.class);
        Mockito.when(c.getSdkNodeId()).thenReturn(null);
        return c;
    }

    private static SDKInternalConfiguration getMockedCfgWithNodeId() {
        SDKInternalConfiguration c = getMockedCfg();
        Mockito.when(c.getSdkNodeId()).thenReturn(46);
        return c;
    }
}
