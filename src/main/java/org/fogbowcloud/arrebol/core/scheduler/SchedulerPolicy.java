package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.Allocation;
import org.fogbowcloud.arrebol.pools.ResourcePool;

import java.util.Collection;

public interface SchedulerPolicy {

    Collection<Allocation> schedule(JobQueue queue, ResourcePool pool);
}
