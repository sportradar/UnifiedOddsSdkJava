/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created on 11/04/2018.
 * // TODO @eti: Javadoc
 */
public class SDKConfigurationYamlReaderTests {
    @Test
    public void basicLoadTest() {
        SDKConfigurationYamlReader sdkConfigurationYamlReader = new SDKConfigurationYamlReader();
        Map<String, String> stringStringMap = sdkConfigurationYamlReader.readConfiguration();

        assertEquals(20, stringStringMap.size());
    }

    @Test
    public void properParametersLoadedTest(){
        SDKConfigurationYamlReader reader = new SDKConfigurationYamlReader();

        assertEquals("test-token-yaml", reader.readAccessToken().get());
        assertEquals(46, (long) reader.readSdkNodeId().get());
        assertEquals(25, (int) reader.readMaxInactivitySeconds().get());
        assertEquals(60, (int) reader.readMaxRecoveryTime().get());
        assertEquals(Locale.ITALIAN, reader.readDefaultLocale().get());
        assertTrue(reader.readDesiredLocales().containsAll(getExpectedDesiredLocales()));
        assertEquals("test-msg-host", reader.readMessagingHost().get());
        assertEquals(7777, (int) reader.readMessagingPort().get());
        assertEquals("msg-uname", reader.readMessagingUsername().get());
        assertEquals("msg-pass", reader.readMessagingPassword().get());
        assertEquals("msg-vhost", reader.readMessagingVirtualHost().get());
        assertTrue(reader.readUseMessagingSsl().get());
        assertEquals("api-host", reader.readApiHost().get());
        assertTrue(reader.readUseApiSsl().get());
        assertTrue(reader.readDisabledProducers().containsAll(getExpectedDisabledProducers()));
        assertEquals(ExceptionHandlingStrategy.Throw, reader.readExceptionHandlingStrategy().get());
        assertTrue(reader.readCleanTrafficLogEntries().get());
        assertEquals(10, (int) reader.readHttpClientTimeout().get());
        assertTrue(reader.readSimpleVariantCaching().get());
        assertTrue(reader.readSchedulerTasksToSkip().containsAll(getExpectedTasksToSkip()));
    }

    private static List<Locale> getExpectedDesiredLocales(){
        return Arrays.asList(Locale.FRENCH, Locale.GERMAN, Locale.ENGLISH);
    }

    private static List<Integer> getExpectedDisabledProducers() {
        return Arrays.asList(5, 6, 7, 8);
    }

    private static List<String> getExpectedTasksToSkip() {
        return Arrays.asList("task1", "task2", "task3");
    }
}
