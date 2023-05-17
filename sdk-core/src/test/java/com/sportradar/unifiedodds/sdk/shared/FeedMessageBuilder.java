package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.uf.datamodel.UFAlive;
import com.sportradar.uf.datamodel.UFBetStop;
import com.sportradar.uf.datamodel.UFOddsChange;
import com.sportradar.uf.datamodel.UFOddsChangeMarket;
import com.sportradar.uf.datamodel.UFSnapshotComplete;
import java.util.Date;
import lombok.val;

/**
 * Class for building test feed messages
 */
@SuppressWarnings({ "ParameterAssignment", "ClassDataAbstractionCoupling", "MagicNumber" })
public class FeedMessageBuilder {

    private final int producerId;

    public FeedMessageBuilder(int producerId) {
        this.producerId = producerId;
    }

    /**
     * Builds UFOddsChange message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFOddsChange message
     */
    public UFOddsChange buildOddsChange(Long eventId, Integer productId, Long requestId, Date timestamp) {
        if (eventId != null && eventId.equals(-1L)) {
            eventId = Helper.generateEventId().getId();
        }

        UFOddsChange message = new UFOddsChange();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    public UFOddsChange buildOddsChangeForExactGoalsMarket() {
        UFOddsChangeMarket exactGoalsMarket = new UFOddsChangeMarket();

        val exactGoalsMarketId = 21;
        exactGoalsMarket.setId(exactGoalsMarketId);
        val over6GoalsSpecifier = "variant=sr:exact_goals:6+";
        exactGoalsMarket.setSpecifiers(over6GoalsSpecifier);
        UFOddsChange.UFOdds odds = new UFOddsChange.UFOdds();

        odds.getMarket().add(exactGoalsMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UFOddsChange buildOddsChangeForExactGoalsWithMissingMarketMapping() {
        UFOddsChangeMarket exactGoalsMarket = new UFOddsChangeMarket();

        val exactGoalsMarketId = 21;
        exactGoalsMarket.setId(exactGoalsMarketId);
        val variantNotSupportedInMarketMappingId = "variant=lo:cricket:odi:25";
        exactGoalsMarket.setSpecifiers(variantNotSupportedInMarketMappingId);
        UFOddsChange.UFOdds odds = new UFOddsChange.UFOdds();

        odds.getMarket().add(exactGoalsMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UFOddsChange buildOddsChangeForPlayerAssistMarket() {
        UFOddsChangeMarket playerAssistMarket = new UFOddsChangeMarket();

        UFOddsChangeMarket.UFOutcome playerToAssistOutcome = new UFOddsChangeMarket.UFOutcome();
        val playerToAssistOutcomeId = "pre:playerprops:18427924:754794:1";
        playerToAssistOutcome.setId(playerToAssistOutcomeId);
        playerAssistMarket.getOutcome().add(playerToAssistOutcome);

        val playerAssistMarketId = 770;
        playerAssistMarket.setId(playerAssistMarketId);
        val playerPropsVariantId = "variant=pre:playerprops:18427924:754794";
        playerAssistMarket.setSpecifiers(playerPropsVariantId);
        UFOddsChange.UFOdds odds = new UFOddsChange.UFOdds();

        odds.getMarket().add(playerAssistMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UFOddsChange buildOddsChangeForOutrightMarket() {
        UFOddsChangeMarket outrightMarket = new UFOddsChangeMarket();

        UFOddsChangeMarket.UFOutcome manchesterCityToWinOutcome = new UFOddsChangeMarket.UFOutcome();
        val manchesterCityOutcomeTextId = "pre:outcometext:4861";
        manchesterCityToWinOutcome.setId(manchesterCityOutcomeTextId);
        outrightMarket.getOutcome().add(manchesterCityToWinOutcome);

        val outrightMarketId = 906;
        outrightMarket.setId(outrightMarketId);
        val marketTextId = "variant=pre:markettext:154923";
        outrightMarket.setSpecifiers(marketTextId);
        UFOddsChange.UFOdds odds = new UFOddsChange.UFOdds();

        odds.getMarket().add(outrightMarket);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    public UFOddsChange buildOddsChangeFor1x2Market() {
        UFOddsChangeMarket market1x2 = new UFOddsChangeMarket();

        UFOddsChangeMarket.UFOutcome draw = new UFOddsChangeMarket.UFOutcome();
        val drawId = "2";
        draw.setId(drawId);
        market1x2.getOutcome().add(draw);

        val idFor1x2Market = 1;
        market1x2.setId(idFor1x2Market);
        UFOddsChange.UFOdds odds = new UFOddsChange.UFOdds();

        odds.getMarket().add(market1x2);

        return setupDefaultOddsChangeMessageValues(odds);
    }

    private UFOddsChange setupDefaultOddsChangeMessageValues(UFOddsChange.UFOdds odds) {
        UFOddsChange message = new UFOddsChange();
        message.setEventId("sr:match:18427924");
        message.setProduct(1);
        message.setTimestamp(new Date().getTime());
        message.setOdds(odds);
        return message;
    }

    /**
     * Builds UFBetStop message
     * @param eventId the event id to be set (if null id=1000, if -1 id=Random, or eventId)
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFBetStop message
     */
    public UFBetStop buildBetStop(Long eventId, Integer productId, Long requestId, Date timestamp) {
        if (eventId != null && eventId.equals(-1L)) {
            eventId = Helper.generateEventId().getId();
        }

        UFBetStop message = new UFBetStop();
        message.setEventId(eventId == null ? "sr:match:1000" : "sr:match:" + eventId);
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @param subscribed if subscribed attributed is 1 or 0
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId, Date timestamp, boolean subscribed) {
        UFAlive message = new UFAlive();
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setSubscribed(subscribed ? 1 : 0);

        return message;
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId, Date timestamp) {
        return buildAlive(productId, timestamp, true);
    }

    /**
     * Builds UFAlive message
     * @param productId the product id message belongs to
     * @return UFAlive message
     */
    public UFAlive buildAlive(Integer productId) {
        return buildAlive(productId, new Date(), true);
    }

    /**
     * Builds UFSnapshotComplete message
     * @param productId the product id message belongs to
     * @param requestId the request id or null
     * @param timestamp timestamp to be applied or DateTime.Now
     * @return UFSnapshotComplete message
     */
    public UFSnapshotComplete buildSnapshotComplete(Integer productId, long requestId, Date timestamp) {
        UFSnapshotComplete message = new UFSnapshotComplete();
        message.setProduct(productId == null ? producerId : productId);
        message.setTimestamp(timestamp == null ? new Date().getTime() : timestamp.getTime());
        message.setRequestId(requestId);

        return message;
    }
}
