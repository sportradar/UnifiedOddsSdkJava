/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.impl.ProducerDataProviderStubs.providerOfSingleEmptyProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReaderStubs.emptyBookmakerDetailsReader;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.*;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.*;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ConfigureDesiredLanguagesTest {

    private final boolean replayMode = true;
    private final List<Locale> desiredLanguages = asList(Locale.ENGLISH, Locale.FRENCH);
    private final Locale anyLanguage = Locale.FRENCH;
    private final Map<String, String> yamlFileContent = new HashMap<>();
    private final Map<String, String> propsFileContent = new HashMap<>();
    private final WhoAmIReader whoAmIReader = emptyBookmakerDetailsReader();
    private final ProducerDataProvider producerDataProvider = providerOfSingleEmptyProducer();
    private final TokenSetter buildFromPropsFile = new TokenSetterImpl(
        new StubSdkConfigurationPropertiesReader(propsFileContent),
        new StubSdkConfigurationYamlReader(yamlFileContent),
        anyConfig -> whoAmIReader,
        anyConfig -> producerDataProvider
    );

    @Test
    public void configureViaJavaApi() {
        UofConfiguration config = buildFromPropsFile
            .setAccessToken("any")
            .selectCustom()
            .setDefaultLanguage(anyLanguage)
            .setDesiredLanguages(desiredLanguages)
            .build();
        val internalConfig = new SdkInternalConfiguration(config, anyProps(), anyYaml());
        val internalConfigForReplay = new SdkInternalConfiguration(config, replayMode, anyProps(), anyYaml());

        assertEquals(desiredLanguages, config.getLanguages());
        assertEquals(desiredLanguages, internalConfig.getDesiredLocales());
        assertEquals(desiredLanguages, internalConfigForReplay.getDesiredLocales());
    }

    private static SdkConfigurationYamlReader anyYaml() {
        return mock(SdkConfigurationYamlReader.class);
    }

    private static SdkConfigurationPropertiesReader anyProps() {
        return mock(SdkConfigurationPropertiesReader.class);
    }
}
