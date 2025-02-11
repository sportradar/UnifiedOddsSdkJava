/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.uf.sportsapi.datamodel.DescSpecifiers;
import com.sportradar.unifiedodds.sdk.internal.impl.UnifiedFeedConstants;

public class SpecifierDescriptions {

    public static DescSpecifiers variant() {
        DescSpecifiers setSpecifiers = new DescSpecifiers();
        setSpecifiers
            .getSpecifier()
            .add(specifier(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME, "variable_text"));
        return setSpecifiers;
    }

    public static DescSpecifiers variantAndVersion() {
        DescSpecifiers setSpecifiers = new DescSpecifiers();
        setSpecifiers
            .getSpecifier()
            .add(specifier(UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME, "variable_text"));
        setSpecifiers.getSpecifier().add(specifier("version", "string"));
        return setSpecifiers;
    }

    private static DescSpecifiers.Specifier specifier(String name, String type) {
        DescSpecifiers.Specifier specifier = new DescSpecifiers.Specifier();
        specifier.setName(name);
        specifier.setType(type);
        return specifier;
    }
}
