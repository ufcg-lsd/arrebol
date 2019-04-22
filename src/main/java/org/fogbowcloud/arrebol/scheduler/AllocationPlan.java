package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.resource.Resource;

public class AllocationPlan {

    //TODO: I do not quite remember what the STOP type mean (is it related to failures?!)
    public enum Type {RUN, STOP};

    private Task task;
    private Resource resource;
    private Type type;

    public AllocationPlan(Task t, Resource r, Type type) {
        this.task = t;
        this.resource = r;
        this.type = type;
    }

    public Task getTask() {
        return this.task;
    }

    public Resource getResource() {
        return this.resource;
    }

    public Type getType() {
        return this.type;
    }
}
