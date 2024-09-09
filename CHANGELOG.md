### Unified Feed SDK 3.x changelog

**3.3.2 (2024-08-28)**
- fix: Information whether competitor is virtual or not can reliably be fetched on all applicable sport events

**3.3.1 (2024-08-12)**
* fix: Fixed translation loading logic in Match. Whenever Match.getName is invoked it no longer attempts to fetch all languages at once and fetches only user-requested language instead.
* fix: Car properties of DriverProfile are now properly exposed

**3.3.0 (2024-07-29)**
* feat: Enabled accessing match statistics on MatchStatus received via Match.getStatus object (beside Soccer also supports Kabaddi statistics)

**3.2.0 (2024-05-15)**
* feat: Improved Variant Market handling when API call is not successful or has some faulty data (i.e. missing outcome name or missing market name)
* fix: CustomBet API error responses are properly extracted and available for analysis either in logs or in the exception message (dependent on the configured exception handling strategy)
* fix: Environment.GlobalReplay works properly with production token. API host is updated to production during the bookmaker discovery call. 

**3.1.0 (2024-02-19)**
* SDK can be used in a construct close-with-resources
* recovering after network outages no longer causes message duplication 
* Markets are no longer excluded from messages handed over to the listener when descriptions of markets are unavailable
* Fix: faulty market descriptions are attempted to be re-fetched irrespective from exception handling strategy configured

**3.0.0 (2023-10-24)**
* Consistent casing used in naming in java code as per Google java style guidelines (refer to Migration Guide)
* Upgrading Apache http client library from 4 to 5
* methods getName/s exposed more consistently in classes: categoryData, sportData, currentSeason, drawResult, manager, tournamentInfo
* upgrading sdk libraries. Rabbit, slf4j, guava, guice, icu4j, snakeyaml, jaxb
* removing libraries: logback
* removing deprecated methods
* OddsFeedConfiguration renamed to UofSdkConfiguration
* Removed selectIntegration and selectProduction from EnvironmentSelector (use selectEnvironment)
* user agent indicating basic environment information is submitted on Sports API calls 
* Configuration from file - renamed values:
  * defaultLocale -> defaultLanguage
  * desiredLocales -> desiredLanguages
  * ufEnvironment -> environment
  * useMessagingSsl -> messagingUseSll
  * useApiSsl -> apiUseSsl
  * moved HttpClient configuration to UofConfigurationBuilder
* removing OddsFeedSessionBuilder.setSpecificListeners method
* renaming class SportsInfoProvider to SportDataProvider
* renaming method UofSdk.getSportsInfoProvider to UofSdk.getSportDataProvider
* renaming OddsFeed to uofSdk
* renaming OddsFeedSession to UofSession
* renaming OddsFeedSessionBuilder to UofSessionBuilder
* renaming OddsFeedListener to UofListener, 
* renaming OddsFeedConfigurationBuilder to UofConfigurationBuilder
* renaming UofSdk.getOddsFeedConfigurationBuilder to UofConfigurationBuilder
* renaming OddsFeedException to UofException
* renaming ReplayOddsFeed to UofSdkForReplay
* removed OperationManager
* Venue can now contain multiple courses, each with holes, id, and name translations.
* venue.getCourses signature was changed from returning List<Hole> to List<Course> to accommodate multiple courses and carry additionally id and name for each of them. Corresponding changes were made to ExportableCompetitorCi
* teamCompetitor.getDivision method was moved up in the inheritance hierarchy to competitorCi and its signature was changed from returning Integer to Division in order for Decision to also carry division name. Corresponding changes were made to ExportableCompetitorCi
* timelineEvent.getHomeScore and timelineEvent.getAwayScore signatures were changed from returning double to BigDecimal to make these methods return types consistent with other occurrences of these same methods elsewhere in the SDK API. Corresponding changes were made to ExportableTimelineEventCi
* removed round.getGroupName along with corresponding adjustments in ExportableCompleteRoundCi and ExportableLoadableRoundCi
* round.getName, round.getNames and round.getPhaseOrGroupLongName are backed by their own corresponding attributes "name" and "group_name" only in the Sports API responses, and no longer falls back to other attributes.

