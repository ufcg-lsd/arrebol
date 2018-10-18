package org.fogbowcloud.arrebol.core.monitors;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskState;
import org.fogbowcloud.arrebol.core.processors.TaskProcessor;
import org.fogbowcloud.arrebol.core.processors.TaskProcessorImpl;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TasksMonitor {

    private ExecutorService tasksExecutorService = Executors.newCachedThreadPool();

    private Map<Task, TaskProcessor> runningTasks;
    private ResourceStateTransitioner resourceStateTransitioner;

    public TasksMonitor(ResourceStateTransitioner resourceStateTransitioner) {
        this.runningTasks = new HashMap<Task, TaskProcessor>();
        this.resourceStateTransitioner = resourceStateTransitioner;
    }

    public void runTask(Task task, final Resource resource) {
        final TaskProcessor taskProcessor = createProcess(task);

        if (this.runningTasks.get(task) == null) {
            this.runningTasks.put(task, taskProcessor);
            task.setState(TaskState.RUNNING);
            this.resourceStateTransitioner.holdResource(resource); // make resource busy in resourcePool
        }

        this.tasksExecutorService.submit(new Runnable() {
            public void run() {
                taskProcessor.executeTask(resource);
            }
        });
    }

    public void stopTask(Task task) {
        TaskProcessor processToHalt = this.runningTasks.remove(task);
        if (processToHalt != null) {
            Resource resource = processToHalt.getResource();
            if (resource != null) {
                // TODO: Find out how to stop the execution of the process
                this.resourceStateTransitioner.releaseResource(resource); // make resource idle in resourcePool
            }
        }
    }

    private TaskProcessor createProcess(Task task) {
        TaskProcessor taskProcessor = new TaskProcessorImpl(task.getId(), task.getAllCommands(), task.getSpecification(), task.getUUID());
        return taskProcessor;
    }
}
