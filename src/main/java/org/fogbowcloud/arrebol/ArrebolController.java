package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.core.scheduler.StandardScheduler;
import org.fogbowcloud.arrebol.core.scheduler.Scheduler;
import org.fogbowcloud.arrebol.pools.resource.ResourceObserver;
import org.fogbowcloud.arrebol.pools.resource.ResourcePool;


public class ArrebolController {

    private Scheduler scheduler;
    private TasksMonitor tasksMonitor;

    private ResourcePool resourcePool;

    public ArrebolController() {
        this.resourcePool = new ResourcePool();

        this.tasksMonitor = new TasksMonitor(this.resourcePool);
        this.scheduler = new StandardScheduler(this.tasksMonitor);

        ResourceObserver schedulerObserver = (ResourceObserver) this.scheduler;
        this.resourcePool.registerObserver(schedulerObserver);
    }

    public void addTask(Task task) {
        this.scheduler.addTask(task);
    }

    public void stopTask(Task task) {
        this.scheduler.stopTask(task);
    }
}
