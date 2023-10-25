/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface Course {
    List<Hole> getHoles();

    Urn getId();

    String getName(Locale language);

    Map<Locale, String> getNames();
}
