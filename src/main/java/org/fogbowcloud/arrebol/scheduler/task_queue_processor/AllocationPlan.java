package org.fogbowcloud.arrebol.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.resource.models.Resource;

public class AllocationPlan {

    private Task task;
    private Resource resource;

    public AllocationPlan(Task t, Resource r) {
        this.task = t;
        this.resource = r;
    }

    public Task getTask() {
        return task;
    }

    public Resource getResource() {
        return resource;
    }
}
