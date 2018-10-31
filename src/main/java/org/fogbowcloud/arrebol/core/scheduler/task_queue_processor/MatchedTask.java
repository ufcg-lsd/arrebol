package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;

public class MatchedTask {

    private Task task;
    private AbstractResource resource;

    public MatchedTask(Task t, AbstractResource r) {
        this.task = t;
        this.resource = r;
    }

    public Task getTask() {
        return task;
    }

    public AbstractResource getResource() {
        return resource;
    }
}
