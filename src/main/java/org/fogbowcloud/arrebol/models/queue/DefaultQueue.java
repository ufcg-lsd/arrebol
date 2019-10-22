package org.fogbowcloud.arrebol.models.queue;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;

@Entity
public class DefaultQueue implements Queue {

    @Id
    private final String queueId;
    @Transient
    private final TaskQueue taskQueue;
    @Transient
    private final DefaultScheduler defaultScheduler;

    private List<String> jobs;

    public DefaultQueue(final String queueId, final TaskQueue taskQueue,
        final DefaultScheduler defaultScheduler) {
        this.queueId = queueId;
        this.taskQueue = taskQueue;
        this.defaultScheduler = defaultScheduler;
        this.jobs = new ArrayList<>();
    }

    @Override
    public String getId() {
        return this.queueId;
    }

    @Override
    public void addJob(Job job) {
        for(Task task : job.getTasks()){
            taskQueue.addTask(task);
        }
    }

    @Override
    public void start(){
        Thread schedulerThread = new Thread(this.defaultScheduler, "scheduler-thread-" + queueId);
        schedulerThread.start();
    }
}
