package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIPeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.CompetitorResult;
import com.sportradar.unifiedodds.sdk.entities.PeriodCompetitorResult;
import com.sportradar.utils.URN;
import java.util.ArrayList;
import java.util.List;

public class PeriodCompetitorResultImpl implements PeriodCompetitorResult {

    private final URN id;
    private final List<CompetitorResult> competitorResults;

    public PeriodCompetitorResultImpl(SAPIPeriodStatus.SAPICompetitor sapiCompetitor) {
        Preconditions.checkNotNull(sapiCompetitor);

        id = URN.parse(sapiCompetitor.getId());
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
    public URN getId() {
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
