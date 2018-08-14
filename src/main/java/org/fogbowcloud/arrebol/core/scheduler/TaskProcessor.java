package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskStatus;
import org.fogbowcloud.arrebol.core.scheduler.TaskProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourcePool;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskProcessor implements Runnable {

    private ResourcePool resourcePool;
    private Map<Task, TaskStatus> taskPool;

    public TaskProcessor(ResourcePool resourcePool) {
        this.resourcePool = resourcePool;
        this.taskPool = new ConcurrentHashMap<Task, TaskStatus>();
    }

    public void run() {
        // TODO
    }

    public void start() {
        // TODO
    }

    public void runTask(Task task) {
        // TODO
        this.taskPool.put(task, TaskStatus.PENDING);
    }

    public void stopTask(Task task) {
        // TODO
    }
}
