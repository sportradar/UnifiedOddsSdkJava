/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import java.util.Optional;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created on 26/03/2018.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName" })
public class TokenSetterTests {

    private static SDKConfigurationPropertiesReader propertiesWithToken;
    private static SDKConfigurationPropertiesReader propertiesWithoutToken;
    private static final SDKConfigurationYamlReader yamlConfigMock = Mockito.mock(
        SDKConfigurationYamlReader.class
    );

    @BeforeClass
    public static void init() {
        propertiesWithToken = Mockito.mock(SDKConfigurationPropertiesReader.class);
        Mockito.when(propertiesWithToken.readAccessToken()).thenReturn(Optional.of("some-token"));

        propertiesWithoutToken = Mockito.mock(SDKConfigurationPropertiesReader.class);
        Mockito.when(propertiesWithoutToken.readAccessToken()).thenReturn(Optional.empty());
    }

    @Test(expected = NullPointerException.class)
    public void tokenSetterConstructRequirementsFailOne() {
        TokenSetter tokenSetter = new TokenSetterImpl(null, Mockito.mock(SDKConfigurationYamlReader.class));
    }

    @Test(expected = NullPointerException.class)
    public void tokenSetterConstructRequirementsFailTwo() {
        TokenSetter tokenSetter = new TokenSetterImpl(
            Mockito.mock(SDKConfigurationPropertiesReader.class),
            null
        );
    }

    @Test
    public void programmaticTokenSet() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithoutToken, yamlConfigMock);

        EnvironmentSelector environmentSelector = tokenSetter.setAccessToken("some-token");

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void programmaticTokenSetNull() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithoutToken, yamlConfigMock);

        tokenSetter.setAccessToken(null);
    }

    @Test
    public void propertiesTokenSet() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithToken, yamlConfigMock);

        EnvironmentSelector environmentSelector = tokenSetter.setAccessTokenFromSdkProperties();

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void propertiesTokenSetNull() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithoutToken, yamlConfigMock);

        tokenSetter.setAccessTokenFromSdkProperties();
    }

    @Test
    public void systemVarTokenSet() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithToken, yamlConfigMock);

        System.setProperty("uf.accesstoken", "some-token");

        EnvironmentSelector environmentSelector = tokenSetter.setAccessTokenFromSystemVar();

        System.clearProperty("uf.accesstoken");

        Assert.assertNotNull(environmentSelector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void systemVarTokenSetNull() {
        TokenSetter tokenSetter = new TokenSetterImpl(propertiesWithoutToken, yamlConfigMock);

        tokenSetter.setAccessTokenFromSystemVar();
    }
}
