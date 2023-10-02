package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.PitcherHand;
import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportablePitcherCi implements Serializable {

    private String id;
    private String name;
    private HomeAway competitor;
    private PitcherHand hand;

    public ExportablePitcherCi(String id, String name, HomeAway competitor, PitcherHand hand) {
        this.id = id;
        this.name = name;
        this.competitor = competitor;
        this.hand = hand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HomeAway getCompetitor() {
        return competitor;
    }

    public void setCompetitor(HomeAway competitor) {
        this.competitor = competitor;
    }

    public PitcherHand getHand() {
        return hand;
    }

    public void setHand(PitcherHand hand) {
        this.hand = hand;
    }
}
