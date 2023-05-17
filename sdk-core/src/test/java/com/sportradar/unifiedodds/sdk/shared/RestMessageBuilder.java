package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.custombet.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.URN;
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

    public static CAPIEventType getEventType(URN eventId, int nbrMarkets) {
        List<CAPIMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CAPIOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++) {
                CAPIOutcomeType outcomeType = new CAPIOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeTypes.add(outcomeType);
            }

            CAPIMarketType marketType = new CAPIMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketTypes.add(marketType);
        }

        CAPIMarketsType marketsType = new CAPIMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CAPIEventType eventType = new CAPIEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CAPIFilteredEventType getFilteredEventType(URN eventId, int nbrMarkets) {
        List<CAPIFilteredMarketType> marketTypes = new ArrayList<>();
        for (int i = 0; i < nbrMarkets; i++) {
            boolean textOrNbr = SR.B();
            int startNbr = SR.I100();
            List<CAPIFilteredOutcomeType> outcomeTypes = new ArrayList<>();
            for (int j = 0; j < SR.I(nbrMarkets + 1); j++) {
                Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
                CAPIFilteredOutcomeType outcomeType = new CAPIFilteredOutcomeType();
                outcomeType.setId(
                    textOrNbr ? "sr:exact_goals:3+:" + (startNbr + j) : String.valueOf(startNbr + j)
                );
                outcomeType.setConflict(isConflict);
                outcomeTypes.add(outcomeType);
            }

            Boolean isConflict = SR.I100() > 20 ? SR.B() : null;
            CAPIFilteredMarketType marketType = new CAPIFilteredMarketType();
            marketType.setId(i + 1);
            marketType.setSpecifiers(SR.I100() > 95 ? "total=1" : "");
            marketType.getOutcomes().addAll(outcomeTypes);
            marketType.setConflict(isConflict);
            marketTypes.add(marketType);
        }

        CAPIFilteredMarketsType marketsType = new CAPIFilteredMarketsType();
        marketsType.getMarkets().addAll(marketTypes);

        CAPIFilteredEventType eventType = new CAPIFilteredEventType();
        eventType.setId(eventId.toString());
        eventType.setMarkets(marketsType);
        return eventType;
    }

    public static CAPIAvailableSelections getAvailableSelections(URN eventId, int nbrMarkets) {
        CAPIAvailableSelections availableSelections = new CAPIAvailableSelections();
        availableSelections.setGeneratedAt(SdkHelper.dateToString(new Date()));
        availableSelections.setEvent(getEventType(eventId, nbrMarkets));
        return availableSelections;
    }

    public static CAPICalculationResponse getCalculationResponse(URN eventId, int nbrSelections) {
        List<CAPIEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CAPIEventType eventType = getEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CAPIAvailableSelectionsAfterCalculationType availableSelections = new CAPIAvailableSelectionsAfterCalculationType();
        availableSelections.getEvents().addAll(eventTypes);

        CAPICalculationResultType calculation = new CAPICalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CAPICalculationResponse calculationResponse = new CAPICalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static CAPIFilteredCalculationResponse getFilteredCalculationResponse(
        URN eventId,
        int nbrSelections
    ) {
        List<CAPIFilteredEventType> eventTypes = new ArrayList<>();
        for (int i = 0; i < nbrSelections; i++) {
            CAPIFilteredEventType eventType = getFilteredEventType(eventId, SR.I(10));
            eventTypes.add(eventType);
        }

        CAPIAvailableSelectionsFilteredOutcomesType availableSelections = new CAPIAvailableSelectionsFilteredOutcomesType();
        availableSelections.getEvents().addAll(eventTypes);

        CAPIFilteredCalculationResultType calculation = new CAPIFilteredCalculationResultType();
        calculation.setOdds(SR.D(100));
        calculation.setProbability(SR.D0());

        CAPIFilteredCalculationResponse calculationResponse = new CAPIFilteredCalculationResponse();
        calculationResponse.setGeneratedAt(SdkHelper.dateToString(new Date()));
        calculationResponse.setAvailableSelections(availableSelections);
        calculationResponse.setCalculation(calculation);

        return calculationResponse;
    }

    public static SAPISportCategoriesEndpoint getSportCategories(
        int sportId,
        int categoryCount,
        int categoryFactor
    ) {
        int newSportId = SR.getId(sportId);
        int newCategoryCount = SR.getId(categoryCount, SR.I100());
        SAPISportCategoriesEndpoint result = new SAPISportCategoriesEndpoint();
        result.setSport(getSport(newSportId));
        SAPICategories categories = new SAPICategories();
        for (int i = 1; i < newCategoryCount + 1; i++) {
            categories.getCategory().add(getCategory(i * categoryFactor));
        }
        result.setCategories(categories);
        return result;
    }

    public static SAPISport getSport(int id) {
        int newId = SR.getId(id);
        SAPISport sport = new SAPISport();
        sport.setId(URN.parse("sr:sport:" + newId).toString());
        sport.setName("Sport " + newId);
        return sport;
    }

    public static SAPICategory getCategory(int id) {
        int newId = SR.getId(id);
        SAPICategory category = new SAPICategory();
        category.setId(URN.parse("sr:category:" + newId).toString());
        category.setName("Category " + newId);
        category.setCountryCode(SR.S100());
        return category;
    }

    public static SAPITournamentLength getTournamentLength(Instant startDate, Instant endDate) {
        Instant newStartDate = startDate == null ? Instant.now().minus(30, ChronoUnit.DAYS) : startDate;
        Instant newEndDate = endDate == null ? Instant.now().plus(30, ChronoUnit.DAYS) : endDate;
        SAPITournamentLength tournamentLength = new SAPITournamentLength();
        tournamentLength.setStartDate(Helper.getCalendar(newStartDate));
        tournamentLength.setEndDate(Helper.getCalendar(newEndDate));
        return tournamentLength;
    }

    public static SAPITournament getTournament(int id, int sportId, int categoryId) {
        int newId = SR.getId(id);
        int newSportId = SR.getId(sportId);
        int newCategoryId = SR.getId(categoryId);
        SAPITournament tournament = new SAPITournament();
        tournament.setId("sr:tournament:" + newId);
        tournament.setName("Tournament " + newId);
        tournament.setSport(getSport(newSportId));
        tournament.setCategory(getCategory(newCategoryId));
        tournament.setTournamentLength(getTournamentLength(null, null));
        tournament.setScheduled(tournament.getTournamentLength().getStartDate());
        tournament.setScheduledEnd(tournament.getTournamentLength().getEndDate());
        return tournament;
    }

    public static SAPITeamCompetitor getTeamCompetitor(int id, HomeAway homeAway) {
        int newId = SR.getId(id);
        SAPITeamCompetitor teamCompetitor = new SAPITeamCompetitor();
        teamCompetitor.setId("sr:competitor:" + newId);
        teamCompetitor.setName("TeanCompetitor " + newId);
        teamCompetitor.setCountryCode("uk");
        teamCompetitor.setQualifier(homeAway.name());
        teamCompetitor.setCountry("uk");
        teamCompetitor.setVirtual(false);
        return teamCompetitor;
    }

    public static SAPIFixture getFixture(int eventId, int sportId, int categoryId) {
        int newEventId = SR.getId(eventId);
        int newSportId = SR.getId(sportId);
        int newCategoryId = SR.getId(categoryId);
        SAPIFixture fixture = new SAPIFixture();
        fixture.setId("sr:match:" + newEventId);
        fixture.setName("Match " + newEventId);
        fixture.setScheduled(Helper.getCalendar(Instant.now()));
        fixture.setStartTime(fixture.getScheduled());
        fixture.setScheduledEnd(Helper.getCalendar(Instant.now().plus(1, ChronoUnit.HOURS)));
        fixture.setTournament(getTournament(123456, newSportId, newCategoryId));
        fixture.setCompetitors(new SAPISportEventCompetitors());
        fixture.getCompetitors().getCompetitor().add(getTeamCompetitor(111, HomeAway.Home));
        fixture.getCompetitors().getCompetitor().add(getTeamCompetitor(222, HomeAway.Away));
        return fixture;
    }
}
