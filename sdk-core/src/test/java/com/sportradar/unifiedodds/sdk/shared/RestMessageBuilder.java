package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static StaticRandom SR;

    public static CapiEventType getEventType(Urn eventId, int nbrMarkets) {
        List<CapiMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CapiOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++) {
                CapiOutcomeType outcomeType = new CapiOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeTypes.add(outcomeType);
            }

            CapiMarketType marketType = new CapiMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketTypes.add(marketType);
        }

        CapiMarketsType marketsType = new CapiMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CapiEventType eventType = new CapiEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CapiFilteredEventType getFilteredEventType(Urn eventId, int nbrMarkets) {
        List<CapiFilteredMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CapiFilteredOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++) {
                Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
                CapiFilteredOutcomeType outcomeType = new CapiFilteredOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeType.setConflict(isConflict);
                outcomeTypes.add(outcomeType);
            }

            Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
            CapiFilteredMarketType marketType = new CapiFilteredMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketType.setConflict(isConflict);
            marketTypes.add(marketType);
        }

        CapiFilteredMarketsType marketsType = new CapiFilteredMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CapiFilteredEventType eventType = new CapiFilteredEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CapiAvailableSelections getAvailableSelections(Urn eventId, int nbrMarkets) {
        CapiAvailableSelections availableSelections = new CapiAvailableSelections();
        availableSelections.setGeneratedAt(SdkHelper.dateToString(new Date()));
        availableSelections.setEvent(getEventType(eventId, nbrMarkets));
        return availableSelections;
    }

    public static CapiCalculationResponse getCalculationResponse(Urn eventId, int nbrSelections) {
        List<CapiEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CapiEventType eventType = getEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CapiAvailableSelectionsAfterCalculationType availableSelections = new CapiAvailableSelectionsAfterCalculationType();
        availableSelections.getEvents().addAll(eventTypes);

        CapiCalculationResultType calculation = new CapiCalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CapiCalculationResponse calculationResponse = new CapiCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static CapiFilteredCalculationResponse getFilteredCalculationResponse(
        Urn eventId,
        int nbrSelections
    ) {
        List<CapiFilteredEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CapiFilteredEventType eventType = getFilteredEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CapiAvailableSelectionsFilteredOutcomesType availableSelections = new CapiAvailableSelectionsFilteredOutcomesType();
        availableSelections.getEvents().addAll(eventTypes);

        CapiFilteredCalculationResultType calculation = new CapiFilteredCalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CapiFilteredCalculationResponse calculationResponse = new CapiFilteredCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static SapiSportCategoriesEndpoint getSportCategories(
        int sportId,
        int categoryCount,
        int categoryFactor
    ) {
        int newSportId = SR.getId(sportId);
        int newCategoryCount = SR.getId(categoryCount, SR.I100());
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
        int newId = SR.getId(id);
        SapiSport sport = new SapiSport();
        sport.setId(Urn.parse("sr:sport:" + newId).toString());
        sport.setName("Sport " + newId);
        return sport;
    }

    public static SapiCategory getCategory(int id) {
        int newId = SR.getId(id);
        SapiCategory category = new SapiCategory();
        category.setId(Urn.parse("sr:category:" + newId).toString());
        category.setName("Category " + newId);
        category.setCountryCode(SR.S100());
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
        int newId = SR.getId(id);
        int newSportId = SR.getId(sportId);
        int newCategoryId = SR.getId(categoryId);
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
        int newId = SR.getId(id);
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
        int newEventId = SR.getId(eventId);
        int newSportId = SR.getId(sportId);
        int newCategoryId = SR.getId(categoryId);
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
