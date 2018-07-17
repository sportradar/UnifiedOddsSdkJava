/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.entities.Stage;
import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;

import java.util.List;
import java.util.Locale;

/**
 * Created on 21/06/2017.
 * // TODO @eti: Javadoc
 */
public class EntityNameExpression implements NameExpression {
    private final static List<String> SUPPORTED_OPERANDS = ImmutableList.<String>builder().add("competitor1").add("competitor2").build();
    private final String propertyName;
    private final SportEvent sportEvent;

    EntityNameExpression(String propertyName, SportEvent sportEvent) {
        Preconditions.checkNotNull(propertyName);
        Preconditions.checkNotNull(sportEvent);

        this.propertyName = propertyName;
        this.sportEvent = sportEvent;
    }

    @Override
    public String buildName(Locale locale) {
        if (sportEvent instanceof Match) {
            return handleMatchEvent(locale);
        } else if (sportEvent instanceof Stage) {
            return handleRaceEvent(locale);
        }

        throw new IllegalArgumentException("The type " + sportEvent.getClass().getName() + " is not supported");
    }

    private String handleMatchEvent(Locale locale) {
        Match match = (Match) sportEvent;

        TeamCompetitor competitor;
        switch (SUPPORTED_OPERANDS.indexOf(propertyName)) {
            case 0:
                competitor = match.getHomeCompetitor();
                break;
            case 1:
                competitor = match.getAwayCompetitor();
                break;
            default:
                throw new IllegalArgumentException("Operand " + propertyName + " is not supported. Supported operands are: " +  String.join("," ,SUPPORTED_OPERANDS));
        }

        if (competitor == null) {
            throw new IllegalStateException("Could not build the requested entity name, event:" + sportEvent + ", operand: " + propertyName + ", locale:" + locale + "(missing competitor data)");
        }

        String name = competitor.getName(locale);

        if (!Strings.isNullOrEmpty(name)) {
            return name;
        }

        throw new IllegalStateException("Could not build the requested entity name, event:" + sportEvent + ", operand: " + propertyName + ", locale:" + locale);
    }

    private String handleRaceEvent(Locale locale) {
        Stage stage = (Stage) sportEvent;
        String name = null;
        switch (SUPPORTED_OPERANDS.indexOf(propertyName)) {
            case 0:
                if (stage.getCompetitors() != null) {
                    name = stage.getCompetitors().stream().findFirst().map(f -> f.getName(locale)).orElse(null);
                }
                break;
            case 1:
                if(stage.getCompetitors() != null) {
                    name = stage.getCompetitors().stream().skip(1).findFirst().map(f -> f.getName(locale)).orElse(null);
                }
                break;
            default:
                throw new IllegalArgumentException("Operand " + propertyName + " is not supported. Supported operands are: " +  String.join("," ,SUPPORTED_OPERANDS));
        }

        if (!Strings.isNullOrEmpty(name)) {
            return name;
        }

        throw new IllegalStateException("Could not build the requested entity name, event:" + sportEvent + ", operand: " + propertyName + ", locale:" + locale);
    }
}
