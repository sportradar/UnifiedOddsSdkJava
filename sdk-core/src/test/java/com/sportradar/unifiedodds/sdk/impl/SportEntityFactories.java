/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.impl.SportEntityFactory;
import com.sportradar.utils.Urn;
import java.util.List;

public class SportEntityFactories {

    public static SportEntityFactory providingSportEvent(SportEvent sportEvent)
        throws ObjectNotFoundException {
        SportEntityFactory sportEntityFactory = mock(SportEntityFactory.class);
        when(
            sportEntityFactory.buildSportEvent(
                any(Urn.class),
                any(Urn.class),
                any(List.class),
                any(Boolean.class)
            )
        )
            .thenReturn(sportEvent);
        when(sportEntityFactory.buildSportEvent(any(Urn.class), any(List.class), any(Boolean.class)))
            .thenReturn(sportEvent);
        return sportEntityFactory;
    }
}
