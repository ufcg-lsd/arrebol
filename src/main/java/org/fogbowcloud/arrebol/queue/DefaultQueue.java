package org.fogbowcloud.arrebol.queue;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.Task;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Job.class)
    private Map<String, Job> jobs;

    public DefaultQueue() {
    }

    public DefaultQueue(final String queueId, final TaskQueue taskQueue,
        final DefaultScheduler defaultScheduler) {
        this.queueId = queueId;
        this.taskQueue = taskQueue;
        this.defaultScheduler = defaultScheduler;
        this.jobs = new HashMap<>();
    }

    @Override
    public String getId() {
        return this.queueId;
    }

    @Override
    public void addJob(Job job) {
        jobs.put(job.getId(), job);
        for (Task task : job.getTasks()) {
            taskQueue.addTask(task);
        }
    }

    @Override
    public void start() {
        Thread schedulerThread = new Thread(this.defaultScheduler, "scheduler-thread-" + queueId);
        schedulerThread.start();
    }

    @Override
    public Job getJob(String id) {
        return this.jobs.get(id);
    }

    @Override
    public Map<String, Job> getJobs() {
        return this.jobs;
    }

    public boolean containsJob(String id) {
        return jobs.containsKey(id);
    }
}
