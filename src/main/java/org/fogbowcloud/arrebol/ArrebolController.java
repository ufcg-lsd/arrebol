package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.core.scheduler.StandardScheduler;
import org.fogbowcloud.arrebol.core.scheduler.Scheduler;
import org.fogbowcloud.arrebol.core.resource.ResourceObserver;
import org.fogbowcloud.arrebol.core.resource.ResourceManager;
import org.fogbowcloud.arrebol.pools.resource.ResourcePool;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;


public class ArrebolController {

    private Scheduler scheduler;
    private TasksMonitor tasksMonitor;

    private ResourceManager resourceManager;

    public ArrebolController() {
        this.resourceManager = new ResourceManager();

        ResourceStateTransitioner resourceStateTransitioner = this.resourceManager.getResourcePool();
        this.tasksMonitor = new TasksMonitor(resourceStateTransitioner);
        this.scheduler = new StandardScheduler(this.tasksMonitor);

        ResourceObserver schedulerObserver = (ResourceObserver) this.scheduler;
        this.resourceManager.registerObserver(schedulerObserver);
    }

    public void start() {
        // TODO: read from bd

        this.tasksMonitor.start();
    }

    public void stop() {
        // TODO: delete all resources

        this.tasksMonitor.stop();
    }

    public void addTask(Task task) {
        this.scheduler.addTask(task);
    }

    public void stopTask(Task task) {
        this.scheduler.stopTask(task);
    }

    public TaskState getTaskState(String taskId) {
        Task task = this.tasksMonitor.getTaskById(taskId);
        TaskState taskState = this.tasksMonitor.getTaskState(task);
        return taskState;
    }
}
