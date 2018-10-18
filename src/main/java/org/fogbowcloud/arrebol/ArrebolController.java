package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.core.scheduler.StandardScheduler;
import org.fogbowcloud.arrebol.core.scheduler.Scheduler;
import org.fogbowcloud.arrebol.pools.resource.ResourceObserver;
import org.fogbowcloud.arrebol.pools.resource.ResourcePoolManager;


public class ArrebolController {

    private Scheduler scheduler;
    private TasksMonitor tasksMonitor;

    private ResourcePoolManager resourcePoolManager;

    public ArrebolController() {
        this.resourcePoolManager = new ResourcePoolManager();

        this.tasksMonitor = new TasksMonitor(this.resourcePoolManager);
        this.scheduler = new StandardScheduler(this.tasksMonitor);

        ResourceObserver schedulerObserver = (ResourceObserver) this.scheduler;
        this.resourcePoolManager.registerObserver(schedulerObserver);
    }

    public void addTask(Task task) {
        this.scheduler.addTask(task);
    }

    public void stopTask(Task task) {
        this.scheduler.stopTask(task);
    }
}
