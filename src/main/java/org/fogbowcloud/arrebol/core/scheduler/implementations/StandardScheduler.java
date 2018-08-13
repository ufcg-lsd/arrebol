package org.fogbowcloud.arrebol.core.scheduler.implementations;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.scheduler.Scheduler;
import org.fogbowcloud.arrebol.core.scheduler.TaskProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourceObserver;

import java.util.ArrayList;
import java.util.List;

public class StandardScheduler implements Scheduler, ResourceObserver {

    private TaskProcessor taskProcessor;

    private List<Task> pedingTasks;
    private List<Resource> freeResources;

    public StandardScheduler(TaskProcessor taskProcessor) {
        this.taskProcessor = taskProcessor;

        this.pedingTasks = new ArrayList<Task>();
        this.freeResources = new ArrayList<Resource>();
    }

    private void runTask(Task task) {
        // TODO
        this.taskProcessor.runTask(task);
    }

    private void stopTask(Task task) {
        // TODO
        this.taskProcessor.stopTask(task);
    }

    private void actOnResources() {
        // TODO
        // iterave over pending tasks and free resources and match their specifications
        // if matched, submit task to taskMonitor (runTask())
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

        actOnResources();
    }
}
