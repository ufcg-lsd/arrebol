package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskState;
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

    public void start() {
        // TODO
    }

    void runTask(Task task, Resource resource) {
        // TODO
        // make resource busy in resourcePool
        this.taskPool.put(TaskState.RUNNING, task);
    }

    void stopTask(Task task) {
        // TODO
        // release resource matched with task
    }

    public void run() {
        // TODO
    }
}
