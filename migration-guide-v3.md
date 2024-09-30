# UOF Java SDK - Migration Guide

The UOF Java SDK 2.x.x is upgraded to 3.0.0, and this is your roadmap to a smooth transition from your current SDK version to the latest version. The upgrade is designed to elevate your experience and align the SDK more closely with your business needs.

This guide is intended to offer practical advice to ensure your transition is not only efficient but also enhances the performance and capabilities of your software.

## 1. Upgrade Dependencies

You should upgrade the *com.sportradar.unifiedodds.sdk.unified-feed-sdk* maven dependency to **3.0.0**. Before performing the upgrade, it's advisable to consider upgrading certain libraries your project depends on.

The UOF SDK's dependencies have been upgraded, and the revised configuration was tested in Java 8, 11, and 17. The following libraries are affected by this upgrade.

| Upgraded libraries   | From               | To                      |
|----------------------|--------------------|-------------------------|
| apache http client   | 4.5.1              | 5.2.1                   |
| rabbitmq.amqp-client | 3.6.0              | 5.17.0                  |
| slf4j                | 1.7.13             | 2.0.7                   |
| guava                | 21.0               | 31.1-jre                |
| guice                | 4.0                | 5.1.0                   |
| icu4j                | 57.1               | 72.1                    |
| snakexml             | 1.20               | 2.0                     |
| jaxb api             | 2.3.0              | 2.3.3                   |
| jaxb runtime changed | sun.xml.bind 2.3.0 | org.glassfish.jax 2.3.8 |
| lombok               | 1.18.24            | removed                 |
| opentelemetry-api    | -                  | 1.25.0 (added)          |

## 2. Build New UofSdk Instance

The root class is renamed from `OddsFeed` to `UofSdk`.

You will need to build the configuration before creating a new `UofSdk` instance.
Example for building it from the properties file:

```java
UofConfiguration config = UofSdk.getUofConfigurationBuilder().buildConfigFromSdkProperties();
UofSdk uofSdk = new UofSdk(new GlobalEventsListener(), config);
```

## 3. Update the Methods and Classes in Your Code

Review your codebase to identify any parts that might be affected by the upgrade. Look for deprecated methods or classes that have been removed in the new version. Update your code to use the new APIs provided by the UOF Java SDK 3.0.0. This may involve making changes to method calls, imports, and class references. Handle any breaking changes or deprecations by updating your code accordingly. You can contact support if you encounter specific issues.

The following classes and methods are changed. Hence, you will need to update your code to use the new names:

#### Root Classes Renamed

- OddsFeedConfiguration to UofConfiguration
- GlobalEventListener to UofGlobalEventListener
- OddsFeedException to UofException
- OddsFeedConfigurationBuilder to UofConfigurationBuilder
- getOddsFeedConfigurationBuilder to getUofConfigurationBuilder
- OddsFeedListener to UofListener
- OddsFeedSessionBuilder to UofSessionBuilder
- OddsFeedSession to UofSession
- OddsFeed to UofSdk
- SportsInfoManager to SportDataProvider
- CustomisableSDKModule to CustomisableSdkModule
- SDKGlobalEventListener to SdkGlobalEventListener
- SDKProducerListener to SdkProducerListener
- SDKConnectionStatusListener to SdkConnectionStatusListener
- SDKEventRecoveryStatusListener to SdkEventRecoveryStatusListener
- ReplayOddsFeed to UofSdkForReplay
- OddsFeedExt to UofSdkExt
- CustomisableOddsFeed to CustomisableUofSdk
- ReplayFeed to UofSdkForReplay
- Removed OperationManager (properties moved to UofConfiguration)
- EnvironmentSelector - removed SelectIntegration() and SelectProduction() - use SelectEnvironment (SdkEnvironment ufEnvironment)
- Configuration related classes moved to `com.sportradar.unifiedodds.sdk.cfg` package

#### Removed Methods and Classes

