# Migrate to UOF Java SDK 5.0.0

Use this guide to upgrade an application from UOF Java SDK 4.x to version 5.0.0. Version 5.0.0 changes replay environment selection, messaging credentials, and client authentication.

## 1. Upgrade dependencies

Version 5.0.0 uses these libraries:

- `com.auth0:java-jwt` 4.6.1, added for client authentication
- `com.rabbitmq:amqp-client` 5.37.0
- `org.slf4j:slf4j-api` 2.0.20
- `com.fasterxml.jackson.core:jackson-databind` 2.22.3
- `io.opentelemetry:opentelemetry-sdk` 1.51.0
- `io.opentelemetry:opentelemetry-api` 1.51.0
- `io.opentelemetry:opentelemetry-sdk-metrics` 1.51.0
- `com.google.guava:guava` 33.7.2-jre
- `com.ibm.icu:icu4j` 77.1
- `org.yaml:snakeyaml` 2.7
- `org.glassfish.jaxb:jaxb-runtime` 2.3.9

If your application pins any of these libraries, for example through dependency management or a Maven bill of materials (BOM), keep the pinned versions compatible with this list.

## 2. Update the methods and classes in your code

### Replay changes

Version 5.0.0 removes `EnvironmentSelector.selectReplay()` and the `Environment.Replay` and `Environment.GlobalReplay` constants. Call `selectEnvironment(Environment)` with the replay environment that matches your credentials:

- Use `Environment.ReplayWithIntegrationCredentials` with an integration access token or with integration client authentication credentials.
- Use `Environment.ReplayWithProductionCredentials` with a production access token or with production client authentication credentials.

In version 4, `selectReplay()`, `Environment.Replay`, and `Environment.GlobalReplay` tried to detect whether an access token belonged to integration or production. Version 5.0.0 does not detect that. Select the replay environment yourself.

If the credentials do not match the selected environment, the SDK does not start. To check your credentials, run `CredentialCheckExample` in `sdk-example/src/main/java/com/sportradar/unifiedodds/example/credentialcheck/CredentialCheckExample.java`.

The following example replaces `selectReplay()` with an explicit replay environment.

```java
// Version 4
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    .setAccessToken("your access token")
    .selectReplay()
    .setDefaultLanguage(Locale.ENGLISH)
    .build();

// Version 5.0.0 with an integration access token
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    .setAccessToken("your integration access token")
    .selectEnvironment(Environment.ReplayWithIntegrationCredentials)
    .setDefaultLanguage(Locale.ENGLISH)
    .build();

// Version 5.0.0 with a production access token
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    .setAccessToken("your production access token")
    .selectEnvironment(Environment.ReplayWithProductionCredentials)
    .setDefaultLanguage(Locale.ENGLISH)
    .build();
```

**IMPORTANT - configuration files:** if you configure the environment via `UFSdkConfiguration.properties` (`uf.sdk.environment`) or `application.yml`, update the `environment` value from `Replay` / `GlobalReplay` to `ReplayWithIntegrationCredentials` / `ReplayWithProductionCredentials`. The SDK does **not** fail on an unrecognized environment value in these files - it silently falls back to `Integration`, so an outdated `Replay` value would make the SDK connect to the integration environment instead of the replay server.

Note that `Environment` constants were reordered. Do not persist or compare `Environment.ordinal()` values; use `name()` instead.

### Custom configuration builder changes

`CustomConfigurationBuilder.setMessagingUsername(String)` and `CustomConfigurationBuilder.setMessagingPassword(String)` were replaced by a single method `setMessagingCredentials(String username, String password)`. Both values are mandatory and must be non-empty.

```java
// Before (v4)
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    .setAccessToken("your access token")
    .selectCustom()
    .setMessagingHost("localhost")
    .setMessagingUsername("guest")
    .setMessagingPassword("guest")
    // ... other configuration options
    .build();

// After (v5)
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    .setAccessToken("your access token")
    .selectCustom()
    .setMessagingHost("localhost")
    .setMessagingCredentials("guest", "guest")
    // ... other configuration options
    .build();
```

The same rule applies to configuration files: `uf.sdk.messagingUsername` and `uf.sdk.messagingPassword` (and their `application.yml` equivalents) must now be either both set or both omitted. If only one of them is set, building the configuration fails with an `IllegalArgumentException`.

### Interface changes

Methods that were introduced with default implementations in the 4.x line to preserve backward compatibility became regular (abstract) interface methods. This affects you **only** if you provide your own implementations of these interfaces (e.g. test doubles or wrappers); code that merely consumes the SDK entities is not affected.

- `Message.getMessageHeaders()` no longer has a default implementation (previously it threw `UnsupportedOperationException`).
- `OutcomeSettlement.getEachWayResult()`, `OutcomeSettlement.getEachWayFactor()` and `OutcomeSettlement.getDeadHeatFactorPlace()` no longer have default implementations (previously they returned `null`).

The following interface methods were added and must be implemented if you provide your own implementation:

- `UofConfiguration.getClientAuthentication()` - returns the configured client authentication or `null` when an access token is used.
- `TokenSetter.setClientAuthentication(UofClientAuthentication.PrivateKeyJwtData)`
- `CustomConfigurationBuilder.setClientAuthenticationHost(String)`, `setClientAuthenticationPort(int)`, `setClientAuthenticationUseSsl(boolean)` and `setClientAuthenticationTenant(String)`

