/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import static com.sportradar.utils.generic.testing.Cardinality.anyFromZeroToTwo;

import com.sportradar.utils.domain.UniqueObjects;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;

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

    public static List<Locale> anyLanguages() {
        val numberOfElements = anyFromZeroToTwo();
        final val uniqueLanguages = new UniqueObjects<>(() -> any());
        return IntStream
            .range(0, numberOfElements)
            .boxed()
            .map(i -> uniqueLanguages.getOne())
            .collect(Collectors.toList());
    }
}
