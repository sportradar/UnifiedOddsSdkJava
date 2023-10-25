/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.SapiCoverage;
import com.sportradar.uf.sportsapi.datamodel.SapiFixture;
import com.sportradar.uf.sportsapi.datamodel.SapiInfo;
import com.sportradar.uf.sportsapi.datamodel.SapiReferenceIds;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableFixtureCi;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import com.sportradar.utils.SdkHelper;
import com.sportradar.utils.Urn;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A representation of a fixture
 *
 * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
 *
 * @see Fixture
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "LineLength",
        "MethodLength",
        "NPathComplexity",
        "UnnecessaryParentheses",
    }
)
public class FixtureImpl implements Fixture {

    private static final Logger logger = LoggerFactory.getLogger(FixtureImpl.class);

    /**
     * A {@link Date} instance specifying when the fixture is scheduled to start
     */
    private final Date startTime;

    /**
     * A value indicating whether the start time of the fixture has been confirmed
     */
    private final boolean startTimeConfirmed;

    /**
     * A {@link Date} instance specifying the live time in case the fixture was re-schedule, or a null reference if the fixture was not re-scheduled
     */
    private final Date nextLiveTime;

    /**
     * An unmodifiable {@link Map} containing additional information about the fixture
     * @see com.google.common.collect.ImmutableMap
     */
    private final Map<String, String> extraInfo;

    /**
     * An unmodifiable {@link List} representing TV channels covering the sport event
     * @see com.google.common.collect.ImmutableList
     */
    private final List<TvChannel> tvChannels;

    /**
     * A {@link CoverageInfo} instance specifying what coverage is available for the sport event
     * associated with current instance
     */
    private final CoverageInfo coverageInfo;

    /**
     * A {@link ProducerInfo} instance providing sportradar related information about the sport event associated
     * with the current instance.
     */
    private final ProducerInfo producerInfo;

    /**
     * The reference ids
     */
    private final Reference references;

    /**
     * An indication if the start tam is yet to be defined
     */
    private final Boolean startTimeTbd;

    /**
     * The {@link Urn} identifier of the replacement event
     */
    private final Urn replacedBy;

    private final List<ScheduledStartTimeChange> scheduledStartTimeChanges;

    private final Urn parentId;

    private final List<Urn> additionalParentsIds;

    /**
     * Initializes a new instance of the {@link FixtureImpl}
     *
     * @param fixture - {@link SapiFixture} used to create the new instance
     */
    public FixtureImpl(SapiFixture fixture) {
        Preconditions.checkNotNull(fixture);

        this.startTime = fixture.getStartTime() == null ? null : SdkHelper.toDate(fixture.getStartTime());
        this.startTimeConfirmed =
            fixture.isStartTimeConfirmed() == null ? false : fixture.isStartTimeConfirmed();

        Date nextLiveTime1;
        try {
            nextLiveTime1 =
                fixture.getNextLiveTime() == null ? null : SdkHelper.toDate(fixture.getNextLiveTime());
        } catch (ParseException e) {
            logger.warn(
                "Fixture[{}] date of next live time is malformed -> {} ::: expected format -> '{}'",
                fixture.getId(),
                fixture.getNextLiveTime(),
                SdkHelper.ISO_8601_24H_FULL_FORMAT,
                e
            );
            nextLiveTime1 = null;
        }
        nextLiveTime = nextLiveTime1;

        startTimeTbd = fixture.isStartTimeTbd();

        Urn urnReplacedBy;
        try {
            urnReplacedBy = fixture.getReplacedBy() == null ? null : Urn.parse(fixture.getReplacedBy());
        } catch (UnsupportedUrnFormatException e) {
            logger.warn(
                "Fixture[{}] 'replaced by' is malformed -> {}",
                fixture.getId(),
                fixture.getReplacedBy()
            );
            urnReplacedBy = null;
        }

        replacedBy = urnReplacedBy;

        this.extraInfo =
            fixture.getExtraInfo() == null
                ? null
                : fixture
                    .getExtraInfo()
                    .getInfo()
                    .stream()
                    .collect(ImmutableMap.toImmutableMap(SapiInfo::getKey, SapiInfo::getValue));
        this.tvChannels =
            fixture.getTvChannels() == null
                ? null
                : fixture
                    .getTvChannels()
                    .getTvChannel()
                    .stream()
                    .map(ch ->
                        new TvChannelImpl(
                            ch.getName(),
                            ch.getStartTime() == null ? null : SdkHelper.toDate(ch.getStartTime()),
                            ch.getStreamUrl()
                        )
                    )
                    .collect(ImmutableList.toImmutableList());
        this.coverageInfo =
            fixture.getCoverageInfo() == null
                ? null
                : new CoverageInfoImpl(
                    fixture.getCoverageInfo().getLevel(),
                    fixture.getCoverageInfo().isLiveCoverage(),
                    fixture
                        .getCoverageInfo()
                        .getCoverage()
                        .stream()
                        .map(SapiCoverage::getIncludes)
                        .collect(Collectors.toList()),
                    fixture.getCoverageInfo().getCoveredFrom()
                );
        this.producerInfo =
            fixture.getProductInfo() == null ? null : new ProducerInfoImpl(fixture.getProductInfo());
        this.references =
            fixture.getReferenceIds() == null
                ? null
                : new ReferenceImpl(
                    new ReferenceIdCi(
                        fixture
                            .getReferenceIds()
                            .getReferenceId()
                            .stream()
                            .collect(
                                Collectors.toMap(
                                    SapiReferenceIds.SapiReferenceId::getName,
                                    SapiReferenceIds.SapiReferenceId::getValue
                                )
                            )
                    )
                );
        this.scheduledStartTimeChanges =
            fixture.getScheduledStartTimeChanges() == null
                ? null
                : fixture
                    .getScheduledStartTimeChanges()
                    .getScheduledStartTimeChange()
                    .stream()
                    .map(ch ->
                        new ScheduledStartTimeChangeImpl(
                            ch.getOldTime() == null ? null : SdkHelper.toDate(ch.getOldTime()),
                            ch.getNewTime() == null ? null : SdkHelper.toDate(ch.getNewTime()),
                            ch.getChangedAt() == null ? null : SdkHelper.toDate(ch.getChangedAt())
                        )
                    )
                    .collect(ImmutableList.toImmutableList());
        this.parentId = fixture.getParent() == null ? null : Urn.parse(fixture.getParent().getId());
        this.additionalParentsIds =
            fixture.getAdditionalParents() != null && !fixture.getAdditionalParents().getParent().isEmpty()
                ? fixture
                    .getAdditionalParents()
                    .getParent()
                    .stream()
                    .map(m -> Urn.parse(m.getId()))
                    .collect(ImmutableList.toImmutableList())
                : null;
    }

