/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import com.google.common.collect.Lists;
import com.sportradar.unifiedodds.sdk.extended.UofExtListener;
import com.sportradar.unifiedodds.sdk.internal.impl.OddsFeedRoutingKeyBuilder;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.UofSessionImpl;
import com.sportradar.unifiedodds.sdk.managers.ProducerManager;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CriticalPath {

    private final Set<SessionData> createdSessionData = new HashSet<>();
    private final SdkInternalConfiguration oddsFeedConfiguration;
    private final UofExtListener uofExtListener;
    private final Supplier<UofSessionImpl> sessionFactory;

    private AtomicBoolean feedOpened;

    private UofSessionImpl systemMessagesSession;

    private ProducerManager producerManager;

    public CriticalPath(
        SdkInternalConfiguration oddsFeedConfiguration,
        UofExtListener uofExtListener,
        Supplier<UofSessionImpl> sessionFactory,
        AtomicBoolean feedOpened,
        ProducerManager producerManager
    ) {
        this.oddsFeedConfiguration = oddsFeedConfiguration;
        this.uofExtListener = uofExtListener;
        this.sessionFactory = sessionFactory;
        this.feedOpened = feedOpened;
        this.producerManager = producerManager;
    }

    @SuppressWarnings("MethodLength")
    public void open() throws IOException {
        // disable the producers that are not requested by specified message interests
        Set<Integer> requestedProducers = new HashSet<>();
        for (SessionData createdSession : createdSessionData) {
            requestedProducers.addAll(
                createdSession.messageInterest.getPossibleSourceProducers(
                    producerManager.getAvailableProducers()
                )
            );
        }

        producerManager
            .getAvailableProducers()
            .keySet()
            .forEach(id -> {
                if (!requestedProducers.contains(id)) {
                    producerManager.disableProducer(id);
                }
            });

        if (producerManager.getActiveProducers().isEmpty()) {
            String interests = createdSessionData
                .stream()
                .map(sessionData -> sessionData.messageInterest.toString())
                .collect(Collectors.joining(", "));
            throw new IllegalStateException(
                String.format(
                    "Message interests [%s] cannot be used. There are no suitable active producers.",
                    interests
                )
            );
        }

        Map<Integer, List<String>> sessionRoutingKeys = OddsFeedRoutingKeyBuilder.generateKeys(
            createdSessionData
                .stream()
                .collect(
                    Collectors.toMap(
                        Object::hashCode,
                        v -> new AbstractMap.SimpleEntry<>(v.messageInterest, v.eventIds)
                    )
                ),
            oddsFeedConfiguration
        );

        boolean aliveRoutingKeySessionPresent = createdSessionData
            .stream()
            .anyMatch(cs -> cs.messageInterest == MessageInterest.SystemAliveMessages);
        if (!aliveRoutingKeySessionPresent) {
            systemMessagesSession = sessionFactory.get();
            SessionData firstCreatedSession = createdSessionData
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Feed created without sessions?"));

            systemMessagesSession.open(
                Lists.newArrayList(MessageInterest.SystemAliveMessages.getRoutingKeys()),
                MessageInterest.SystemAliveMessages,
                firstCreatedSession.uofListener,
                uofExtListener
            );
        }

        for (SessionData sessionData : createdSessionData) {
            sessionData.session.open(
                sessionRoutingKeys.get(sessionData.hashCode()),
                sessionData.messageInterest,
                sessionData.uofListener,
                uofExtListener
            );
        }
    }

    public void close() {
        if (systemMessagesSession != null) {
            systemMessagesSession.close();
        }
        for (SessionData sessionData : createdSessionData) {
            sessionData.session.close();
        }
    }

    public UofSessionBuilder createBuilder() {
        return new CriticalPath.UofSessionBuilderImpl(this);
    }

    public boolean anySessionsConfigured() {
        return !createdSessionData.isEmpty();
    }

    private void createSession(
        UofSessionImpl session,
        MessageInterest oddsInterest,
        Set<Urn> eventIds,
        UofListener uofListener
    ) {
        if (this.feedOpened.get()) {
            throw new IllegalStateException("Sessions can not be created once the feed has been opened");
        } else {
            SessionData sessionData = new SessionData(session, oddsInterest, eventIds, uofListener);

            createdSessionData.add(sessionData);
        }
    }

    class UofSessionBuilderImpl implements UofSessionBuilder {

        private CriticalPath criticalPath;
        private UofListener mainUofListener;
        private MessageInterest msgInterestLevel;
        private Set<Urn> eventIds;

        UofSessionBuilderImpl(CriticalPath criticalPath) {
            this.criticalPath = criticalPath;
        }

        @Override
        public UofSessionBuilder setListener(UofListener listener) {
            this.mainUofListener = listener;
            return this;
        }

        @Override
        public UofSessionBuilder setMessageInterest(MessageInterest msgInterest) {
            this.msgInterestLevel = msgInterest;
            return this;
        }

        @Override
        public UofSessionBuilder setSpecificEventsOnly(Set<Urn> specificEventsOnly) {
            this.msgInterestLevel = MessageInterest.SpecifiedMatchesOnly;

            if (this.eventIds == null) {
                this.eventIds = new HashSet<>();
            }

            this.eventIds.addAll(specificEventsOnly);

            return this;
        }

        @Override
        public UofSessionBuilder setSpecificEventsOnly(Urn specificEventsOnly) {
            return setSpecificEventsOnly(Collections.singleton(specificEventsOnly));
        }

        @Override
        public UofSession build() {
            UofSessionImpl session = sessionFactory.get();
            criticalPath.createSession(session, msgInterestLevel, eventIds, mainUofListener);

            this.msgInterestLevel = null;
            this.eventIds = null;
            this.mainUofListener = null;

            return session;
        }
    }

    class SessionData {

        private final UofSessionImpl session;
        private final MessageInterest messageInterest;
        private final Set<Urn> eventIds;
        private final UofListener uofListener;

        SessionData(
            UofSessionImpl session,
            MessageInterest messageInterest,
            Set<Urn> eventIds,
            UofListener uofListener
        ) {
            this.session = session;
            this.messageInterest = messageInterest;
            this.eventIds = eventIds;
            this.uofListener = uofListener;
        }
    }
}
