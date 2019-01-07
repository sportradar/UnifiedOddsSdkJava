/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.oddsentities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

public class MarketWriter {

    private Locale locale;
    private boolean includeMappings;
    private boolean writeLog;
    private final Logger logger;

    public MarketWriter(Locale locale, boolean includeMappings, boolean writeLog){
        logger = LoggerFactory.getLogger(this.getClass().getName());
        this.locale = locale;
        this.includeMappings = includeMappings;
        this.writeLog = writeLog;
    }

    public void writeMarketNames(List<? extends Market> markets) {
        if (markets == null)
        {
            writeMessage("No markets for this sportEvent.");
            return;
        }

        for (Market market:markets) {
            if(market instanceof MarketCancel)
            {
                writeMarket((MarketCancel)market);
            }
            else if(market instanceof MarketWithOdds)
            {
                writeMarket((MarketWithOdds) market);
            }
            else if(market instanceof MarketWithSettlement)
            {
                writeMarket((MarketWithSettlement) market);
            }
            else
            {
                writeMarket(market);
            }
        }
    }

    private void writeMarket(Market market)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        writeMessage(sb.toString());

        writeMarketMappings(market);
    }

    private void writeMarket(MarketCancel market)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        sb.append(", VoidReason:").append(market.getVoidReason());
        writeMessage(sb.toString());

        writeMarketMappings(market);
    }

    private void writeMarket(MarketWithOdds market) {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        sb.append(", MarketStatus:").append(market.getStatus());
        sb.append(", IsFavourite:").append(market.isFavourite());
        writeMessage(sb.toString());

        writeMarketMappings(market);

        List<OutcomeOdds> outcomes = market.getOutcomeOdds();
        if (outcomes == null || outcomes.isEmpty()) {
            return;
        }

        for (OutcomeOdds outcome:outcomes) {
            sb = new StringBuilder();
            sb.append("OutcomeId:").append(outcome.getId());
            sb.append(", Name:").append(outcome.getName());
            sb.append(", Odds:").append(outcome.getOdds(OddsDisplayType.Decimal));
            sb.append(", OddsUS:").append(outcome.getOdds(OddsDisplayType.American));
            sb.append(", IsActive:").append(outcome.isActive());
            sb.append(", IsPlayerOutcome:").append(outcome.isPlayerOutcome());
            sb.append(", Probabilities:").append(outcome.getProbability());
            writeMessage(sb.toString());
        }
        writeMarketOutcomeMappings(market);
    }

    private void writeMarket(MarketWithSettlement market)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        sb.append(", VoidReason:").append(market.getVoidReason());
        writeMessage(sb.toString());

        writeMarketMappings(market);

        List<OutcomeSettlement> outcomes = market.getOutcomeSettlements();
        if (outcomes == null || outcomes.isEmpty()) {
            return;
        }

        for (OutcomeSettlement outcome:outcomes) {
            sb = new StringBuilder();
            sb.append("OutcomeId:").append(outcome.getId());
            sb.append(", Name:").append(outcome.getName());
            sb.append(", VoidFactor:").append(outcome.getVoidFactor());
            sb.append(", DeadHeatFactor:").append(outcome.getDeadHeatFactor());
            sb.append(", OutcomeResult:").append(outcome.getOutcomeResult());
            writeMessage(sb.toString());
        }
        writeMarketOutcomeMappings(market);
    }

    private String writeSpecifiers(Map<String, String> specifiers)
    {
        if(specifiers == null || specifiers.isEmpty())
        {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        specifiers.forEach((s1, s2) -> sj.add(String.format("%s=%s", s1, s2)));
        return sj.toString();
    }

    private String writeAdditionalInfo(Map<String, String> additionalInfo)
    {
        if(additionalInfo == null || additionalInfo.isEmpty())
        {
            return "";
        }
        StringJoiner sj = new StringJoiner(",");
        additionalInfo.forEach((s1, s2) -> sj.add(String.format("%s=%s", s1, s2)));
        return sj.toString();
    }

    private void writeMarketMappings(Market market) {
        if (includeMappings) {
            String result = MarketMappingWriter.writeMarketMapping(market, locale);
            if (result.isEmpty()) {
                result = "No market mappings for market: " + market.getId();
            }
            writeMessage(result);
        }
    }

    private void writeMarketOutcomeMappings(Market market) {
        if (includeMappings) {
            String result = MarketMappingWriter.writeMarketOutcomeMapping(market, locale);
            if (result.isEmpty()) {
                result = "No market outcome mappings for outcome: " + market.getId();
            }
            writeMessage(result);
        }
    }

    private void writeMessage(String message)
    {
        if (writeLog) {
            logger.info(message);
        } else {
            System.out.print(message);
        }
    }
}
