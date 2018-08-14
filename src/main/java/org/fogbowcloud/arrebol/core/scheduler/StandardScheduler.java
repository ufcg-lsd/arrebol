package org.fogbowcloud.arrebol.core.scheduler;

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

    public Task pickTaskToRun() {
        return null; //TODO
    }

    private void actOnResources() {
        // TODO
        // iterave over pending tasks and free resources and match their specifications
        // if matched, submit task to taskMonitor (runTask())

        // questions:
        // 1) how decide when a task can be submitted to a specific resource?
        //    option A: if resource has the specification for that task, match them
        //      probl: give to a small task a big resource
        //    option B: sort task and resource and try matche big with big, small with smal..
        //      probl: and if list of tasks can be priority?
    }

    public void runTask(Task task) {
        // TODO
        this.taskProcessor.runTask(task);
    }

    public void stopTask(Task task) {
        // TODO
        this.taskProcessor.stopTask(task);
    }

    public void addTask(Task task) {
        // TODO
        // check if there are free resources that matches the task specification
        //   if yes, run task.
        //   if not:
        this.pedingTasks.add(task);
    }

    public List<Task> getPendingTasks() {
        return this.pedingTasks;
    }

    public void update(Resource r) {
        this.freeResources.add(r);

        actOnResources();
    }
}
