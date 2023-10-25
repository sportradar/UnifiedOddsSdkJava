/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.replay;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.SportDataProvider;
import com.sportradar.unifiedodds.sdk.SportEntityFactory;
import com.sportradar.unifiedodds.sdk.entities.ReplaySportEvent;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.entities.ReplaySportEventImpl;
import com.sportradar.utils.Urn;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to manage the set of SportEvents whose messages to replay and to control the
 * Replay (play, stop, and in what speed to play).
 */
@Singleton
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MultipleStringLiterals" })
public class ReplayManager {

    /**
     * The client interaction log instance
     */
    private static final Logger clientInteractionLog = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkClientInteractionLog.class
    );

    private static final Logger logger = LoggerFactory.getLogger(ReplayManager.class);
    private final SdkInternalConfiguration config;
    private final SportDataProvider sportDataProvider;
    private final HttpHelper httpHelper;
    private final Deserializer deserializer;
    private final LogHttpDataFetcher logHttpDataFetcher;
    private final SportEntityFactory sportEntityFactory;

    @Inject
    ReplayManager(
        SdkInternalConfiguration config,
        SportDataProvider sportDataProvider,
        HttpHelper httpHelper,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer,
        LogHttpDataFetcher logHttpDataFetcher,
        SportEntityFactory sportEntityFactory
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(sportDataProvider);
        Preconditions.checkNotNull(httpHelper);
        Preconditions.checkNotNull(deserializer);
        Preconditions.checkNotNull(logHttpDataFetcher);
        Preconditions.checkNotNull(sportEntityFactory);

        this.config = config;
        this.sportDataProvider = sportDataProvider;
        this.httpHelper = httpHelper;
        this.deserializer = deserializer;
        this.logHttpDataFetcher = logHttpDataFetcher;
        this.sportEntityFactory = sportEntityFactory;

        logger.info("Replay manager initialized");
    }

    /**
     * @return the list of SportEvents whose recorded messages are scheduled for replay
     */
    public List<SportEvent> getReplayList() {
        List<ReplaySportEvent> replaySportEventsList = getReplaySportEventsList();
        if (replaySportEventsList == null) {
            return null;
        }

        return replaySportEventsList
            .stream()
            .map(r -> sportDataProvider.getSportEvent(r.getId()))
            .collect(Collectors.toList());
    }

    /**
     * @return the list of ReplaySportEvents whose recorded messages are scheduled for replay
     */
    public List<ReplaySportEvent> getReplaySportEventsList() {
        final List<ReplaySportEvent> events = new LinkedList<>();

        DataProvider<ReplaySetContent> dataProvider = getDataProvider("/replay/" + buildNodeIdQuery("?"));

        ReplaySetContent replaySetContent;
        try {
            replaySetContent = dataProvider.getData();
        } catch (DataProviderException e) {
            logger.warn("ReplayManager.getReplaySportEventsList() request failed, ex:", e);
            clientInteractionLog.info(
                "ReplayManager.getReplaySportEventsList() -> FAILED with {} events",
                events.size()
            );
            return null;
        }

        if (replaySetContent != null && replaySetContent.getEvent() != null) {
            for (ReplayEvent replayEvent : replaySetContent.getEvent()) {
                ReplaySportEvent event = new ReplaySportEventImpl(
                    Urn.parse(replayEvent.getId()),
                    replayEvent.getPosition(),
                    replayEvent.getStartTime()
                );
                events.add(event);
            }
        }

        clientInteractionLog.info("ReplayManager.getReplayList() -> count: {}", events.size());
        return events;
    }

    /**
     * Add a SportEvent to the list of SportEvents whose recorded messages will be replayed. The
     * SportEvents to add have to be older than 48hours, there is no max time, but typically
     * Sportradar does not guarantee that SportEvents older than 30 days can be replayed.
     *
     * @param event the SportEvent to add
     * @return an indication of the request success
     */
    public boolean addSportEventToReplay(SportEvent event) {
        return addSportEventToReplay(event, null);
    }

    /**
     * Add a SportEvent to the list of SportEvents whose recorded messages will be replayed. The
     * SportEvents to add have to be older than 48hours, there is no max time, but typically
     * Sportradar does not guarantee that SportEvents older than 30 days can be replayed.
     *
     * @param event the SportEvent to add
     * @param startTime minutes relative to event start time
     * @return an indication of the request success
     */
    public boolean addSportEventToReplay(SportEvent event, Integer startTime) {
        Preconditions.checkNotNull(event);

        return addSportEventToReplay(event.getId(), startTime);
    }

    /**
     * Add a SportEvent to the list of SportEvents whose recorded messages will be replayed. The
     * SportEvents to add have to be older than 48hours, there is no max time, but typically
     * Sportradar does not guarantee that SportEvents older than 30 days can be replayed.
     *
     * @param id the id of the SportEvent to add
     * @return an indication of the request success
     */
    public boolean addSportEventToReplay(Urn id) {
        return this.addSportEventToReplay(id, null);
    }

    /**
     * Add a SportEvent to the list of SportEvents whose recorded messages will be replayed. The
     * SportEvents to add have to be older than 48hours, there is no max time, but typically
     * Sportradar does not guarantee that SportEvents older than 30 days can be replayed.
     *
     * @param id the id of the SportEvent to add
     * @param startTime minutes relative to event start time
     * @return an indication of the request success
     */
    public boolean addSportEventToReplay(Urn id, Integer startTime) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(
            startTime == null || startTime >= 0,
            "starTime can either be null or >= 0"
        );

        String responseMessage;
        boolean status = false;
        try {
            HttpHelper.ResponseData responseData = httpHelper.put(
                String.format(
                    "https://%s/v1/replay/events/%s?%s%s",
                    config.getApiHostAndPort(),
                    id,
                    startTime == null ? "" : "start_time=" + startTime,
                    buildNodeIdQuery("&")
                )
            );
            status = responseData.isSuccessful();
            responseMessage = responseData.getMessage();
        } catch (CommunicationException e) {
            logger.warn("Error adding sport event to replay que", e);
            responseMessage = "Exception: " + e.getMessage();
        }

        if (!status) {
            logger.info(
                "ReplayManager.addSportEventToReplay({}, {}) failed - message: {}",
                id,
                startTime,
                responseMessage == null ? "Error" : responseMessage
            );
        }

        clientInteractionLog.info(
            "ReplayManager.addSportEventToReplay({},{}) -> response status: {}, message: {}",
            id,
            startTime,
            responseStatusString(status),
            responseMessage == null ? "Unknown" : responseMessage
        );
        return status;
    }

    /**
     * Removes a sport event from the replay list
     *
     * @param id the identifier of the event which should be removed from the replay list
     * @return <code>true</code> if the removal was successful, otherwise <code>false</code>
     */
    public boolean removeSportEventFromReplay(Urn id) {
        Preconditions.checkNotNull(id);

        String responseMessage;
        boolean status = false;
        try {
            HttpHelper.ResponseData responseData = httpHelper.delete(
                String.format(
                    "https://%s/v1/replay/events/%s%s",
                    config.getApiHostAndPort(),
                    id,
                    buildNodeIdQuery("?")
                )
            );
            status = responseData.isSuccessful();
            responseMessage = responseData.getMessage();
        } catch (CommunicationException e) {
            logger.warn("Error removing sport event to replay que", e);
            responseMessage = "Exception: " + e.getMessage();
        }

        if (!status) {
            logger.info(
                "ReplayManager.removeSportEventFromReplay({}) failed - message: {}",
                id,
                responseMessage == null ? "Error" : responseMessage
            );
        }

        clientInteractionLog.info(
            "ReplayManager.removeSportEventFromReplay({}) -> response status: {}, message: {}",
            id,
            responseStatusString(status),
            responseMessage == null ? "Unknown" : responseMessage
        );
        return status;
    }

    /**
     * Starts playing the messages for the SportEvents in the play list. The speed will be the
     * default speed (10x faster than actual recorded speed) and with a maximum delay between events
     * of 10 seconds (even if the actual time would have been longer).
     *
     * @return an indication of the request success
     */
    public boolean play() {
        boolean responseStatus = tryPerformRequest(
            String.format("https://%s/v1/replay/play%s", config.getApiHostAndPort(), buildNodeIdQuery("?"))
        );

        clientInteractionLog.info(
            "ReplayManager.play() -> response status: {}",
            responseStatusString(responseStatus)
        );
        return responseStatus;
    }

    /**
     * Starts playing the messages for the SportEvents in the play list.
     *
     * @param speedupFactor how much faster to replay the recorded events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would have been longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart.
     * @return an indication of the request success
     */
    public boolean play(double speedupFactor, int maxDelayInMs) {
        return play(speedupFactor, maxDelayInMs, null, null);
    }

    /**
     * Starts playing the messages for the SportEvents in the play list.
     *
     * @param speedupFactor how much faster to replay the recorded events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would have been longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart.
     * @param runParallel an indication if every event in the queue should be replayed independently. Speed factor and
     *        delay will be applied for each event separately.
     * @return an indication of the request success
     */
    public boolean play(double speedupFactor, int maxDelayInMs, Boolean runParallel) {
        return play(speedupFactor, maxDelayInMs, null, null, runParallel);
    }

    /**
     * Starts playing the messages for the SportEvents in the play list
     *
     * @param speedupFactor how much faster to replay the recorded events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would have been longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart
     * @param producerId the producer from which the messages should be played
     * @param rewriteTimestamps an indication if the message timestamps should be rewritten with current timestamps
     * @return an indication of the request success
     */
    public boolean play(
        double speedupFactor,
        int maxDelayInMs,
        Integer producerId,
        Boolean rewriteTimestamps
    ) {
        return play(speedupFactor, maxDelayInMs, producerId, rewriteTimestamps, null);
    }

    /**
     * Starts playing the messages for the SportEvents in the play list
     *
     * @param speedupFactor how much faster to replay the recorded events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would have been longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart
     * @param producerId the producer from which the messages should be played
     * @param rewriteTimestamps an indication if the message timestamps should be rewritten with current timestamps
     * @param runParallel an indication if every event in the queue should be replayed independently. Speed factor and
     *        delay will be applied for each event separately.
     * @return an indication of the request success
     */
    public boolean play(
        double speedupFactor,
        int maxDelayInMs,
        Integer producerId,
        Boolean rewriteTimestamps,
        Boolean runParallel
    ) {
        List<NameValuePair> queryParams = Stream
            .of(
                new BasicNameValuePair("speed", String.valueOf((int) speedupFactor)),
                new BasicNameValuePair("max_delay", String.valueOf(maxDelayInMs)),
                new BasicNameValuePair(
                    "node_id",
                    config.getSdkNodeId() != null ? config.getSdkNodeId().toString() : null
                ),
                new BasicNameValuePair("product", producerId != null ? producerId.toString() : null),
                new BasicNameValuePair(
                    "use_replay_timestamp",
                    rewriteTimestamps != null ? rewriteTimestamps.toString() : null
                ),
                new BasicNameValuePair("run_parallel", runParallel != null ? runParallel.toString() : null)
            )
            .filter(v -> v.getValue() != null)
            .collect(Collectors.toList());

        boolean responseStatus = tryPerformRequest(
            String.format(
                "https://%s/v1/replay/play%s",
                config.getApiHostAndPort(),
                new URIBuilder().addParameters(queryParams).setCharset(StandardCharsets.UTF_8).toString()
            )
        );

        clientInteractionLog.info(
            "ReplayManager.play({},{},{},{}) -> response status: {}",
            speedupFactor,
            maxDelayInMs,
            producerId,
            rewriteTimestamps,
            responseStatusString(responseStatus)
        );
        return responseStatus;
    }

    /**
     * Stops playing recorded messages from the playlist.
     *
     * @return an indication of the request success
     */
    public boolean stop() {
        boolean responseStatus = tryPerformRequest(
            String.format("https://%s/v1/replay/stop%s", config.getApiHostAndPort(), buildNodeIdQuery("?"))
        );

        clientInteractionLog.info(
            "ReplayManager.stop() -> response status: {}",
            responseStatusString(responseStatus)
        );
        return responseStatus;
    }

    /**
     * Stops playing recorded messages from the playlist and clears the playlist. (When the
     * ReplayManager is created this method is always executed automatically)
     *
     * @return an indication of the request success
     */
    public boolean clear() {
        boolean responseStatus = internalClear();
        clientInteractionLog.info(
            "ReplayManager.clear() -> response status: {}",
            responseStatusString(responseStatus)
        );

        return responseStatus;
    }

    /**
     * Get the current status of the replayer
     *
     * @return the current status of the replayer (Playing or Stopped)
     */
    public ReplayStatus getPlayStatus() {
        DataProvider<PlayerStatus> dataProvider = getDataProvider("/replay/status" + buildNodeIdQuery("?"));

        PlayerStatus data;
        try {
            data = dataProvider.getData();
        } catch (DataProviderException e) {
            logger.warn("ReplayManager.getPlayStatus() request failed, ex:", e);
            clientInteractionLog.info("ReplayManager.getPlayStatus() -> FAILED");
            return null;
        }

        ReplayStatus status;
        if (data.getStatus() != null && data.getStatus().toLowerCase().equals("playing")) {
            status = ReplayStatus.Playing;
        } else {
            status = ReplayStatus.Stopped;
        }

        clientInteractionLog.info("ReplayManager.getPlayStatus() -> {}", status);
        return status;
    }

    /**
     * Returns a {@link List} of available replay scenarios
     *
     * @return a {@link List} of available replay scenarios
     */
    public List<ReplayScenario> getAvailableScenarios() {
        DataProvider<ReplayScenariosType> dataProvider = getDataProvider(
            "/replay/scenario" + buildNodeIdQuery("?")
        );

        ReplayScenariosType data;
        try {
            data = dataProvider.getData();
        } catch (DataProviderException e) {
            logger.warn("ReplayManager.getAvailableScenarios() request failed, ex:", e);
            clientInteractionLog.info("ReplayManager.getAvailableScenarios() -> FAILED");
            return null;
        }

        List<ReplayScenarioType> replayScenario = data.getReplayScenario();

        return replayScenario == null
            ? null
            : replayScenario
                .stream()
                .map(scenario ->
                    new ReplayScenario(scenario, sportEntityFactory, config.getExceptionHandlingStrategy())
                )
                .collect(Collectors.toList());
    }

    /**
     * Starts the replay of the selected scenario
     *
     * @param id the identifier of the scenario that you want to replay
     * @return <code>true</code> if the request executed successfully, otherwise <code>false</code>
     */
    public boolean playScenario(int id) {
        boolean responseStatus = tryPerformRequest(
            String.format(
                "https://%s/v1/replay/scenario/play/%d%s",
                config.getApiHostAndPort(),
                id,
                buildNodeIdQuery("?")
            )
        );

        clientInteractionLog.info(
            "ReplayManager.playScenario({}) -> response status: {}",
            id,
            responseStatusString(responseStatus)
        );
        return responseStatus;
    }

    /**
     * Starts the replay of the selected scenario
     *
     * @param id the identifier of the scenario that you want to replay
     * @param speedupFactor how much faster to replay the scenario events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would be longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart.
     * @return an indication of the request success
     */
    public boolean playScenario(int id, double speedupFactor, int maxDelayInMs) {
        return playScenario(id, speedupFactor, maxDelayInMs, null, null);
    }

    /**
     * Starts playing the messages for the SportEvents in the play list
     *
     * @param id the identifier of the scenario that you want to replay
     * @param speedupFactor how much faster to replay the recorded events (by default this is 10x
     *        faster)
     * @param maxDelayInMs the longest delay between two messages, if the delay would be longer
     *        than this it is shortened to this much. This is to avoid waiting for very long if two
     *        messages were very far apart
     * @param producerId the producer from which the messages should be played
     * @param rewriteTimestamps an indication if the message timestamps should be rewritten with current timestamps
     * @return an indication of the request success
     */
    public boolean playScenario(
        int id,
        double speedupFactor,
        int maxDelayInMs,
        Integer producerId,
        Boolean rewriteTimestamps
    ) {
        List<NameValuePair> queryParams = Stream
            .of(
                new BasicNameValuePair("speed", String.valueOf((int) speedupFactor)),
                new BasicNameValuePair("max_delay", String.valueOf(maxDelayInMs)),
                new BasicNameValuePair(
                    "node_id",
                    config.getSdkNodeId() != null ? config.getSdkNodeId().toString() : null
                ),
                new BasicNameValuePair("product", producerId != null ? producerId.toString() : null),
                new BasicNameValuePair(
                    "use_replay_timestamp",
                    rewriteTimestamps != null ? rewriteTimestamps.toString() : null
                )
            )
            .filter(v -> v.getValue() != null)
            .collect(Collectors.toList());

        boolean responseStatus = tryPerformRequest(
            String.format(
                "https://%s/v1/replay/scenario/play/%d%s",
                config.getApiHostAndPort(),
                id,
                new URIBuilder().addParameters(queryParams).setCharset(StandardCharsets.UTF_8).toString()
            )
        );

        clientInteractionLog.info(
            "ReplayManager.playScenario({},{},{},{},{}) -> response status: {}",
            id,
            speedupFactor,
            maxDelayInMs,
            producerId,
            rewriteTimestamps,
            responseStatusString(responseStatus)
        );
        return responseStatus;
    }

    private boolean internalClear() {
        return tryPerformRequest(
            String.format("https://%s/v1/replay/reset%s", config.getApiHostAndPort(), buildNodeIdQuery("?"))
        );
    }

    private boolean tryPerformRequest(String path) {
        try {
            HttpHelper.ResponseData post = httpHelper.post(path);

            if (!post.isSuccessful()) {
                logger.warn("Replay request[{}] failed, message: {}", path, post.getMessage());
            }

            return post.isSuccessful();
        } catch (CommunicationException e) {
            logger.warn("An exception occurred while performing replay operation", e);
            return false;
        }
    }

    private String responseStatusString(boolean status) {
        return status ? "OK" : "FAILED";
    }

    private String buildNodeIdQuery(String prefix) {
        return config.getSdkNodeId() == null ? "" : prefix + "node_id=" + config.getSdkNodeId();
    }

    private <T> DataProvider<T> getDataProvider(String query) {
        Preconditions.checkNotNull(query);

        return new DataProvider<>(
            query,
            config.getApiHostAndPort(),
            true,
            config.getDefaultLocale(),
            logHttpDataFetcher,
            deserializer
        );
    }
}
