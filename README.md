Unified Feed SDK 2.x
----------------

The Unified Odds SDK provides a simple and efficient way to access Sportradar's odds and sport information for a bookmaker.
It combines subscription of messages and RESTful API calls into a unified Java interface that hides most of the complexity including recovery.

### A Basic way to use the UofSdk

First you need to implement the SDK event listeners that will receive callbacks for each message/event.
* [UofListener](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofListener.html)
* [UofGlobalEventsListener](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofGlobalEventsListener.html)

Then to actually connect and start receiving messages you do the following:
```java
MyUofListener listener = new MyUofListener();
MyUofGlobalEventsListener globalEventsListener = new MyUofGlobalEventsListener();

UofConfiguration config = UofSdk.getConfigurationBuilder().setAccessToken("your-token").build();

UofSdk uofSdk = new UofSdk(globalEventsListener, config);

UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();
sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.AllMessages).build();

uofSdk.open();
```

See: [UofSdk](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofSdk.html),
[UofSessionBuilder](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofSessionBuilder.html),
[UofGlobalEventsListener](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofGlobalEventsListener.html)
and [UofListener](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofListener.html)

That should be it!

If you want to get available sport events, active tournaments, or all sports you can get the [SportDataProvider](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/SportDataProvider.html)
from the main [UofSdk](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofSdk.html) instance:
```java
SportDataProvider sportDataProvider = uofSdk.getSportDataProvider();
// Get all sports, translated in the desired locales
for (Sport sport : sportDataProvider.getSports()) {

}
// Get all soccer active tournaments, the returned data will be translated in the desired locales
for (SportEvent tournament : sportDataProvider.getActiveTournaments("soccer")) {

}
// Get all competitions scheduled for today
for (SportEvent sportEvent : sportDataProvider.getCompetitionsFor(new Date())) {

}

// Get all live competitions
for (SportEvent sportEvent : sportDataProvider.getLiveCompetitions()) {

}
```

### More Advanced Usage
Note that there is one thread handling message reception and calling your registered listener per session,
so the processing within your listener should be as quick as possible to not prevent following messages from being
processed.

Another more scalable way of listening to events is to have two different sessions one for high-priority messages
and another for low-priority-messages. 
This means that the low priority messages will not prevent high-priority messages from getting processed
(ex., [BetSettlement](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html)
is considered low-priority,
[OddsChange](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/OddsChange.html) is considered high-priority).
To create two different sessions for the high and low-priority messages you do the following:
```java
MyUofListener listener = new MyUofListener();
MyUofGlobalEventsListener globalEventsListener = new MyUofGlobalEventsListener();

UofConfiguration config = UofSdk.getConfigurationBuilder().setAccessToken("your-token").build();

UofSdk uofSdk = new UofSdk(globalEventsListener, config);

UofSessionBuilder sessionBuilder = uofSdk.getSessionBuilder();
sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.HiPrioMessagesOnly).build();
sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.LoPrioMessagesOnly).build();

uofSdk.open();
```

Note that the same listener is used for both channels, but when creating the two different sessions, different
[MessageInterest](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/MessageInterest.html)
levels are provided. In this case, you will get two different threads doing the processing of the different types of messages.

#### *Live Only Processing*
If you wish to only process live events in your system and maybe process prematch events in a completely different system,
you can do this in a similar manner.
```java
sessionBuilder.setListener(listener).setMessageInterest(MessageInterest.LiveMessagesOnly).build();
```
This kind of session will receive all messages except
[OddsChange](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/OddsChange.html)
happening before the game starts
(you will start receiving
[OddsChange](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/OddsChange.html)
some minutes before the game starts) and
[BetSettlement](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html)
resulting from confirmed results (you will still receive
[BetSettlments](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html)
when the game ends, but only after 15minutes or even later after the game confirms the match results).

### Localization
By default all the data is available in English. You can add additional desired "prefetch" languages
and set the default locale with the use of the
[UofConfigurationBuilder](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/cfg/UofConfigurationBuilder.html)
([addDesiredLocales](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/cfg/UofConfigurationBuilder.html#addDesiredLocales-java.util.List-),
[setDefaultLocale](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/cfg/UofConfigurationBuilder.html#setDefaultLocale-java.util.Locale-)).
If you need to access a locale that was not specified as the default locale and neither added to the desired locales
list, you can still access the locale translated content trough the
[SportDataProvider](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/SportDataProvider.html)
and
[MarketDescriptionManager](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/MarketDescriptionManager.html).

### System Failures
The Unified Odds SDK is designed to help you handle various networking outages and Sportradar subsystem failures.
If some malfunction of the system is detected(Sportradar subsystem stops working, alive interval violations,...),
the SDK will dispatch a
[ProducerDown](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/ProducerDown.html)
event, when this happens it is advised that you disable all the markets related to this producer.

When the SDK detects that the malfunction is corrected it will automatically reconnect and request the most recent
odds information and any other missed messages(a recovery request will be executed), after the recovery is completed the
[ProducerUp](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/ProducerUp.html)
event is dispatched, after the producer is up again you can safely re-enable all the markets.

If your system crashes or if you take down/restart your system you need to provide the timestamp of the last processed
message per producer, so the SDK performs the recovery for the missed messages(the max time from the last processed
message can not be more than 3 days). You can do this trough the
[ProducerManager](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/ProducerManager.html)
available on the
[UofSdk](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/UofSdk.html)
instance. If the last processed message timestamp is not provided, the SDK will perform a
full recovery, beware: with a full recovery you do not recover any lost
[BetSettlement](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/com/sportradar/unifiedodds/sdk/oddsentities/BetSettlement.html)
messages!
```java
// as an example, we set the last message received timestamp to 2 days ago for the producer with the id 1(LiveOdds)
Calendar cal = Calendar.getInstance();
cal.add(Calendar.DATE, -2);
ProducerManager producerManager = uofSdk.getProducerManager();
producerManager.setProducerLastMessageTimestamp(1, cal.getTime().getTime());

// session creation,...

uofSdk.open(); // finally we open the feed
```

### Further reading
* [Online Javadocs](http://sdk.sportradar.com/content/unifiedfeedsdk/java2/javadoc/)