**2.0.60.0 (2023-05-17)**
* CustomBetManager respects ExceptionHandlingStrategy for all non-argument-validating exceptions
* CustomBetManager now throws CommunicationException on API failures
* API failures across the board carry to the client: url used and http code returned 
* BookingManager respects ExceptionHandlingStrategy for CommunicationExceptions

**2.0.59.0 (2023-01-26)**
* Added support for group urn type
* Fix ignore fixture_change_fixture endpoint for virtual producers

**2.0.58.3 (2023-01-12)**
* Fix: GlobalReplay environment not discards messages from disabled packages for the customer

**2.0.58.2 (2023-01-09)**
* Fix: TLS version selection for RabbitMq connection no longer causes a crash in certain JVMs

**2.0.58.1 (2022-12-27)**
* Fix: Artifact descriptor in maven central is fixed, so that SDK is usable in maven projects again

**2.0.58.0 (2022-12-23)**
* Added GlobalReplay environment

**2.0.57.0 (2022-09-20)**
* CustomBet - added support for calculate-filter endpoint
* Fix: http request logged twice
* Fix: NPE when LocalizedNamedValue does not have description

**2.0.56.0 (2022-07-15)**
* Fix: recovery request url when configuring custom environment
* Fix: handling for system session connection reset

**2.0.55.0 (2022-06-02)**
* Added method isOpen to OddsFeed
* Feed close cleanup improvements (cache items invalidated, session closed)

**2.0.54.1 (2022-05-05)**
* Fix: fast HttpClient timeout fixed

**2.0.54.0 (2022-04-26)**
* ***Contains critical bug and should not be used. Use version 2.0.54.1. ***
* Separate HttpClient for critical and other requests
* Added configuration option for fast HttpClient in OperationManager (default timeout 5s)
* Extended SportsInfoManager with getTimelineEvents
* Improved how SportInfoManager is handling exceptions
* Improved logging for raw data events

**2.0.53.0 (2022-02-23)**
* Added getBetradarName to Round
* Improvement: handling of alive messages in regard to producer status
* Fix: multiple connections in case of disconnection
* Fix: Competition competitors did not expose IsVirtual correctly

**2.0.52.1 (2022-01-20)**
* Fix: after disconnection some feed messages can not be deserialized

**2.0.52.0 (2021-12-10)**
* Added support for event results in SportEventStatus received from api
* Extended MarketDescriptionManager with parallelPrefetchVariantMarketDescriptions
* Improved logging exception during message consumption
* Fix: connecting to replay server with production token
* Fix: setting custom configuration from properties files
* Fix: loading competitor associated players data from match summary
* Fix: throws exception if match, stage or draw not found exception happens

**2.0.51.0 (2021-11-18)**
* Improvements for connection resilience
* Added onRecoveryInitiate to SDKProducerStatusListener
* Added RabbitConnectionTimeout and RabbitHeartbeat to OperationManager
* Improved logging regarding connection and recovery process
* Fix: how connection and channels are made
* Extended StageType with Run enum value
* Changed default UF environment from Integration to GlobalIntegration

**2.0.50.0 (2021-10-06)**
* Extended configuration with ufEnvironment attribute
* Extended TokenSetter and EnvironmentSelector 
* New values added to Environment enum (GlobalIntegration, GlobalProduction, ProxySingapore, ProxyTokyo)

**2.0.49.1 (2021-09-09)**
* When fetching non-cached fixture change endpoint fails due to server error, try also normal fixture endpoint
* Update: default timeout for max recovery time set to 1h
* Fix: Properly fetch multiple languages for categories
* other minor improvements and bug fixes

**2.0.49.0 (2021-07-23)**
* Extended Stage with getStatus returning StageStatus (with match status)

