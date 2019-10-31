package org.fogbowcloud.arrebol.processor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.execution.docker.constants.DockerConstants;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.resource.WorkerPool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;

@Entity
public class DefaultJobProcessor implements JobProcessor {

    @Id
    @Column(name = "ID")
    private String queueId;
    @Transient
    private TaskQueue taskQueue;
    @Transient
    private DefaultScheduler defaultScheduler;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = StaticPool.class)
    private WorkerPool pool;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Job.class)
    private Map<String, Job> jobs;

    public DefaultJobProcessor() {
    }

    public DefaultJobProcessor(final String queueId, final TaskQueue taskQueue,
        final DefaultScheduler defaultScheduler, final WorkerPool workerPool) {
        this.queueId = queueId;
        this.taskQueue = taskQueue;
        this.defaultScheduler = defaultScheduler;
        this.pool = workerPool;
        this.jobs = Collections.synchronizedMap(new HashMap<>());
    }

    @Override
    public String getId() {
        return this.queueId;
    }

    @Override
    public String getName() {
        return taskQueue.getName();
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

    @Override
    public void addWorkers(Collection<Worker> workers) {
        this.pool.addWorkers(workers);
    }

    @Override
    public int getWorkerPoolSize() {
        return this.pool.getWorkers().size();
    }

    @Override
    public int getWorkerNodesSize() {
        Set<String> addresses = new HashSet<>();
        for(Worker w : this.pool.getWorkers()) {
            String address = w.getMetadata().get(DockerConstants.ADDRESS_METADATA_KEY);
            addresses.add(address);
        }
        return addresses.size();
    }
}
