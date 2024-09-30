/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategies.anyErrorHandlingStrategy;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.CacheBuilder;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorReferenceIds;
import com.sportradar.uf.sportsapi.datamodel.SapiPlayerCompetitor;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamExtended;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCompetitorCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableJerseyCi;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.testutil.serialization.JavaSerializer;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.Languages;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.val;

public class ProfileCaches {

    @SneakyThrows
    public static void exportAndImportTheOnlyItemIn(ProfileCacheImpl cache) {
        val exported = cache.exportItems();
        assertThat(exported).hasSize(1);
        val serialized = JavaSerializer.serialize(exported.get(0));

        purge(cache, exported.get(0));

        val deserialized = JavaSerializer.deserialize(serialized);
        cache.importItems(asList((ExportableCompetitorCi) deserialized));
    }

    @SneakyThrows
    public static void exportAndImportItemsIn(ProfileCacheImpl cache) {
        val exported = cache.exportItems();
        List<byte[]> serialised = serialize(exported);

        purge(exported, cache);

        cache.importItems(deserialize(serialised));
    }

    public static void exportAndImportItemsUsingLegacyPreV3_3_3ImportIntegration(ProfileCacheImpl cache) {
        val exported = cache.exportItems();
        List<byte[]> serialised = serialize(exported);

        purge(exported, cache);

        List<ExportableCi> legacyExportable = convertToLegavyPreV3_3_3Exportable(deserialize(serialised));
        cache.importItems(legacyExportable);
    }

    private static List<ExportableCi> convertToLegavyPreV3_3_3Exportable(List<ExportableCi> exported) {
        return exported
            .stream()
            .map(e -> convertToExportableCreatedViaLegacyPreV3_3_3ImportIntegration(e))
            .collect(toList());
    }

    private static ExportableCi convertToExportableCreatedViaLegacyPreV3_3_3ImportIntegration(
        ExportableCi exportedCi
    ) {
        if (exportedCi instanceof ExportableCompetitorCi) {
            return convertToExportableCreatedViaLegacyPreV3_3_3ImportIntegration(
                (ExportableCompetitorCi) exportedCi
            );
        } else {
            return exportedCi;
        }
    }

    private static ExportableCompetitorCi convertToExportableCreatedViaLegacyPreV3_3_3ImportIntegration(
        ExportableCompetitorCi exported
    ) {
        return new ExportableCompetitorCi(
            exported.getId(),
            exported.getNames(),
            exported.getDefaultLocale(),
            exported.getCountryNames(),
            exported.getAbbreviations(),
            exported.isVirtual(),
            exported.getCountryCode(),
            exported.getReferenceId(),
            exported.getAssociatedPlayerIds(),
            exported.getAssociatedPlayerJerseyNumbers(),
            createExportableJerseyWithoutPropertiesIntroducedInV3_3_3(exported.getJerseys()),
            exported.getManager(),
            exported.getVenue(),
            exported.getGender(),
            exported.getAgeGroup(),
            exported.getRaceDriverProfile(),
            exported.getCachedLocales(),
            exported.getState(),
            exported.getSportId(),
            exported.getCategoryId(),
            exported.getShortName(),
            exported.getDivision()
        );
    }

    private List<String> extractPlayerIds(SapiTeamExtended competitor) {
        List<String> playerIds = competitor.getPlayers() != null
            ? competitor.getPlayers().getPlayer().stream().map(SapiPlayerCompetitor::getId).collect(toList())
            : null;
        return playerIds;
    }

    private Map<String, String> extractReferenceIds(SapiTeamExtended competitor) {
        val referenceIdMap = competitor.getReferenceIds() != null
            ? competitor
                .getReferenceIds()
                .getReferenceId()
                .stream()
                .collect(
                    Collectors.toMap(
                        SapiCompetitorReferenceIds.SapiReferenceId::getName,
                        SapiCompetitorReferenceIds.SapiReferenceId::getValue
                    )
                )
            : null;
        return referenceIdMap;
    }

    private static List<ExportableJerseyCi> createExportableJerseyWithoutPropertiesIntroducedInV3_3_3(
        List<ExportableJerseyCi> exportedJerseyCis
    ) {
        return exportedJerseyCis
            .stream()
            .map(jersey ->
                new ExportableJerseyCi(
                    jersey.getBase(),
                    jersey.getNumber(),
                    jersey.getSleeve(),
                    jersey.getType(),
                    jersey.getStripesColor(),
                    jersey.getSplitColor(),
                    jersey.getShirtType(),
                    jersey.getSleeveDetail()
                )
            )
            .collect(toList());
    }

    private static List<byte[]> serialize(List<ExportableCi> exported) {
        return exported.stream().map(JavaSerializer::serialize).collect(toList());
    }

    private static List<ExportableCi> deserialize(List<byte[]> serialised) {
        return serialised
            .stream()
            .map(JavaSerializer::deserialize)
            .map(e -> (ExportableCi) e)
            .collect(toList());
    }

    private static void purge(List<ExportableCi> exported, ProfileCacheImpl cache) {
        exported.forEach(e -> purge(cache, e));
    }

    private static void purge(ProfileCacheImpl cache, ExportableCi exported) {
        Urn id = Urn.parse(exported.getId());
        if ("competitor".equals(id.getType())) {
            cache.purgeCompetitorProfileCacheItem(id);
        } else if ("player".equals(id.getType())) {
            cache.purgePlayerProfileCacheItem(id);
        } else {
            throw new RuntimeException("Expected competitor or player, but was " + id.getType());
        }
    }

    public static class BuilderStubbingOutDataRouterManager {

        private Optional<DataRouterManager> dataRouterManager = Optional.empty();

        private Optional<Locale> language = Optional.empty();

        public static BuilderStubbingOutDataRouterManager stubbingOutDataRouterManager() {
            return new BuilderStubbingOutDataRouterManager();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager with(DataRouterManager dataRouterManager) {
            this.dataRouterManager = Optional.of(dataRouterManager);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutDataRouterManager withDefaultLanguage(Locale language) {
            this.language = Optional.of(language);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public ProfileCacheImpl build() {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getDefaultLocale()).thenReturn(language.orElse(Languages.any()));
            when(config.getExceptionHandlingStrategy()).thenReturn(anyErrorHandlingStrategy());
            return new ProfileCacheImpl(
                new CacheItemFactoryImpl(
                    dataRouterManager.orElse(mock(DataRouterManager.class)),
                    config,
                    CacheBuilder.newBuilder().build()
                ),
                dataRouterManager.orElse(mock(DataRouterManager.class)),
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().build(),
                CacheBuilder.newBuilder().build()
            );
        }
    }
}