**2.0.48.0 (2021-06-23)**
* Added OperationManager to provide option to set sdk values (before OddsFeed instance creation)
* Added option to ignore sport event status from timeline endpoint for BetPal events

**2.0.47.0 (2021-06-15)**
* Added pitcher, batter, pitch_count, pitches_seen, total_hits, total_pitches to SportEventStatus properties
* PeriodScore - match status code 120 mapped to penalties
* Connecting to rabbit with the newest supported SSL protocol
* Fix: Re-fetching variant market descriptions after deleting the SDK cache for an event
* Fix: merging tournament groups

**2.0.46.0 (2021-05-28)**
* OutcomeProbabilities extended with getAdditionalProbabilities
* Extended MarketWithProbabilities with getMarketMetaData
* Extended Tournament with getSchedule

**2.0.45.0 (2021-04-29)**
* Added SportInfoManager.getPeriodStatuses to fetch period summary for stages 
* Extended CompetitionStatus with getPeriodOfLadder
  
**2.0.44.2 (2021-04-09)**
* fix: converting GoalScorer
* fix: importing Venue

**2.0.44.1 (2021-04-02)**
* fix: parsing TeamStatisticsDTO for sportEventStatusDTO

**2.0.44.0 (2021-03-31)**
* added getEventChangeManager() to OddsFeed for a periodical fixture and result change updates
* changed return value type for TimelineEvent.getPlayer() from Player to EventPlayer (breaking change)
* changed return value type for TimelineEvent.getGoalScorer() from Player to GoalScorer (breaking change)
* extended Player with GoalScorer (added getMethod()) used in TimelineEvent
* extended Player with EventPlayer (added getBench()) used in TimelineEvent
* added Competitor.getShortName
* added Fixture.ProducerInfo.isInLiveMatchTracker
* reverted populating Round name, groupName and phaseOrGroupLongName to exactly what is received from API
* added getStartTime, getEndTime and getAamsId to the MarketMetaData
* added PeriodOfLeader to the SportEventStatus.Properties
* updated FixtureChangeType - also if not specified in fixtureChange message returns FixtureChangeType.NotAvailable
* improved connection error reporting 
* fix: WNS event ids can have negative value
* fix: NPE merging tournament groups
* fix: merging tournament groups (when no name or id)
* fix: exporting/importing data

**2.0.43.0 (2021-02-09)**
* added support for eSoccer - returns SoccerEvent instead of Match
* added SportsInfoManager.getLotteries
* added support for simple_team urn
* improved example to support all desired locales
* fix: merging tournament groups
* fix: merging tournament data
* fix: NPE in data router fixture handling

**2.0.42.1 (2020-12-18)**
* fix: NPE in case of missing market description
* improved logging

**2.0.42.0 (2020-11-13)**
* added new stage types in StageType enum (Practice, Qualifying, QualifyingPart, Lap)
* fix: use competitor data from fixture.xml API endpoint
* fix: loading sportEventType and stageType from parent stage if available
* fix: parsing outcomes
* fix: restarting recovery timestamp fixed when alive violation happened during previous one

**2.0.41.0 (2020-10-13)**
* Round - added getGroup, added getGroupName (breaking change)
* Stage - added getAdditionalParentStages, getStageType (breaking change - result changed from SportEventType to
 StageType)
* extended EventResult with getDistance and getCompetitorResult methods
* Competition extended with getLiveOdds and getSportEventType methods
* added getCourse to the Venue
* added getCoverage to Match
* optimized message deserialization
* optimized fetching outcome names for multiple languages
* improved connection error handling and reporting
* improved fetching of multiple locales for market names
* fix: allow null values when fetching multiple locales for market names
* fix: added support for indonesian locale
* fix: event status enumeration

**2.0.40.1 (2020-09-22)**
* added MaxConnPerRoute and MaxConnTotal configuration for HTTP client

