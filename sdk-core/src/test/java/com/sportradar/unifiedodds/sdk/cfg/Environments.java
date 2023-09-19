/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Environments {

    private static Random random = new Random();

    private Environments() {}

    public static Environment any() {
        List<Environment> environments = Arrays.asList(Environment.values());
        return environments.get(random.nextInt(environments.size()));
    }

    public static Set<Environment> getReplayEnvironments() {
        Set<Environment> replayEnvironments = new HashSet<>();
        replayEnvironments.add(Environment.Replay);
        replayEnvironments.add(Environment.GlobalReplay);
        return replayEnvironments;
    }

    public static Set<Environment> getNonReplayEnvironments() {
        Set<Environment> environments = newHashSet(Environment.values());
        environments.removeAll(getReplayEnvironments());
        return environments;
    }
}
