# Sportradar Unified Odds SDK for Java 4.x

A comprehensive Java SDK that simplifies access to Sportradar's real-time odds and sports data for bookmakers. This SDK seamlessly integrates message subscriptions with RESTful API calls, providing a unified interface while handling complex recovery mechanisms automatically.

## 📋 Migration Information

- **Upgrading from version 2.x to 3.x?** See our [Migration Guide v3](migration-guide-v3.md)
- **Upgrading from version 3.x to 4.x?** See our [Migration Guide v4](migration-guide-v4.md)

## 🚀 Quick Start Guide

### Step 1: Implement Event Listeners

Create your custom listeners to handle incoming messages and events:

- **[UofListener](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/UofListener.html)** - Handles odds and betting-related messages
- **[UofGlobalEventsListener](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/UofGlobalEventsListener.html)** - Handles system-wide events and status updates

### Step 2: Initialize and Configure the SDK

```java
// Create your listener instances
MyUofListener listener = new MyUofListener();
MyUofGlobalEventsListener globalEventsListener = new MyUofGlobalEventsListener();

// Configure the SDK with your access token
UofConfiguration config = UofSdk.getConfigurationBuilder()
    .setAccessToken("your-token")
    .build();

// Initialize the SDK
UofSdk uofSdk = new UofSdk(globalEventsListener, config);

// Create and configure a session
UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();
sessionBuilder.setListener(listener)
    .setMessageInterest(MessageInterest.AllMessages)
    .build();

// Start receiving data
uofSdk.open();
```

**Key Components:**
- [UofSdk](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/UofSdk.html) - Main SDK interface
- [UofSessionBuilder](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/UofSessionBuilder.html) - Session configuration builder

## 📊 Accessing Sports Data

Retrieve sports information, tournaments, and events using the [SportDataProvider](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/SportDataProvider.html):

```java
SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();

// Retrieve all available sports (with localized translations)
for (Sport sport : sportDataProvider.getSports()) {
    // Process sports data
}

// Get active soccer tournaments
for (SportEvent tournament : sportDataProvider.getActiveTournaments("soccer")) {
    // Process tournament data
}

// Fetch today's scheduled competitions
for (SportEvent sportEvent : sportDataProvider.getCompetitionsFor(new Date())) {
    // Process scheduled events
}

// Get currently live competitions
for (SportEvent sportEvent : sportDataProvider.getLiveCompetitions()) {
    // Process live events
}
```

## ⚡ Advanced Configuration Options

### High-Performance Message Processing

For optimal performance, consider separating high-priority and low-priority message processing into different sessions. This prevents low-priority messages from blocking critical updates.

**Message Priority Classification:**
- **High Priority:** [OddsChange](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/OddsChange.html) events
- **Low Priority:** [BetSettlement](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html) events

```java
MyUofListener listener = new MyUofListener();
MyUofGlobalEventsListener globalEventsListener = new MyUofGlobalEventsListener();

UofConfiguration config = UofSdk.getConfigurationBuilder()
    .setAccessToken("your-token")
    .build();

UofSdk uofSdk = new UofSdk(globalEventsListener, config);

// Create separate sessions for different message priorities
UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();

// High-priority session
sessionBuilder.setListener(listener)
    .setMessageInterest(MessageInterest.HiPrioMessagesOnly)
    .build();

// Low-priority session  
sessionBuilder.setListener(listener)
    .setMessageInterest(MessageInterest.LoPrioMessagesOnly)
    .build();

uofSdk.open();
```

This approach creates dedicated processing threads for each [MessageInterest](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/MessageInterest.html) level, ensuring optimal performance.

### Live-Only Event Processing

For systems that exclusively handle live events, configure the SDK to process only live messages:

```java
sessionBuilder.setListener(listener)
    .setMessageInterest(MessageInterest.LiveMessagesOnly)
    .build();
```

**Live-Only Mode Behavior:**
- Excludes pre-match [OddsChange](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/OddsChange.html) events (starts receiving a few minutes before game begins)
- Filters out [BetSettlement](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html) messages from confirmed results
- Still receives settlements when games end, but only after result confirmation (typically 15+ minutes post-game)

## 🌍 Internationalization Support

### Default Localization
The SDK provides English content by default. Customize language preferences using [UofConfigurationBuilder](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/cfg/UofConfigurationBuilder.html):

```java
UofConfiguration config = UofSdk.getConfigurationBuilder()
    .setAccessToken("your-token")
    .setDefaultLocale(Locale.GERMAN)
    .addDesiredLocales(Arrays.asList(Locale.FRENCH, Locale.SPANISH))
    .build();
```

### Dynamic Locale Access
Access additional locales on-demand through:
- [SportDataProvider](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/SportDataProvider.html)
- [MarketDescriptionManager](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/MarketDescriptionManager.html)

## 🛠️ System Resilience & Recovery

### Automatic Failure Detection
The SDK continuously monitors system health and automatically handles various failure scenarios:

**When Issues Are Detected:**
- Network outages
- Sportradar subsystem failures  
- Alive interval violations

The SDK dispatches [ProducerDown](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/ProducerDown.html) events. **Recommended action:** Disable all markets for the affected producer.

### Automatic Recovery Process
When systems recover, the SDK:
1. Automatically reconnects
2. Requests latest odds information
3. Retrieves missed messages via recovery requests
4. Dispatches [ProducerUp](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/ProducerUp.html) events

**After ProducerUp:** Safely re-enable all markets for the producer.

### Manual Recovery Configuration
For system restarts or crashes, provide the last processed message timestamp to ensure complete data recovery:

```java
// Example: Set recovery point to 2 days ago for LiveOdds producer (ID: 1)
Calendar cal = Calendar.getInstance();
cal.add(Calendar.DATE, -2);

ProducerManager producerManager = uofSdk.getProducerManager();
producerManager.setProducerLastMessageTimestamp(1, cal.getTime().getTime());

// Configure sessions...

uofSdk.open(); // Start the feed
```

**Important Recovery Notes:**
- Maximum recovery window: 3 days
- Without timestamp: Full recovery is performed
- **Warning:** Full recovery does NOT restore missed [BetSettlement](https://sportradar.github.io/UnifiedOddsSdkJava/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html) messages

### Performance Optimization Tips

⚠️ **Critical Performance Consideration:** Each session uses a single thread for message reception and listener callbacks. Keep your listener processing as lightweight as possible to prevent message queue backups.

## 📚 Additional Resources

- **[Complete API Documentation](https://sportradar.github.io/UnifiedOddsSdkJava/)** - Comprehensive Javadocs
- **[Migration Guides](migration-guide-v4.md)** - Version upgrade instructions
- **[SDK Examples](sdk-example/)** - Sample implementations and use cases
