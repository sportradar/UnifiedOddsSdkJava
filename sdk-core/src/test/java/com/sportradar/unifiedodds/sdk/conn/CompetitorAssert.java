/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiJersey;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Jersey;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.Languages;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
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

    public CompetitorAssert isEqualTo(SapiCompetitorProfileEndpoint profile) {
        val competitor = profile.getCompetitor();
        Assertions.assertThat(actual.getName(locale.get())).isEqualTo(competitor.getName());
        Assertions.assertThat(actual.getCountryCode()).isEqualTo(competitor.getCountryCode());
        Assertions.assertThat(actual.getAbbreviation(locale.get())).isEqualTo(competitor.getAbbreviation());
        Assertions.assertThat(actual.getGender()).isEqualTo(competitor.getGender());
        Assertions.assertThat(actual.getAgeGroup()).isEqualTo(competitor.getAgeGroup());
        Assertions.assertThat(actual.getShortName()).isEqualTo(competitor.getShortName());
        Assertions.assertThat(actual.getState()).isEqualTo(competitor.getState());
        Assertions.assertThat(actual.getCountry(locale.get())).isEqualTo(competitor.getCountry());
        Assertions.assertThat(actual.isVirtual()).isEqualTo(BooleanUtils.isTrue(competitor.isVirtual()));
        assertJerseyLists(profile);
        assertPlayerLists(profile);
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

    private void assertJerseyLists(SapiCompetitorProfileEndpoint profile) {
        val jerseys = actual.getJerseys().stream().map(this::toString).collect(toList());
        val sapiJerseys = profile.getJerseys().getJersey().stream().map(this::toString).collect(toList());
        Assertions.assertThat(jerseys).containsExactlyInAnyOrderElementsOf(sapiJerseys);
    }

    private String toString(Jersey jersey) {
        return Arrays.toString(
            new Object[] { jersey.getBase(), jersey.getNumber(), jersey.getSleeve(), jersey.getType() }
        );
    }

    private String toString(SapiJersey jersey) {
        return Arrays.toString(
            new Object[] { jersey.getBase(), jersey.getNumber(), jersey.getSleeve(), jersey.getType() }
        );
    }

    private void assertPlayerLists(SapiCompetitorProfileEndpoint profile) {
        val players = actual
            .getPlayers()
            .stream()
            .map(p -> p.getId() + " - " + p.getName(locale.get()))
            .collect(toList());
        val sapiPlayers = profile
            .getPlayers()
            .getPlayer()
            .stream()
            .map(p -> p.getId() + " - " + p.getName())
            .collect(toList());
        Assertions.assertThat(players).containsExactlyInAnyOrderElementsOf(sapiPlayers);
    }
}
