/*
    Copyright (C) 2011-2020 Andy Cheung

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License Version 2 only, 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    The copyright holder permits this library as subject to the "Classpath"
    exception as provided below:

        Linking this library statically or dynamically with other modules is
        making a combined work based on this library.  Thus, the terms and
        conditions of the GNU General Public License cover the whole
        combination.

        As a special exception, the copyright holders of this library give you
        permission to link this library with independent modules to produce an
        executable, regardless of the license terms of these independent
        modules, and to copy and distribute the resulting executable under
        terms of your choice, provided that you also meet, for each linked
        independent module, the terms and conditions of the license of that
        module.  An independent module is a module which is not derived from
        or based on this library.  If you modify this library, you may extend
        this exception to your version of the library, but you are not
        obligated to do so.  If you do not wish to do so, delete this
        exception statement from your version.
*/

package ac.adproj.nio.model;

import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The runnable task that executes corresponding completion handler,
 * and a Future implementation to gather its executing status.
 *
 * @param <R> The type of result.
 * @param <A> The type of attachment.
 * @author Andy Cheung
 */
public abstract class AsyncTask<R, A> implements Runnable, Future<R> {
    private final Lock lock;
    private R result;
    private TaskStatus status;
    private CompletionHandler<R, ? super A> handler;
    private A attachment;
    private Thread executingThread;

    private AsyncTask() {
        lock = new ReentrantLock();
        status = TaskStatus.NOT_FINISHED;
    }

    public AsyncTask(A attachment, CompletionHandler<R, ? super A> completionHandler) {
        this();
        this.handler = completionHandler;
        this.attachment = attachment;
    }

    /**
     * The entrance method of executable task.
     */
    @Override
    public void run() {
        executingThread = Thread.currentThread();
        lock.lock();

        try {
            result = execute(attachment);
            status = TaskStatus.FINISHED;
            if (handler != null) {
                handler.completed(result, attachment);
            }
        } catch (Exception e) {
            status = TaskStatus.FAILED;
            if (handler != null) {
                handler.failed(e, attachment);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * The actual running method of the executable task.
     * @param attachment The attachment specified by invoker.
     * @return The result that specified by invoker.
     * @throws Exception If something goes wrong.
     */
    public abstract R execute(A attachment) throws Exception;

    /**
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     *
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete (in this implementation, it will do nothing when it's false).
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        if (mayInterruptIfRunning && !executingThread.isInterrupted()) {
            executingThread.interrupt();
            status = TaskStatus.INTERRUPTED;
        }

        return executingThread.isInterrupted();
    }

    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    @Override
    public boolean isCancelled() {
        return status == TaskStatus.INTERRUPTED;
    }

    /**
     * Returns {@code true} if this task completed.
     * <p>
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this task completed
     */
    @Override
    public boolean isDone() {
        return status == TaskStatus.FINISHED;
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     */
    @Override
    public R get() {
        lock.lock();
        try {
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    @Override
    public R get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        try {
            if (!lock.tryLock(timeout, unit)) {
                throw new TimeoutException();
            }

            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * The asynchronous task status enumeration.
     */
    public enum TaskStatus {
        /**
         * The task is not yet executed.
         */
        NOT_FINISHED,
        /**
         * The task was executed successfully.
         */
        FINISHED,
        /**
         * The task encounters an exception.
         */
        FAILED,
        /**
         * The task was interrupted through cancel() method.
         */
        INTERRUPTED
    }
}
