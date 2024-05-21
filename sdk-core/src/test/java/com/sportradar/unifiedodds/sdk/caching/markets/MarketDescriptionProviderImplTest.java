/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviders.subbingOutCaches;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.FreeTextMarketDescription.freeTextMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.nascarOutrightsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.EXACT_GOALS_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.FREE_TEXT_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.nascarOutrightsVariant;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providingList;
import static com.sportradar.unifiedodds.sdk.impl.VariantDescriptionDataProviders.providingList;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.of;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.utils.domain.specifiers.MarketSpecifiers;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MarketDescriptionProviderImplTest {

    @Nested
    class GetMarketDescription {

        @Nested
        class InvariantMarket {

            @Test
            void translatesInvariantMarketName() throws Exception {
                Locale aLanguage = ENGLISH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(in(aLanguage), oddEvenMarketDescription(aLanguage))
                    )
                    .build();

                val actual = provider.getMarketDescription(
                    OddEvenMarketIds.ODD_EVEN_MARKET_ID,
                    emptyMap(),
                    singletonList(aLanguage),
                    false
                );

                assertThat(actual.getName(aLanguage))
                    .isEqualTo(oddEvenMarketDescription(aLanguage).getName());
            }
        }

        @Nested
        class VariantMarket {

            @Test
            void translatesStructuredVariantMarketName() throws Exception {
                Locale aLanguage = FRENCH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(in(aLanguage), exactGoalsMarketDescription(aLanguage))
                    )
                    .withVariantCache(providingList(in(aLanguage), fivePlusVariantDescription()))
                    .build();

                val actual = provider.getMarketDescription(
                    EXACT_GOALS_MARKET_ID,
                    MarketSpecifiers.variant(fivePlusVariant()),
                    singletonList(aLanguage),
                    true
                );

                assertThat(actual.getName(aLanguage))
                    .isEqualTo(exactGoalsMarketDescription(aLanguage).getName());
            }

            @Test
            void translatesStructuredVariantMarketNameEvenIfVariantIsMissing() throws Exception {
                Locale aLanguage = FRENCH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(in(aLanguage), exactGoalsMarketDescription(aLanguage))
                    )
                    .build();

                val actual = provider.getMarketDescription(
                    EXACT_GOALS_MARKET_ID,
                    MarketSpecifiers.variant(fivePlusVariant()),
                    singletonList(aLanguage),
                    true
                );

                assertThat(actual.getName(aLanguage))
                    .isEqualTo(exactGoalsMarketDescription(aLanguage).getName());
            }

            @Test
            void translatesUnstructuredVariantMarketNameForFreeTextMarket() throws Exception {
                Locale aLanguage = ENGLISH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(providingList(in(aLanguage), freeTextMarketDescription()))
                    .withVariantMarketCache(
                        providing(
                            in(aLanguage),
                            nascarOutrightsVariant(),
                            of(nascarOutrightsMarketDescription())
                        )
                    )
                    .build();

                val actual = provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    singletonList(aLanguage),
                    true
                );

                assertThat(actual.getName(aLanguage)).isEqualTo(nascarOutrightsMarketDescription().getName());
            }

            @Test
            void translatesUnstructuredVariantMarketNameProvidingBestEffortResponseWhenOneLocaleMissingInBatch()
                throws Exception {
                Locale existingVariantLanguage = ENGLISH;
                Locale missingVariantLanguage = FRENCH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(
                            in(existingVariantLanguage),
                            freeTextMarketDescription(),
                            in(missingVariantLanguage),
                            freeTextMarketDescription()
                        )
                    )
                    .withVariantMarketCache(
                        providing(
                            in(existingVariantLanguage),
                            nascarOutrightsVariant(),
                            of(nascarOutrightsMarketDescription())
                        )
                    )
                    .build();

                provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    singletonList(existingVariantLanguage),
                    true
                );

                val description = provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    asList(missingVariantLanguage, existingVariantLanguage),
                    true
                );

                assertThat(description.getName(existingVariantLanguage))
                    .isEqualTo(nascarOutrightsMarketDescription().getName());
                assertThat(description.getName(missingVariantLanguage)).isNull();
            }

            @Test
            @SuppressWarnings("LineLength")
            void translateUnstructuredVariantMarketNameWithInvariantNameOnFreshCacheAnyLanguageInBatchIsMissingVariant()
                throws Exception {
                Locale existingVariantLanguage = ENGLISH;
                Locale missingVariantLanguage = FRENCH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(
                            in(existingVariantLanguage),
                            freeTextMarketDescription(),
                            in(missingVariantLanguage),
                            freeTextMarketDescription()
                        )
                    )
                    .withVariantMarketCache(
                        providing(
                            in(existingVariantLanguage),
                            nascarOutrightsVariant(),
                            of(nascarOutrightsMarketDescription())
                        )
                    )
                    .build();

                val description = provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    asList(missingVariantLanguage, existingVariantLanguage),
                    true
                );

                assertThat(description.getName(existingVariantLanguage))
                    .isEqualTo(freeTextMarketDescription().getName());
                assertThat(description.getName(missingVariantLanguage))
                    .isEqualTo(freeTextMarketDescription().getName());
            }

            @Test
            void providesOutcomesOfPreviouslyCachedUnstructuredVariantMarketEvenIfLocaleDoesNotMatch()
                throws Exception {
                Locale existingVariantLanguage = ENGLISH;
                Locale missingVariantLanguage = FRENCH;

                val provider = subbingOutCaches()
                    .withInvariantMarketCache(
                        providingList(
                            in(existingVariantLanguage),
                            freeTextMarketDescription(),
                            in(missingVariantLanguage),
                            freeTextMarketDescription()
                        )
                    )
                    .withVariantMarketCache(
                        providing(
                            in(existingVariantLanguage),
                            nascarOutrightsVariant(),
                            of(nascarOutrightsMarketDescription())
                        )
                    )
                    .build();

                provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    singletonList(existingVariantLanguage),
                    true
                );

                val description = provider.getMarketDescription(
                    FREE_TEXT_MARKET_ID,
                    ImmutableMap.of(
                        UnifiedFeedConstants.VARIANT_DESCRIPTION_NAME,
                        nascarOutrightsMarketDescription().getVariant()
                    ),
                    singletonList(missingVariantLanguage),
                    true
                );

                assertThat(setOfIds(description.getOutcomes()))
                    .isEqualTo(setOfIds(nascarOutrightsMarketDescription().getOutcomes()));
            }

            private Set<String> setOfIds(List<OutcomeDescription> outcomes) {
                return outcomes.stream().map(OutcomeDescription::getId).collect(toSet());
            }

            private Set<String> setOfIds(DescOutcomes outcomes) {
                return outcomes.getOutcome().stream().map(DescOutcomes.Outcome::getId).collect(toSet());
            }
        }
    }
}
