/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.entities.TeamCompetitor;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;

public class TeamCompetitors {

    public static TeamCompetitorBuilder teamCompetitor() {
        return new TeamCompetitorBuilder();
    }

    public static final class TeamCompetitorBuilder {

        private final Map<Locale, String> names = new HashMap<>();

        public TeamCompetitorBuilder withName(LanguageHolder language, String name) {
            this.names.put(language.get(), name);
            return this;
        }

        public TeamCompetitor build() {
            val competitor = mock(TeamCompetitor.class);
            when(competitor.getName(any()))
                .thenAnswer(inv -> {
                    Locale language = inv.getArgument(0);
                    return names.get(language);
                });
            when(competitor.getNames()).thenReturn(names);
            return competitor;
        }
    }
}