**2.0.40 (2020-08-19)**
* extended SeasonInfo with startDate, endDate, year and tournamentId
* fix: CustomBetManager return new CustomBetSelectionBuilder
* fix: make serialize synchronised
* fix: fetching market description are marked done only after successful obtaining data
* fix: Immutable map exception parsing competitor references
* fix: un-needed URN parse exception logging
* fix: using ReplayOddsFeed with integration token
* fix: how after parameter is sent in getFixtureChanges 
* fix: reloading market description in case variant descriptions are not available
* improved logging of initial message processing

**2.0.39 (2020-07-09)**
* added getSport() and getCategory() to Competitor interface
* throttling recovery requests
* fix: closing recovery HTTP client on OddsFeed.close()

**2.0.38 (2020-06-24)**
* added overloaded methods for fixture and result changes with filters
* added separate HTTP request timeout value for recovery endpoints
* updated supported languages
* improved reporting of invalid message interest combinations
* fix: NPE when obtaining market descriptions

**2.0.37 (2020-05-11)**
* added support for result changes endpoint
* fix: responses are encoded/decoded using UTF-8
* fix: lotteries endpoint isn't invoked if WNS is disabled

**2.0.36 (2020-04-16)**
* added getSchedule method to BasicTournament interface
* added bookmakerId to the ClientProperties
* fix: new Replay API endpoint supports both production and integration tokens
* fix: exporting Fixture cache items

**2.0.35.1 (2020-03-25)**
* changed Replay API URL

**2.0.35 (2020-03-23)**
* added support for HTTP(S) proxies

**2.0.34 (2020-03-16)**
* fix: fetching outcome mappings
* fix: loading sport events after cache import
* improved HTTP request logging
* improved handling user exceptions inside listener callbacks

**2.0.33 (2020-02-18)**
* added getState() to the Competitor
* added getState() to the Venue
* improved fetching fixtures for Replay environment
* improved loading configuration from files
* fix: handling sports without tournaments

**2.0.32 (2020-01-14)**
* added new Replay API endpoints
* improved market and variant caching
* fix: fetching outcome mappings for special markets that exists only on dynamic variant endpoint

**2.0.31.1 (2019-12-10)**
* fix: fetching and merging player profile data

**2.0.31 (2019-12-09)**
* added getReplaySportEventsList to the ReplayManager
* added missing summary endpoint competitors caching
* added example for parsing messages in separate thread
* improved logic for getting player profiles
* improved fetching and loading PeriodStatistics
* fix: loading playerprops variant market descriptions
* fix: logging for HTTP POST requests

**2.0.30 (2019-11-08)**
* fix: better market description cache handling

**2.0.29 (2019-10-24)**
* added cache state export/import
* added getAdditionalProbabilities() to the OutcomesOdds
* added getAgeGroup to the Competitor
* added getGreenCards() to the TeamStatistics
* added Statistics to the SportEventStatus.toKeyValueStore()
* improved parsing dates without timezone
* fix: possible multi-thread issues caching competitors
* fix: Manager.getNationality returned name
* fix: replay ignores messages from inactive producers
* fix: odds format is now consistent with the documentation
* fix: green card can be null in sport event statistics

**2.0.28 (2019-09-05)**
* exposed option to delete old matches from cache (SportInfoManager.deleteSportEventsFromCache)
* loading home and away penalty score from penalty PeriodScore if present
* fix: updated CustomConfigurationBuilder not to override pre-configured values
* fix: interrupted recoveries are restarted on alive message
* fix: SportInfoManager.getCompetitonsFor(Date, Locale) invokes for correct locale

**2.0.27 (2019-07-18)**
* added getListOfSportEvents and getAvailableTournaments to the SportInfoManager
* added isDecidedByFed to the MatchStatus
* added getRaceDriver to the Competitor
* added isExhibitionGames to the BasicTournament and Tournament
* added getId to the Group
* added getName to the TeamStatistics
* fix: not loading variant market data in multi-language scenario

