/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * An implementation of all the SDK builder interfaces which is used to build the {@link OddsFeedConfiguration} used to
 * initialize the {@link com.sportradar.unifiedodds.sdk.OddsFeed} object
 */
public class OddsFeedConfigurationBuilderImpl implements ConfigurationAccessTokenSetter, OddsFeedConfigurationBuilder {
    private final static int MIN_INACTIVITY_SECONDS = 20;
    private final static int MAX_INACTIVITY_SECONDS = 180;
    private final static int MIN_RECOVERY_EXECUTION_MINUTES = 15;
    private final static int MAX_RECOVERY_EXECUTION_MINUTES = 60 * 6;

    private final SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader;

    private String accessToken;
    private Locale defaultLocale;
    private List<Locale> desiredLocales;
    private boolean useMessagingSsl;
    private boolean useApiSsl;
    private String host;
    private String apiHost;
    private int port;
    private int inactivitySeconds;
    private int maxRecoveryExecutionMinutes;
    private String messagingPassword;
    private Integer sdkNodeId;
    private boolean useIntegrationEnvironment;
    private List<Integer> disabledProducers;

    public OddsFeedConfigurationBuilderImpl(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader) {
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);

        this.sdkConfigurationPropertiesReader = sdkConfigurationPropertiesReader;

