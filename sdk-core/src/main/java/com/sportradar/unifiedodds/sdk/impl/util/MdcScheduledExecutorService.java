/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.util;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

@SuppressWarnings({ "LambdaBodyLength", "LineLength" })
public class MdcScheduledExecutorService implements ScheduledExecutorService {

    /**
     * A {@link Logger} instance used for logging
     */
    private static Logger logger = LoggerFactory.getLogger(MdcScheduledExecutorService.class);

    /**
     * A {@link ScheduledExecutorService} wrapped by the current {@link MdcScheduledExecutorService} instance
     */
    private final ScheduledExecutorService scheduledExecutor;

    /**
     * The MDC fixed context used by current executor
     */
    private final Map<String, String> fixedContext;

    /**
     * Initializes a new instance of the {@link MdcScheduledExecutorService} class
     * @param scheduledExecutor A {@link ScheduledExecutorService} wrapped by the current {@link MdcScheduledExecutorService} instance
     * @param fixedContext The MDC fixed context used by current executor
     */
    public MdcScheduledExecutorService(
        ScheduledExecutorService scheduledExecutor,
        Map<String, String> fixedContext
    ) {
        Preconditions.checkNotNull(scheduledExecutor, "scheduledExecutor cannot be a null reference");
        Preconditions.checkNotNull(fixedContext, "fixedContext cannot be a null reference");

        this.scheduledExecutor = scheduledExecutor;
        this.fixedContext = fixedContext;
    }

