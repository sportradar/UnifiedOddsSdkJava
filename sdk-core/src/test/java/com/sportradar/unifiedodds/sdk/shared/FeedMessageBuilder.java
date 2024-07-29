package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.datamodel.*;
import com.sportradar.unifiedodds.sdk.conn.GlobalVariables;
import com.sportradar.unifiedodds.sdk.conn.ProducerId;
import com.sportradar.unifiedodds.sdk.conn.SportEvent;
import java.util.Date;
import lombok.val;

/**
 * Class for building test feed messages
 */
@SuppressWarnings({ "ParameterAssignment", "ClassDataAbstractionCoupling", "MagicNumber" })
public class FeedMessageBuilder {

    private final GlobalVariables globalVariables;

    public FeedMessageBuilder(GlobalVariables globalVariables) {
        this.globalVariables = globalVariables;
    }

    /**
     * Builds UfOddsChange message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UfOddsChange message
     */
    public UfOddsChange buildOddsChange(Long eventId, Integer productId, Long requestId, Date timestamp) {
        if (eventId != null && eventId.equals(-1L)) {
            eventId = Helper.generateEventId().getId();
        }

        UfOddsChange message = new UfOddsChange();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? globalVariables.getProducer().get() : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    public String oddsChange(UfOddsChangeMarket market) {
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();
        odds.getMarket().add(market);
        val oddsChange = new UfOddsChange();
        oddsChange.setProduct(globalVariables.getProducer().get());
        oddsChange.setEventId(globalVariables.getSportEventUrn().toString());
        oddsChange.setTimestamp(new Date().getTime());
        oddsChange.setOdds(odds);
        return Helper.serializeToJaxbXml(oddsChange);
    }

    public String oddsChange(UfSportEventStatus status) {
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();
        val oddsChange = new UfOddsChange();
        oddsChange.setProduct(globalVariables.getProducer().get());
        oddsChange.setEventId(globalVariables.getSportEventUrn().toString());
        oddsChange.setTimestamp(new Date().getTime());
        oddsChange.setOdds(odds);
        oddsChange.setSportEventStatus(status);
        return Helper.serializeToJaxbXml(oddsChange);
    }

    public UfOddsChange buildOddsChangeForExactGoalsMarket() {
        UfOddsChangeMarket exactGoalsMarket = new UfOddsChangeMarket();

        val exactGoalsMarketId = 21;
        exactGoalsMarket.setId(exactGoalsMarketId);
        val over6GoalsSpecifier = "variant=sr:exact_goals:6+";
        exactGoalsMarket.setSpecifiers(over6GoalsSpecifier);
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();

        odds.getMarket().add(exactGoalsMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UfOddsChange buildOddsChangeForExactGoalsWithMissingMarketMapping() {
        UfOddsChangeMarket exactGoalsMarket = new UfOddsChangeMarket();

        val exactGoalsMarketId = 21;
        exactGoalsMarket.setId(exactGoalsMarketId);
        val variantNotSupportedInMarketMappingId = "variant=lo:cricket:odi:25";
        exactGoalsMarket.setSpecifiers(variantNotSupportedInMarketMappingId);
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();

        odds.getMarket().add(exactGoalsMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UfOddsChange buildOddsChangeForPlayerAssistMarket() {
        UfOddsChangeMarket playerAssistMarket = new UfOddsChangeMarket();

        UfOddsChangeMarket.UfOutcome playerToAssistOutcome = new UfOddsChangeMarket.UfOutcome();
        val playerToAssistOutcomeId = "pre:playerprops:18427924:754794:1";
        playerToAssistOutcome.setId(playerToAssistOutcomeId);
        playerAssistMarket.getOutcome().add(playerToAssistOutcome);

        val playerAssistMarketId = 770;
        playerAssistMarket.setId(playerAssistMarketId);
        val playerPropsVariantId = "variant=pre:playerprops:18427924:754794";
        playerAssistMarket.setSpecifiers(playerPropsVariantId);
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();

        odds.getMarket().add(playerAssistMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UfOddsChange buildOddsChangeForOutrightMarket() {
        UfOddsChangeMarket outrightMarket = new UfOddsChangeMarket();

        UfOddsChangeMarket.UfOutcome manchesterCityToWinOutcome = new UfOddsChangeMarket.UfOutcome();
        val manchesterCityOutcomeTextId = "pre:outcometext:4861";
        manchesterCityToWinOutcome.setId(manchesterCityOutcomeTextId);
        outrightMarket.getOutcome().add(manchesterCityToWinOutcome);

        val outrightMarketId = 906;
        outrightMarket.setId(outrightMarketId);
        val marketTextId = "variant=pre:markettext:154923";
        outrightMarket.setSpecifiers(marketTextId);
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();

        odds.getMarket().add(outrightMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UfOddsChange buildOddsChangeFor1x2Market() {
        UfOddsChangeMarket market1x2 = new UfOddsChangeMarket();

        UfOddsChangeMarket.UfOutcome draw = new UfOddsChangeMarket.UfOutcome();
        val drawId = "2";
        draw.setId(drawId);
        market1x2.getOutcome().add(draw);

        val idFor1x2Market = 1;
        market1x2.setId(idFor1x2Market);
        UfOddsChange.UfOdds odds = new UfOddsChange.UfOdds();

        odds.getMarket().add(market1x2);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    private UfOddsChange setupDefaultOddsChangeMessageValues(UfOddsChange.UfOdds odds) {
        UfOddsChange message = new UfOddsChange();
        message.setEventId("sr:match:18427924");
        message.setProduct(1);
        message.setTimestamp(new Date().getTime());
        message.setOdds(odds);
        return message;
    }

    /**
     * Builds UfBetStop message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UfBetStop message
     */
    public UfBetStop buildBetStop(Long eventId, Integer productId, Long requestId, Date timestamp) {
        if (eventId != null && eventId.equals(-1L)) {
            eventId = Helper.generateEventId().getId();
        }

        UfBetStop message = new UfBetStop();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? globalVariables.getProducer().get() : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    /**
     * Builds UfAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @param subscribed if subscribed attributed is 1 or 0
     * @return UfAlive message
     */
    public UfAlive buildAlive(Integer productId, Date timestamp, boolean subscribed) {
        UfAlive message = new UfAlive();
        message.setProduct(productId == null ? globalVariables.getProducer().get() : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setSubscribed(subscribed ? 1 : 0);

        return message;
    }

    /**
     * Builds UfAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UfAlive message
     */
    public UfAlive buildAlive(Integer productId, Date timestamp) {
        return buildAlive(productId, timestamp, true);
    }

    /**
     * Builds UfAlive message
     * @param productId the product id message belongs to
     * @return UfAlive message
     */
    public UfAlive buildAlive(Integer productId) {
        return buildAlive(productId, new Date(), true);
    }

    /**
     * Builds UfSnapshotComplete message
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UfSnapshotComplete message
     */
    public UfSnapshotComplete buildSnapshotComplete(Integer productId, long requestId, Date timestamp) {
        UfSnapshotComplete message = new UfSnapshotComplete();
        message.setProduct(productId == null ? globalVariables.getProducer().get() : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }
}
