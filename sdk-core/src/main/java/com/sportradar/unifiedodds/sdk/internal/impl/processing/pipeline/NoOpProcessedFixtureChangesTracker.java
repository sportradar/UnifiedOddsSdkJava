package com.sportradar.unifiedodds.sdk.internal.impl.processing.pipeline;

import com.google.inject.Inject;
import com.sportradar.uf.datamodel.UfFixtureChange;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
@SuppressWarnings({ "LineLength" })
public class NoOpProcessedFixtureChangesTracker implements ProcessedFixtureChangesTracker {

    @Inject
    NoOpProcessedFixtureChangesTracker() {
        // empty
    }

    /**
     * Checks if the received fixture change was already processed and returns an indication about the processed state.
     * The underlying implementation needs to "remember" which fixture changes come in, so it won't return incorrect indications.
     *
     * @param fixtureChange the fixture change that needs to be checked
     * @return if the fixture wasn't processed yet, the result is <code>false</code>, otherwise <code>true</code>
     */
    @Override
    public boolean onFixtureChangeReceived(UfFixtureChange fixtureChange) {
        // fixture change check is already being performed on the session level,
        // no need for this precise monitoring in normal ops mode
        return false;
    }
}
