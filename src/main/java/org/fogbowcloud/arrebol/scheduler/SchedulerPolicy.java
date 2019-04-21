package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.resource.ResourcePool;

import java.util.Collection;

public interface SchedulerPolicy {

    Collection<AllocationPlan> schedule(JobQueue queue, ResourcePool pool);
}
