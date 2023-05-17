/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.shared;

/**
 * Class for providing dynamic market xml responses
 */
@SuppressWarnings({ "LineLength" })
public class DynamicMarketResponseProvider {

    private final String dynamicMarketEndpointResultForOutrightMarket =
        "<?xml version='1.0' encoding='UTF-8'?>\n" +
        "  <market_descriptions response_code=\"OK\">\n" +
        "    <market id=\"906\" name=\"Premier League - Winner\" variant=\"pre:markettext:154923\">\n" +
        "      <outcomes>\n" +
        "        <outcome id=\"pre:outcometext:4861\" name=\"Manchester City\"/>\n" +
        "      </outcomes>\n" +
        "    </market>\n" +
        "  </market_descriptions>";

    private final String dynamicMarketEndpointResultForPlayerAssistMarket =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
        "  <market_descriptions response_code=\"OK\">\n" +
        "    <market id=\"770\" name=\"Player assist (incl. overtime)\" variant=\"pre:playerprops:18427924:754794\">\n" +
        "      <outcomes>\n" +
        "        <outcome id=\"pre:playerprops:18427924:754794:1\" name=\"Messi, Lionel 1+\"/>\n" +
        "      </outcomes>\n" +
        "    </market>\n" +
        "  </market_descriptions>";

    public String createLionelMessiToAssistMarketResponse() {
        return dynamicMarketEndpointResultForPlayerAssistMarket;
    }

    public String createPremierLeagueOutrightWinnerMarketResponse() {
        return dynamicMarketEndpointResultForOutrightMarket;
    }
}
