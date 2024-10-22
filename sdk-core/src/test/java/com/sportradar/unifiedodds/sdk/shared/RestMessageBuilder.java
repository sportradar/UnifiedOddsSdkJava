package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.utils.Urn;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Class for building rest messages
 */
@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "HideUtilityClassConstructor",
        "LineLength",
        "MagicNumber",
        "StaticVariableName",
        "VariableDeclarationUsageDistance",
        "VisibilityModifier",
    }
)
public class RestMessageBuilder {

    public static SapiSportCategoriesEndpoint getSportCategories(
        int sportId,
        int categoryCount,
        int categoryFactor
    ) {
        int newSportId = StaticRandom.getId(sportId);
        int newCategoryCount = StaticRandom.getId(categoryCount, StaticRandom.I100());
        SapiSportCategoriesEndpoint result = new SapiSportCategoriesEndpoint();
        result.setSport(getSport(newSportId));
        SapiCategories categories = new SapiCategories();
        for (int i = 1; i < newCategoryCount + 1; i++) {
            categories.getCategory().add(getCategory(i * categoryFactor));
        }
        result.setCategories(categories);
        return result;
    }

    public static SapiSport getSport(int id) {
        int newId = StaticRandom.getId(id);
        SapiSport sport = new SapiSport();
        sport.setId(Urn.parse("sr:sport:" + newId).toString());
        sport.setName("Sport " + newId);
        return sport;
    }

    public static SapiCategory getCategory(int id) {
        int newId = StaticRandom.getId(id);
        SapiCategory category = new SapiCategory();
        category.setId(Urn.parse("sr:category:" + newId).toString());
        category.setName("Category " + newId);
        category.setCountryCode(StaticRandom.S100());
        return category;
    }

    public static SapiTournamentLength getTournamentLength(Instant startDate, Instant endDate) {
        Instant newStartDate = startDate == null ? Instant.now().minus(30, ChronoUnit.DAYS) : startDate;
        Instant newEndDate = endDate == null ? Instant.now().plus(30, ChronoUnit.DAYS) : endDate;
        SapiTournamentLength tournamentLength = new SapiTournamentLength();
        tournamentLength.setStartDate(Helper.getCalendar(newStartDate));
        tournamentLength.setEndDate(Helper.getCalendar(newEndDate));
        return tournamentLength;
    }

    public static SapiTournament getTournament(int id, int sportId, int categoryId) {
        int newId = StaticRandom.getId(id);
        int newSportId = StaticRandom.getId(sportId);
        int newCategoryId = StaticRandom.getId(categoryId);
        SapiTournament tournament = new SapiTournament();
        tournament.setId("sr:tournament:" + newId);
        tournament.setName("Tournament " + newId);
        tournament.setSport(getSport(newSportId));
        tournament.setCategory(getCategory(newCategoryId));
        tournament.setTournamentLength(getTournamentLength(null, null));
        tournament.setScheduled(tournament.getTournamentLength().getStartDate());
        tournament.setScheduledEnd(tournament.getTournamentLength().getEndDate());
        return tournament;
    }

    public static SapiTeamCompetitor getTeamCompetitor(int id, HomeAway homeAway) {
        int newId = StaticRandom.getId(id);
        SapiTeamCompetitor teamCompetitor = new SapiTeamCompetitor();
        teamCompetitor.setId("sr:competitor:" + newId);
        teamCompetitor.setName("TeanCompetitor " + newId);
        teamCompetitor.setCountryCode("uk");
        teamCompetitor.setQualifier(homeAway.name());
        teamCompetitor.setCountry("uk");
        teamCompetitor.setVirtual(false);
        return teamCompetitor;
    }

    public static SapiFixture getFixture(int eventId, int sportId, int categoryId) {
        int newEventId = StaticRandom.getId(eventId);
        int newSportId = StaticRandom.getId(sportId);
        int newCategoryId = StaticRandom.getId(categoryId);
        SapiFixture fixture = new SapiFixture();
        fixture.setId("sr:match:" + newEventId);
        fixture.setName("Match " + newEventId);
        fixture.setScheduled(Helper.getCalendar(Instant.now()));
        fixture.setStartTime(fixture.getScheduled());
        fixture.setScheduledEnd(Helper.getCalendar(Instant.now().plus(1, ChronoUnit.HOURS)));
        fixture.setTournament(getTournament(123456, newSportId, newCategoryId));
        fixture.setCompetitors(new SapiSportEventCompetitors());
        fixture.getCompetitors().getCompetitor().add(getTeamCompetitor(111, HomeAway.Home));
        fixture.getCompetitors().getCompetitor().add(getTeamCompetitor(222, HomeAway.Away));
        return fixture;
    }
}
