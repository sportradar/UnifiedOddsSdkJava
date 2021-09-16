/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.uf.datamodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnifiedOddsStatistics implements UnifiedOddsStatisticsMBean {
    private static final int LONG_PROCESSING_TIME_THRESHOLD = 50; // ms
    private static final long start = System.currentTimeMillis();

    private int messages;
    private long lastMessageReceived;
    private int betSettlements;
    private int betCancels;
    private int betSettlementRollbacks;
    private int oddsChanges;
    private int streamingHttpGet;
    private int jaxbHttpGet;
    private int recoveryMessages;
    private int fixtureChanges;
    private int betCancelRollbacks;
    private String lastUrl;
    private int purgesDone;
    private long totalPurgeTime;
    private final static Logger logger = LoggerFactory.getLogger(UnifiedOddsStatistics.class);
    private long totalXmlDeserTime;
    private long totalLongProcTime;
    private int longProcessing;
    private int liveMessages;
    private int prematchMessages;
    private long totalMsgSizeReceived;

    private final ThreadLocal<byte[]> tmpBuf = new ThreadLocal<byte[]>() {
        @Override
        public byte[] initialValue() {
            return new byte[200];
        }
    };

    @Override
    public int getNumberOfMessagesReceived() {
        return messages;
    }

    @Override
    public int getNumberOfRecoveryMessagesReceived() {
        return recoveryMessages;
    }

    @Override
    public long getTimeOfLastMessageReceived() {
        return lastMessageReceived;
    }

    @Override
    public int getNumberOfOddsChangesReceived() {
        return oddsChanges;
    }

    @Override
    public int getNumberOfBetSettlementsReceived() {
        return betSettlements;
    }

    @Override
    public int getNumberOfRollbackBetSettlementsReceived() {
        return betSettlementRollbacks;
    }

    public void onMessageReceived(long now, long finished, Object o) {
        lastMessageReceived = now;
        messages++;
        //totalXmlDeserTime += xmlDeserTimeNs;
        if ((finished - now) > LONG_PROCESSING_TIME_THRESHOLD) {
            longProcessing++;
            totalLongProcTime += (finished - now);
        }
        //totalMsgSizeReceived += body.length;
        if (o instanceof UFOddsChange)
            oddsChanges++;
        else if (o instanceof UFBetSettlement)
            betSettlements++;
        else if (o instanceof UFBetCancel)
            betCancels++;
        else if (o instanceof UFRollbackBetCancel)
            betCancelRollbacks++;
        else if (o instanceof UFRollbackBetSettlement)
            betSettlementRollbacks++;
        else if (o instanceof UFFixtureChange)
            fixtureChanges++;
        String msgExcerpt = new String(tmpBuf.get());
        if (msgExcerpt.contains("request_id"))
            recoveryMessages++;
        int pid = msgExcerpt.indexOf("product=\"");
        if (pid != -1) {
            int p = msgExcerpt.charAt(pid + 9) - '0';
            if (p == 1)
                liveMessages++;
            else
                prematchMessages++;
        }
    }

    public void unload() {
        tmpBuf.remove();
    }

    public void onStreamingHttpGet(String path) {
        streamingHttpGet++;
        lastUrl = path;
    }

    public void onJaxbHttpGet(String path) {
        jaxbHttpGet++;
        lastUrl = path;
    }

    @Override
    public int getNumberOfBetCancelsReceived() {
        return betCancels;
    }

    @Override
    public int getNumberOfRollbackBetCancelsReceived() {
        return betCancelRollbacks;
    }

    @Override
    public int getNumberOfFixtureChangesReceived() {
        return fixtureChanges;
    }

    @Override
    public int getSecondsSinceStart() {
        return (int) ((System.currentTimeMillis() - start) / 1000);
    }

    @Override
    public int getNumberOfHttpGetStreaming() {
        return streamingHttpGet;
    }

    @Override
    public int getNumberOfHttpGetJaxb() {
        return jaxbHttpGet;
    }

    @Override
    public String getLastHttpGetURL() {
        return lastUrl;
    }

    @Override
    public int getNumberOfCachePurgesDone() {
        return purgesDone;
    }

    @Override
    public long getTimeSpentPurgingCaches() {
        return totalPurgeTime;
    }

    public void cachePurgeRun(long purgeTime) {
        logger.debug("Trimmed all caches. Took " + purgeTime + "ms");
        purgesDone++;
        totalPurgeTime += purgeTime;
    }

    @Override
    public int getNumberOfLongProcessingTimes() {
        return longProcessing;
    }

    @Override
    public long getLongMessageProcessingTimeInMs() {
        return totalLongProcTime;
    }

    @Override
    public long getXmlDeserilizationTimeInMs() {
        return totalXmlDeserTime / 1000 / 1000;
    }

    @Override
    public int getNumberOfLiveMessages() {
        return liveMessages;
    }

    @Override
    public int getNumberOfPrematchMessages() {
        return prematchMessages;
    }

    @Override
    public long getBytesReceived() {
        return totalMsgSizeReceived;
    }
}
