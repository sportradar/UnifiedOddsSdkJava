package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.StageType;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportableStageCI extends ExportableCompetitionCI {
    private String parentStageId;
    private List<String> stagesIds;
    private StageType stageType;
    private String categoryId;

    ExportableStageCI(String id, Map<Locale, String> names, Date scheduled, Date scheduledEnd,
                      Boolean startTimeTbd, String replacedBy, BookingStatus bookingStatus, List<String> competitorIds,
                      ExportableVenueCI venue, ExportableSportEventConditionsCI conditions,
                      Map<String, Map<String, String>> competitorsReferences, String parentStageId, List<String> stagesIds,
                      StageType stageType, String categoryId, String liveOdds) {
        super(id, names, scheduled, scheduledEnd, startTimeTbd, replacedBy, bookingStatus, competitorIds, venue,
              conditions, competitorsReferences, liveOdds);
        this.parentStageId = parentStageId;
        this.stagesIds = stagesIds;
        this.stageType = stageType;
        this.categoryId = categoryId;
    }

    public String getParentStageId() {
        return parentStageId;
    }

    public void setParentStageId(String parentStageId) {
        this.parentStageId = parentStageId;
    }

    public List<String> getStagesIds() {
        return stagesIds;
    }

    public void setStagesIds(List<String> stagesIds) {
        this.stagesIds = stagesIds;
    }

    public StageType getStageType() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType = stageType;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
