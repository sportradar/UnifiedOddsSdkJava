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
import java.util.stream.Collectors;

public class MarketWriter {

    private List<Locale> locales;
    private Locale defaultLocale;
    private boolean includeMappings;
    private boolean writeLog;
    private final Logger logger;

    public MarketWriter(List<Locale> locales, boolean includeMappings, boolean writeLog){
        logger = LoggerFactory.getLogger(this.getClass().getName());
        this.locales = locales;
        this.includeMappings = includeMappings;
        this.writeLog = writeLog;
        this.defaultLocale = locales.stream().findFirst().orElse(Locale.ENGLISH);
    }

    public void writeMarketNames(List<? extends Market> markets) {
        if (markets == null)
        {
            writeMessage("No markets for this sportEvent.");
            return;
        }

        for (Market market:markets) {
            if(market instanceof MarketWithOdds)
            {
                writeMarket((MarketWithOdds) market);
            }
            else if(market instanceof MarketCancel)
            {
                writeMarket((MarketCancel)market);
            }
            else if(market instanceof MarketWithSettlement)
            {
                writeMarket((MarketWithSettlement) market);
            }
            else if(market instanceof MarketWithProbabilities)
            {
                writeMarket((MarketWithProbabilities) market);
            }
            else
            {
                writeMarket(market);
            }
        }
    }

    public void writeMarket(Market market)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        writeMessage(sb.toString());

        writeMarketMappings(market);
    }

    public void writeMarket(MarketCancel market)
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

    public void writeMarket(MarketWithProbabilities market)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Name:").append(market.getName());
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        sb.append(", MarketMetaData:").append(getMarketMetaData(market.getMarketMetadata()));
        writeMessage(sb.toString());

        writeMarketMappings(market);
    }

    public void writeMarket(MarketWithOdds market) {
        StringBuilder sb = new StringBuilder();
        sb.append("MarketId:").append(market.getId());
        sb.append(", Names:[");
        StringBuilder finalSb = sb;
        locales.forEach(l->{
            finalSb.append(l.getLanguage()).append(": ").append(market.getName(l)).append("; ");
        });
        sb = finalSb;
        sb.append("]");
        sb.append(", Specifiers:'").append(writeSpecifiers(market.getSpecifiers())).append("'");
        sb.append(", AdditionalInfo:'").append(writeAdditionalInfo(market.getAdditionalMarketInfo())).append("'");
        sb.append(", MarketStatus:").append(market.getStatus());
        sb.append(", IsFavourite:").append(market.isFavourite());
        sb.append(", MarketDefinition:[").append(writeMarketDefinition(market.getMarketDefinition(), locales)).append("]");
        sb.append(", MarketMetaData:").append(getMarketMetaData(market.getMarketMetadata()));
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
            sb.append(", OutcomeDefinition:[").append(writeOutcomeDefinition(outcome.getOutcomeDefinition(), locales)).append("]");
            sb.append(", AdditionalProbabilities:[").append(getAdditionalProbabilities(outcome.getAdditionalProbabilities())).append("]");
            writeMessage(sb.toString());
        }
        writeMarketOutcomeMappings(market);
    }

    public void writeMarket(MarketWithSettlement market)
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

    private static String writeSpecifiers(Map<String, String> specifiers)
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

    private String writeMarketDefinition(MarketDefinition definition, List<Locale> locales)
    {
        if(definition == null)
        {
            return "";
        }
        StringJoiner sj = new StringJoiner(", ");
        sj.add("NameTemplates=[");
        locales.forEach(l -> {
            sj.add(l.getLanguage() + ":" + definition.getNameTemplate(l));
        });
        sj.add("], OutcomeType=" + definition.getOutcomeType());
        if(definition.getGroups() != null && !definition.getGroups().isEmpty()) {
            sj.add(String.format("Groups=%s", String.join(",", definition.getGroups())));
        }
        if(definition.getAttributes() != null && !definition.getAttributes().isEmpty()) {
            String mapAsString = definition.getAttributes().keySet().stream()
                    .map(key -> key + "=" + definition.getAttributes().get(key))
                    .collect(Collectors.joining(", ", "{", "}"));
            sj.add(String.format("Groups=%s", mapAsString));
        }
        return sj.toString();
    }

    private String writeOutcomeDefinition(OutcomeDefinition definition, List<Locale> locales)
    {
        if(definition == null)
        {
            return "";
        }
        StringJoiner sj = new StringJoiner(", ");
        sj.add("NameTemplates=[");
        locales.forEach(l -> {
            sj.add(l.getLanguage() + ":" + definition.getNameTemplate(l));
        });
        sj.add("]");

        return sj.toString();
    }

    private void writeMarketMappings(Market market) {
        if (includeMappings) {
            String result = MarketMappingWriter.writeMarketMapping(market, defaultLocale);
            if (result == null || result.isEmpty()) {
                result = "No market mappings for market: " + market.getId();
            }
            writeMessage(result);
        }
    }

    private void writeMarketOutcomeMappings(Market market) {
        if (includeMappings) {
            String result = MarketMappingWriter.writeMarketOutcomeMapping(market, defaultLocale);
            if (result == null || result.isEmpty()) {
                result = "No market outcome mappings for outcome: " + market.getId();
            }
            writeMessage(result);
        }
    }

    private String getAdditionalProbabilities(AdditionalProbabilities probabilities){
        if(probabilities == null ||
                (probabilities.getWin() == null
                && probabilities.getLose() == null
                && probabilities.getHalfWin() == null
                && probabilities.getHalfLose() == null
                && probabilities.getRefund() == null)){
            return "";
        }
        return String.format("Win={}, Lose={}, HalfWin={}, HalfLose={}, Refund={}",
                             probabilities.getWin(),
                             probabilities.getLose(),
                             probabilities.getHalfWin(),
                             probabilities.getHalfLose(),
                             probabilities.getRefund());
    }

    private String getMarketMetaData(MarketMetadata metadata){
        if(metadata == null){
            return "";
        }
        return metadata.toString();
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
