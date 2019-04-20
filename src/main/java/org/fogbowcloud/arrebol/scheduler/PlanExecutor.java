package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.scheduler.task_queue_processor.AllocationPlan;

import java.util.Collection;

public interface PlanExecutor {

    void execute (Collection<AllocationPlan> plans);
}
