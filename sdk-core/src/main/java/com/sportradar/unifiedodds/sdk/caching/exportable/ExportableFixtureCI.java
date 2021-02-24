package com.sportradar.unifiedodds.sdk.caching.exportable;

import com.sportradar.utils.URN;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ExportableFixtureCI implements Serializable {
    private Date startTime;
    private boolean startTimeConfirmed;
    private Date nextLiveTime;
    private Map<String, String> extraInfo;
    private List<ExportableTvChannelCI> tvChannels;
    private ExportableCoverageInfoCI coverageInfo;
    private ExportableProducerInfoCI producerInfo;
    private Map<String, String> references;
    private Boolean startTimeTbd;
    private String replacedBy;
    private List<ExportableScheduledStartTimeChangeCI> scheduledStartTimeChanges;
    private String parentId;
    private List<String> additionalParentsIds;

    public ExportableFixtureCI(Date startTime, boolean startTimeConfirmed, Date nextLiveTime, Map<String, String> extraInfo, List<ExportableTvChannelCI> tvChannels,
                               ExportableCoverageInfoCI coverageInfo, ExportableProducerInfoCI producerInfo, Map<String, String> references, Boolean startTimeTbd, String replacedBy,
                               List<ExportableScheduledStartTimeChangeCI> scheduledStartTimeChanges, String parentId, List<String> additionalParentsIds) {
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

    public List<ExportableTvChannelCI> getTvChannels() {
        return tvChannels;
    }

    public void setTvChannels(List<ExportableTvChannelCI> tvChannels) {
        this.tvChannels = tvChannels;
    }

    public ExportableCoverageInfoCI getCoverageInfo() {
        return coverageInfo;
    }

    public void setCoverageInfo(ExportableCoverageInfoCI coverageInfo) {
        this.coverageInfo = coverageInfo;
    }

    public ExportableProducerInfoCI getProducerInfo() {
        return producerInfo;
    }

    public void setProducerInfo(ExportableProducerInfoCI producerInfo) {
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

    public List<ExportableScheduledStartTimeChangeCI> getScheduledStartTimeChanges() {
        return scheduledStartTimeChanges;
    }

    public void setScheduledStartTimeChanges(List<ExportableScheduledStartTimeChangeCI> scheduledStartTimeChanges) {
        this.scheduledStartTimeChanges = scheduledStartTimeChanges;
    }

    public String getParentId() { return parentId; }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public List<String> getAdditionalParentsIds() { return additionalParentsIds; }

    public void setAdditionalParentsIds(List<String> additionalParentsIds) { this.additionalParentsIds = additionalParentsIds; }
}
