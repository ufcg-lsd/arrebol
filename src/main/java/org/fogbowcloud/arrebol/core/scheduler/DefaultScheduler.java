package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.pool.ResourceObserver;
import org.fogbowcloud.arrebol.pool.ResourcePool;

import java.util.ArrayList;
import java.util.List;

public class DefaultScheduler implements Scheduler, ResourceObserver {

    private List<Task> pedingTasks;
    private List<Resource> freeResources;

    public DefaultScheduler() {
        this.pedingTasks = new ArrayList<Task>();
        this.freeResources = new ArrayList<Resource>();
    }

    public void addTask(Task task) {
        this.pedingTasks.add(task);
    }

    public Task pickTaskToRun() {
        return null; //TODO
    }

    public List<Task> getPendingTasks() {
        return this.pedingTasks;
    }

    public void update(Resource r) {
        this.freeResources.add(r);
    }
}
