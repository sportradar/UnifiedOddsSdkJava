/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.producers;

import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;

import com.sportradar.utils.generic.testing.RandomInteger;
import com.sportradar.utils.generic.testing.RandomObjectPicker;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.val;

public final class ProducerIds {

    public static final Integer PREMIUM_CRICKET_PRODUCER_ID = 5;
    public static final Integer LIVE_PRODUCER_ID = 1;

    @SuppressWarnings("MagicNumber")
    private static final Integer[] PRODUCER_IDS = {
        LIVE_PRODUCER_ID,
        3,
        4,
        PREMIUM_CRICKET_PRODUCER_ID,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
    };

    private static Random random = new Random();

    private ProducerIds() {}

    public static Integer[] producerIds() {
        return PRODUCER_IDS;
    }

    public static Integer[] nonPremiumCricketProducerIds() {
        return Arrays
            .stream(PRODUCER_IDS)
            .filter(id -> id != PREMIUM_CRICKET_PRODUCER_ID)
            .toArray(Integer[]::new);
    }

    public static int anyProducerId() {
        return pickOneRandomlyFrom(PRODUCER_IDS);
    }
}
