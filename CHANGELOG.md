### Unified Feed SDK 2.x changelog

**2.0.17 (2018-11-16)**
* Exposed BookmakerDetails method on OddsFeed - exposing bookmakerId, serverTimeDifference and token expiration date
* Removed fetching of fixture for TournamentRound (only summary, if needed)
* Improved handling of competitor reference id(s)
* Minimized rest api calls - removed calls for eng, when not needed (requires language to be set in configuration)
* OutcomeSettlement default deadHeatFactor set to 1 (before 0)
* Removed purging of sportEventStatus on betSettlement message
* Fix: null bug for nodeId in configuration
* Fix: the ordinal name spelling for all languages

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