**2.0.26 (2019-06-21)**
* added isStartTimeTbd and getReplacedBy to the SportEvent
* added getStripesColor, getSplitColor, getShirtType and getSleeveDetail to the Jersey
* added getMatchStatusCode to PeriodScore
* improved on updating when new outcomes are available (outrights)
* exposed option for user to receive raw feed and api data

**2.0.25 (2019-06-07)**
* added getDivision to the TeamCompetitor
* added getGender to the Competitor and PlayerProfile
* added getStreamUrl to the TvChannel
* added getPhase to the Round
* CompetitionStatus.getStatus() no more deprecated (fixed underlining issue)
* improved caching of variant market descriptions
* fix: wrong periodType in PeriodScore

**2.0.24 (2019-05-22)**
* added support for custom bets
* added customBetManager and getAvailableLanguages to the OddsFeed
* added setSpecificEventsOnly to the OddsFeedSessionBuilder
* added getFixtureChanges to the SportsInfoManager interface
* exposed option to reload market descriptions
* fix: preserving the order of default and desired locales in configuration
* fix: return cached event status for sport events

**2.0.23 (2019-04-18)**
* exposed Round.getGroupId()
* improved handling of SportEventStatus updates
* fix: incorrect message validation
* fix: initial value for eventStatus

**2.0.22 (2019-04-08)**
* added support for non-cached fixture endpoint
* improved fetching logic for the summary endpoint
* fix: handling pre:outcometext and simpleteam ids in cache
* fix: null check for names in Category and Sport

**2.0.21 (2019-03-12)**
* exposed EventResult.getGrid()
* exposed Reference.getAamsId()
* added support for ReferenceIds for competitors within season/tournament groups
* added support for simpleteam competitors and related API calls
* improved recovery logic on channel disconnect
* improved how type on PeriodScore is loaded
* added runParallel argument on play method on ReplayManager
* fix: null check for names in Category and Sport
* fix: how periods score data is saved and exposed (period type and description was wrong)

**2.0.20 (2019-02-14)**
* exposed getTimestamps() on Message
* added getRecoveryInfo() (info about last recovery) to the Producer
* added support for replay feed to the Feed instance
* fix: MarketMappingDataImpl.canMap - added producerId check
* fix: Sport.getCategories now returns all categories, delayed fetching until needed
* fix: Locking problem with market description fetching.
* fix: null check when mapping CoveredFrom
* fix: added null check in PeriodScoreImpl
* note: artifact moved to maven central

**2.0.19 (2019-01-07)**
* added Fixture.CoverageInfo.CoveredFrom property
* added getOutcomeType method to MarketDefinition (to replace includes_outcomes_of_type)
* fix: Competitor.References - fixture is fetched only if competitor references are explicitly requested by user
* fix: avoiding fetching fixture for BookingStatus when received via schedule

**2.0.18 (2018-12-18)**
* added method getWillBeRestarted() to SnapshotCompleted message
* added OddsGenerationProperties to the OddsChange message
* replay session using any token returns production replay summary endpoint
* added support for custom api hosts (recovery for producers uses custom urls)
* added Season start time and end time to exposed dates
* rename Staging to Integration environment
* other minor fixes and improvements

**2.0.17 (2018-11-16)**
* exposed BookmakerDetails method on OddsFeed - exposing bookmakerId, serverTimeDifference and token expiration date
* removed fetching of fixture for TournamentRound (only summary, if needed)
* improved handling of competitor reference id(s)
* minimized rest api calls - removed calls for eng, when not needed (requires language to be set in configuration)
* OutcomeSettlement default deadHeatFactor set to 1 (before 0)
* removed purging of sportEventStatus on betSettlement message
* fix: null bug for nodeId in configuration
* fix: the ordinal name spelling for all languages

**2.0.16 (2018-10-17)**
* added Competition.getStatusIfPresent() to retrieve if from cache without API call
* added Match.getEventTimelineIfPresent() to retrieve if from cache without API call
* added properties to TimelineEvent (goalScorer, player, assists,...)
* added warn message for negative nodeId (negative id is reserved for internal use only)
* improvement: added a dedicated executor for the RecoveryManager
* fix: SportEventStatusCache considers source of status and cache timeout
* fix: for null competitor abbreviation (must not be null)
* fix: calling the summary with nodeId if specified (on replay server) - sport event status changes during replay of the match

