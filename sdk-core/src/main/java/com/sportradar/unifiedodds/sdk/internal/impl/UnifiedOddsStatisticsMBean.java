/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

@SuppressWarnings({ "AbbreviationAsWordInName" })
public interface UnifiedOddsStatisticsMBean {
    public int getNumberOfMessagesReceived();

    public long getTimeOfLastMessageReceived();

    public int getNumberOfRecoveryMessagesReceived();

    public int getNumberOfOddsChangesReceived();

    public int getNumberOfBetSettlementsReceived();

    public int getNumberOfRollbackBetSettlementsReceived();

    public int getNumberOfBetCancelsReceived();

    public int getNumberOfRollbackBetCancelsReceived();

    public int getNumberOfFixtureChangesReceived();

    public int getSecondsSinceStart();

    public int getNumberOfHttpGetStreaming();

    public int getNumberOfHttpGetJaxb();

    public String getLastHttpGetURL();

    public int getNumberOfCachePurgesDone();

    public long getTimeSpentPurgingCaches();

    public int getNumberOfLongProcessingTimes();

    public long getLongMessageProcessingTimeInMs();

    public long getXmlDeserilizationTimeInMs();

    public int getNumberOfLiveMessages();

    public int getNumberOfPrematchMessages();

    public long getBytesReceived();
}
