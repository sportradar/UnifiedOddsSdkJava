# UOF Java SDK - Migration Guide

The UOF Java SDK 3.x.x is upgraded to 4.0.0, and this is your roadmap to a smooth transition from your current SDK version to the latest version. The upgrade is designed to elevate your experience and align the SDK more closely with your business needs.

This guide is intended to offer practical advice to ensure your transition is not only efficient but also enhances the performance and capabilities of your software.

## 1. Upgrade Dependencies

* com.rabbitmq:amqp-client 5.24.0
* org.apache.httpcomponents.client5:httpclient5 5.4.1
* com.google.guava:guava 33.2.0-jre
* com.google.inject:guice 7.0.0
* com.ibm.icu:icu4j 75.1
* org.yaml:snakeyaml 2.2
* io.opentelemetry:opentelemetry-sdk 1.44.0 (added)
* io.opentelemetry:opentelemetry-api 1.44.0
* io.opentelemetry:opentelemetry-sdk-metrics 1.44.0 (added)
* io.opentelemetry:opentelemetry-exporter-otlp 1.44.0 (added)

## 2. Update the Methods and Classes in Your Code

Review your codebase to identify any parts that might be affected by the upgrade. Look for deprecated methods or classes
that have been removed in the new version. Update your code to use the new APIs provided by the SDK. This may involve
making changes to method calls, imports, and class references. Handle any breaking changes or deprecations by updating
your code accordingly. You can contact support if you encounter specific issues.

The following classes and methods are changed. Hence, you will need to update your code to use the new names:

#### Restructured Packages

In major release v4, certain classes have been repackaged. Internal classes, which customers should never use or
integrate with in their code, are located in the com.sportradar.unifiedodds.sdk.internal package and its subpackages.


| Class                       | New package                                       |
|-----------------------------|---------------------------------------------------|
| AvailableSelections         | com.sportradar.unifiedodds.sdk.entities.custombet |
| AvailableSelectionsFilter   | com.sportradar.unifiedodds.sdk.entities.custombet |
| Calculation                 | com.sportradar.unifiedodds.sdk.entities.custombet |
| CalculationFilter           | com.sportradar.unifiedodds.sdk.entities.custombet |
| Market                      | com.sportradar.unifiedodds.sdk.entities.custombet |
| MarketFilter                | com.sportradar.unifiedodds.sdk.entities.custombet |
| OutcomeFilter               | com.sportradar.unifiedodds.sdk.entities.custombet |
| Selection                   | com.sportradar.unifiedodds.sdk.entities.custombet |
| CommunicationException      | com.sportradar.unifiedodds.sdk.exceptions         |
| BookingManager              | com.sportradar.unifiedodds.sdk.managers           |
| CacheType                   | com.sportradar.unifiedodds.sdk.managers           |
| CashOutProbabilitiesManager | com.sportradar.unifiedodds.sdk.managers           |
| CustomBetManager            | com.sportradar.unifiedodds.sdk.managers           |
| CustomBetSelectionBuilder   | com.sportradar.unifiedodds.sdk.managers           |
| EventChangeManager          | com.sportradar.unifiedodds.sdk.managers           |
| EventRecoveryRequestIssuer  | com.sportradar.unifiedodds.sdk.managers           |
| MarketDescriptionManager    | com.sportradar.unifiedodds.sdk.managers           |
| ProducerManager             | com.sportradar.unifiedodds.sdk.managers           |
| RecoveryManager             | com.sportradar.unifiedodds.sdk.managers           |
| ReplayManager               | com.sportradar.unifiedodds.sdk.managers           |   
| SportDataProvider           | com.sportradar.unifiedodds.sdk.managers           |
| RoutingKeyInfo              | com.sportradar.unifiedodds.sdk.extended           |

#### Removed Methods

Removed default method implementations from the following interfaces:

- Calculation.isHarmonization
- CalculationFilter.isHarmonization
- Selection.getOdds
- MatchStatus.getStatistics
- MatchStatus.getHomePenaltyScore
- MatchStatus.getAwayPenaltyScore
- MatchStatus.isDecidedByFed
- CustomBetSelectionBuilder.setOdds
- CustomBetSelectionBuilder.build

#### Updated Methods

- ExportableCompetitorCi.setVirtual() - parameter type is changed to Boolean instead of boolean
- ExportableCompetitorCi.isVirtual() - return type is changed to Boolean instead of boolean
- Competitor.isVirtual() - return type is changed to Boolean instead of boolean

Note: 
* If the interfaces with the removed default methods are implemented in the client code, ensure that the corresponding methods are implemented in their respective classes to avoid compilation errors and maintain expected functionality.
* Competitor.isVirtual() is true or false unless the competitor profile has failed to load. In that case it is null.

#### Renamed Methods / Properties

- AvailableSelections.getEvent to getEventId
- AvailableSelectionsFilter.getEvent to getEventId

#### New Features

SDK Usage Service: This service will allow us to anonymously track producer downtrends, helping us proactively identify
potential bottlenecks and broader implementation trends before they lead to future issues.

New configuration properties:
- UofUsageConfiguration exposed via UofConfiguration.getUsage for properties configuring usage export (metrics)
- UofConfiguration.getUsage().isExportEnabled (default: enabled)
- Added ConfigurationBuilder.enableUsageExport(bool enable) method to enable/disable usage export

## 3. Test your project

Thoroughly test your project after making the changes. Test all critical functionality to ensure that everything still
works as expected. Pay special attention to any areas of your setup that interact with the SDK, as these are likely to
be the most affected by the upgrade.

## 4. Update the Documentation

Update your project's documentation and any training materials to reflect the changes introduced by the upgrade. This will help your team members understand and work with the new version.

## 5. Deploy to Production

Once you are confident that your project works correctly with the upgraded SDK, you can deploy the updated version to
your production environment.

## 6. Monitoring and Maintenance

After deployment, monitor your project closely for any unexpected issues or performance problems. Be prepared to address any post-upgrade issues promptly.

## 7. Feedback and Reporting

If you encounter any bugs or issues in the SDK, consider reporting them to support@sportradar.com. Providing feedback
can help improve the SDK for future releases.