| Removed Methods / Classes                                                                                             | Alternative Methods / Classes                                                                                                                           |
|-----------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| ConfigurationAccessTokenSetter                                                                                        | TokenSetter                                                                                                                                             |
| OddsFeedConfigurationBuilder                                                                                          | -                                                                                                                                                       |
| selectIntegration and selectProduction methods from EnvironmentSelector                                               | selectEnvironment                                                                                                                                       |
| OddsFeedSessionBuilder.setSpecificListeners                                                                           | -                                                                                                                                                       |
| onProducerDown(ProducerDown / producerDown) and onProducerUp (ProducerUp / producerUp) from SdkProducerStatusListener | Producer status messages are now received through onProducerStatusChange (ProducerStatus) only                                                          |
| Environment.Staging                                                                                                   | Use Environment.Integration instead                                                                                                                     |
| EventClock.getRemainingDate                                                                                           | Use EventClock.getRemainingTime instead                                                                                                                 |
| EventsStatus.getApiId                                                                                                 | Use EventStatus.getApiName instead                                                                                                                      |
| MarketDescription.getIncludesOutcomeOfTypes                                                                           | Use MarketDescription.getOutcomeType instead                                                                                                            |
| PeriodStatistics.getTeamStatisticDtos                                                                                 | Use PeriodStatistics.getTeamStatistics instead                                                                                                          |
| OutcomeOdds.getOdds()                                                                                                 | Use OutcomeOdds.getOdds(OddsDisplayType) instead. To get the same value from getOdds(OddsDisplayType), use outcomeOdds.getOdds(OddsDisplayType.Decimal) |
| OutcomeSettlement.isWinning                                                                                           | Use OutcomeSettlement.getOutcomeResult == OutcomeResult.Won                                                                                             |
| Message.getTimestamp                                                                                                  | Use Message.getTimestamps.getCreated instead                                                                                                            |

#### Added Methods / New Methods

- categoryData.getName
- sportData.getName
- currentSeasonInfo.getNames
- drawResult.getNames
- manager.getNames
- tournamentInfo.getName

#### Removed Methods

- round.getGroupName

Corresponding method removals and constructor argument adjustments were applied on `exportableCompleteRoundCi` and `exportableLoadableRoundCi` classes.

#### Restructured Methods

- `venue.getCourses` signature was changed from returning `List<Hole>` to `List<Course>` to accommodate multiple courses.
- Course entity is introduced and contains the following methods:
    - `List<Hole> getHoles`
    - `Urn getId`
    - `getName` and `getNames` consistent with these method semantics elsewhere in the SDK having the same signatures.

  Corresponding changes were made to the `ExportableVenueCi` class along with the introduction of the `ExportableCourseCi` class.

- `teamCompetitor.getDivision` method was moved up in the inheritance hierarchy to competitor and its signature was changed from returning `Integer` to `Division` to accommodate division name.
- Division entity is introduced containing:
    - `Integer getDivision`
    - `String getDivisionName`

  Corresponding changes were made to the `ExportableCompetitorCi` class along with the introduction of the `ExportableDivisionCi` class.

- `timelineEvent.getHomeScore` and `timelineEvent.getAwayScore` signatures were changed from returning `double` to `BigDecimal` to make these methods return types consistent with other occurrences of score-related methods elsewhere in the SDK.
- Corresponding changes were made to `ExportableTimelineEventCi`.
- Change of behaviour of `round.getName`, `round.getNames`, and `round.getPhaseOrGroupLongName`.
- `round.getName` and `round.getNames` methods now return the value of the "name" property from the Sports API or an empty string if the API does not provide it, and no longer fallback to "group_name" and "group_long_name" attributes.
- `round.getPhaseOrGroupLongName` method returns the value of the "group_long_name" property from the Sports API or an empty string if the API does not provide it, and no longer falls back to "name" attributes.

#### Name Changes

The following changes were made to improve consistency:

