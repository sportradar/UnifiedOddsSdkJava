package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.EventChangeListener;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.utils.Urn;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventChangesListener implements EventChangeListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void onFixtureChange(Urn eventId, Date updated, SportEvent sportEvent) {
        logger.info("FixtureChange: Id={}, Update={}, SportId={}", eventId, updated, sportEvent.getSportId());
    }

    @Override
    public void onResultChange(Urn eventId, Date updated, SportEvent sportEvent) {
        logger.info("ResultChange: Id={}, Update={}, SportId={}", eventId, updated, sportEvent.getSportId());
    }
}