    public FixtureImpl(ExportableFixtureCi exportable) {
        Preconditions.checkNotNull(exportable);
        this.startTime = exportable.getStartTime();
        this.startTimeConfirmed = exportable.isStartTimeConfirmed();
        this.nextLiveTime = exportable.getNextLiveTime();
        this.extraInfo =
            exportable.getExtraInfo() != null ? ImmutableMap.copyOf(exportable.getExtraInfo()) : null;
        this.tvChannels =
            exportable.getTvChannels() != null
                ? exportable
                    .getTvChannels()
                    .stream()
                    .map(TvChannelImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : null;
        this.coverageInfo =
            exportable.getCoverageInfo() != null ? new CoverageInfoImpl(exportable.getCoverageInfo()) : null;
        this.producerInfo =
            exportable.getProducerInfo() != null ? new ProducerInfoImpl(exportable.getProducerInfo()) : null;
        this.references =
            exportable.getReferences() != null
                ? new ReferenceImpl(new ReferenceIdCi(exportable.getReferences()))
                : null;
        this.startTimeTbd = exportable.getStartTimeTbd();
        this.replacedBy = exportable.getReplacedBy() != null ? Urn.parse(exportable.getReplacedBy()) : null;
        this.scheduledStartTimeChanges =
            exportable.getScheduledStartTimeChanges() != null
                ? exportable
                    .getScheduledStartTimeChanges()
                    .stream()
                    .map(ScheduledStartTimeChangeImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : null;
        this.parentId =
            exportable.getParentId() == null || exportable.getParentId().isEmpty()
                ? null
                : Urn.parse(exportable.getParentId());
        this.additionalParentsIds =
            exportable.getAdditionalParentsIds() == null || exportable.getAdditionalParentsIds().isEmpty()
                ? null
                : exportable
                    .getAdditionalParentsIds()
                    .stream()
                    .map(m -> Urn.parse(m))
                    .collect(ImmutableList.toImmutableList());
    }

    /**
     * Returns the {@link Date} instance specifying when the fixture is scheduled to start
     *
     * @return - the {@link Date} instance specifying when the fixture is scheduled to start
     */
    @Override
    public Date getStartTime() {
        return startTime;
    }

    /**
     * Returns the value indicating whether the start time of the fixture has been confirmed
     *
     * @return - the value indicating whether the start time of the fixture has been confirmed
     */
    @Override
    public boolean isStartTimeConfirmed() {
        return startTimeConfirmed;
    }

    /**
     * An indication if the start tam is yet to be defined
     *
     * @return an indication if the start tam is yet to be defined
     */
    @Override
    public Boolean getStartTimeTbd() {
        return startTimeTbd;
    }

    /**
     * Returns the {@link Date} instance specifying the live time in case the fixture was re-schedule,
     * or a null reference if the fixture was not re-scheduled
     *
     * @return - the {@link Date} instance specifying the live time in case the fixture was re-schedule,
     * or a null reference if the fixture was not re-scheduled
     */
    @Override
    public Date getNextLiveTime() {
        return nextLiveTime;
    }

    /**
     * Returns an unmodifiable {@link Map} containing additional information about the fixture
     * @see com.google.common.collect.ImmutableMap
     *
     * @return - an unmodifiable {@link Map} containing additional information about the fixture
     */
    @Override
    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }

    /**
     * Returns an unmodifiable {@link List} representing TV channels covering the sport event
     * @see com.google.common.collect.ImmutableList
     *
     * @return - an unmodifiable {@link List} representing TV channels covering the sport event
     */
    @Override
    public List<TvChannel> getTvChannels() {
        return tvChannels;
    }

    /**
     * Returns the {@link CoverageInfo} instance specifying what coverage is available for the sport event
     *
     * @return - the {@link CoverageInfo} instance specifying what coverage is available for the sport event
     */
    @Override
    public CoverageInfo getCoverageInfo() {
        return coverageInfo;
    }

    /**
     * Returns the {@link ProducerInfo} instance providing sportradar related information about the sport event associated
     *
     * @return - the {@link ProducerInfo} instance providing sportradar related information about the sport event associated
     */
    @Override
    public ProducerInfo getProducerInfo() {
        return producerInfo;
    }

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    @Override
    public Reference getReferences() {
        return references;
    }

    /**
     * Returns the {@link Urn} identifier of the replacement event
     *
     * @return the {@link Urn} identifier of the replacement event
     */
    @Override
    public Urn getReplacedBy() {
        return replacedBy;
    }

    /**
     * Returns the list of all {@link ScheduledStartTimeChange} to start time
     *
     * @return the list of all {@link ScheduledStartTimeChange} to start time
     */
    @Override
    public List<ScheduledStartTimeChange> getScheduledStartTimeChanges() {
        return scheduledStartTimeChanges;
    }

    /**
     * Returns an id of the parent stage associated with the current instance
     * @return id of the parent stage associated with the current instance
     */
    @Override
    public Urn getParentStageId() {
        return parentId;
    }

    /**
     * Returns the list specifying the additional parent ids associated with the current instance
     * @return the list specifying the additional parent ids associated with the current instance
     */
    @Override
    public List<Urn> getAdditionalParentsIds() {
        return additionalParentsIds;
    }

    /**
     * Returns a {@link String} describing the current {@link Fixture} instance
     *
     * @return - a {@link String} describing the current {@link Fixture} instance
     */
    @Override
    public String toString() {
        return (
            "FixtureImpl{" +
            "startTime=" +
            startTime +
            ", startTimeConfirmed=" +
            startTimeConfirmed +
            ", nextLiveTime=" +
            nextLiveTime +
            ", extraInfo=" +
            extraInfo +
            ", tvChannels=" +
            tvChannels +
            ", coverageInfo=" +
            coverageInfo +
            ", producerInfo=" +
            producerInfo +
            ", references=" +
            references +
            ", scheduledStartTimeChange=" +
            scheduledStartTimeChanges +
            '}'
        );
    }

    public ExportableFixtureCi export() {
        return new ExportableFixtureCi(
            startTime,
            startTimeConfirmed,
            nextLiveTime,
            extraInfo != null ? new HashMap<>(extraInfo) : null,
            tvChannels != null
                ? tvChannels.stream().map(t -> ((TvChannelImpl) t).export()).collect(Collectors.toList())
                : null,
            coverageInfo != null ? ((CoverageInfoImpl) coverageInfo).export() : null,
            producerInfo != null ? ((ProducerInfoImpl) producerInfo).export() : null,
            references != null ? new HashMap<>(references.getReferences()) : null,
            startTimeTbd,
            replacedBy != null ? replacedBy.toString() : null,
            scheduledStartTimeChanges != null
                ? scheduledStartTimeChanges
                    .stream()
                    .map(s -> ((ScheduledStartTimeChangeImpl) s).export())
                    .collect(Collectors.toList())
                : null,
            parentId == null ? null : parentId.toString(),
            additionalParentsIds == null || additionalParentsIds.isEmpty()
                ? null
                : additionalParentsIds.stream().map(m -> m.toString()).collect(Collectors.toList())
        );
    }
}
