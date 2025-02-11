/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import static com.sportradar.utils.stream.optional.NonNullMapper.ifNotNull;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static java.util.Optional.ofNullable;

import com.sportradar.utils.Urn;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExportableCourseCi implements Serializable {

    private final String id;
    private final List<ExportableHoleCi> holes;
    private final Map<Locale, String> name;

    public ExportableCourseCi(Urn id, Map<Locale, String> name, List<ExportableHoleCi> holes) {
        this.name = ofNullable(name).orElse(EMPTY_MAP);
        this.id = ifNotNull(id).map(Urn::toString);
        this.holes = ofNullable(holes).orElse(EMPTY_LIST);
    }

    public List<ExportableHoleCi> getHoles() {
        return holes;
    }

    public Urn getId() {
        return ifNotNull(id).map(Urn::parse);
    }

    public Map<Locale, String> getName() {
        return name;
    }
}