- URN renamed to Urn.
- Classes within the package `com.sportradar.unifiedodds.sdk.caching.exportable` used to have the suffix "CI" like `ExportableCI` or `ExportableSportEventCI`, which has been renamed to "Ci" like `ExportableCi` or `ExportableSportEventCi`. This change was applied uniformly to all classes in the package.
- All classes within the package `com.sportradar.uf.datamodel` used to start with the prefix "UF" like `UFReportingStatus` and `UFEventStatus`. The prefix was renamed to "Uf" like `UfReportingStatus` and `UfEventStatus`. This convention change was applied uniformly across hundreds of classes in the package.

## 4. Update the Configuration

The configuration settings were split between the configuration class and OperationManager. OperationManager is removed and all settings are consolidated within the UofConfiguration interface.

You have three choices for constructing the final configuration through:

1. Properties file
2. YAML file
3. Programmatically using ConfigurationBuilder

Certain options that were previously available in properties and YAML files have been removed and can exclusively be configured using the configuration builder.

#### Through Properties / YAML file

You'll need to re-configure the following either in the properties file or YAML:

| Keys                        | Required/Optional | Comments                                                                                              |
|-----------------------------|-------------------|-------------------------------------------------------------------------------------------------------|
| accessToken                 | required          | The token you are currently using can also be used with our new version.                              |
| defaultLanguage or desiredLanguages | required | desiredLanguages is renamed from desiredLocales.                                                      |
| nodeId                      | optional          | Recommended to be set – must be unique per SDK instance.                                              |
| environment                 | optional          | If not set, 'Integration' will be used. Renamed from ufEnvironment.                                   |
| inactivitySeconds           | -                 | This is removed from properties and YAML files. However, you can set this through the configuration builder. |
| messagingHost               | optional          | Sets the URL of the messaging host (broker), e.g., mq.betradar.com. This setting is used only when using a custom environment. |
| messagingUseSsl             | optional          | Sets the value indicating whether a secure connection to the message broker should be used. Renamed from useMessagingSsl. |
| messagingVirtualHost        | optional          | Sets the name of the virtual host configured on the messaging server (broker). This setting is used only when using a custom environment. |
| apiHost                     | optional          | Sets the URL of the API host, e.g., https://api.betradar.com. This setting is used only when using a custom environment. |
| apiUseSsl                   | optional          | Sets the value indicating whether a secure connection to the Sports API should be used. Renamed from useApiSsl. |
| exceptionHandlingStrategy   | optional          | Sets an ExceptionHandlingStrategy enum member specifying how to handle exceptions thrown to outside callers (‘Catch’ or ‘Throw’). |
| disabledProducers           | optional          | Sets the comma-delimited list of IDs of disabled producers, e.g., 1,2,7,9.                             |
| maxRecoveryTime             | optional          | This is removed from properties and YAML files. However, you can set this through the configuration builder. |
| adjustAfterAge              | optional          | This is removed from properties and YAML files. However, you can set this through the configuration builder. |
| httpClientTimeout           | optional          | This is removed from properties and YAML files. However, you can set this through the configuration builder. |
| recoveryHttpClientTimeout   | optional          | This is removed from properties and YAML files. However, you can set this through the configuration builder. |

#### Through ConfigurationBuilder

The full configuration can also be set up via the ConfigurationBuilder obtained via `uofSdk.getUofConfigurationBuilder()`. The resulting UofConfiguration contains all the previously set configurations for the SDK.

## 5. Test your project

Thoroughly test your project after making the changes. Test all critical functionality to ensure that everything still works as expected. Pay special attention to any areas of your setup that interact with the UOF Java SDK, as these are likely to be the most affected by the upgrade.

## 6. Update the Documentation

Update your project's documentation and any training materials to reflect the changes introduced by the upgrade. This will help your team members understand and work with the new version.

## 7. Deploy to Production

Once you are confident that your project works correctly with the upgraded UOF Java SDK, you can deploy the updated version to your production environment.

## 8. Monitoring and Maintenance

After deployment, monitor your project closely for any unexpected issues or performance problems. Be prepared to address any post-upgrade issues promptly.

## 9. Feedback and Reporting

If you encounter any bugs or issues in the UOF Java SDK 3.0.0, consider reporting them to support@sportradar.com. Providing feedback can help improve the SDK for future releases.