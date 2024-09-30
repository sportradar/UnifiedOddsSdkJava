/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.sportsapi.datamodel.*;
import java.util.function.Function;
import lombok.val;

public class DescMarketFixture {

    public static DescMarketExtended market(DescMarket market) {
        return new DescMarketExtended(market);
    }

    public static final class DescMarketExtended extends DescMarket {

        private final DescMarket market;

        public DescMarketExtended(DescMarket market) {
            this.market = market;
        }

        public DescMarketExtended firstOutcome(
            Function<DescOutcomes.Outcome, DescOutcomes.Outcome> modifier
        ) {
            val outcome = this.market.getOutcomes().getOutcome().get(0);
            modifier.apply(outcome);
            return this;
        }

        public DescOutcomes.Outcome getFirstOutcome() {
            return market.getOutcomes().getOutcome().get(0);
        }

        @Override
        public DescOutcomes getOutcomes() {
            return market.getOutcomes();
        }

        @Override
        public void setOutcomes(DescOutcomes value) {
            market.setOutcomes(value);
        }

        @Override
        public DescSpecifiers getSpecifiers() {
            return market.getSpecifiers();
        }

        @Override
        public void setSpecifiers(DescSpecifiers value) {
            market.setSpecifiers(value);
        }

        @Override
        public Mappings getMappings() {
            return market.getMappings();
        }

        @Override
        public void setMappings(Mappings value) {
            market.setMappings(value);
        }

        @Override
        public Attributes getAttributes() {
            return market.getAttributes();
        }

        @Override
        public void setAttributes(Attributes value) {
            market.setAttributes(value);
        }

        @Override
        public int getId() {
            return market.getId();
        }

        @Override
        public void setId(int value) {
            market.setId(value);
        }

        @Override
        public String getName() {
            return market.getName();
        }

        @Override
        public void setName(String value) {
            market.setName(value);
        }

        @Override
        public String getGroups() {
            return market.getGroups();
        }

        @Override
        public void setGroups(String value) {
            market.setGroups(value);
        }

        @Override
        public String getDescription() {
            return market.getDescription();
        }

        @Override
        public void setDescription(String value) {
            market.setDescription(value);
        }

        @Override
        public String getIncludesOutcomesOfType() {
            return market.getIncludesOutcomesOfType();
        }

        @Override
        public void setIncludesOutcomesOfType(String value) {
            market.setIncludesOutcomesOfType(value);
        }

        @Override
        public String getVariant() {
            return market.getVariant();
        }

        @Override
        public void setVariant(String value) {
            market.setVariant(value);
        }

        @Override
        public String getOutcomeType() {
            return market.getOutcomeType();
        }

        @Override
        public void setOutcomeType(String value) {
            market.setOutcomeType(value);
        }
    }
}
