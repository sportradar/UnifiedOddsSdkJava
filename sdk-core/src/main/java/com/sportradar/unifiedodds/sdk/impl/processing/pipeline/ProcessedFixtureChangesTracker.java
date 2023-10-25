package com.sportradar.unifiedodds.sdk.impl.processing.pipeline;

import com.sportradar.uf.datamodel.UfFixtureChange;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
@SuppressWarnings({ "LineLength" })
public interface ProcessedFixtureChangesTracker {
    /**
     * Checks if the received fixture change was already processed and returns an indication about the processed state.
     * The underlying implementation needs to "remember" which fixture changes come in, so it won't return incorrect indications.
     *
     * @param fixtureChange the fixture change that needs to be checked
     * @return if the fixture wasn't processed yet, the result is <code>false</code>, otherwise <code>true</code>
     */
    boolean onFixtureChangeReceived(UfFixtureChange fixtureChange);
}
