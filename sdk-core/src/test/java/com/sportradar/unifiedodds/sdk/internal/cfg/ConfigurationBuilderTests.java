/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
@Category(ConfigurationBuilderSetup.class)
@SuppressWarnings({ "MagicNumber", "MultipleStringLiterals" })
public class ConfigurationBuilderTests extends ConfigurationBuilderSetup {

    @Test
    public void missingLanguageThrows() {
        assertThatThrownBy(() -> integrationBuilder(defaultAccessToken).build())
            .isInstanceOf(InvalidParameterException.class);
        assertThatThrownBy(() -> productionBuilder(defaultAccessToken).build())
            .isInstanceOf(InvalidParameterException.class);
        assertThatThrownBy(() -> replayBuilder(defaultAccessToken).build())
            .isInstanceOf(InvalidParameterException.class);
        assertThatThrownBy(() -> customBuilder(defaultAccessToken).build())
            .isInstanceOf(InvalidParameterException.class);
    }

    @Test
    public void accessTokenHasCorrectValue() {
        Assert.assertEquals(
            defaultAccessToken,
            integrationBuilder(defaultAccessToken).setDefaultLanguage(languageHu).build().getAccessToken()
        );
        Assert.assertEquals(
            defaultAccessToken,
            productionBuilder(defaultAccessToken).setDefaultLanguage(languageHu).build().getAccessToken()
        );
        Assert.assertEquals(
            defaultAccessToken,
            replayBuilder(defaultAccessToken).setDefaultLanguage(languageHu).build().getAccessToken()
        );
        Assert.assertEquals(
            defaultAccessToken,
            customBuilder(defaultAccessToken).setDefaultLanguage(languageHu).build().getAccessToken()
        );
    }