**2.0.15 (2018-09-17)**
* added new event status: EventStatus.Interrupted
* updated api xsd schemas
* added handling of PlayerCompetitor available in Competitor.Players
* changed return type of method Competitor.getPlayers() to list of Player (to support players and competitors)
* sport event EventStatus exposed directly on Competition.getEventStatus()
* optimized data fetching on sport event (data from schedule for a day is not fetched again)

**2.0.14 (2018-08-24)**
* added support for getting outcome odds in different formats - getOdds(OddsDisplayType)
* AMQP threads named based on Bookmaker/nodeId
* fix: added missing support for node_id on snapshot_complete routing keys
* fix: added missing null checks for getMappings
* fix: added null check when fetching sportId

**2.0.13 (2018-08-02)**
* fix: competitor references are saved per sport event

**2.0.12 (2018-07-25)**
* added check for feed opened before closing its components
* added purging of Draw event data on BetStop and BetSettlement
* added support for 2 routing keys for VirtualSports message interest
* added support for new producer field (stateful_recovery_window_in_minutes)
* added Pitcher info on SportEventConditions

**2.0.11 (2018-06-26)**
* introduced a new "UnparsableMessage" callback
* increased max concurrent HTTP requests to 15(from 2)
* fixed market cache refresh failure retry
* added support for custom API IP(with custom port)
* other minor fixes

**2.0.10 (2018-06-11)**
* exposure of fixture.getScheduledStartTimeChanges
* exposure of Reference.getRotationNumber
* fixed OutcomeMappingData.getProducerOutcomeId return value
* fixed Competition.getVenue return value

**2.0.9 (2018-05-22)**
* market/outcome name generation performance improvement(if the required data is cached)
* fixed Stage.getParent(in some cases the "middle" parent stage was skipped and the method returned always the "top" level parent)
* event competitors list cache logic updated(always overwrite instead of merge)
* simpleteam caching logic update
* log entries improved

**2.0.8 (2018-05-07)**
* introduced the new SDKProducerStatusListener.onProducerStatusChange in favour of onProducerDown and onProducerUp
* added support for node id on per event recoveries
* fixed ReplayManager.getPlayStatus
* removed unneeded YAML reader warning
* fixed system time validation(some locales had issues while parsing the server response time)

**2.0.7 (2018-04-16)**
* exposure of fixture.getReplacedBy
* exposure of Venue.getCountryCode
* exposure of Round.getPhaseOrGroupLongName
* sport event status caching improved
* improved alive messages monitoring
* added system time validation against API time
* added support for YAML configuration
* other logic & code improvements

**2.0.6 (2018-03-28)**
* exposure of fixture.getStartTimeTbd
* added support for decimal results on "EventResult"(Stage entity results)
* added the possibility to set a custom messaging virtual host on the CustomConfigurationBuilder
* added sample SDK properties to the included example
* added support to purge sport event status on demand(trough SportsInfoManager)
* improved handling of "simpleteam" entities
* when a match gets booked successfully, the SDK now updates the internal state of the booking status
* minor fixes and improvements

**2.0.5 (2018-03-13)**
* configuration build process refactored, previous build path was deprecated!
* added a new market type - MarketCancel, accessible trough the BetCancel message
* addition of the EventStatus.Postponed enum value
* match status fetching logic improvements
* fixed flex score market name generation(0.0 -> 0, 1.0 -> 1,...)
* fixture.getNextLiveTime date parsing fixed
* improved handling of malformed market specifiers
* added the possibility to get adjusted market mappings
* added additional features to the ReplayManager play & playScenario methods
* added support for using staging tokens on the replay server
* added advanced replay event scenarios(available in the example project)
* minor improvements on the feed session message interests checking
* example projects updated to use staging as default environment
* default staging broker URL fixed
* log fixes and improvements

