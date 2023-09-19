/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import com.google.common.collect.ImmutableSet;
import com.sportradar.unifiedodds.sdk.ProducerScope;
import java.util.Set;

public class ProducerStubs {

    private ProducerStubs() {}

    public static Producer stubLiveProducer() {
        return new LiveProducerStub();
    }

    public static Producer stubPrematchProducer() {
        return new PrematchProducerStub();
    }

    private static class LiveProducerStub implements Producer {

        private static final int ID = 1;

        @Override
        public int getId() {
            return ID;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public long getLastMessageTimestamp() {
            return 0;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isFlaggedDown() {
            return false;
        }

        @Override
        public String getApiUrl() {
            return null;
        }

        @Override
        public Set<ProducerScope> getProducerScopes() {
            return ImmutableSet.of(ProducerScope.Live);
        }

        @Override
        public long getLastProcessedMessageGenTimestamp() {
            return 0;
        }

        @Override
        public long getProcessingQueDelay() {
            return 0;
        }

        @Override
        public long getTimestampForRecovery() {
            return 0;
        }

        @Override
        public int getStatefulRecoveryWindowInMinutes() {
            return 0;
        }
    }

    private static class PrematchProducerStub implements Producer {

        private static final int ID = 3;

        @Override
        public int getId() {
            return ID;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getDescription() {
            return null;
        }

        @Override
        public long getLastMessageTimestamp() {
            return 0;
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean isFlaggedDown() {
            return false;
        }

        @Override
        public String getApiUrl() {
            return null;
        }

        @Override
        public Set<ProducerScope> getProducerScopes() {
            return ImmutableSet.of(ProducerScope.Live);
        }

        @Override
        public long getLastProcessedMessageGenTimestamp() {
            return 0;
        }

        @Override
        public long getProcessingQueDelay() {
            return 0;
        }

        @Override
        public long getTimestampForRecovery() {
            return 0;
        }

        @Override
        public int getStatefulRecoveryWindowInMinutes() {
            return 0;
        }
    }
}
