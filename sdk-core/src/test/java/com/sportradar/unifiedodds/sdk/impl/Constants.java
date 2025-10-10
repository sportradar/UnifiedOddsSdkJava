package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;

@SuppressWarnings({ "HideUtilityClassConstructor", "LineLength" })
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
    public static final boolean IS_LOCAL = System.getenv().get("CI") == null;
    public static final String ADMIN_USERNAME = "guest";
    public static final String ADMIN_PASSWORD = "guest";
    public static final String TOXIPROXY_IP = IS_LOCAL ? "localhost" : "toxiproxy";
    public static final BaseUrl TOXIPROXY_BASE_URL = BaseUrl.of(TOXIPROXY_IP, 8474);
    public static final String RABBIT_IP = IS_LOCAL ? "localhost" : "rabbit";
    public static final BaseUrl RABBIT_MANAGEMENT_BASE_URL = BaseUrl.of(Constants.RABBIT_IP, 15672);
    public static final int PROXIED_RABBIT_PORT = 8089;
    public static final BaseUrl PROXIED_RABBIT_BASE_URL = BaseUrl.of(TOXIPROXY_IP, PROXIED_RABBIT_PORT);
    public static final BaseUrl RABBIT_BASE_URL = BaseUrl.of(RABBIT_IP, 5672);
    public static final BaseUrl RABBIT_BASE_URL_WITHIN_DOCKER_NETWORK = BaseUrl.of(TOXIPROXY_IP, 5672);
    public static final String SDK_USERNAME = "testuser";
    public static final String SDK_PASSWORD = "testpass";
    public static final String UF_EXCHANGE = "unifiedfeed";
    public static final String UF_VIRTUALHOST = "/virtualhost";

    public static final String COMMON_IAM_CLIENT_ID = "ciam-client-id";
    public static final String COMMON_IAM_KEY_ID = "ciam-key-id";
    public static final String COMMON_IAM_PRIVATE_KEY =
        "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCscIgz7vRrfRhcqSxHSl5yNMJocCBepWPdqO5iXNpYK+pzNr5rGjExXOiERaSSTRy6IkvA4byyfGd/yS5ilIO20hyYKfGfORm0ZtUx91USM5KKD+aLIwf/kKbNYQfMcfecCJtLe/edr4IJw8b1s/ycgI/KG18UjFyRp1MAHIVrvMtdxIriXLanJUksJhklxCZIf/cLd77zMfwXcpn1vLXl0309oFCQGtAG5PakHmOzGrmITjadvHjxcTRFo6Nh54YYhK2Qtouc/Ns0OoA0pjgpOdiF65ryMaJLXT+54QAbtRsmrh9hADm2RDirA6JdFq0gVULtj6tcWIrk2dV/GUyxAgMBAAECggEABM1tWlPq+k+NioQaznakOXudwp/vzi7+lbq/E93N5hjSrV2D5rKgANsmGHaiPMGuyCzNGGIOZIKpimypZkJdsOK6ZuRRJ+vvUDUby38odIAGR7fQZgWGXcrdqGM4dEGkIZ7Nk2flvNOkNEkqbPyeT2NXDda0D9E7wUHhl64pAEJ3R+oWdTVfyNUbmKg4RSV3Fe8L6n2kE7W+xiPzHQEou1d8iHXoPf9+du4CUthh/9Ls5DcozE6PRZlrTlEn/OGdNJNpYr25nsVlMNkySQhb31inFGK0PwhEuSbjkxzDK2+wOmtrCHTcNtCLtqfSUZy9QQms8rKp4T3oADjs5Czt+QKBgQC4d/igMY8ZJO6e6xKuFPgPRQWVNiUgJAmtSdeeKu7jFtZs+NV/5fb20hRFcF32lhbTre3BzyFBr6cdbj5RI/ueh9ap7a3gJVIsgigdV68NFgNGcEOsyrKJl6FsjYT+jVmWQwD1OMJt9V+yat2SinscJc0bjExflp7PlDX2bm3zGQKBgQDvTnKJqqNLHPldYokfVVmxy+xW5k2yKw6kk0MElo+ECrmv6b/1I533PnAxumTLmEcIn4OPTZJH6GmWZvAF2T+t0oawXdGM4srcAvDFEe1/eEn0RAGqS3J785x+Ad3W7MPiDWB2P0g0I6r/RtLUAZAFBtB1c4QMl/SYJRI3DRgxWQKBgE25zzrAZwkzNJAlgIN1NmhIrueGHvKB/MnVWVK9yFZLIyJbXqo7XWn2brArfowOBaQ/nvSQr/SbbJDMCNnrjXPozJxhF2hgkVVLqldiwNGEuvCNERbXeY7cIxo4LxeKu3rC1L5+of3u69mjiaTbULA1hUzcOrn0Hwpb6rjhItbRAoGAFIsNdhf58KbKGWQuFUyEaKjy3vriTJs5pJMnu6sZIuKw0hMk8WxfUSxBAWPY50lpP6pB5vlnx1iCR/EKRdZCiy95dIZ5SLjnZT+zX5eMZdbzsk5sPiw/5bgiBQcv/hVRM4SmdUA1eJRfR47S5nRD4sqU9qA2A8a/rpfwGl27Z/ECgYEAoOYiT8A0qz12BTQddeyqFRv38w+0pVaLtBidQyU1ZoiBh2K7FvRaW/GNyxXavewrkXJ8kIcBHZ2oIBqr9skH287sZHA3SrJ9y6q2Oj3QQvCrBSw0hMYQJkWaXjhYqqt22SsOj/L7ezJIbrW7lG8AbfsVLQeK3khVuNh9Cencoa8=";
}
