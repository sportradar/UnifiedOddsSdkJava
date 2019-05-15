/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedMessageInterestCombination;
import com.sportradar.utils.URN;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

/**
 * Created on 03/07/2018.
 * // TODO @eti: Javadoc
 */
public class RoutingKeyBuilderTests {
    private static final int SDK_NODE_ID = 46;
    private static final String SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID = "-.-.-.snapshot_complete.-.-.-.-";
    private static final String SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID = "-.-.-.snapshot_complete.-.-.-." + SDK_NODE_ID;

    private Map<Integer, Entry<MessageInterest, Set<URN>>> createdSessions = new HashMap<>();
    private Map<Integer, List<String>> validationMap = new HashMap<>();

    @Before
    public void beforeAll() {
        createdSessions.clear();
    }

    @Test
    public void validMsgInterestsCombination_Test1() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.AllMessages, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test1_nodeId() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.AllMessages, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfgWithNodeId());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_1() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_2_1() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LiveMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_2_2() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.VirtualSports, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_3() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LiveMessagesOnly, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.VirtualSports, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test2_3_nodeId() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LiveMessagesOnly, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.VirtualSports, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID
        ));
        validationMap.put(2, Arrays.asList(
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.LiveMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".46.#",
                MessageInterest.VirtualSports.getRoutingKeys().get(1) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfgWithNodeId());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_1_1() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.HiPrioMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_1_2() {
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LoPrioMessagesOnly, null));

        validationMap.put(2, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test
    public void validMsgInterestsCombination_Test3_2() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.HiPrioMessagesOnly, null));
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LoPrioMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
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
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.HiPrioMessagesOnly, null));
        createdSessions.put(2, new SimpleEntry<>(MessageInterest.LoPrioMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".46.#",
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".-.#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITH_NODE_ID
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
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.PrematchMessagesOnly, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.HiPrioMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.PrematchMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test2() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.AllMessages, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.HiPrioMessagesOnly, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.HiPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test3() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.AllMessages, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.VirtualSports, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.AllMessages.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));

        Map<Integer, List<String>> result = OddsFeedRoutingKeyBuilder.generateKeys(createdSessions, getMockedCfg());

        compareResults(result, validationMap);
    }

    @Test(expected = UnsupportedMessageInterestCombination.class)
    public void invalidMsgInterestCombination_Test4() {
        createdSessions.put(1, new SimpleEntry<>(MessageInterest.LoPrioMessagesOnly, null));
        createdSessions.put(3, new SimpleEntry<>(MessageInterest.VirtualSports, null));

        validationMap.put(1, Arrays.asList(
                MessageInterest.LoPrioMessagesOnly.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
        ));
        validationMap.put(3, Arrays.asList(
                MessageInterest.VirtualSports.getRoutingKeys().get(0) + ".#",
                MessageInterest.SystemAliveMessages.getRoutingKeys().get(0),
                SNAPSHOT_COMPLETE_ROUTING_KEY_WITHOUT_NODE_ID
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
        Mockito.when(c.getSdkNodeId()).thenReturn(SDK_NODE_ID);
        return c;
    }
}
