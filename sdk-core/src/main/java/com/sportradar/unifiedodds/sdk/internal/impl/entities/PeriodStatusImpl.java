package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiPeriodStatus;
import com.sportradar.unifiedodds.sdk.entities.PeriodCompetitorResult;
import com.sportradar.unifiedodds.sdk.entities.PeriodStatus;
import java.util.ArrayList;
import java.util.List;

public class PeriodStatusImpl implements PeriodStatus {

    private final Integer number;
    private final String type;
    private final String status;
    private final List<PeriodCompetitorResult> periodResults;

    public PeriodStatusImpl(SapiPeriodStatus periodStatus) {
        Preconditions.checkNotNull(periodStatus);

        this.number = periodStatus.getNumber();
        this.type = periodStatus.getType();
        this.status = periodStatus.getStatus();

        periodResults = new ArrayList<>();
        if (periodStatus.getCompetitor() != null) {
            periodStatus
                .getCompetitor()
                .forEach(sapiCompetitor -> periodResults.add(new PeriodCompetitorResultImpl(sapiCompetitor)));
        }
    }

    /**
     * Returns the number of the specific lap
     *
     * @return the number of the specific lap.
     */
    @Override
    public Integer getNumber() {
        return number;
    }

    /**
     * Returns the type
     *
     * @return the type (possible values: lap)
     */
    @Override
    public String getType() {
        return type;
    }

    /**
     * Returns the status
     *
     * @return the status (possible values: not_started, started, completed)
     */
    @Override
    public String getStatus() {
        return status;
    }

    /**
     * Returns the list of period results
     *
     * @return the list of period results
     */
    @Override
    public List<PeriodCompetitorResult> getPeriodResults() {
        return periodResults;
    }
}
