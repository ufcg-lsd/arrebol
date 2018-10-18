package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskState;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.SimpleTaskQueueProcessor;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.MatchedTask;
import org.fogbowcloud.arrebol.core.scheduler.task_queue_processor.TaskQueueProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourceObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StandardScheduler implements Scheduler, ResourceObserver {

    private TasksMonitor tasksMonitor;

    private List<Task> pendingTasks;
    private List<Resource> freeResources;
    private ConcurrentHashMap<String, Task> taskPool;

    private TaskQueueProcessor taskQueueProcessor;

    public StandardScheduler(TasksMonitor tasksMonitor) {
        this.tasksMonitor = tasksMonitor;

        this.pendingTasks = new ArrayList<Task>();
        this.freeResources = new ArrayList<Resource>();
        this.taskPool = new ConcurrentHashMap<String, Task>();

        this.taskQueueProcessor = new SimpleTaskQueueProcessor();
    }

    private MatchedTask pickTaskToRun() {
        // questions:
        // 1) how decide when a task can be submitted to a specific resource?
        //    option A: if resource has the specification for that task, match them
        //      probl: give to a small task a big resource
        //    option B: sort task and resource and try matche big with big, small with smal..
        //      probl: and if list of tasks can be priority?
        // solution: delegated to TaskQueueProcessor object
        return this.taskQueueProcessor.pickTaskToRun(this.pendingTasks, this.freeResources);
    }

    /**
     * Put pending tasks to run on resources.
     * The decision of what tasks choose to run os free resources is delegated to a TaskQueueProcessor object
     * thrugh the call for pickTaskToRun method.
     */
    private void actOnResources() {
        MatchedTask matchedTask = pickTaskToRun();
        while (matchedTask.getTask() != null) { // while there are tasks matched with resources
            runTask(matchedTask.getTask(), matchedTask.getResource());
            matchedTask = pickTaskToRun();
        }
    }

    public void runTask(Task task, Resource resource) {
        this.pendingTasks.remove(task);
        this.freeResources.remove(resource);

        // taskMonitor is the responsible for changing task state and resource state
        this.tasksMonitor.runTask(task, resource);
    }

    public void stopTask(Task task) {
        task.setState(TaskState.CLOSED); // check if it is better create another possible state (asks Fubica)
        this.tasksMonitor.stopTask(task);
    }

    public void addTask(Task task) {
        this.taskPool.put(task.getId(), task);
        this.pendingTasks.add(task);

        actOnResources();
    }

    public List<Task> getPendingTasks() {
        return this.pendingTasks;
    }

    public void update(Resource r) {
        this.freeResources.add(r);

        actOnResources();
    }
}