**2.0.4 (2018-02-20)**
* exposure of additional information on the fixture
* added SDK support for MDC context tag(uf-sdk-tag)
* improved recovery id generation logic
* fixed ReplayManager.addSportEventToReplay method
* improved WNS lotteries endpoint handling
* minor fixes

**2.0.3 (2018-01-29)**
* added support for the WNS producer
* addition of "OutcomeResult" to OutcomeSettlement
* OutcomeSettlement.isWinning deprecated in favour of "OutcomeResult"
* entities ordinal name generation fix
* other minor improvements and bug fixes

**2.0.2 (2018-01-15)**
* added support for replay server node id
* added support for seamless access to staging env(trough the configuration)
* extended support for SDK configuration trough the properties file
* exposure of named value ids(void reasons, betstop reason,...)
* events cache improvements
* variant markets caching update

**2.0.1 (2017-12-22)**
* modification of the variant market name generation
* introduced new fixture change types(the new types previously mapped to "OTHER")
* further improvements on the cache loading system
* added support for $event name template
* added support for negative SDK node id
* added additional replay server features(ReplayManager methods update)
* default replay queue clearing removed
* MarketMappingData.getProducerId deprecated, replaced with MarketMappingData.getProducerIds
* PeriodStatistics.getTeamStatisticDTOS deprecated, replaced with PeriodStatistics.getTeamStatistics
* EventClock.getRemainingDate deprecated, replaced with EventClock.getRemainingTime
* other minor fixes/improvements/Javadoc fixes

**2.0.0 - STABLE (2017-11-28)**
* modification of the variant market name generation
* addition of the Match.getEventTimeline
* manual cache purge methods exposure
* added support for LiveOdds booking
* addition of the Tournament.getSeasons
* additional player & competitor properties exposure
* SDK node id support
* javadoc fixes
* improvements (tournament round loading, additional data exposure,..)
* log improvements (API response messages, caching,..)
* bug fixes
* examples update

**2.0.0 - BETA-4 (2017-11-06)**
* SportEvent hierarchy overhaul - 'stage' support
* added support for probability endpoints
* improvements (player markets optimizations,..)
* bug fixes

**2.0.0 - BETA-3 (2017-10-09)**
* further recovery process improvements
* message validation
* virtual sports support
* addition of CurrentSeason.getSchedule()
* added additional examples
* added support for %server market name template
* various optimization to reduce GC impact on performance
* logs improvements
* other bug fixes
* minor Javadoc fixes

**2.0.0 - BETA-2 (2017-09-18)**
* variant market name generation fix
* introduction of new ProducerDownReason & ProducerUpReason enum values
* added support for specific event recovery
* added a new method on the Producer object, getTimestampForRecovery - the return value of this method should be used to initiate a SDK recovery after a manual restart
* improved support for event name generation(SportEvent.getName(Locale))
* introduction of a sport specific type object -> SoccerEvent
* **breaking change** the SportEventStatus was replaced with the CompetitionStatus(added support for soccer statistics,...)
* **breaking change** introduced a new type of SportEvent called LongTerm event which is than extended by Tournament and Season
* recovery procedure improvements
* stability improvements
* improvement of various log entries
* Javadoc improvements & fixes
* other bug fixes

**2.0.0.0 - BETA (2017-08-28)**
* Data translation fixes
* Improved recovery manager
* Improved Javadoc
* Improved logs
* Spelling corrections
* PlayerOutcomeOdds introduction
* Re-introduction of the enum PeriodType
* SportsInfo interface renamed to SportsInfoManager
* Replay feed initialization procedure updated
* Added support for accessing sports without tournaments (SportsInfoManager.getSports())
* And other fixes
* NOTE: There will be minor changes in the SportEventStatus interface(including the addition of match statistics)

**2.0.0.0 - ALPHA (2017-07-25)**
* Initial release
