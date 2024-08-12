/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.RuntimeConfiguration;
import com.sportradar.unifiedodds.sdk.SdkConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mockito;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class ConfiguredConnectionFactoryTest {

    private static final String ANY = "any";
    private final SdkInternalConfiguration config = createSuitableConfig();

    private final ConnectionFactory rabbitConnectionFactory = mock(ConnectionFactory.class);

    private final WhoAmIReader whoAmIReader = mock(WhoAmIReader.class);

    private final TimeUtils timeUtils = mock(TimeUtils.class);

    private final ExecutorService executorService = mock(ExecutorService.class);

    private final ConfiguredConnectionFactory configuredConnectionFactory = new ConfiguredConnectionFactory(
        rabbitConnectionFactory,
        config,
        ANY,
        mock(SdkConnectionStatusListener.class),
        executorService,
        timeUtils
    );

    @BeforeEach
    public void setAnyTime() {
        when(timeUtils.nowInstant()).thenReturn(Instant.now());
    }

    @Test
    public void shouldNotInstantiateWithoutConnectionFactory() {
        assertThatThrownBy(() ->
                new ConfiguredConnectionFactory(
                    null,
                    config,
                    ANY,
                    mock(SdkConnectionStatusListener.class),
                    mock(ExecutorService.class),
                    timeUtils
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("rabbitConnectionFactory");
    }

    @Test
    public void shouldNotInstantiateWithoutConfiguration() {
        assertThatThrownBy(() ->
                new ConfiguredConnectionFactory(
                    rabbitConnectionFactory,
                    null,
                    ANY,
                    mock(SdkConnectionStatusListener.class),
                    mock(ExecutorService.class),
                    timeUtils
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("config");
    }

    @Test
    public void shouldNotInstantiateWithoutConnectionStatusListener() {
        assertThatThrownBy(() ->
                new ConfiguredConnectionFactory(
                    rabbitConnectionFactory,
                    config,
                    ANY,
                    null,
                    mock(ExecutorService.class),
                    timeUtils
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("connectionStatusListener");
    }

    @Test
    public void shouldNotInstantiateWithoutExecutorService() {
        assertThatThrownBy(() ->
                new ConfiguredConnectionFactory(
                    rabbitConnectionFactory,
                    config,
                    ANY,
                    mock(SdkConnectionStatusListener.class),
                    null,
                    timeUtils
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("dedicatedRabbitMqExecutor");
    }

    @Test
    public void shouldNotInstantiateWithoutTimeUtils() {
        assertThatThrownBy(() ->
                new ConfiguredConnectionFactory(
                    rabbitConnectionFactory,
                    config,
                    ANY,
                    mock(SdkConnectionStatusListener.class),
                    mock(ExecutorService.class),
                    null
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("timeUtils");
    }

    @Test
    public void shouldNotCreateConnectionWithoutWhoAmIReader() {
        assertThatThrownBy(() -> configuredConnectionFactory.createConfiguredConnection(null, ANY))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void shouldNotCreateConnectionWithoutToken() {
        when(config.getAccessToken()).thenReturn(null);

        assertThatThrownBy(() -> configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldCreateConnectionForConfiguredHost() throws Exception {
        final val host = "http://host";
        when(config.getMessagingHost()).thenReturn(host);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setHost(host));
    }

    @Test
    public void shouldCreateConnectionForConfiguredPort() throws Exception {
        final int port = 9034;
        when(config.getPort()).thenReturn(port);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setPort(port));
    }

    @Test
    public void shouldNotSetSslVersionIfConfiguredNotTo() throws Exception {
        when(config.getUseMessagingSsl()).thenReturn(false);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v ->
            v.verify(rabbitConnectionFactory, never()).useSslProtocol(anyString())
        );
    }

    @Test
    public void shouldSetSslVersionIfConfiguredTo() throws Exception {
        when(config.getUseMessagingSsl()).thenReturn(true);
        String desiredTlsVersion = "TLSv1.3";

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, desiredTlsVersion);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).useSslProtocol(desiredTlsVersion));
    }

    @Test
    public void shouldSetConfiguredVirtualHost() throws Exception {
        final val virtualHost = "specifiedVirtualHost";
        when(config.getMessagingVirtualHost()).thenReturn(virtualHost);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setVirtualHost(virtualHost));
    }

    @Test
    public void shouldSetDefaultVirtualHostIfNotConfigured() throws Exception {
        when(config.getMessagingVirtualHost()).thenReturn(null);
        final int bookmakerId = 5;
        when(whoAmIReader.getBookmakerId()).thenReturn(bookmakerId);
        final val defaultVirtualHost = "/unifiedfeed/" + bookmakerId;

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setVirtualHost(defaultVirtualHost)
        );
    }

    @Test
    public void shouldValidateBookmakerIdWhenItIsZero() throws Exception {
        when(config.getMessagingVirtualHost()).thenReturn(null);
        when(whoAmIReader.getBookmakerId()).thenReturn(0);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(whoAmIReader).validateBookmakerDetails());
    }

    @Test
    public void connectionShouldDescribeSdkVersionUsed()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        final val version = "specifiedVersion";
        final val factory = new ConfiguredConnectionFactory(
            rabbitConnectionFactory,
            config,
            version,
            mock(SdkConnectionStatusListener.class),
            mock(ExecutorService.class),
            timeUtils
        );

        factory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> {
                assertEquals(version, props.get("SrUfSdkVersion"));
            }
        );
    }

    @Test
    public void connectionShouldDescribeSdkTypeUsed()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> assertEquals("java", props.get("SrUfSdkType"))
        );
    }

    @Test
    public void connectionNameShouldIndicateLanguageAndQueueEngine()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> assertEquals("RabbitMQ / Java", props.get("SrUfSdkConnName"))
        );
    }

    @Test
    public void connectionShouldDescribeBookmakerId()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        final int bookmakerId = 6;
        final val bookmakerIdString = "6";
        when(whoAmIReader.getBookmakerId()).thenReturn(bookmakerId);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> assertEquals(bookmakerIdString, props.get("SrUfSdkBId"))
        );
    }

    @Test
    public void connectionShouldDescribeItsCreationTimeInDigitOnlyFormat()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        when(timeUtils.nowInstant()).thenReturn(Instant.parse("2022-02-15T18:35:24.00Z"));

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> assertEquals("202202151835", props.get("SrUfSdkInit"))
        );
    }

    @Test
    public void connectionShouldNotLosePreviouslyDefinedDescription()
        throws NoSuchAlgorithmException, IOException, KeyManagementException, TimeoutException {
        final val existingProperties = new HashMap<String, Object>();
        existingProperties.put("existingProperty", "existingValue");
        when(rabbitConnectionFactory.getClientProperties()).thenReturn(existingProperties);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyClientPropertiesSetIn(
            rabbitConnectionFactory,
            props -> assertEquals("existingValue", props.get("existingProperty"))
        );
    }

    @Test
    public void shouldSetClientProperties() throws Exception {
        final val existingProperties = new HashMap<String, Object>();
        existingProperties.put("existingProperty", "existingValue");
        when(rabbitConnectionFactory.getClientProperties()).thenReturn(existingProperties);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v ->
            v.verify(rabbitConnectionFactory).setClientProperties(existingProperties)
        );
    }

    @Test
    public void shouldSetConfiguredUsername() throws Exception {
        final val username = "specifiedUsername";
        when(config.getMessagingUsername()).thenReturn(username);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setUsername(username));
    }

    @Test
    public void shouldSetTokenAsUsernameIfUsernameWasNotConfigured() throws Exception {
        final val token = "specifiedToken";
        when(config.getAccessToken()).thenReturn(token);
        when(config.getMessagingUsername()).thenReturn(null);

        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setUsername(token));
    }

    @Test
    public void shouldSetConfiguredPassword() throws Exception {
        final val password = "specifiedPassword";
        when(config.getMessagingPassword()).thenReturn(password);
        final val factory = new ConfiguredConnectionFactory(
            rabbitConnectionFactory,
            config,
            ANY,
            mock(SdkConnectionStatusListener.class),
            executorService,
            timeUtils
        );

        factory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setPassword(password));
    }

    @Test
    public void shouldSetEmptyStringAsPasswordIfPasswordWasNotConfigured() throws Exception {
        when(config.getMessagingPassword()).thenReturn(null);
        final val factory = new ConfiguredConnectionFactory(
            rabbitConnectionFactory,
            config,
            ANY,
            mock(SdkConnectionStatusListener.class),
            executorService,
            timeUtils
        );

        factory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setPassword(""));
    }

    @Test
    public void shouldEnableAutoRecoveryForTheConnectionBeingCreated() throws Exception {
        configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

        verifyConnectionCreatedWith(v -> v.verify(rabbitConnectionFactory).setAutomaticRecoveryEnabled(true));
    }

    @Test
    public void shouldSetConfiguredTimeout() throws Exception {
        try (final val operationManager = Mockito.mockStatic(RuntimeConfiguration.class)) {
            final int configuredTimeoutSeconds = 5;
            operationManager
                .when(RuntimeConfiguration::getRabbitConnectionTimeout)
                .thenReturn(configuredTimeoutSeconds);

            configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

            final int configuredTimeoutMillis = 5000;
            verifyConnectionCreatedWith(v ->
                v.verify(rabbitConnectionFactory).setConnectionTimeout(configuredTimeoutMillis)
            );
        }
    }

    @Test
    public void shouldSetConfiguredHeartbeat() throws Exception {
        try (final val operationManager = Mockito.mockStatic(RuntimeConfiguration.class)) {
            final int configuredHeartbeat = 30;
            operationManager.when(RuntimeConfiguration::getRabbitHeartbeat).thenReturn(configuredHeartbeat);

            configuredConnectionFactory.createConfiguredConnection(whoAmIReader, ANY);

            verifyConnectionCreatedWith(v ->
                v.verify(rabbitConnectionFactory).setRequestedHeartbeat(configuredHeartbeat)
            );
        }
    }

    private void verifyConnectionCreatedWith(final ThrowingConsumer<InOrder> connectionVerifier)
        throws Exception {
        InOrder inOrder = Mockito.inOrder(rabbitConnectionFactory, whoAmIReader);
        connectionVerifier.accept(inOrder);
        inOrder.verify(rabbitConnectionFactory).newConnection(executorService);
    }

    private void verifyClientPropertiesSetIn(
        final ConnectionFactory factory,
        final Consumer<Map<String, Object>> verification
    ) {
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(factory).setClientProperties(captor.capture());
        verification.accept(captor.getValue());
    }

    private static SdkInternalConfiguration createSuitableConfig() {
        SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        when(config.getAccessToken()).thenReturn("anyAccessToken");
        return config;
    }

    private static interface ThrowingConsumer<T> {
        public void accept(T argument) throws Exception;
    }
}
