/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableFixtureCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableScheduledStartTimeChangeCI;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A representation of a fixture
 *
 * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
 *
 * @see Fixture
 */
public class
FixtureImpl implements Fixture {
    private static final Logger logger = LoggerFactory.getLogger(FixtureImpl.class);
    private static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

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
     * The {@link URN} identifier of the replacement event
     */
    private final URN replacedBy;

    private final List<ScheduledStartTimeChange> scheduledStartTimeChanges;

    /**
     * Initializes a new instance of the {@link FixtureImpl}
     *
     * @param fixture - {@link SAPIFixture} used to create the new instance
     */
    public FixtureImpl(SAPIFixture fixture) {
        Preconditions.checkNotNull(fixture);

        this.startTime = fixture.getStartTime() == null ? null :
                fixture.getStartTime().toGregorianCalendar().getTime();
        this.startTimeConfirmed = fixture.isStartTimeConfirmed() == null ? false : fixture.isStartTimeConfirmed();

        Date nextLiveTime1;
        try {
            nextLiveTime1 = fixture.getNextLiveTime() == null ? null : parseNextLiveTime(fixture.getNextLiveTime());
        } catch (ParseException e) {
            logger.warn("Fixture[{}] date of next live time is malformed -> {} ::: expected format -> '{}'", fixture.getId(), fixture.getNextLiveTime(), ISO_8601_24H_FULL_FORMAT,e);
            nextLiveTime1 = null;
        }
        nextLiveTime = nextLiveTime1;

        startTimeTbd = fixture.isStartTimeTbd();

        URN urnReplacedBy;
        try {
            urnReplacedBy = fixture.getReplacedBy() == null ? null : URN.parse(fixture.getReplacedBy());
        } catch (UnsupportedUrnFormatException e) {
            logger.warn("Fixture[{}] 'replaced by' is malformed -> {}", fixture.getId(), fixture.getReplacedBy());
            urnReplacedBy = null;
        }

        replacedBy = urnReplacedBy;

        this.extraInfo = fixture.getExtraInfo() == null ? null :
                fixture.getExtraInfo().getInfo().stream()
                        .collect(ImmutableMap.toImmutableMap(SAPIInfo::getKey, SAPIInfo::getValue));
        this.tvChannels = fixture.getTvChannels() == null ? null :
                fixture.getTvChannels().getTvChannel().stream()
                        .map(ch -> new TvChannelImpl(
                                ch.getName(),
                                ch.getStartTime() == null ? null : ch.getStartTime().toGregorianCalendar().getTime(),
                                ch.getStreamUrl()))
                        .collect(ImmutableList.toImmutableList());
        this.coverageInfo = fixture.getCoverageInfo() == null ? null :
                new CoverageInfoImpl(
                        fixture.getCoverageInfo().getLevel(),
                        fixture.getCoverageInfo().isLiveCoverage(),
                        fixture.getCoverageInfo().getCoverage()
                                .stream().map(SAPICoverage::getIncludes)
                                .collect(Collectors.toList()),
                        fixture.getCoverageInfo().getCoveredFrom());
        this.producerInfo = fixture.getProductInfo() == null ? null :
                new ProducerInfoImpl(
                        fixture.getProductInfo().getIsAutoTraded() != null,
                        fixture.getProductInfo().getIsInHostedStatistics() != null,
                        fixture.getProductInfo().getIsInLiveCenterSoccer() != null,
                        fixture.getProductInfo().getIsInLiveScore() != null,
                        this.prepareProductLinks(fixture.getProductInfo().getLinks()),
                        this.prepareProductStreams(fixture.getProductInfo().getStreaming())
                );
        this.references = fixture.getReferenceIds() == null ? null :
                new ReferenceImpl(
                        new ReferenceIdCI(
                                fixture.getReferenceIds().getReferenceId()
                                        .stream().collect(Collectors.toMap(SAPIReferenceIds.SAPIReferenceId::getName, SAPIReferenceIds.SAPIReferenceId::getValue))));
        this.scheduledStartTimeChanges = fixture.getScheduledStartTimeChanges() == null ? null :
                fixture.getScheduledStartTimeChanges().getScheduledStartTimeChange().stream()
                        .map(ch -> new ScheduledStartTimeChangeImpl(
                                ch.getOldTime() == null ? null : ch.getOldTime().toGregorianCalendar().getTime(),
                                ch.getNewTime() == null ? null : ch.getNewTime().toGregorianCalendar().getTime(),
                                ch.getChangedAt() == null ? null : ch.getChangedAt().toGregorianCalendar().getTime()))
                        .collect(ImmutableList.toImmutableList());
    }

    public FixtureImpl(ExportableFixtureCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.startTime = exportable.getStartTime();
        this.startTimeConfirmed = exportable.isStartTimeConfirmed();
        this.nextLiveTime = exportable.getNextLiveTime();
        this.extraInfo = ImmutableMap.copyOf(exportable.getExtraInfo());
        this.tvChannels = exportable.getTvChannels().stream().map(TvChannelImpl::new).collect(ImmutableList.toImmutableList());
        this.coverageInfo = new CoverageInfoImpl(exportable.getCoverageInfo());
        this.producerInfo = new ProducerInfoImpl(exportable.getProducerInfo());
        this.references = new ReferenceImpl(new ReferenceIdCI(exportable.getReferences()));
        this.startTimeTbd = exportable.getStartTimeTbd();
        this.replacedBy = URN.parse(exportable.getReplacedBy());
        this.scheduledStartTimeChanges = exportable.getScheduledStartTimeChanges().stream().map(ScheduledStartTimeChangeImpl::new).collect(ImmutableList.toImmutableList());
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
     * Prepares the {@link SAPIProductInfoLinks} entities for further use in the {@link FixtureImpl}
     *
     * @param links - the {@link SAPIProductInfoLinks} instance that should be prepped for further use
     * @return - a {@link List} of processed {@link ProducerInfoLink} entities
     */
    private List<ProducerInfoLink> prepareProductLinks(SAPIProductInfoLinks links) {
        return links == null ? null :
                links.getLink().stream()
                        .map(link -> new ProducerInfoLinkImpl(link.getRef(), link.getName()))
                        .collect(Collectors.toList());
    }

    /**
     * Prepares the {@link SAPIStreamingChannels} entities for further use in the {@link FixtureImpl}
     *
     * @param streamingChannels - the {@link SAPIStreamingChannels} instance that should be prepped for further use
     * @return - a {@link List} of processed {@link StreamingChannel} entities
     */
    private List<StreamingChannel> prepareProductStreams(SAPIStreamingChannels streamingChannels) {
        return streamingChannels == null ? null :
                streamingChannels.getChannel().stream()
                        .map(channel -> new StreamingChannelImpl(channel.getId(), channel.getName()))
                        .collect(Collectors.toList());
    }

    /**
     * Returns the {@link URN} identifier of the replacement event
     *
     * @return the {@link URN} identifier of the replacement event
     */
    @Override
    public URN getReplacedBy() {
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
     * Returns a {@link String} describing the current {@link Fixture} instance
     *
     * @return - a {@link String} describing the current {@link Fixture} instance
     */
    @Override
    public String toString() {
        return "FixtureImpl{" +
                "startTime=" + startTime +
                ", startTimeConfirmed=" + startTimeConfirmed +
                ", nextLiveTime=" + nextLiveTime +
                ", extraInfo=" + extraInfo +
                ", tvChannels=" + tvChannels +
                ", coverageInfo=" + coverageInfo +
                ", producerInfo=" + producerInfo +
                ", references=" + references +
                ", scheduledStartTimeChange=" + scheduledStartTimeChanges +
                '}';
    }

    private static Date parseNextLiveTime(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_24H_FULL_FORMAT);
        return sdf.parse(date);
    }

    public ExportableFixtureCI export() {
        return new ExportableFixtureCI(
                startTime,
                startTimeConfirmed,
                nextLiveTime,
                new HashMap<>(extraInfo),
                tvChannels.stream().map(t -> ((TvChannelImpl) t).export()).collect(Collectors.toList()),
                ((CoverageInfoImpl) coverageInfo).export(),
                ((ProducerInfoImpl) producerInfo).export(),
                new HashMap<>(references.getReferences()),
                startTimeTbd,
                replacedBy.toString(),
                scheduledStartTimeChanges.stream().map(s -> ((ExportableScheduledStartTimeChangeCI) s).export()).collect(Collectors.toList())
        );
    }
}
