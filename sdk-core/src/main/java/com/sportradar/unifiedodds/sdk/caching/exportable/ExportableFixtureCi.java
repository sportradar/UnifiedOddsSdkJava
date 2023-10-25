package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableFixtureCi implements Serializable {

    private Date startTime;
    private boolean startTimeConfirmed;
    private Date nextLiveTime;
    private Map<String, String> extraInfo;
    private List<ExportableTvChannelCi> tvChannels;
    private ExportableCoverageInfoCi coverageInfo;
    private ExportableProducerInfoCi producerInfo;
    private Map<String, String> references;
    private Boolean startTimeTbd;
    private String replacedBy;
    private List<ExportableScheduledStartTimeChangeCi> scheduledStartTimeChanges;
    private String parentId;
    private List<String> additionalParentsIds;

    public ExportableFixtureCi(
        Date startTime,
        boolean startTimeConfirmed,
        Date nextLiveTime,
        Map<String, String> extraInfo,
        List<ExportableTvChannelCi> tvChannels,
        ExportableCoverageInfoCi coverageInfo,
        ExportableProducerInfoCi producerInfo,
        Map<String, String> references,
        Boolean startTimeTbd,
        String replacedBy,
        List<ExportableScheduledStartTimeChangeCi> scheduledStartTimeChanges,
        String parentId,
        List<String> additionalParentsIds
    ) {
        this.startTime = startTime;
        this.startTimeConfirmed = startTimeConfirmed;
        this.nextLiveTime = nextLiveTime;
        this.extraInfo = extraInfo;
        this.tvChannels = tvChannels;
        this.coverageInfo = coverageInfo;
        this.producerInfo = producerInfo;
        this.references = references;
        this.startTimeTbd = startTimeTbd;
        this.replacedBy = replacedBy;
        this.scheduledStartTimeChanges = scheduledStartTimeChanges;
        this.parentId = parentId;
        this.additionalParentsIds = additionalParentsIds;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public boolean isStartTimeConfirmed() {
        return startTimeConfirmed;
    }

    public void setStartTimeConfirmed(boolean startTimeConfirmed) {
        this.startTimeConfirmed = startTimeConfirmed;
    }

    public Date getNextLiveTime() {
        return nextLiveTime;
    }

    public void setNextLiveTime(Date nextLiveTime) {
        this.nextLiveTime = nextLiveTime;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, String> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<ExportableTvChannelCi> getTvChannels() {
        return tvChannels;
    }

    public void setTvChannels(List<ExportableTvChannelCi> tvChannels) {
        this.tvChannels = tvChannels;
    }

    public ExportableCoverageInfoCi getCoverageInfo() {
        return coverageInfo;
    }

    public void setCoverageInfo(ExportableCoverageInfoCi coverageInfo) {
        this.coverageInfo = coverageInfo;
    }

    public ExportableProducerInfoCi getProducerInfo() {
        return producerInfo;
    }

    public void setProducerInfo(ExportableProducerInfoCi producerInfo) {
        this.producerInfo = producerInfo;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(Map<String, String> references) {
        this.references = references;
    }

    public Boolean getStartTimeTbd() {
        return startTimeTbd;
    }

    public void setStartTimeTbd(Boolean startTimeTbd) {
        this.startTimeTbd = startTimeTbd;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    public List<ExportableScheduledStartTimeChangeCi> getScheduledStartTimeChanges() {
        return scheduledStartTimeChanges;
    }

    public void setScheduledStartTimeChanges(
        List<ExportableScheduledStartTimeChangeCi> scheduledStartTimeChanges
    ) {
        this.scheduledStartTimeChanges = scheduledStartTimeChanges;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getAdditionalParentsIds() {
        return additionalParentsIds;
    }

    public void setAdditionalParentsIds(List<String> additionalParentsIds) {
        this.additionalParentsIds = additionalParentsIds;
    }
}
