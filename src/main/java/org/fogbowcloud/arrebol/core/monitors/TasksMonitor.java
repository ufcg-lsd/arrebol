package org.fogbowcloud.arrebol.core.monitors;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskState;
import org.fogbowcloud.arrebol.core.processors.TaskProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;

import java.util.HashMap;
import java.util.Map;

public class TasksMonitor implements Runnable {

    private Map<TaskState, Task> taskPool;
    private Map<Task, TaskProcessor> runningTasks;
    private ResourceStateTransitioner ResourceStateTransitioner;

    public TasksMonitor(ResourceStateTransitioner ResourceStateTransitioner) {
        this.taskPool = new HashMap<TaskState, Task>();
        this.runningTasks = new HashMap<Task, TaskProcessor>();
        this.ResourceStateTransitioner = ResourceStateTransitioner;
    }

    public void runTask(Task task, Resource resource) {
        // TODO
        // make resource busy in resourcePool
        this.taskPool.put(TaskState.RUNNING, task);
    }

    public void stopTask(Task task) {
        // TODO
        // release resource matched with task
    }

    protected TaskProcessor createProcess(Task task) {
        // TODO
        return null;
    }

    public void run() {
        // TODO
    }
}
