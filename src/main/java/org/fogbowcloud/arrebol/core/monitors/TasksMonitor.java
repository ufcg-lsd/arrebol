package org.fogbowcloud.arrebol.core.monitors;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.core.processors.TaskProcessor;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TasksMonitor implements Runnable, Monitor, TaskSubmitter {

    private ExecutorService tasksExecutorService = Executors.newCachedThreadPool();

    private Thread monitoringServiceRunner;
    private boolean active;

    private Map<Task, TaskProcessor> runningTasks;
    private ResourceStateTransitioner resourceStateTransitioner;

    public TasksMonitor(ResourceStateTransitioner resourceStateTransitioner) {
        this.runningTasks = new HashMap<Task, TaskProcessor>();
        this.resourceStateTransitioner = resourceStateTransitioner;
        this.active = false;
    }

    @Override
    public void run() {
        while(this.active) {
            monitorTasks();
            try {
                long timeout = 30000;
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                stop();
            }
        }
    }

    public void runTask(Task task, final AbstractResource resource) {
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
            AbstractResource resource = processToHalt.getResource();
            if (resource != null) {
                // TODO: Find out how to stop the execution of the process
                this.resourceStateTransitioner.releaseResource(resource); // make resource idle in resourcePool
            }
        }
    }

    public void start() {
        this.active = true;
        this.monitoringServiceRunner = new Thread(this);
        this.monitoringServiceRunner.start();
    }

    public void stop() {
        this.active = false;
        this.monitoringServiceRunner.interrupt();
    }

    private void monitorTasks() {
        for (TaskProcessor tp : getRunningProcesses()) {
            AbstractResource resource = tp.getResource();
            Task task = getTaskById(tp.getTaskId());

            if (tp.getStatus().equals(TaskState.FAILED)) {
                this.runningTasks.remove(task);
                if (resource != null)
                    this.resourceStateTransitioner.putResourceToRemove(resource);
            }
            if (tp.getStatus().equals(TaskState.FINISHED)) {
                this.runningTasks.remove(task);
                if (task != null)
                    task.finish();
                if (resource != null)
                    this.resourceStateTransitioner.releaseResource(resource);
            }
        }
    }

    public Task getTaskById(String taskId) {
        for (Task task : runningTasks.keySet()) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        return null;
    }

    private TaskProcessor createProcess(Task task) {
        TaskProcessor taskProcessor = new TaskProcessor(task.getId(), task.getAllCommands(), task.getSpecification(), task.getId());
        return taskProcessor;
    }

    private List<TaskProcessor> getRunningProcesses() {
        List<TaskProcessor> processes = new ArrayList<TaskProcessor>();
        processes.addAll(this.runningTasks.values());
        return processes;
    }

    public TaskState getTaskState(Task task) {
        // TODO
        return null;
    }

}