        setDefaultValues();
    }

    /**
     * Set your access token as provided by Sportradar (without this set you will not be able to
     * connect)
     *
     * @param accessToken the access token
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setAccessToken(String accessToken) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(accessToken), "Invalid access token format: " + accessToken);

        this.accessToken = accessToken;
        return this;
    }

    /**
     * Try to set your access token as provided by Sportradar trough the system variable "uf.accesstoken",
     * you can set the access token with the following JVM argument -Duf.accesstoken=your-access-token.
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setAccessTokenFromSystemVar() {
        String token = System.getProperty("uf.accesstoken");
        if (token == null) {
            token = System.getenv("uf.accesstoken");
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(token), "Token system variable uf.accesstoken not found");

        this.setAccessToken(token);
        return this;
    }

    /**
     * Try to set your access token as provided by Sportradar trough the SDK properties, "uf.sdk.accessToken"
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setAccessTokenFromSdkProperties() {
        Optional<String> ifPresent = sdkConfigurationPropertiesReader.readAccessToken();

        String token = ifPresent.orElseThrow(() -> new IllegalArgumentException("Could not read the access token from the SDK properties(uf.sdk.accessToken)"));

        this.setAccessToken(token);
        return this;
    }

    /**
     * Sets the default locale. This is the locale that will be used for any getter that returns
     * localized Strings (i.e. Sport name, Tournament name, Team name, Player name etc). The default
     * locale is English if not specified.
     *
     * @param defaultLocale the locale to use as standard.
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setDefaultLocale(Locale defaultLocale) {
        Preconditions.checkNotNull(defaultLocale, "Default locale can not be null");

        this.defaultLocale = defaultLocale;
        return this;
    }

    /**
     * Which locales should be auto-fetched. The default is only English if none is provided.
     *
     * @param locales a list of locales that should be auto-fetched
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder addDesiredLocales(List<Locale> locales) {
        Preconditions.checkNotNull(locales, "Desired locales can not be null");

        desiredLocales.addAll(locales);
        return this;
    }

    /**
     * Sets a value indicating whether SSL should be used when connecting to AMQP broker
     *
     * @param useSsl value indicating whether the SDK should use SSL
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setMessagingUseSsl(boolean useSsl) {
        this.useMessagingSsl = useSsl;
        return this;
    }

    /**
     * Sets a value indicating whether SSL should be used when requesting API endpoints
     *
     * @param useSsl value indicating whether the SDK should use SSL when requesting API endpoints
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setApiUseSsl(boolean useSsl) {
        this.useApiSsl = useSsl;
        return this;
    }

    /**
     * Specify the Sportradar host to receive messages from (if not specified this defaults to
     * mq.betradar.com)
     *
     * @param host - the Sportradar host to receive messages from
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setMessagingHost(String host) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "Invalid messaging host");

        this.host = host;
        return this;
    }

    /**
     * Specify the Sportradar host used for API access (if not specified this defaults to
     * api.betradar.com)
     *
     * @param apiHost the Sportradar host used for API access
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setApiHost(String apiHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(apiHost), "Invalid API host");

        this.apiHost = apiHost;
        return this;
    }

    /**
     * Sets the port used to connect to AMQP broker
     *
     * @param port the port used to connect to AMQP broker
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setMessagingPort(int port) {
        Preconditions.checkArgument(port > 0, "Invalid port value");

        this.port = port;
        return this;
    }

    /**
     * Specify how many seconds that is maximum length of inactivity (default and lowest 20 seconds, highest 180s)
     *
     * @param inactivitySeconds the number of seconds of inactivity before flagging a producer as down
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setInactivitySeconds(int inactivitySeconds) {
        Preconditions.checkArgument(inactivitySeconds >= MIN_INACTIVITY_SECONDS, "Inactivity seconds value must be more than " + MIN_INACTIVITY_SECONDS);
        Preconditions.checkArgument(inactivitySeconds <= MAX_INACTIVITY_SECONDS, "Inactivity seconds value must be less than " + MAX_INACTIVITY_SECONDS);

        this.inactivitySeconds = inactivitySeconds;
        return this;
    }

    /**
     * Specify the maximum execution time of a recovery request. (default and highest 6 hours, lowest 15m)
     *
     * @param executionMinutes the number of minutes before the recovery request is repeated
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setMaxRecoveryExecutionMinutes(int executionMinutes) {
        Preconditions.checkArgument(executionMinutes >= MIN_RECOVERY_EXECUTION_MINUTES, "Recovery execution minutes must be more than " + MIN_RECOVERY_EXECUTION_MINUTES);
        Preconditions.checkArgument(executionMinutes <= MAX_RECOVERY_EXECUTION_MINUTES, "Recovery execution minutes must be less than " + MAX_RECOVERY_EXECUTION_MINUTES);

        this.maxRecoveryExecutionMinutes = executionMinutes;
        return this;
    }

    /**
     * Set the password of the broker to which you are connecting - this is not required for the connection to the
     * default Sportradar Rabbit servers
     *
     * @param password - the password used to connect to the AMQP broker(ex: your testing replay server)
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setMessagingPassword(String password) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
        this.messagingPassword = password;
        return this;
    }

    /**
     * Set the node identifier of the currently running SDK instance. If you run multiple SDK instances on different systems,
     * the node id should be different on each one of them.
     *
     * @param id the node identifier
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setSdkNodeId(int id) {
        this.sdkNodeId = id;
        return this;
    }

    /**
     * Set the setting property which will ensure the SDK connects to the integration environment
     * (please note that the integration environment requires different access tokens than the production ones)
     *
     * @param useIntegrationEnvironment an indication if the integration environment should be used
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setUseIntegrationEnvironment(boolean useIntegrationEnvironment) {
        this.useIntegrationEnvironment = useIntegrationEnvironment;
        return this;
    }

    /**
     * Set a list of producer identifiers which should be disabled automatically when the SDK starts
     *
     * @param disabledProducers a {@link List} of producer identifiers which should be disabled
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder setDisabledProducers(List<Integer> disabledProducers) {
        Preconditions.checkNotNull(disabledProducers, "Disabled producers list can not be null");

        this.disabledProducers.addAll(disabledProducers);
        return this;
    }

    /**
     * Reads the SDK properties file and sets the available properties
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Override
    public OddsFeedConfigurationBuilder loadConfigFromSdkProperties() {
        sdkConfigurationPropertiesReader.readMaxInactivitySeconds().ifPresent(val -> inactivitySeconds = val);
        sdkConfigurationPropertiesReader.readApiHost().ifPresent(val -> apiHost = val);
        sdkConfigurationPropertiesReader.readMessagingHost().ifPresent(val -> host = val);
        sdkConfigurationPropertiesReader.readUseApiSsl().ifPresent(val -> useApiSsl = val);
        sdkConfigurationPropertiesReader.readUseMessagingSsl().ifPresent(val -> useMessagingSsl = val);
        sdkConfigurationPropertiesReader.readMaxRecoveryTime().ifPresent(val -> maxRecoveryExecutionMinutes = val);
        sdkConfigurationPropertiesReader.readUseIntegration().ifPresent(val -> useIntegrationEnvironment = val);
        sdkConfigurationPropertiesReader.readSdkNodeId().ifPresent(val -> sdkNodeId = val);
        sdkConfigurationPropertiesReader.readDefaultLocale().ifPresent(val -> defaultLocale = val);

        desiredLocales.addAll(sdkConfigurationPropertiesReader.readDesiredLocales());
        disabledProducers.addAll(sdkConfigurationPropertiesReader.readDisabledProducers());

        return this;
    }

    /**
     * Builds an {@link OddsFeedConfiguration} instance with the provided data
     *
     * @return a new  {@link OddsFeedConfiguration} instance built with the provided data
     */
    @Override
    public OddsFeedConfiguration build() {
        if (useIntegrationEnvironment) {
            host = "stgmq.betradar.com";
            apiHost = "stgapi.betradar.com";
            useApiSsl = true;
            useMessagingSsl = true;
            port = 5671;
        }

        ExceptionHandlingStrategy exceptionHandlingStrategy = sdkConfigurationPropertiesReader.readExceptionHandlingStrategy()
                .orElse(ExceptionHandlingStrategy.Catch);

        OddsFeedConfiguration oddsFeedConfiguration = new OddsFeedConfiguration(
                accessToken,
                defaultLocale,
                desiredLocales,
                host,
                apiHost,
                inactivitySeconds,
                maxRecoveryExecutionMinutes,
                useMessagingSsl,
                useApiSsl,
                port,
                null,
                messagingPassword,
                sdkNodeId,
                useIntegrationEnvironment,
                disabledProducers,
                exceptionHandlingStrategy,
                null,
                null);

        setDefaultValues();
        return oddsFeedConfiguration;
    }

    private void setDefaultValues() {
        accessToken = null;
        defaultLocale = Locale.ENGLISH;
        desiredLocales = new ArrayList<>();
        useMessagingSsl = true;
        useApiSsl = true;
        host = "mq.betradar.com";
        apiHost = "api.betradar.com";
        port = 5671;
        inactivitySeconds = 20;
        maxRecoveryExecutionMinutes = MAX_RECOVERY_EXECUTION_MINUTES;
        messagingPassword = null;
        sdkNodeId = null;
        useIntegrationEnvironment = false;
        disabledProducers = new ArrayList<>();
    }
}
