/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiFixtureChange;
import com.sportradar.uf.sportsapi.datamodel.SapiFixtureChangesEndpoint;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.ZonedDateTime;

public class SapiFixtureChanges {

    public static SapiFixtureChangesEndpoint fixtureChanges() {
        SapiFixtureChangesEndpoint result = new SapiFixtureChangesEndpoint();
        result.getFixtureChange().add(fixtureChange("sr:match:50383677", "2024-10-16T10:20:35+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:51849659", "2024-10-16T10:20:27+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54167709", "2024-10-16T10:20:26+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:51467749", "2024-10-16T10:20:26+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54433321", "2024-10-16T10:20:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54433319", "2024-10-16T10:21:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54375877", "2024-10-16T10:22:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54372891", "2024-10-16T10:23:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54372715", "2024-10-16T10:24:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54370413", "2024-10-16T10:25:25+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:50383659", "2024-10-16T10:26:23+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54589289", "2024-10-16T10:27:21+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54623335", "2024-10-16T10:28:19+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54604117", "2024-10-16T10:29:19+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54600229", "2024-10-16T10:30:18+00:00"));
        result.getFixtureChange().add(fixtureChange("sr:match:54590319", "2024-10-16T10:31:15+00:00"));
        return result;
    }

    private static SapiFixtureChange fixtureChange(String eventId, String updateTime) {
        SapiFixtureChange result = new SapiFixtureChange();
        result.setSportEventId(eventId);
        result.setUpdateTime(XmlGregorianCalendars.forInstant(ZonedDateTime.parse(updateTime).toInstant()));
        return result;
    }
}
