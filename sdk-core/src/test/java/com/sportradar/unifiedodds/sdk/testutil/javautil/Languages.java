/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.javautil;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class Languages {

    private static Random random = new Random();

    private Languages() {}

    public static Locale any() {
        List<Locale> languagePool = Arrays.asList(
            Locale.ENGLISH,
            Locale.GERMANY,
            Locale.CANADA,
            Locale.FRENCH,
            Locale.CHINESE,
            Locale.ITALY,
            Locale.JAPAN
        );
        return languagePool.get(random.nextInt(languagePool.size()));
    }
}
