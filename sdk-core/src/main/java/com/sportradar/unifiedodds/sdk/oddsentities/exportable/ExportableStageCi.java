package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.entities.StageType;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableStageCi extends ExportableCompetitionCi {

    private String parentStageId;
    private List<String> stagesIds;
    private StageType stageType;
    private String categoryId;
    private List<String> additionalParentsIds;

    ExportableStageCi(
        String id,
        Map<Locale, String> names,
        Date scheduled,
        Date scheduledEnd,
        Boolean startTimeTbd,
        String replacedBy,
        BookingStatus bookingStatus,
        List<String> competitorIds,
        ExportableVenueCi venue,
        ExportableSportEventConditionsCi conditions,
        Map<String, Map<String, String>> competitorsReferences,
        String parentStageId,
        List<String> stagesIds,
        StageType stageType,
        String categoryId,
        String liveOdds,
        SportEventType sportEventType,
        List<String> additionalParentsIds
    ) {
        super(
            id,
            names,
            scheduled,
            scheduledEnd,
            startTimeTbd,
            replacedBy,
            bookingStatus,
            competitorIds,
            venue,
            conditions,
            competitorsReferences,
            liveOdds,
            sportEventType
        );
        this.parentStageId = parentStageId;
        this.stagesIds = stagesIds;
        this.stageType = stageType;
        this.categoryId = categoryId;
        this.additionalParentsIds = additionalParentsIds;
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

    public List<String> getAdditionalParentsIds() {
        return additionalParentsIds;
    }

    public void setAdditionalParentsIds(List<String> additionalParentsIds) {
        this.additionalParentsIds = additionalParentsIds;
    }
}
