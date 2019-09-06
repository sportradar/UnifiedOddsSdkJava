### Unified Feed SDK 2.x changelog
**2.0.27 (2019-09-05)**
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