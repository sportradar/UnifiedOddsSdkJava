/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.shared;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.xmlunit.matchers.EvaluateXPathMatcher;

@SuppressWarnings({ "DeclarationOrder", "LineLength" })
public class DynamicMarketResponseProviderTest {

    private final DynamicMarketResponseProvider dynamicMarketResponseProvider = new DynamicMarketResponseProvider();

    @Test
    public void dynamicMarketResponseProviderShouldCreatePremierLeagueOutrightWinnerResponseWithMarketAndOutcome() {
        val premierLeagueWinnerOutrightResponse = dynamicMarketResponseProvider.createPremierLeagueOutrightWinnerMarketResponse();

        String pathToXmlMarketId = "/market_descriptions/market/@id";

        String outrightWinnerMarketId = "906";
        assertThat(
            premierLeagueWinnerOutrightResponse,
            EvaluateXPathMatcher.hasXPath(pathToXmlMarketId, equalTo(outrightWinnerMarketId))
        );

        String pathToXmlOutcomeId = "/market_descriptions/market/outcomes/outcome/@id";

        String manchesterCityOutcomeId = "pre:outcometext:4861";
        assertThat(
            premierLeagueWinnerOutrightResponse,
            EvaluateXPathMatcher.hasXPath(pathToXmlOutcomeId, equalTo(manchesterCityOutcomeId))
        );
        String pathToXmlOutcomeName = "/market_descriptions/market/outcomes/outcome/@name";
        String manchesterCityOutcomeName = "Manchester City";
        assertThat(
            premierLeagueWinnerOutrightResponse,
            EvaluateXPathMatcher.hasXPath(pathToXmlOutcomeName, equalTo(manchesterCityOutcomeName))
        );
    }

    @Test
    public void dynamicMarketResponseProviderShouldCreateLionelMessiToAssistResponseWithMarketAndOutcome() {
        val lionelMessiToAssistResponse = dynamicMarketResponseProvider.createLionelMessiToAssistMarketResponse();
        String pathToPlayerAssistMarket = "/market_descriptions/market/@id";
        String playerToAssistMarketId = "770";
        assertThat(
            lionelMessiToAssistResponse,
            EvaluateXPathMatcher.hasXPath(pathToPlayerAssistMarket, equalTo(playerToAssistMarketId))
        );

        String pathToLionelMessiToAssistOutcomeId = "/market_descriptions/market/outcomes/outcome/@id";
        String lionelMessiOutcomeId = "pre:playerprops:18427924:754794:1";
        assertThat(
            lionelMessiToAssistResponse,
            EvaluateXPathMatcher.hasXPath(pathToLionelMessiToAssistOutcomeId, equalTo(lionelMessiOutcomeId))
        );
        String pathToLionelMessiOutcomeName = "/market_descriptions/market/outcomes/outcome/@name";
        String lionelMessiOutcomeName = "Messi, Lionel 1+";
        assertThat(
            lionelMessiToAssistResponse,
            EvaluateXPathMatcher.hasXPath(pathToLionelMessiOutcomeName, equalTo(lionelMessiOutcomeName))
        );
    }
}
