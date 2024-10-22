/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.SapiResultChange;
import com.sportradar.uf.sportsapi.datamodel.SapiResultChangesEndpoint;
import com.sportradar.unifiedodds.sdk.testutil.jaxb.XmlGregorianCalendars;
import java.time.Instant;
import lombok.val;

public class SapiResultChanges {

    public static SapiResultChangesEndpoint resultChanges() {
        val result = new SapiResultChangesEndpoint();
        result.getResultChange().add(resultChange("sr:match:54432755", "2024-10-16T12:58:47+00:00"));
        result.getResultChange().add(resultChange("sr:match:54482573", "2024-10-16T12:58:46+00:00"));
        result.getResultChange().add(resultChange("sr:match:54571433", "2024-10-16T12:58:07+00:00"));
        result.getResultChange().add(resultChange("sr:match:54603131", "2024-10-16T12:58:02+00:00"));
        result.getResultChange().add(resultChange("sr:match:54567929", "2024-10-16T12:57:40+00:00"));
        result.getResultChange().add(resultChange("sr:match:54565411", "2024-10-16T12:57:40+00:00"));
        result.getResultChange().add(resultChange("sr:match:54590131", "2024-10-16T12:57:00+00:00"));
        result.getResultChange().add(resultChange("sr:match:54090703", "2024-10-16T12:56:22+00:00"));
        result.getResultChange().add(resultChange("sr:match:54601741", "2024-10-16T12:56:18+00:00"));
        result.getResultChange().add(resultChange("sr:match:51674931", "2024-10-16T12:56:18+00:00"));
        return result;
    }

    private static SapiResultChange resultChange(String match, String updateTime) {
        val result = new SapiResultChange();
        result.setSportEventId(match);
        result.setUpdateTime(XmlGregorianCalendars.forInstant(Instant.parse(updateTime)));
        return result;
    }
}
