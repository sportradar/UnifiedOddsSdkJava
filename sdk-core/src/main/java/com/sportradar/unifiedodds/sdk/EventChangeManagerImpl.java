package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.entities.FixtureChange;
import com.sportradar.unifiedodds.sdk.entities.ResultChange;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.SportsInfoManagerImpl;
import com.sportradar.utils.URN;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class EventChangeManagerImpl implements EventChangeManager {

    private class EventUpdate{
        URN id;
        Date updated;
        SportEvent sportEvent;
        boolean isFixture;

        public EventUpdate(URN id, Date updated, SportEvent sportEvent, boolean isFixture) {
            this.id = id;
            this.updated = updated;
            this.sportEvent = sportEvent;
            this.isFixture = isFixture;
        }
    }

    private static final Logger executionLogger = LoggerFactory.getLogger(EventChangeManagerImpl.class);
    private static final Logger clientInteractionLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkClientInteractionLog.class);
    private final SDKInternalConfiguration configuration;
    private final SportEventCache sportEventCache;
    private final SportsInfoManagerImpl sportsInfoManager;
    private final ScheduledExecutorService fixtureTaskScheduler;
    private final ScheduledExecutorService resultTaskScheduler;
    private ScheduledFuture<?> fixtureFuture;
    private ScheduledFuture<?> resultFuture;
    private boolean isRunning;
    private EventChangeListener eventChangeListener;
    private Date lastFixtureChange;
    private Date lastResultChange;
    private Duration fixtureUpdateInterval;
    private Duration resultUpdateInterval;
    private List<EventUpdate> eventUpdates;
    private final ReentrantLock fixtureLock = new ReentrantLock();
    private final ReentrantLock resultLock = new ReentrantLock();
    private final ReentrantLock dispatchLock = new ReentrantLock();

    @Inject
    EventChangeManagerImpl(SportsInfoManager sportsInfoManager,
                           SportEventCache sportEventCache,
                           SDKInternalConfiguration configuration) {
        Preconditions.checkNotNull(sportsInfoManager);
        Preconditions.checkNotNull(sportEventCache);
        Preconditions.checkNotNull(configuration);

        this.sportsInfoManager = (SportsInfoManagerImpl) sportsInfoManager;
        this.sportEventCache = sportEventCache;
        this.configuration = configuration;
        this.fixtureTaskScheduler = Executors.newScheduledThreadPool(1);
        this.resultTaskScheduler = Executors.newScheduledThreadPool(1);

        setFixtureChangeInterval(Duration.ofMinutes(60));
        setResultChangeInterval(Duration.ofMinutes(60));
        isRunning = false;
        lastFixtureChange = null;
        lastResultChange = null;
        eventChangeListener = null;
        eventUpdates = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void setListener(EventChangeListener listener) { eventChangeListener = listener; }

    @Override
    public Date getLastFixtureChange() { return lastFixtureChange; }

    @Override
    public Date getLastResultChange() { return lastResultChange; }

    @Override
    public Duration getFixtureChangeInterval() { return fixtureUpdateInterval; }

    @Override
    public Duration getResultChangeInterval() { return resultUpdateInterval; }

    @Override
    public boolean isRunning() { return isRunning; }

    @Override
    public void setFixtureChangeInterval(Duration fixtureChangeInterval) {
        if(fixtureChangeInterval.getSeconds() < 60 || fixtureChangeInterval.getSeconds() > Duration.ofHours(12).getSeconds())
        {
            throw new IllegalArgumentException("Interval must be between 1 minute and 12 hours");
        }

        clientInteractionLogger.info("Setting new fixture change interval to {}s.", fixtureChangeInterval.getSeconds());
        this.fixtureUpdateInterval = fixtureChangeInterval;
        if (isRunning)
        {
            restartScheduler(fixtureTaskScheduler, true);
        }
    }

    @Override
    public void setResultChangeInterval(Duration resultChangeInterval) {
        if(resultChangeInterval.getSeconds() < 60 || resultChangeInterval.getSeconds() > Duration.ofHours(12).getSeconds())
        {
            throw new IllegalArgumentException("Interval must be between 1 minute and 12 hours");
        }

        clientInteractionLogger.info("Setting new result change interval to {}s.", resultChangeInterval.getSeconds());
        this.resultUpdateInterval = resultChangeInterval;
        if (isRunning)
        {
            restartScheduler(resultTaskScheduler, false);
        }
    }

    @Override
    public void setFixtureChangeTimestamp(Date fixtureChangeTimestamp) {
        if (isRunning)
        {
            throw new IllegalArgumentException("Manager must first be stopped.");
        }

        if(fixtureChangeTimestamp.before(Date.from(Instant.now().minus(Duration.ofDays(1)))) || fixtureChangeTimestamp.after(new Date()))
        {
            throw new IllegalArgumentException("Timestamp must be in the last 24 hours.");
        }

        clientInteractionLogger.info("Set LastFixtureChange to {}.", fixtureChangeTimestamp);
        this.lastFixtureChange = fixtureChangeTimestamp;
    }

    @Override
    public void setResultChangeTimestamp(Date resultChangeTimestamp) {
        if (isRunning)
        {
            throw new IllegalArgumentException("Manager must first be stopped.");
        }

        if(resultChangeTimestamp.before(Date.from(Instant.now().minus(Duration.ofDays(1)))) || resultChangeTimestamp.after(new Date()))
        {
            throw new IllegalArgumentException("Timestamp must be in the last 24 hours.");
        }

        clientInteractionLogger.info("Set LastResultChange to {}.", resultChangeTimestamp);
        this.lastResultChange = resultChangeTimestamp;
    }

    @Override
    public void start() {
        if (!isRunning)
        {
            clientInteractionLogger.info("Starting periodical fetching of fixture and result changes.");
            isRunning = true;
            restartScheduler(fixtureTaskScheduler, true);
            restartScheduler(resultTaskScheduler, false);
        }
        else
        {
            clientInteractionLogger.info("Invoking Start of already started process.");
        }
    }

    @Override
    public void stop() {
        if (isRunning)
        {
            clientInteractionLogger.info("Stopping periodical fetching of fixture and result changes.");
            isRunning = false;
        }
        fixtureTaskScheduler.shutdownNow();
        resultTaskScheduler.shutdownNow();
    }

    private void restartScheduler(ScheduledExecutorService service, boolean isFixture) {
        try {
            if(service.isShutdown()){
                if(isFixture) fixtureFuture = null; else resultFuture = null;
                service = Executors.newScheduledThreadPool(1);
            }
            if(isFixture) {
                if(fixtureFuture == null) {
                    fixtureFuture = service.scheduleAtFixedRate(() -> fetchFixtures(), 1, this.fixtureUpdateInterval.getSeconds(), TimeUnit.SECONDS);
                }
                else{
                    fixtureFuture.cancel(false);
//                    if(fixtureLock.isLocked()) {
//                        fixtureLock.unlock();
//                    }
                    fixtureFuture = service.scheduleAtFixedRate(() -> fetchFixtures(), 1, this.fixtureUpdateInterval.getSeconds(), TimeUnit.SECONDS);
                }
            } else{
                if(resultFuture == null) {
                    resultFuture = service.scheduleAtFixedRate(() -> fetchResults(), 1, this.resultUpdateInterval.getSeconds(), TimeUnit.SECONDS);
                }
                else{
                    resultFuture.cancel(false);
//                    if(resultLock.isLocked()) {
//                        resultLock.unlock();
//                    }
                    resultFuture = service.scheduleAtFixedRate(() -> fetchResults(), 1, this.resultUpdateInterval.getSeconds(), TimeUnit.SECONDS);
                }

            }
        }
        catch (Exception ex){
            executionLogger.warn(ex.getMessage());
        }
    }

    private void updateLastFixtureChange(Date newDate)
    {
        if (newDate.after(lastFixtureChange))
        {
            lastFixtureChange = newDate;
        }
    }

    private void updateLastResultChange(Date newDate)
    {
        if (newDate.after(lastResultChange))
        {
            lastFixtureChange = newDate;
        }
    }

    private void fetchFixtures()
    {
        if (!isRunning)
        {
            executionLogger.debug("Invoked fixture change fetch when isRunning=false.");
            return;
        }

        if (eventChangeListener == null)
        {
            executionLogger.debug("Invoked fixture change fetch when no listener specified. Aborting.");
            return;
        }

        fixtureLock.lock();

        if(!isRunning)
        {
            return;
        }

        try
        {
            List<FixtureChange> changes;
            if (lastFixtureChange == null)
            {
                executionLogger.info("Invoking getFixtureChanges. After=null");
                changes = sportsInfoManager.getFixtureChanges(configuration.getDefaultLocale());
            }
            else
            {
                executionLogger.info("Invoking getFixtureChanges. After={}", lastFixtureChange);
                changes = sportsInfoManager.getFixtureChanges(lastFixtureChange, null, configuration.getDefaultLocale());
            }

            if(changes != null){
                changes = changes.stream().sorted(Comparator.comparing(c->c.getUpdateTime().getTime())).collect(Collectors.toList());
            }

            for (FixtureChange fixtureChange : changes)
            {
                if(!isRunning)
                {
                    break;
                }

                EventUpdate eventUpdate = eventUpdates.stream().filter(a -> a.id.equals(fixtureChange.getSportEventId())).findFirst().orElse(null);
                if (eventUpdate != null)
                {
                    if(fixtureChange.getUpdateTime().after(eventUpdate.updated))
                    {
                        eventUpdates.remove(eventUpdate);
                    }
                    else
                    {
                        updateLastFixtureChange(fixtureChange.getUpdateTime());
                        continue;
                    }
                }
                sportEventCache.purgeCacheItem(fixtureChange.getSportEventId());
                SportEvent sportEvent = sportsInfoManager.getSportEventForEventChange(fixtureChange.getSportEventId());
                eventUpdates.add(new EventUpdate(fixtureChange.getSportEventId(), fixtureChange.getUpdateTime(), sportEvent, true));
                updateLastFixtureChange(fixtureChange.getUpdateTime());
            }
        }
        catch (Exception ex)
        {
            executionLogger.error("Error fetching fixture changes. Exception={}", ex.getMessage());
        }

        if(fixtureLock.isLocked()) {
            fixtureLock.unlock();
        }

        dispatchUpdateChangeMessages();
    }

    private void fetchResults()
    {
        if (!isRunning)
        {
            executionLogger.debug("Invoked result change fetch when isRunning=false.");
            return;
        }

        if (eventChangeListener == null)
        {
            executionLogger.debug("Invoked result change fetch when no listener specified. Aborting.");
            return;
        }

        resultLock.lock();

        if(!isRunning)
        {
            return;
        }

        try
        {
            List<ResultChange> changes;
            if (lastResultChange == null)
            {
                executionLogger.info("Invoking getResultChanges. After=null");
                changes = sportsInfoManager.getResultChanges(configuration.getDefaultLocale());
            }
            else
            {
                executionLogger.info("Invoking getResultChanges. After={}", lastFixtureChange);
                changes = sportsInfoManager.getResultChanges(lastFixtureChange, null, configuration.getDefaultLocale());
            }

            if(changes != null){
                changes = changes.stream().sorted(Comparator.comparing(c->c.getUpdateTime().getTime())).collect(Collectors.toList());
            }

            for (ResultChange resultChange : changes)
            {
                if(!isRunning)
                {
                    break;
                }

                EventUpdate eventUpdate = eventUpdates.stream().filter(a -> a.id.equals(resultChange.getSportEventId())).findFirst().orElse(null);
                if (eventUpdate != null)
                {
                    if(resultChange.getUpdateTime().after(eventUpdate.updated))
                    {
                        eventUpdates.remove(eventUpdate);
                    }
                    else
                    {
                        updateLastResultChange(resultChange.getUpdateTime());
                        continue;
                    }
                }
                sportEventCache.purgeCacheItem(resultChange.getSportEventId());
                SportEvent sportEvent = sportsInfoManager.getSportEventForEventChange(resultChange.getSportEventId());
                eventUpdates.add(new EventUpdate(resultChange.getSportEventId(), resultChange.getUpdateTime(), sportEvent, false));
                updateLastResultChange(resultChange.getUpdateTime());
            }
        }
        catch (Exception ex)
        {
            executionLogger.error("Error fetching result changes. Exception={}", ex.getMessage());
        }

        if(resultLock.isLocked()) {
            resultLock.unlock();
        }

        dispatchUpdateChangeMessages();
    }

    private void dispatchUpdateChangeMessages()
    {
        if(eventChangeListener == null)
        {
            return;
        }

        dispatchLock.lock();
        while(!eventUpdates.isEmpty())
        {
            EventUpdate eventUpdate = eventUpdates.get(0);
            String updateStr = eventUpdate.isFixture ? "fixture" : "result";
            try
            {
                clientInteractionLogger.debug("Dispatching {} change [{}] for {}. Updated={}",
                                              updateStr,
                                              eventUpdates.size(),
                                              eventUpdate.id,
                                              eventUpdate.updated);
                if (eventUpdate.isFixture)
                {
                    eventChangeListener.onFixtureChange(eventUpdate.id, eventUpdate.updated, eventUpdate.sportEvent);
                }
                else
                {
                    eventChangeListener.onResultChange(eventUpdate.id, eventUpdate.updated, eventUpdate.sportEvent);
                }

                eventUpdates.remove(eventUpdate);
            }
            catch (Exception exception)
            {
                executionLogger.warn("Error during user processing of event {} change message: {}",
                                     updateStr,
                                     exception.getMessage());
            }
        }
        if(dispatchLock.isLocked()){
            dispatchLock.unlock();
        }
    }
}
