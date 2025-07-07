/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.caching.markets.GenericAnswers.withAllMethodsThrowingByDefault;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.List;
import lombok.SneakyThrows;
import lombok.val;

public class DataRouterManagers {

    private DataRouterManagers() {}

    public static DataRouterManager any() {
        return new NoOpDataRouterManager();
    }

    @SneakyThrows
    public static DataRouterManager providingSportEventsList(
        LanguageHolder language,
        int from,
        int to,
        List<SportEvent> sportEvents
    ) {
        val manager = mock(DataRouterManager.class, withAllMethodsThrowingByDefault());
        doReturn(sportEvents.stream().map(SportEvent::getId).collect(toList()))
            .when(manager)
            .requestListSportEvents(language.get(), from, to);
        return manager;
    }

    @SneakyThrows
    public static DataRouterManager failsToProvideSportEventsList(LanguageHolder language, int from, int to) {
        val manager = mock(DataRouterManager.class, withAllMethodsThrowingByDefault());
        doThrow(new CommunicationException("failed to get list of sport events", "/some/url"))
            .when(manager)
            .requestListSportEvents(language.get(), from, to);
        return manager;
    }
}
