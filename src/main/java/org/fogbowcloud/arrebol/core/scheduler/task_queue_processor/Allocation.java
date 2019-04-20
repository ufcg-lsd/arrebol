package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.resource.models.Resource;

public class Allocation {

    private Task task;
    private Resource resource;

    public Allocation(Task t, Resource r) {
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