    /**
     * Creates and executes a one-shot action that becomes enabled
     * after the given delay.
     *
     * @param command the task to execute
     * @param delay   the time from now to delay execution
     * @param unit    the time unit of the delay parameter
     * @return a ScheduledFuture representing pending completion of
     *         the task and whose <tt>get()</tt> method will return
     *         <tt>null</tt> upon completion
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if the task cannot be
     *                              scheduled for execution
     * @throws NullPointerException if command is null
     */
    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        logger.info("schedule runnable");
        return scheduledExecutor.schedule(setMdcContext(command, fixedContext), delay, unit);
    }

    /**
     * Creates and executes a ScheduledFuture that becomes enabled after the
     * given delay.
     *
     * @param callable the function to execute
     * @param delay    the time from now to delay execution
     * @param unit     the time unit of the delay parameter
     * @return a ScheduledFuture that can be used to extract result or cancel
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if the task cannot be
     *                              scheduled for execution
     * @throws NullPointerException if callable is null
     */
    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        logger.info("schedule callable");
        return scheduledExecutor.schedule(setMdcContext(callable, fixedContext), delay, unit);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the given
     * period; that is executions will commence after
     * <tt>initialDelay</tt> then <tt>initialDelay+period</tt>, then
     * <tt>initialDelay + 2 * period</tt>, and so on.
     * If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.  If any execution of this task
     * takes longer than its period, then subsequent executions
     * may start late, but will not concurrently execute.
     *
     * @param command      the task to execute
     * @param initialDelay the time to delay first execution
     * @param period       the period between successive executions
     * @param unit         the time unit of the initialDelay and period parameters
     * @return a ScheduledFuture representing pending completion of
     *         the task, and whose <tt>get()</tt> method will throw an
     *         exception upon cancellation
     * @throws java.util.concurrent.RejectedExecutionException
     *                                  if the task cannot be
     *                                  scheduled for execution
     * @throws NullPointerException     if command is null
     * @throws IllegalArgumentException if period less than or equal to zero
     */
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(
        Runnable command,
        long initialDelay,
        long period,
        TimeUnit unit
    ) {
        logger.info("schedule runnable at fixed rate");
        return scheduledExecutor.scheduleWithFixedDelay(
            setMdcContext(command, fixedContext),
            initialDelay,
            period,
            unit
        );
    }

    /**
     * Creates and executes a periodic action that becomes enabled first
     * after the given initial delay, and subsequently with the
     * given delay between the termination of one execution and the
     * commencement of the next.  If any execution of the task
     * encounters an exception, subsequent executions are suppressed.
     * Otherwise, the task will only terminate via cancellation or
     * termination of the executor.
     *
     * @param command      the task to execute
     * @param initialDelay the time to delay first execution
     * @param delay        the delay between the termination of one
     *                     execution and the commencement of the next
     * @param unit         the time unit of the initialDelay and delay parameters
     * @return a ScheduledFuture representing pending completion of
     *         the task, and whose <tt>get()</tt> method will throw an
     *         exception upon cancellation
     * @throws java.util.concurrent.RejectedExecutionException
     *                                  if the task cannot be
     *                                  scheduled for execution
     * @throws NullPointerException     if command is null
     * @throws IllegalArgumentException if delay less than or equal to zero
     */
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(
        Runnable command,
        long initialDelay,
        long delay,
        TimeUnit unit
    ) {
        logger.info("schedule runnable with fixed delay");
        return scheduledExecutor.scheduleWithFixedDelay(
            setMdcContext(command, fixedContext),
            initialDelay,
            delay,
            unit
        );
    }

    /**
     * Initiates an orderly shutdown in which previously submitted
     * tasks are executed, but no new tasks will be accepted.
     * Invocation has no additional effect if already shut down.
     * <p/>
     * <p>This method does not wait for previously submitted tasks to
     * complete execution.  Use {@link #awaitTermination awaitTermination}
     * to do that.
     *
     * @throws SecurityException if a security manager exists and
     *                           shutting down this ExecutorService may manipulate
     *                           threads that the caller is not permitted to modify
     *                           because it does not hold {@link
     *                           RuntimePermission}<tt>("modifyThread")</tt>,
     *                           or the security manager's <tt>checkAccess</tt> method
     *                           denies access.
     */
    @Override
    public void shutdown() {
        scheduledExecutor.shutdown();
    }

    /**
     * Attempts to stop all actively executing tasks, halts the
     * processing of waiting tasks, and returns a list of the tasks
     * that were awaiting execution.
     * <p/>
     * <p>This method does not wait for actively executing tasks to
     * terminate.  Use {@link #awaitTermination awaitTermination} to
     * do that.
     * <p/>
     * <p>There are no guarantees beyond best-effort attempts to stop
     * processing actively executing tasks.  For example, typical
     * implementations will cancel via {@link Thread#interrupt}, so any
     * task that fails to respond to interrupts may never terminate.
     *
     * @return list of tasks that never commenced execution
     * @throws SecurityException if a security manager exists and
     *                           shutting down this ExecutorService may manipulate
     *                           threads that the caller is not permitted to modify
     *                           because it does not hold {@link
     *                           RuntimePermission}<tt>("modifyThread")</tt>,
     *                           or the security manager's <tt>checkAccess</tt> method
     *                           denies access.
     */
    @Override
    public List<Runnable> shutdownNow() {
        return scheduledExecutor.shutdownNow();
    }

    /**
     * Returns <tt>true</tt> if this executor has been shut down.
     *
     * @return <tt>true</tt> if this executor has been shut down
     */
    @Override
    public boolean isShutdown() {
        return scheduledExecutor.isShutdown();
    }

    /**
     * Returns <tt>true</tt> if all tasks have completed following shut down.
     * Note that <tt>isTerminated</tt> is never <tt>true</tt> unless
     * either <tt>shutdown</tt> or <tt>shutdownNow</tt> was called first.
     *
     * @return <tt>true</tt> if all tasks have completed following shut down
     */
    @Override
    public boolean isTerminated() {
        return scheduledExecutor.isTerminated();
    }

    /**
     * Blocks until all tasks have completed execution after a shutdown
     * request, or the timeout occurs, or the current thread is
     * interrupted, whichever happens first.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return <tt>true</tt> if this executor terminated and
     *         <tt>false</tt> if the timeout elapsed before termination
     * @throws InterruptedException if interrupted while waiting
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return scheduledExecutor.awaitTermination(timeout, unit);
    }

    /**
     * Submits a value-returning task for execution and returns a
     * Future representing the pending results of the task. The
     * Future's <tt>get</tt> method will return the task's result upon
     * successful completion.
     * <p/>
     * <p/>
     * If you would like to immediately block waiting
     * for a task, you can use constructions of the form
     * <tt>result = exec.submit(aCallable).get();</tt>
     * <p/>
     * <p> Note: The {@link java.util.concurrent.Executors} class includes a set of methods
     * that can convert some other common closure-like objects,
     * for example, {@link java.security.PrivilegedAction} to
     * {@link java.util.concurrent.Callable} form so they can be submitted.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if the task cannot be
     *                              scheduled for execution
     * @throws NullPointerException if the task is null
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        logger.info("submit callable");
        return scheduledExecutor.submit(task);
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's <tt>get</tt> method will
     * return the given result upon successful completion.
     *
     * @param task   the task to submit
     * @param result the result to return
     * @return a Future representing pending completion of the task
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if the task cannot be
     *                              scheduled for execution
     * @throws NullPointerException if the task is null
     */
    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        logger.info("submit runnable with result");
        return scheduledExecutor.submit(task, result);
    }

    /**
     * Submits a Runnable task for execution and returns a Future
     * representing that task. The Future's <tt>get</tt> method will
     * return <tt>null</tt> upon <em>successful</em> completion.
     *
     * @param task the task to submit
     * @return a Future representing pending completion of the task
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if the task cannot be
     *                              scheduled for execution
     * @throws NullPointerException if the task is null
     */
    @Override
    public Future<?> submit(Runnable task) {
        logger.info("submit runnable");
        return scheduledExecutor.submit(task);
    }

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results when all complete.
     * {@link java.util.concurrent.Future#isDone} is <tt>true</tt> for each
     * element of the returned list.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks the collection of tasks
     * @return A list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list, each of which has completed.
     * @throws InterruptedException if interrupted while waiting, in
     *                              which case unfinished tasks are cancelled.
     * @throws NullPointerException if tasks or any of its elements are <tt>null</tt>
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if any task cannot be
     *                              scheduled for execution
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
        logger.info("invoke all callables");
        return scheduledExecutor.invokeAll(tasks);
    }

    /**
     * Executes the given tasks, returning a list of Futures holding
     * their status and results
     * when all complete or the timeout expires, whichever happens first.
     * {@link java.util.concurrent.Future#isDone} is <tt>true</tt> for each
     * element of the returned list.
     * Upon return, tasks that have not completed are cancelled.
     * Note that a <em>completed</em> task could have
     * terminated either normally or by throwing an exception.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks   the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return a list of Futures representing the tasks, in the same
     *         sequential order as produced by the iterator for the
     *         given task list. If the operation did not time out,
     *         each task will have completed. If it did time out, some
     *         of these tasks will not have completed.
     * @throws InterruptedException if interrupted while waiting, in
     *                              which case unfinished tasks are cancelled
     * @throws NullPointerException if tasks, any of its elements, or
     *                              unit are <tt>null</tt>
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if any task cannot be scheduled
     *                              for execution
     */
    @Override
    public <T> List<Future<T>> invokeAll(
        Collection<? extends Callable<T>> tasks,
        long timeout,
        TimeUnit unit
    ) throws InterruptedException {
        logger.info("invoke all callables with time-out");
        return scheduledExecutor.invokeAll(tasks, timeout, unit);
    }

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do. Upon normal or exceptional return,
     * tasks that have not completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks the collection of tasks
     * @return the result returned by one of the tasks
     * @throws InterruptedException     if interrupted while waiting
     * @throws NullPointerException     if tasks or any element task
     *                                  subject to execution is <tt>null</tt>
     * @throws IllegalArgumentException if tasks is empty
     * @throws java.util.concurrent.ExecutionException
     *                                  if no task successfully completes
     * @throws java.util.concurrent.RejectedExecutionException
     *                                  if tasks cannot be scheduled
     *                                  for execution
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
        logger.info("invoke any callable");
        return scheduledExecutor.invokeAny(tasks);
    }

    /**
     * Executes the given tasks, returning the result
     * of one that has completed successfully (i.e., without throwing
     * an exception), if any do before the given timeout elapses.
     * Upon normal or exceptional return, tasks that have not
     * completed are cancelled.
     * The results of this method are undefined if the given
     * collection is modified while this operation is in progress.
     *
     * @param tasks   the collection of tasks
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result returned by one of the tasks.
     * @throws InterruptedException if interrupted while waiting
     * @throws NullPointerException if tasks, or unit, or any element
     *                              task subject to execution is <tt>null</tt>
     * @throws java.util.concurrent.TimeoutException
     *                              if the given timeout elapses before
     *                              any task successfully completes
     * @throws java.util.concurrent.ExecutionException
     *                              if no task successfully completes
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if tasks cannot be scheduled
     *                              for execution
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
        logger.info("invoke any with time-out");
        return scheduledExecutor.invokeAny(tasks, timeout, unit);
    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the <tt>Executor</tt> implementation.
     *
     * @param command the runnable task
     * @throws java.util.concurrent.RejectedExecutionException
     *                              if this task cannot be
     *                              accepted for execution.
     * @throws NullPointerException if command is null
     */
    @Override
    public void execute(Runnable command) {
        logger.info("execute");
        scheduledExecutor.execute(command);
    }

    private static <V> Callable<V> setMdcContext(final Callable<V> callable, final Map context) {
        return () -> {
            Map oldContext = MDC.getCopyOfContextMap();
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                return callable.call();
            } finally {
                if (oldContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(oldContext);
                }
            }
        };
    }

    /**
     * Sets the MDC context on the thread executing the task and cleans the MDC when completed
     */
    private static Runnable setMdcContext(final Runnable runnable, final Map context) {
        return () -> {
            Map oldContext = MDC.getCopyOfContextMap();
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            try {
                runnable.run();
            } finally {
                if (oldContext == null) {
                    MDC.clear();
                } else {
                    MDC.setContextMap(oldContext);
                }
            }
        };
    }
}
