package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.pool.ResourcePool;

import java.util.ArrayList;
import java.util.List;

public class DefaultScheduler implements Scheduler {

    private List<Task> openTasks = new ArrayList<Task>();

    public DefaultScheduler() {
        this.openTasks = new ArrayList<Task>();
    }

    public void addTask(Task task) {
        this.openTasks.add(task);
    }

    public Task pickTaskForRun() {
        return null; //TODO
    }

    public List<Task> getOpenTasks() {
        return this.openTasks;
    }
}
