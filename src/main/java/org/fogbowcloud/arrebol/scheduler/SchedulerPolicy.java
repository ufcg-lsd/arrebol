/* (C)2020 */
package org.fogbowcloud.arrebol.scheduler;

import java.util.Collection;
import org.fogbowcloud.arrebol.processor.TaskQueue;
import org.fogbowcloud.arrebol.resource.WorkerPool;

public interface SchedulerPolicy {

  Collection<AllocationPlan> schedule(TaskQueue queue, WorkerPool pool);
}
