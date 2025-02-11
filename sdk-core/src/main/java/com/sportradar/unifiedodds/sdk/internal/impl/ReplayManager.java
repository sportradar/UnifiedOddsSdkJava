/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.entities.ReplaySportEvent;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.entities.ReplaySportEventImpl;
import com.sportradar.unifiedodds.sdk.managers.ReplayScenario;
import com.sportradar.unifiedodds.sdk.managers.ReplayStatus;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
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

    public boolean addSportEventToReplay(SportEvent event) {
        return addSportEventToReplay(event, null);
    }

    public boolean addSportEventToReplay(SportEvent event, Integer startTime) {
        Preconditions.checkNotNull(event);

        return addSportEventToReplay(event.getId(), startTime);
    }

    public boolean addSportEventToReplay(Urn id) {
        return this.addSportEventToReplay(id, null);
    }

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

    public boolean play(double speedupFactor, int maxDelayInMs) {
        return play(speedupFactor, maxDelayInMs, null, null);
    }

    public boolean play(double speedupFactor, int maxDelayInMs, Boolean runParallel) {
        return play(speedupFactor, maxDelayInMs, null, null, runParallel);
    }

    public boolean play(
        double speedupFactor,
        int maxDelayInMs,
        Integer producerId,
        Boolean rewriteTimestamps
    ) {
        return play(speedupFactor, maxDelayInMs, producerId, rewriteTimestamps, null);
    }

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

    public boolean clear() {
        boolean responseStatus = internalClear();
        clientInteractionLog.info(
            "ReplayManager.clear() -> response status: {}",
            responseStatusString(responseStatus)
        );

        return responseStatus;
    }

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

    public boolean playScenario(int id, double speedupFactor, int maxDelayInMs) {
        return playScenario(id, speedupFactor, maxDelayInMs, null, null);
    }

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
