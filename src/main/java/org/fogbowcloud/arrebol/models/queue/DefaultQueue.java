package org.fogbowcloud.arrebol.models.queue;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;

@Entity
public class DefaultQueue implements Queue {

    @Id
    @Column(name = "ID")
    private String queueId;
    @Transient
    private TaskQueue taskQueue;
    @Transient
    private DefaultScheduler defaultScheduler;

    @ElementCollection(targetClass = String.class)
    private List<String> jobs;

    public DefaultQueue() {
    }

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
        jobs.add(job.getId());
        for (Task task : job.getTasks()) {
            taskQueue.addTask(task);
        }
        QueueDBManager.getInstance().save(this);
    }

    @Override
    public void start() {
        Thread schedulerThread = new Thread(this.defaultScheduler, "scheduler-thread-" + queueId);
        schedulerThread.start();
    }
}
