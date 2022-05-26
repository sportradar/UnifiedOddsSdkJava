package com.sportradar.unifiedodds.sdk.impl;

public class Constants {
    public static final String ODDS_CHANGE_KEY = "hi.-.live.odds_change.1.sr:match.10927088.-";
    public static final String ODDS_CHANGE_MSG_URI = "test/feed_xml/odds_change.xml";
    public static final String BET_STOP_KEY = "hi.-.live.bet_stop.1.sr:match.9578495.-";
    public static final String BET_STOP_MSG_URI = "test/feed_xml/bet_stop.xml";
    public static final String BET_SETTLEMENT_KEY = "lo.-.live.bet_settlement.1.sr:match.9583135.-";
    public static final String BET_SETTLEMENT_MSG_URI = "test/feed_xml/bet_settlement.xml";
    public static final String BET_CANCEL_KEY = "hi.-.live.bet_cancel.1.sr:match.10237855.-";
    public static final String BET_CANCEL_MSG_URI = "test/feed_xml/bet_cancel.xml";
    public static final String SNAPSHOT_COMPLETE_KEY = "-.-.-.snapshot_complete.-.-.-.1";
    public static final String SNAPSHOT_COMPLETE_MSG_URI = "test/feed_xml/snapshot_completed.xml";
    public static final String ALIVE_KEY = "-.-.-.alive.-.-.-.-";
    public static final String ALIVE_MSG_URI = "test/feed_xml/alive.xml";

    public static final String FIXTURE_CHANGE_MSG_URI = "test/feed_xml/fixture_change.xml";
    public static final String ROLLBACK_BET_SETTLEMENT_MSG_URI = "test/feed_xml/rollback_bet_settlement.xml";
    public static final String ROLLBACK_BET_CANCEL_MSG_URI = "test/feed_xml/rollback_bet_cancel.xml";

    public static final String SCHEDULE_MSG_URI = "test/rest/schedule.en.xml";

    // local rabbit data for tests
    public static final String RABBIT_IP = "192.168.64.104";
    public static final String SDK_USERNAME = "testuser";
    public static final String SDK_PASSWORD = "testpass";
    public static final String UF_EXCHANGE = "unifiedfeed";
    public static final String UF_VIRTUALHOST = "/virtualhost";
}
