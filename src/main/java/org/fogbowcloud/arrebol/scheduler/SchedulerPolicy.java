package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.resource.WorkerPool;

import java.util.Collection;

public interface SchedulerPolicy {

    Collection<AllocationPlan> schedule(TaskQueue queue, WorkerPool pool);
}
