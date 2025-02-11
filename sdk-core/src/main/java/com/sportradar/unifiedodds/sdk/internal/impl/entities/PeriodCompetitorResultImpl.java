package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiPeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.CompetitorResult;
import com.sportradar.unifiedodds.sdk.entities.PeriodCompetitorResult;
import com.sportradar.utils.Urn;
import java.util.ArrayList;
import java.util.List;

public class PeriodCompetitorResultImpl implements PeriodCompetitorResult {

    private final Urn id;
    private final List<CompetitorResult> competitorResults;

    public PeriodCompetitorResultImpl(SapiPeriodStatus.SapiCompetitor sapiCompetitor) {
        Preconditions.checkNotNull(sapiCompetitor);

        id = Urn.parse(sapiCompetitor.getId());
        competitorResults = new ArrayList<>();
        if (sapiCompetitor.getResult() != null) {
            sapiCompetitor
                .getResult()
                .forEach(sapiResult -> competitorResults.add(new CompetitorResultImpl(sapiResult)));
        }
    }

    /**
     * Returns the competitor id
     *
     * @return the competitor id
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the list of the competitor results
     *
     * @return the list of the competitor results
     */
    @Override
    public List<CompetitorResult> getCompetitorResults() {
        return competitorResults;
    }
}
