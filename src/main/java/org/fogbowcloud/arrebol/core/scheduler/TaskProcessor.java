package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskStatus;
import org.fogbowcloud.arrebol.core.scheduler.TaskProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourcePool;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TaskProcessor implements Runnable {

    private ResourceStateTransitioner ResourceStateTransitioner;
    private Map<TaskState, Task> taskPool;

    public TaskProcessor(ResourceStateTransitioner ResourceStateTransitioner) {
        this.ResourceStateTransitioner = ResourceStateTransitioner;
        this.taskPool = new ConcurrentHashMap<TaskState, Task>();
    }

    public void run() {
        // TODO
    }

    public void start() {
        // TODO
    }

    void runTask(Task task) {
        // TODO
        this.taskPool.put(task, TaskStatus.PENDING);
    }

    void stopTask(Task task) {
        // TODO
    }
}
