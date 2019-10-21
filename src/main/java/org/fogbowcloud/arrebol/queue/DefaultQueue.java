package org.fogbowcloud.arrebol.queue;

import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;

public class DefaultQueue implements Queue {

    private final String queueId;
    private final TaskQueue taskQueue;
    private final DefaultScheduler defaultScheduler;

    public DefaultQueue(final String queueId, final TaskQueue taskQueue,
        final DefaultScheduler defaultScheduler) {
        this.queueId = queueId;
        this.taskQueue = taskQueue;
        this.defaultScheduler = defaultScheduler;
    }

    @Override
    public String getId() {
        return this.queueId;
    }

    @Override
    public boolean addTaskToQueue(Task task) {
        return taskQueue.addTask(task);
    }

    @Override
    public void startSchedulerThread(){
        Thread schedulerThread = new Thread(this.defaultScheduler, "scheduler-thread-" + queueId);
        schedulerThread.start();
    }
}
