/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.junit.Assert.assertNull;

import com.sportradar.unifiedodds.sdk.entities.Manager;
import java.util.Locale;
import org.assertj.core.api.Assertions;

public class ManagerAssertions {

    private Manager manager;

    public ManagerAssertions(Manager manager) {
        this.manager = manager;
    }

    public static ManagerAssertions assertThat(Manager manager) {
        return new ManagerAssertions(manager);
    }

    public ManagerAssertions hasNameTranslated(Locale language, String translation) {
        Assertions.assertThat(manager.getNames().get(language)).isEqualTo(translation);
        Assertions.assertThat(manager.getName(language)).isEqualTo(translation);
        return this;
    }

    public void hasNameNotTranslatedTo(Locale language) {
        Assertions.assertThat(manager.getNames().containsKey(language)).isFalse();
        assertNull(manager.getName(language));
    }
}
