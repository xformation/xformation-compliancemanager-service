/*
 * */
package com.synectiks.process.common.scheduler;

/**
 * Used by the scheduler to configure itself.
 */
public interface JobSchedulerConfig {
    /**
     * Determines if the scheduler can start.
     *
     * @return true if the scheduler can be started, false otherwise
     */
    boolean canStart();

    /**
     * Determines if the scheduler can execute the next loop iteration.
     * This method will be called at the beginning of each scheduler loop iteration so it should be fast!
     *
     * @return true if scheduler can execute next loop iteration
     */
    boolean canExecute();

    /**
     * The number of worker threads to start.
     *
     * @return number of worker threads
     */
    int numberOfWorkerThreads();
}