### Removed Methods

 - `EnvironmentSelector.selectReplay()`
 - enum values `Environment.Replay` and `Environment.GlobalReplay`
 - `CustomConfigurationBuilder.setMessagingUsername(String)` (use `setMessagingCredentials(String, String)`)
 - `CustomConfigurationBuilder.setMessagingPassword(String)` (use `setMessagingCredentials(String, String)`)

### Behavior changes

- When fetching the bookmaker details (`whoami`) fails, the SDK no longer probes the other environments (integration/production) to tell you which environment your access token belongs to. Only the configured environment is checked, so verify yourself that the token (or the Client Authentication credentials) matches the selected environment. To validate your credentials, refer to `CredentialCheckExample` (`sdk-example/src/main/java/com/sportradar/unifiedodds/example/credentialcheck/CredentialCheckExample.java`).
- The messaging username and password are now obfuscated in the logged configuration, the same way the access token is.

## 3. New Features

#### Client Authentication with Private Key JWT

Version 5.0.0 introduces a new client authentication feature that supports **Private Key JWT** authentication method. This feature implements OAuth 2.0 Client Credentials Grant with JWT assertion, providing enhanced security for API communications and for the connection to the message broker (RabbitMQ).

##### Migration Notes

- Ensure your RSA private key is properly formatted and the corresponding public key is registered with Sportradar
- Contact Sportradar support for assistance with key registration and authentication setup
- After registering your public key, you will receive `key id` and `service id` that must be used in the client authentication configuration

##### Basic Usage

To configure client authentication with Private Key JWT:

IMPORTANT: The client authentication credentials (service id) and the selected SDK environment must belong together, i.e. the public key must be registered in the same environment (production or integration) you select.

```java
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import java.security.PrivateKey;

// Create client authentication configuration
UofClientAuthentication.PrivateKeyJwtData clientAuth = UofClientAuthentication
    .privateKeyJwt()
    .setSigningKeyId("your-key-id") // Key id provided to you after registering the Public Key with Sportradar
    .setClientId("your-client-id") // Sportradar Service Id
    .setPrivateKey(yourRSAPrivateKey)
    .build();

// Use with SDK configuration
UofConfiguration config = UofSdk.getUofConfigurationBuilder()
    // client authentication is configured instead of an access token
    .setClientAuthentication(clientAuth)
    .selectEnvironment(Environment.Integration)
    // ... other configuration options
    .build();

// Use with SDK configuration (custom)
UofConfiguration custom = UofSdk.getUofConfigurationBuilder()
        .setClientAuthentication(clientAuth)
        .selectCustom()
        // overwriting default host, port, tenant and SSL usage for client authentication server if needed, e.g. for testing
        .setClientAuthenticationHost("auth.example.com")
        .setClientAuthenticationPort(443)
        .setClientAuthenticationUseSsl(true)
        .setClientAuthenticationTenant("https://auth.example.com/") // optional, defaults to the authentication server URL
        // ... other configuration options
        .build();
```

##### Configuration Options

The `UofClientAuthentication.PrivateKeyJwtData.Builder` provides the following configuration methods:

- **`setSigningKeyId(String keyId)`** - Sets the key identifier used in JWT header ("kid" parameter)
- **`setClientId(String clientId)`** - Sets the client identifier (Sportradar Service Id) for JWT "iss" and "sub" claims
- **`setPrivateKey(PrivateKey privateKey)`** - Sets the RSA private key for JWT signing

When using the custom environment (`selectCustom()`), the following additional methods are available on the `CustomConfigurationBuilder`. They require client authentication to be configured beforehand:

- **`setClientAuthenticationHost(String host)`** - Sets the host of the client authentication server
- **`setClientAuthenticationPort(int port)`** - Sets the port of the client authentication server
- **`setClientAuthenticationUseSsl(boolean useSsl)`** - Sets whether HTTPS (`true`) or HTTP (`false`) is used to communicate with the client authentication server
- **`setClientAuthenticationTenant(String tenant)`** - Sets the (optional) authentication tenant. The tenant is used as the audience (`aud` claim) of the signed JWT. When not set, it defaults to the full URL of the client authentication server (e.g. `https://auth.sportradar.com/`), which is what you want in almost all cases.

For the predefined environments (production, integration, global production/integration and the replay environments) the SDK selects the client authentication server automatically.

Configuration via properties and yml files is not supported for client authentication configuration. Configuration files used to configure SDK in version 4.x.x are still available, but the client authentication must be set programmatically.

The configured client authentication is exposed via `UofConfiguration.getClientAuthentication()` (`null` when the access token is used).

##### Security Requirements

- **RSA Keys Only** - Only RSA SHA256 algorithm is supported for private keys at the moment
- **Key Registration** - The corresponding public key must be registered with Sportradar authentication service
- **SSL Recommended** - Use HTTPS for secure transmission of authentication credentials

## 4. Test Your Project

Thoroughly test your project after making the changes. Test all critical functionality to ensure that everything still works as expected. Pay special attention to any areas of your setup that interact with the SDK, as these are likely to be the most affected by the upgrade.

## 5. Update the Documentation

Update your project's documentation and any training materials to reflect the changes introduced by the upgrade. This will help your team members understand and work with the new version.

## 6. Deploy to Production

Once you are confident that your project works correctly with the upgraded SDK, you can deploy the updated version to
your production environment.

## 7. Monitoring and Maintenance

After deployment, monitor your project closely for any unexpected issues or performance problems. Be prepared to address any post-upgrade issues promptly.

## 8. Feedback and Reporting

If you encounter any bugs or issues in the SDK, consider reporting them to support@sportradar.com. Providing feedback
can help improve the SDK for future releases.
