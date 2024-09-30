/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.CompetitorPlayer;
import com.sportradar.unifiedodds.sdk.entities.Jersey;
import com.sportradar.unifiedodds.sdk.entities.Player;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.Languages;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class CompetitorAssert extends AbstractAssert<CompetitorAssert, Competitor> {

    private final Optional<Locale> locale;

    protected CompetitorAssert(Competitor competitor, Locale locale) {
        super(competitor, CompetitorAssert.class);
        this.locale = Optional.of(locale);
    }

    public static CompetitorAssert assertThat(Competitor competitor, LanguageHolder language) {
        return new CompetitorAssert(competitor, language.get());
    }

    public static CompetitorAssert assertThat(Competitor competitor) {
        return new CompetitorAssert(competitor, Languages.any());
    }

    public CompetitorAssert hasSameUrnAndNameAs(SapiTeam competitor) {
        Assertions.assertThat(actual.getId().toString()).isEqualTo(competitor.getId());
        Assertions.assertThat(actual.getName(locale.get())).isEqualTo(competitor.getName());
        return this;
    }

    public CompetitorAssert isVirtual() {
        Assertions.assertThat(actual.isVirtual()).isTrue();
        return this;
    }

    public CompetitorAssert isNotVirtual() {
        Assertions.assertThat(actual.isVirtual()).isFalse();
        return this;
    }

    public void hasSameJerseysAs(SapiJerseys sapiJerseys) {
        val expectedJerseys = sapiJerseys.getJersey();
        Assertions.assertThat(expectedJerseys).isNotEmpty();

        val jerseysSerialized = actual.getJerseys().stream().map(this::toString).collect(toList());
        val sapiJerseysSerialized = expectedJerseys.stream().map(this::toString).collect(toList());
        Assertions.assertThat(jerseysSerialized).containsExactlyInAnyOrderElementsOf(sapiJerseysSerialized);
    }

    private String toString(Jersey jersey) {
        return Arrays.toString(
            new Object[] {
                jersey.getBase(),
                jersey.getNumber(),
                jersey.getSleeve(),
                jersey.getType(),
                jersey.getStripesColor(),
                jersey.getSplitColor(),
                jersey.getShirtType(),
                jersey.getSleeveDetail(),
                jersey.getStripes(),
                jersey.getHorizontalStripes(),
                jersey.getHorizontalStripesColor(),
                jersey.getSquares(),
                jersey.getSquaresColor(),
                jersey.getSplit(),
            }
        );
    }

    private String toString(SapiJersey jersey) {
        return Arrays.toString(
            new Object[] {
                jersey.getBase(),
                jersey.getNumber(),
                jersey.getSleeve(),
                jersey.getType(),
                jersey.getStripesColor(),
                jersey.getSplitColor(),
                jersey.getShirtType(),
                jersey.getSleeveDetail(),
                jersey.isStripes(),
                jersey.isHorizontalStripes(),
                jersey.getHorizontalStripesColor(),
                jersey.isSquares(),
                jersey.getSquaresColor(),
                jersey.isSplit(),
            }
        );
    }

    public CompetitorAssert hasPlayersWithSameIdsAndNamesAs(
        com.sportradar.uf.sportsapi.datamodel.SapiPlayers sapiPlayers
    ) {
        val playersIdsAndNames = actual
            .getPlayers()
            .stream()
            .map(p -> concat(p.getId().toString(), p.getName(locale.get())))
            .collect(toList());
        val sapiPlayersIdsAndNames = sapiPlayers
            .getPlayer()
            .stream()
            .map(p -> concat(p.getId(), p.getName()))
            .collect(toList());
        Assertions.assertThat(playersIdsAndNames).containsExactlyInAnyOrderElementsOf(sapiPlayersIdsAndNames);
        return this;
    }

    public CompetitorAssert hasPlayersWithSameJerseyNumbersAs(
        com.sportradar.uf.sportsapi.datamodel.SapiPlayers sapiPlayers
    ) {
        val playersIdsAndJerseyNumbers = actual
            .getPlayers()
            .stream()
            .map(p -> concat(p.getId().toString(), ((CompetitorPlayer) p).getJerseyNumber()))
            .collect(toList());
        val sapiPlayersIdsAndJerseyNumbers = sapiPlayers
            .getPlayer()
            .stream()
            .map(p -> concat(p.getId(), p.getJerseyNumber()))
            .collect(toList());
        Assertions
            .assertThat(playersIdsAndJerseyNumbers)
            .containsExactlyInAnyOrderElementsOf(sapiPlayersIdsAndJerseyNumbers);
        return this;
    }

    private static String concat(String id, Object propertyToConcat) {
        return String.format("%s - %s", id, propertyToConcat);
    }
}
