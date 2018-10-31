package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.core.monitors.TaskSubmitter;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.SimpleTaskQueueProcessor;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.MatchedTask;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.TaskQueueProcessor;
import org.fogbowcloud.arrebol.core.resource.ResourceObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StandardScheduler implements Scheduler, ResourceObserver {

    private TaskSubmitter taskSubmitter;

    private List<Task> pendingTasks;
    private List<AbstractResource> freeResources;
    private ConcurrentHashMap<String, Task> taskPool;

    private TaskQueueProcessor taskQueueProcessor;

    public StandardScheduler(TaskSubmitter taskSubmitter) {
        this.taskSubmitter = taskSubmitter;

        this.pendingTasks = new ArrayList<Task>();
        this.freeResources = new ArrayList<AbstractResource>();
        this.taskPool = new ConcurrentHashMap<String, Task>();

        this.taskQueueProcessor = new SimpleTaskQueueProcessor();
    }

    private MatchedTask pickTaskToRun() {
        // questions:
        // 1) how decide when a task can be submitted to a specific resource?
        //    option A: if resource has the specification for that task, match them
        //      probl: give to a small task a big resource
        //    option B: sort task and resource and try matche big with big, small with small..
        //      probl: and if list of tasks can be priority?
        // solution: delegated to TaskQueueProcessor object
        return this.taskQueueProcessor.pickTaskToRun(this.pendingTasks, this.freeResources);
    }

    /**
     * Put pending tasks to run on resources.
     * The decision of what tasks choose to run os free resources is delegated to a TaskQueueProcessor object
     * thrugh the call for pickTaskToRun().
     */
    private void actOnResources() {
        MatchedTask matchedTask = pickTaskToRun();
        while (matchedTask.getTask() != null) { // while there are tasks matched with resources
            runTask(matchedTask.getTask(), matchedTask.getResource());
            matchedTask = pickTaskToRun();
        }
    }

    public void stopTask(Task task) {
        task.setState(TaskState.CLOSED); // check if it is better create another possible state (asks Fubica)
        this.taskSubmitter.stopTask(task);
    }

    public void addTask(Task task) {
        this.taskPool.put(task.getId(), task);
        this.pendingTasks.add(task);

        actOnResources();
    }

    public List<Task> getPendingTasks() {
        return this.pendingTasks;
    }

    public void update(AbstractResource r) {
        this.freeResources.add(r);

        actOnResources();
    }

    private void runTask(Task task, AbstractResource resource) {
        this.pendingTasks.remove(task);
        this.freeResources.remove(resource);

        // taskMonitor is the responsible for changing task state and resource state
        this.taskSubmitter.runTask(task, resource);
    }
}
