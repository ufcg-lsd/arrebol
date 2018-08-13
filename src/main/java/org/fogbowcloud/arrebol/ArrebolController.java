package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskStatus;
import org.fogbowcloud.arrebol.core.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.core.scheduler.Scheduler;
import org.fogbowcloud.arrebol.pool.ResourceObserver;
import org.fogbowcloud.arrebol.pool.ResourcePool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArrebolController {

    private Scheduler scheduler;
    private TaskProcessor taskProcessor;

    private ResourcePool resourcePool;

    public ArrebolController() {
        this.scheduler = new StandardScheduler(this.taskProcessor);
        this.resourcePool = new ResourcePool();

        ResourceObserver schedulerObserver = (ResourceObserver) this.scheduler;
        this.resourcePool.registerObserver(schedulerObserver);

        this.taskProcessor = new TaskProcessorImpl(this.resourcePool);
    }

    public void addTask(Task task) {
        this.scheduler.addTask(task);
    }
}