    @Test
    public void defaultLanguageSetHasCorrectValue() {
        Locale lang1 = languageDe;
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDefaultLanguage(lang1)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDefaultLanguage(lang1)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken).setDefaultLanguage(lang1).build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken).setDefaultLanguage(lang1).build();

        Assert.assertEquals(lang1, integrationConfig.getDefaultLanguage());
        Assert.assertEquals(lang1, productionConfig.getDefaultLanguage());
        Assert.assertEquals(lang1, replayConfig.getDefaultLanguage());
        Assert.assertEquals(lang1, customConfig.getDefaultLanguage());

        Assert.assertEquals(1, integrationConfig.getLanguages().size());
        Assert.assertEquals(1, productionConfig.getLanguages().size());
        Assert.assertEquals(1, replayConfig.getLanguages().size());
        Assert.assertEquals(1, customConfig.getLanguages().size());

        Assert.assertEquals(lang1, integrationConfig.getLanguages().get(0));
        Assert.assertEquals(lang1, productionConfig.getLanguages().get(0));
        Assert.assertEquals(lang1, replayConfig.getLanguages().get(0));
        Assert.assertEquals(lang1, customConfig.getLanguages().get(0));

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void languagesSetHasCorrectValue() {
        List<Locale> langs3 = Arrays.asList(languageHu, languageNl, languageDe);
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDesiredLanguages(langs3)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDesiredLanguages(langs3)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken).setDesiredLanguages(langs3).build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken).setDesiredLanguages(langs3).build();

        Assert.assertEquals(langs3.get(0), integrationConfig.getDefaultLanguage());
        Assert.assertEquals(langs3.get(0), productionConfig.getDefaultLanguage());
        Assert.assertEquals(langs3.get(0), replayConfig.getDefaultLanguage());
        Assert.assertEquals(langs3.get(0), customConfig.getDefaultLanguage());

        Assert.assertEquals(3, integrationConfig.getLanguages().size());
        Assert.assertEquals(3, productionConfig.getLanguages().size());
        Assert.assertEquals(3, replayConfig.getLanguages().size());
        Assert.assertEquals(3, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void languagesSetWithNullListDoesNotThrow() {
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDefaultLanguage(defaultLanguage)
            .setDesiredLanguages(null)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDefaultLanguage(defaultLanguage)
            .setDesiredLanguages(null)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken)
            .setDefaultLanguage(defaultLanguage)
            .setDesiredLanguages(null)
            .build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken)
            .setDefaultLanguage(defaultLanguage)
            .setDesiredLanguages(null)
            .build();

        Assert.assertEquals(defaultLanguage, integrationConfig.getDefaultLanguage());
        Assert.assertEquals(defaultLanguage, productionConfig.getDefaultLanguage());
        Assert.assertEquals(defaultLanguage, replayConfig.getDefaultLanguage());
        Assert.assertEquals(defaultLanguage, customConfig.getDefaultLanguage());

        Assert.assertEquals(1, integrationConfig.getLanguages().size());
        Assert.assertEquals(1, productionConfig.getLanguages().size());
        Assert.assertEquals(1, replayConfig.getLanguages().size());
        Assert.assertEquals(1, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void settingLanguageMultipleTimesConfigsOnlyUniqueOnes() {
        List<Locale> lang1 = Arrays.asList(defaultLanguage, defaultLanguage, defaultLanguage);
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken).setDesiredLanguages(lang1).build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken).setDesiredLanguages(lang1).build();

        Assert.assertEquals(lang1.get(0), integrationConfig.getDefaultLanguage());
        Assert.assertEquals(lang1.get(0), productionConfig.getDefaultLanguage());
        Assert.assertEquals(lang1.get(0), replayConfig.getDefaultLanguage());
        Assert.assertEquals(lang1.get(0), customConfig.getDefaultLanguage());

        Assert.assertEquals(1, integrationConfig.getLanguages().size());
        Assert.assertEquals(1, productionConfig.getLanguages().size());
        Assert.assertEquals(1, replayConfig.getLanguages().size());
        Assert.assertEquals(1, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void combinationOfDefaultLanguageWithLanguages() {
        List<Locale> lang1 = Arrays.asList(defaultLanguage, languageDe);
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();

        Assert.assertEquals(languageNl, integrationConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, productionConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, replayConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, customConfig.getDefaultLanguage());

        Assert.assertEquals(3, integrationConfig.getLanguages().size());
        Assert.assertEquals(3, productionConfig.getLanguages().size());
        Assert.assertEquals(3, replayConfig.getLanguages().size());
        Assert.assertEquals(3, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void combinationOfDefaultLanguageWithLanguagesIncludingDefaultLanguage() {
        List<Locale> lang1 = Arrays.asList(defaultLanguage, languageDe, languageNl);
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken)
            .setDefaultLanguage(languageNl)
            .setDesiredLanguages(lang1)
            .build();

        Assert.assertEquals(languageNl, integrationConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, productionConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, replayConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, customConfig.getDefaultLanguage());

        Assert.assertEquals(3, integrationConfig.getLanguages().size());
        Assert.assertEquals(3, productionConfig.getLanguages().size());
        Assert.assertEquals(3, replayConfig.getLanguages().size());
        Assert.assertEquals(3, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void settingCombinationOfLanguagesWithDefaultLanguage() {
        List<Locale> lang1 = Arrays.asList(defaultLanguage, languageDe);
        UofConfiguration integrationConfig = integrationBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .setDefaultLanguage(languageNl)
            .build();
        UofConfiguration productionConfig = productionBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .setDefaultLanguage(languageNl)
            .build();
        UofConfiguration replayConfig = replayBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .setDefaultLanguage(languageNl)
            .build();
        UofConfiguration customConfig = customBuilder(defaultAccessToken)
            .setDesiredLanguages(lang1)
            .setDefaultLanguage(languageNl)
            .build();

        Assert.assertEquals(languageNl, integrationConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, productionConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, replayConfig.getDefaultLanguage());
        Assert.assertEquals(languageNl, customConfig.getDefaultLanguage());

        Assert.assertEquals(3, integrationConfig.getLanguages().size());
        Assert.assertEquals(3, productionConfig.getLanguages().size());
        Assert.assertEquals(3, replayConfig.getLanguages().size());
        Assert.assertEquals(3, customConfig.getLanguages().size());

        configHasDefaultValuesSet(integrationConfig);
        configHasDefaultValuesSet(productionConfig);
        configHasDefaultValuesSet(replayConfig);
        configHasDefaultValuesSet(customConfig);
    }

    @Test
    public void disabledProducersSetSingleHasCorrectValue() {
        List<Integer> disableProducers = Collections.singletonList(1);
        UofConfiguration integrationConfig = buildConfig("i").setDisabledProducers(disableProducers).build();
        UofConfiguration productionConfig = buildConfig("p").setDisabledProducers(disableProducers).build();
        UofConfiguration replayConfig = buildConfig("r").setDisabledProducers(disableProducers).build();
        UofConfiguration customConfig = buildCustomConfig().setDisabledProducers(disableProducers).build();

        Assert.assertEquals(
            disableProducers.get(0),
            integrationConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            productionConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            replayConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            customConfig.getProducer().getDisabledProducers().get(0)
        );

        Assert.assertEquals(1, integrationConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(1, productionConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(1, replayConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(1, customConfig.getProducer().getDisabledProducers().size());
    }

    @Test
    public void disabledProducersSetMultipleHasCorrectValue() {
        List<Integer> disableProducers = Arrays.asList(1, 3, 5);
        UofConfiguration integrationConfig = buildConfig("i").setDisabledProducers(disableProducers).build();
        UofConfiguration productionConfig = buildConfig("p").setDisabledProducers(disableProducers).build();
        UofConfiguration replayConfig = buildConfig("r").setDisabledProducers(disableProducers).build();
        UofConfiguration customConfig = buildCustomConfig().setDisabledProducers(disableProducers).build();

        Assert.assertEquals(
            disableProducers.get(0),
            integrationConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            productionConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            replayConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            customConfig.getProducer().getDisabledProducers().get(0)
        );

        Assert.assertEquals(3, integrationConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, productionConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, replayConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, customConfig.getProducer().getDisabledProducers().size());
    }

    @Test
    public void disabledProducersSetMultipleOnlyUniqueAreSaved() {
        List<Integer> disableProducers = Arrays.asList(1, 3, 5, 5, 3, 1, 1, 1, 3);
        UofConfiguration integrationConfig = buildConfig("i").setDisabledProducers(disableProducers).build();
        UofConfiguration productionConfig = buildConfig("p").setDisabledProducers(disableProducers).build();
        UofConfiguration replayConfig = buildConfig("r").setDisabledProducers(disableProducers).build();
        UofConfiguration customConfig = buildCustomConfig().setDisabledProducers(disableProducers).build();

        Assert.assertEquals(
            disableProducers.get(0),
            integrationConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            productionConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            replayConfig.getProducer().getDisabledProducers().get(0)
        );
        Assert.assertEquals(
            disableProducers.get(0),
            customConfig.getProducer().getDisabledProducers().get(0)
        );

        Assert.assertEquals(3, integrationConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, productionConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, replayConfig.getProducer().getDisabledProducers().size());
        Assert.assertEquals(3, customConfig.getProducer().getDisabledProducers().size());
    }

    @Test
    @Parameters({ "-111", "0", "111" })
    public void nodeIdIsSet(Integer nodeId) {
        UofConfiguration integrationConfig = buildConfig("i").setNodeId(nodeId).build();
        UofConfiguration productionConfig = buildConfig("p").setNodeId(nodeId).build();
        UofConfiguration replayConfig = buildConfig("r").setNodeId(nodeId).build();
        UofConfiguration customConfig = buildCustomConfig().setNodeId(nodeId).build();

        Assert.assertEquals(nodeId, integrationConfig.getNodeId());
        Assert.assertEquals(nodeId, productionConfig.getNodeId());
        Assert.assertEquals(nodeId, replayConfig.getNodeId());
        Assert.assertEquals(nodeId, customConfig.getNodeId());
    }

    @Test
    public void exceptionHandlingStrategyThrowIsSet() {
        exceptionHandlingStrategyIsSet(ExceptionHandlingStrategy.Throw);
    }

    @Test
    public void exceptionHandlingStrategyCatchIsSet() {
        exceptionHandlingStrategyIsSet(ExceptionHandlingStrategy.Catch);
    }

    @Test
    public void exceptionHandlingStrategyWhenNullThrow() {
        assertThatThrownBy(() -> buildConfig("i").setExceptionHandlingStrategy(null))
            .isInstanceOf(NullPointerException.class);
    }

    private void exceptionHandlingStrategyIsSet(ExceptionHandlingStrategy strategy) {
        UofConfiguration integrationConfig = buildConfig("i").setExceptionHandlingStrategy(strategy).build();
        UofConfiguration productionConfig = buildConfig("p").setExceptionHandlingStrategy(strategy).build();
        UofConfiguration replayConfig = buildConfig("r").setExceptionHandlingStrategy(strategy).build();
        UofConfiguration customConfig = buildCustomConfig().setExceptionHandlingStrategy(strategy).build();

        Assert.assertEquals(strategy, integrationConfig.getExceptionHandlingStrategy());
        Assert.assertEquals(strategy, productionConfig.getExceptionHandlingStrategy());
        Assert.assertEquals(strategy, replayConfig.getExceptionHandlingStrategy());
        Assert.assertEquals(strategy, customConfig.getExceptionHandlingStrategy());
    }

    @Test
    public void httpClientTimeoutIsSetMin() {
        httpClientTimeoutCheck(ConfigLimit.HTTP_CLIENT_TIMEOUT_MIN);
    }

    @Test
    public void httpClientTimeoutIsSetMid() {
        httpClientTimeoutCheck(ConfigLimit.HTTP_CLIENT_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void httpClientTimeoutIsSetMax() {
        httpClientTimeoutCheck(ConfigLimit.HTTP_CLIENT_TIMEOUT_MAX);
    }

    private void httpClientTimeoutCheck(Integer timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i").setHttpClientTimeout(timeoutSeconds).build();
        UofConfiguration productionConfig = buildConfig("p").setHttpClientTimeout(timeoutSeconds).build();
        UofConfiguration replayConfig = buildConfig("r").setHttpClientTimeout(timeoutSeconds).build();
        UofConfiguration customConfig = buildCustomConfig().setHttpClientTimeout(timeoutSeconds).build();

        Assert.assertEquals(timeout, integrationConfig.getApi().getHttpClientTimeout());
        Assert.assertEquals(timeout, productionConfig.getApi().getHttpClientTimeout());
        Assert.assertEquals(timeout, replayConfig.getApi().getHttpClientTimeout());
        Assert.assertEquals(timeout, customConfig.getApi().getHttpClientTimeout());
    }

    @Test
    public void httpClientRecoveryTimeoutIsSetMin() {
        httpClientRecoveryTimeoutCheck(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MIN);
    }

    @Test
    public void httpClientRecoveryTimeoutIsSetMid() {
        httpClientRecoveryTimeoutCheck(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void httpClientRecoveryTimeoutIsSetMax() {
        httpClientRecoveryTimeoutCheck(ConfigLimit.HTTP_CLIENT_RECOVERY_TIMEOUT_MAX);
    }

    private void httpClientRecoveryTimeoutCheck(int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i")
            .setHttpClientRecoveryTimeout(timeoutSeconds)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setHttpClientRecoveryTimeout(timeoutSeconds)
            .build();
        UofConfiguration replayConfig = buildConfig("r").setHttpClientRecoveryTimeout(timeoutSeconds).build();
        UofConfiguration customConfig = buildCustomConfig()
            .setHttpClientRecoveryTimeout(timeoutSeconds)
            .build();

        Assert.assertEquals(timeout, integrationConfig.getApi().getHttpClientRecoveryTimeout());
        Assert.assertEquals(timeout, productionConfig.getApi().getHttpClientRecoveryTimeout());
        Assert.assertEquals(timeout, replayConfig.getApi().getHttpClientRecoveryTimeout());
        Assert.assertEquals(timeout, customConfig.getApi().getHttpClientRecoveryTimeout());
    }

    @Test
    public void httpClientFastFailingTimeoutIsSetMin() {
        httpClientFastFailingTimeoutCheck(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MIN);
    }

    @Test
    public void httpClientFastFailingTimeoutIsSetMid() {
        httpClientFastFailingTimeoutCheck(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void httpClientFastFailingTimeoutIsSetMax() {
        httpClientFastFailingTimeoutCheck(ConfigLimit.HTTP_CLIENT_FAST_FAILING_TIMEOUT_MAX);
    }

    private void httpClientFastFailingTimeoutCheck(int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i")
            .setHttpClientFastFailingTimeout(timeoutSeconds)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setHttpClientFastFailingTimeout(timeoutSeconds)
            .build();
        UofConfiguration replayConfig = buildConfig("r")
            .setHttpClientFastFailingTimeout(timeoutSeconds)
            .build();
        UofConfiguration customConfig = buildCustomConfig()
            .setHttpClientFastFailingTimeout(timeoutSeconds)
            .build();

        Assert.assertEquals(timeout, integrationConfig.getApi().getHttpClientFastFailingTimeout());
        Assert.assertEquals(timeout, productionConfig.getApi().getHttpClientFastFailingTimeout());
        Assert.assertEquals(timeout, replayConfig.getApi().getHttpClientFastFailingTimeout());
        Assert.assertEquals(timeout, customConfig.getApi().getHttpClientFastFailingTimeout());
    }

    @Test
    public void httpClientMaxConnTotalIsSet() {
        final int maxConnTotal = 123;
        UofConfiguration integrationConfig = buildConfig("i").setHttpClientMaxConnTotal(maxConnTotal).build();
        UofConfiguration productionConfig = buildConfig("p").setHttpClientMaxConnTotal(maxConnTotal).build();
        UofConfiguration replayConfig = buildConfig("r").setHttpClientMaxConnTotal(maxConnTotal).build();
        UofConfiguration customConfig = buildCustomConfig().setHttpClientMaxConnTotal(maxConnTotal).build();

        Assert.assertEquals(maxConnTotal, integrationConfig.getApi().getHttpClientMaxConnTotal());
        Assert.assertEquals(maxConnTotal, productionConfig.getApi().getHttpClientMaxConnTotal());
        Assert.assertEquals(maxConnTotal, replayConfig.getApi().getHttpClientMaxConnTotal());
        Assert.assertEquals(maxConnTotal, customConfig.getApi().getHttpClientMaxConnTotal());
    }

    @Test
    public void httpClientMaxConnPerRouteIsSet() {
        final int maxRoute = 11;
        UofConfiguration integrationConfig = buildConfig("i").setHttpClientMaxConnPerRoute(maxRoute).build();
        UofConfiguration productionConfig = buildConfig("p").setHttpClientMaxConnPerRoute(maxRoute).build();
        UofConfiguration replayConfig = buildConfig("r").setHttpClientMaxConnPerRoute(maxRoute).build();
        UofConfiguration customConfig = buildCustomConfig().setHttpClientMaxConnPerRoute(maxRoute).build();

        Assert.assertEquals(maxRoute, integrationConfig.getApi().getHttpClientMaxConnPerRoute());
        Assert.assertEquals(maxRoute, productionConfig.getApi().getHttpClientMaxConnPerRoute());
        Assert.assertEquals(maxRoute, replayConfig.getApi().getHttpClientMaxConnPerRoute());
        Assert.assertEquals(maxRoute, customConfig.getApi().getHttpClientMaxConnPerRoute());
    }

    @Test
    public void inactivitySecondsIsSetMin() {
        inactivitySecondsCheck(ConfigLimit.INACTIVITY_SECONDS_MIN);
    }

    @Test
    public void inactivitySecondsIsSetMid() {
        inactivitySecondsCheck(ConfigLimit.INACTIVITY_SECONDS_DEFAULT + 1);
    }

    @Test
    public void inactivitySecondsIsSetMax() {
        inactivitySecondsCheck(ConfigLimit.INACTIVITY_SECONDS_MAX);
    }

    private void inactivitySecondsCheck(int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i").setInactivitySeconds(timeoutSeconds).build();
        UofConfiguration productionConfig = buildConfig("p").setInactivitySeconds(timeoutSeconds).build();
        UofConfiguration replayConfig = buildConfig("r").setInactivitySeconds(timeoutSeconds).build();
        UofConfiguration customConfig = buildCustomConfig().setInactivitySeconds(timeoutSeconds).build();

        Assert.assertEquals(timeout, integrationConfig.getProducer().getInactivitySeconds());
        Assert.assertEquals(timeout, productionConfig.getProducer().getInactivitySeconds());
        Assert.assertEquals(timeout, replayConfig.getProducer().getInactivitySeconds());
        Assert.assertEquals(timeout, customConfig.getProducer().getInactivitySeconds());
    }

    @Test
    public void inactivitySecondsPrematchIsSetMin() {
        inactivitySecondsPrematchCheck(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MIN);
    }

    @Test
    public void inactivitySecondsPrematchIsSetMid() {
        inactivitySecondsPrematchCheck(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_DEFAULT + 1);
    }

    @Test
    public void inactivitySecondsPrematchIsSetMax() {
        inactivitySecondsPrematchCheck(ConfigLimit.INACTIVITY_SECONDS_PREMATCH_MAX);
    }

    private void inactivitySecondsPrematchCheck(int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i")
            .setInactivitySecondsPrematch(timeoutSeconds)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setInactivitySecondsPrematch(timeoutSeconds)
            .build();
        UofConfiguration replayConfig = buildConfig("r").setInactivitySecondsPrematch(timeoutSeconds).build();
        UofConfiguration customConfig = buildCustomConfig()
            .setInactivitySecondsPrematch(timeoutSeconds)
            .build();

        Assert.assertEquals(timeout, integrationConfig.getProducer().getInactivitySecondsPrematch());
        Assert.assertEquals(timeout, productionConfig.getProducer().getInactivitySecondsPrematch());
        Assert.assertEquals(timeout, replayConfig.getProducer().getInactivitySecondsPrematch());
        Assert.assertEquals(timeout, customConfig.getProducer().getInactivitySecondsPrematch());
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusIsSetTrue() {
        ignoreBetPalTimelineSportEventStatusCheck(true);
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusIsSetFalse() {
        ignoreBetPalTimelineSportEventStatusCheck(false);
    }

    private void ignoreBetPalTimelineSportEventStatusCheck(boolean ignoreBetPalTimelineSportEventStatus) {
        UofConfiguration integrationConfig = buildConfig("i")
            .setIgnoreBetPalTimelineSportEventStatus(ignoreBetPalTimelineSportEventStatus)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setIgnoreBetPalTimelineSportEventStatus(ignoreBetPalTimelineSportEventStatus)
            .build();
        UofConfiguration replayConfig = buildConfig("r")
            .setIgnoreBetPalTimelineSportEventStatus(ignoreBetPalTimelineSportEventStatus)
            .build();
        UofConfiguration customConfig = buildCustomConfig()
            .setIgnoreBetPalTimelineSportEventStatus(ignoreBetPalTimelineSportEventStatus)
            .build();

        Assert.assertEquals(
            ignoreBetPalTimelineSportEventStatus,
            integrationConfig.getCache().getIgnoreBetPalTimelineSportEventStatus()
        );
        Assert.assertEquals(
            ignoreBetPalTimelineSportEventStatus,
            productionConfig.getCache().getIgnoreBetPalTimelineSportEventStatus()
        );
        Assert.assertEquals(
            ignoreBetPalTimelineSportEventStatus,
            replayConfig.getCache().getIgnoreBetPalTimelineSportEventStatus()
        );
        Assert.assertEquals(
            ignoreBetPalTimelineSportEventStatus,
            customConfig.getCache().getIgnoreBetPalTimelineSportEventStatus()
        );
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusCacheTimeoutIsSetMin() {
        ignoreBetPalTimelineSportEventStatusCacheTimeoutCheck(ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN);
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusCacheTimeoutIsSetMid() {
        ignoreBetPalTimelineSportEventStatusCacheTimeoutCheck(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_DEFAULT + 1
        );
    }

    @Test
    public void ignoreBetPalTimelineSportEventStatusCacheTimeoutIsSetMax() {
        ignoreBetPalTimelineSportEventStatusCacheTimeoutCheck(ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX);
    }

    private void ignoreBetPalTimelineSportEventStatusCacheTimeoutCheck(int timeoutHours) {
        Duration timeout = Duration.ofHours(timeoutHours);
        UofConfiguration integrationConfig = buildConfig("i")
            .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(timeoutHours)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(timeoutHours)
            .build();
        UofConfiguration replayConfig = buildConfig("r")
            .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(timeoutHours)
            .build();
        UofConfiguration customConfig = buildCustomConfig()
            .setIgnoreBetPalTimelineSportEventStatusCacheTimeout(timeoutHours)
            .build();
        Assert.assertEquals(
            timeout,
            integrationConfig.getCache().getIgnoreBetPalTimelineSportEventStatusCacheTimeout()
        );

        Assert.assertEquals(
            timeout,
            productionConfig.getCache().getIgnoreBetPalTimelineSportEventStatusCacheTimeout()
        );
        Assert.assertEquals(
            timeout,
            replayConfig.getCache().getIgnoreBetPalTimelineSportEventStatusCacheTimeout()
        );
        Assert.assertEquals(
            timeout,
            customConfig.getCache().getIgnoreBetPalTimelineSportEventStatusCacheTimeout()
        );
    }

    @Test
    public void profileCacheTimeoutIsSetMin() {
        profileCacheTimeoutCheck(ConfigLimit.PROFILECACHE_TIMEOUT_MIN);
    }

    @Test
    public void profileCacheTimeoutIsSetMid() {
        profileCacheTimeoutCheck(ConfigLimit.PROFILECACHE_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void profileCacheTimeoutIsSetMax() {
        profileCacheTimeoutCheck(ConfigLimit.PROFILECACHE_TIMEOUT_MAX);
    }

    private void profileCacheTimeoutCheck(int timeoutHours) {
        Duration timeout = Duration.ofHours(timeoutHours);
        UofConfiguration integrationConfig = buildConfig("i").setProfileCacheTimeout(timeoutHours).build();
        UofConfiguration productionConfig = buildConfig("p").setProfileCacheTimeout(timeoutHours).build();
        UofConfiguration replayConfig = buildConfig("r").setProfileCacheTimeout(timeoutHours).build();
        UofConfiguration customConfig = buildCustomConfig().setProfileCacheTimeout(timeoutHours).build();

        Assert.assertEquals(timeout, integrationConfig.getCache().getProfileCacheTimeout());
        Assert.assertEquals(timeout, productionConfig.getCache().getProfileCacheTimeout());
        Assert.assertEquals(timeout, replayConfig.getCache().getProfileCacheTimeout());
        Assert.assertEquals(timeout, customConfig.getCache().getProfileCacheTimeout());
    }

    @Test
    public void variantMarketDescriptionCacheTimeoutIsSetMin() {
        variantMarketDescriptionCacheTimeoutCheck(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN);
    }

    @Test
    public void variantMarketDescriptionCacheTimeoutIsSetMid() {
        variantMarketDescriptionCacheTimeoutCheck(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void variantMarketDescriptionCacheTimeoutIsSetMax() {
        variantMarketDescriptionCacheTimeoutCheck(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX);
    }

    private void variantMarketDescriptionCacheTimeoutCheck(int timeoutHours) {
        Duration timeout = Duration.ofHours(timeoutHours);
        UofConfiguration integrationConfig = buildConfig("i")
            .setVariantMarketDescriptionCacheTimeout(timeoutHours)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setVariantMarketDescriptionCacheTimeout(timeoutHours)
            .build();
        UofConfiguration replayConfig = buildConfig("r")
            .setVariantMarketDescriptionCacheTimeout(timeoutHours)
            .build();
        UofConfiguration customConfig = buildCustomConfig()
            .setVariantMarketDescriptionCacheTimeout(timeoutHours)
            .build();

        Assert.assertEquals(timeout, integrationConfig.getCache().getVariantMarketDescriptionCacheTimeout());
        Assert.assertEquals(timeout, productionConfig.getCache().getVariantMarketDescriptionCacheTimeout());
        Assert.assertEquals(timeout, replayConfig.getCache().getVariantMarketDescriptionCacheTimeout());
        Assert.assertEquals(timeout, customConfig.getCache().getVariantMarketDescriptionCacheTimeout());
    }

    @Test
    public void sportEventCacheTimeoutIsSetMin() {
        sportEventCacheTimeoutCheck(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN);
    }

    @Test
    public void sportEventCacheTimeoutIsSetMid() {
        sportEventCacheTimeoutCheck(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void sportEventCacheTimeoutIsSetMax() {
        sportEventCacheTimeoutCheck(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX);
    }

    private void sportEventCacheTimeoutCheck(int timeoutHours) {
        Duration timeout = Duration.ofHours(timeoutHours);
        UofConfiguration integrationConfig = buildConfig("i").setSportEventCacheTimeout(timeoutHours).build();
        UofConfiguration productionConfig = buildConfig("p").setSportEventCacheTimeout(timeoutHours).build();
        UofConfiguration replayConfig = buildConfig("r").setSportEventCacheTimeout(timeoutHours).build();
        UofConfiguration customConfig = buildCustomConfig().setSportEventCacheTimeout(timeoutHours).build();

        Assert.assertEquals(timeout, integrationConfig.getCache().getSportEventCacheTimeout());
        Assert.assertEquals(timeout, productionConfig.getCache().getSportEventCacheTimeout());
        Assert.assertEquals(timeout, replayConfig.getCache().getSportEventCacheTimeout());
        Assert.assertEquals(timeout, customConfig.getCache().getSportEventCacheTimeout());
    }

    @Test
    public void sportEventStatusCacheTimeoutIsSetMin() {
        sportEventStatusCacheTimeoutCheck(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN);
    }

    @Test
    public void sportEventStatusCacheTimeoutIsSetMid() {
        sportEventStatusCacheTimeoutCheck(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_DEFAULT + 1);
    }

    @Test
    public void sportEventStatusCacheTimeoutIsSetMax() {
        sportEventStatusCacheTimeoutCheck(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX);
    }

    private void sportEventStatusCacheTimeoutCheck(int timeoutMinutes) {
        Duration timeout = Duration.ofMinutes(timeoutMinutes);
        UofConfiguration integrationConfig = buildConfig("i")
            .setSportEventStatusCacheTimeout(timeoutMinutes)
            .build();
        UofConfiguration productionConfig = buildConfig("p")
            .setSportEventStatusCacheTimeout(timeoutMinutes)
            .build();
        UofConfiguration replayConfig = buildConfig("r")
            .setSportEventStatusCacheTimeout(timeoutMinutes)
            .build();
        UofConfiguration customConfig = buildCustomConfig()
            .setSportEventStatusCacheTimeout(timeoutMinutes)
            .build();

        Assert.assertEquals(timeout, integrationConfig.getCache().getSportEventStatusCacheTimeout());
        Assert.assertEquals(timeout, productionConfig.getCache().getSportEventStatusCacheTimeout());
        Assert.assertEquals(timeout, replayConfig.getCache().getSportEventStatusCacheTimeout());
        Assert.assertEquals(timeout, customConfig.getCache().getSportEventStatusCacheTimeout());
    }

    @Test
    public void rabbitConnectionTimeoutIsSetMin() {
        rabbitConnectionTimeoutCheck(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN);
    }

    @Test
    public void rabbitConnectionTimeoutIsSetMid() {
        rabbitConnectionTimeoutCheck(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_DEFAULT + 1);
    }

    @Test
    public void rabbitConnectionTimeoutIsSetMax() {
        rabbitConnectionTimeoutCheck(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX);
    }

    private void rabbitConnectionTimeoutCheck(int timeoutSec) {
        Duration timeout = Duration.ofSeconds(timeoutSec);
        UofConfiguration integrationConfig = buildConfig("i").setRabbitConnectionTimeout(timeoutSec).build();
        UofConfiguration productionConfig = buildConfig("p").setRabbitConnectionTimeout(timeoutSec).build();
        UofConfiguration replayConfig = buildConfig("r").setRabbitConnectionTimeout(timeoutSec).build();
        UofConfiguration customConfig = buildCustomConfig().setRabbitConnectionTimeout(timeoutSec).build();

        Assert.assertEquals(timeout, integrationConfig.getRabbit().getConnectionTimeout());
        Assert.assertEquals(timeout, productionConfig.getRabbit().getConnectionTimeout());
        Assert.assertEquals(timeout, replayConfig.getRabbit().getConnectionTimeout());
        Assert.assertEquals(timeout, customConfig.getRabbit().getConnectionTimeout());
    }

    @Test
    public void rabbitHeartbeatTimeoutIsSetMin() {
        rabbitHeartbeatCheck(ConfigLimit.RABBIT_HEARTBEAT_MIN);
    }

    @Test
    public void rabbitHeartbeatTimeoutIsSetMid() {
        rabbitHeartbeatCheck(ConfigLimit.RABBIT_HEARTBEAT_DEFAULT + 1);
    }

    @Test
    public void rabbitHeartbeatTimeoutIsSetMax() {
        rabbitHeartbeatCheck(ConfigLimit.RABBIT_HEARTBEAT_MAX);
    }

    private void rabbitHeartbeatCheck(int timeoutSeconds) {
        Duration timeout = Duration.ofSeconds(timeoutSeconds);
        UofConfiguration integrationConfig = buildConfig("i").setRabbitHeartbeat(timeoutSeconds).build();
        UofConfiguration productionConfig = buildConfig("p").setRabbitHeartbeat(timeoutSeconds).build();
        UofConfiguration replayConfig = buildConfig("r").setRabbitHeartbeat(timeoutSeconds).build();
        UofConfiguration customConfig = buildCustomConfig().setRabbitHeartbeat(timeoutSeconds).build();

        Assert.assertEquals(timeout, integrationConfig.getRabbit().getHeartBeat());
        Assert.assertEquals(timeout, productionConfig.getRabbit().getHeartBeat());
        Assert.assertEquals(timeout, replayConfig.getRabbit().getHeartBeat());
        Assert.assertEquals(timeout, customConfig.getRabbit().getHeartBeat());
    }

    @Test
    public void statisticsIntervalIsSetMin() {
        statisticsIntervalCheck(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN);
    }

    @Test
    public void statisticsIntervalIsSetMid() {
        statisticsIntervalCheck(ConfigLimit.STATISTICS_INTERVAL_MINUTES_DEFAULT + 1);
    }

    @Test
    public void statisticsIntervalIsSetMax() {
        statisticsIntervalCheck(ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX);
    }

    private void statisticsIntervalCheck(int timeoutMinutes) {
        Duration timeout = Duration.ofMinutes(timeoutMinutes);
        UofConfiguration integrationConfig = buildConfig("i").setStatisticsInterval(timeoutMinutes).build();
        UofConfiguration productionConfig = buildConfig("p").setStatisticsInterval(timeoutMinutes).build();
        UofConfiguration replayConfig = buildConfig("r").setStatisticsInterval(timeoutMinutes).build();
        UofConfiguration customConfig = buildCustomConfig().setStatisticsInterval(timeoutMinutes).build();

        Assert.assertEquals(timeout, integrationConfig.getAdditional().getStatisticsInterval());
        Assert.assertEquals(timeout, productionConfig.getAdditional().getStatisticsInterval());
        Assert.assertEquals(timeout, replayConfig.getAdditional().getStatisticsInterval());
        Assert.assertEquals(timeout, customConfig.getAdditional().getStatisticsInterval());
    }

    @Test
    public void omitMarketMappingsIsSetTrue() {
        omitMarketMappingsCheck(true);
    }

    @Test
    public void omitMarketMappingsIsSetFalse() {
        omitMarketMappingsCheck(false);
    }

    private void omitMarketMappingsCheck(boolean omitMarketMappings) {
        UofConfiguration integrationConfig = buildConfig("i").omitMarketMappings(omitMarketMappings).build();
        UofConfiguration productionConfig = buildConfig("p").omitMarketMappings(omitMarketMappings).build();
        UofConfiguration replayConfig = buildConfig("r").omitMarketMappings(omitMarketMappings).build();
        UofConfiguration customConfig = buildCustomConfig().omitMarketMappings(omitMarketMappings).build();

        Assert.assertEquals(omitMarketMappings, integrationConfig.getAdditional().omitMarketMappings());
        Assert.assertEquals(omitMarketMappings, productionConfig.getAdditional().omitMarketMappings());
        Assert.assertEquals(omitMarketMappings, replayConfig.getAdditional().omitMarketMappings());
        Assert.assertEquals(omitMarketMappings, customConfig.getAdditional().omitMarketMappings());
    }

    @Test
    public void enableUsageExportIsSetTrue() {
        enableUsageExportCheck(true);
    }

    @Test
    public void enableUsageExportIsSetFalse() {
        enableUsageExportCheck(false);
    }

    private void enableUsageExportCheck(boolean enableUsageExport) {
        UofConfiguration integrationConfig = buildConfig("i").enableUsageExport(enableUsageExport).build();
        UofConfiguration productionConfig = buildConfig("p").enableUsageExport(enableUsageExport).build();
        UofConfiguration replayConfig = buildConfig("r").enableUsageExport(enableUsageExport).build();
        UofConfiguration customConfig = buildCustomConfig().enableUsageExport(enableUsageExport).build();

        Assert.assertEquals(enableUsageExport, integrationConfig.getUsage().isExportEnabled());
        Assert.assertEquals(enableUsageExport, productionConfig.getUsage().isExportEnabled());
        Assert.assertEquals(enableUsageExport, replayConfig.getUsage().isExportEnabled());
        Assert.assertEquals(enableUsageExport, customConfig.getUsage().isExportEnabled());
    }
}
