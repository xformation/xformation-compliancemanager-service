/*
 * */
package com.synectiks.process.common.scheduler;

import org.junit.Test;

import com.synectiks.process.common.scheduler.JobSchedulerService.InterruptibleSleeper;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobSchedulerServiceTest {
    @Test
    public void interruptibleSleeper() throws Exception {
        final Semaphore semaphore = spy(new Semaphore(1));
        final InterruptibleSleeper sleeper = new InterruptibleSleeper(semaphore);

        when(semaphore.tryAcquire(1, TimeUnit.SECONDS)).thenReturn(false);
        assertThat(sleeper.sleep(1, TimeUnit.SECONDS)).isTrue();
        verify(semaphore, times(1)).drainPermits();
        verify(semaphore, times(1)).tryAcquire(1, TimeUnit.SECONDS);

        reset(semaphore);

        when(semaphore.tryAcquire(1, TimeUnit.SECONDS)).thenReturn(true);
        assertThat(sleeper.sleep(1, TimeUnit.SECONDS)).isFalse();
        verify(semaphore, times(1)).drainPermits();
        verify(semaphore, times(1)).tryAcquire(1, TimeUnit.SECONDS);

        reset(semaphore);

        sleeper.interrupt();
        verify(semaphore, times(1)).release();
    }
}
