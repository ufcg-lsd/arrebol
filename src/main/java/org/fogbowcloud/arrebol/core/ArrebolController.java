package org.fogbowcloud.arrebol.core;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.job.JobState;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.resource.MatchAnyResource;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.resource.ResourcePool;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;

import java.util.*;

public class ArrebolController {

    private DefaultScheduler scheduler;
    private Properties properties;

    private final Logger LOGGER = Logger.getLogger(ArrebolController.class);

    public ArrebolController(Properties properties) {
        this.properties = properties;

        String queueId = UUID.randomUUID().toString();
        String queueName = "defaultQueue";
        JobQueue queue = new JobQueue(queueId, queueName);

        //create the pool
        //FIXME: we are missing something related to worker/resource func
        int poolId = 1;
        Collection<Resource> resources = createPool(5, poolId);
        ResourcePool pool = new StaticPool(poolId, resources);

        //create the scheduler
        //bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        DefaultScheduler scheduler = new DefaultScheduler(queue, pool, policy);
    }

    private Collection<Resource> createPool(int size, int poolId){
        Collection<Resource> resources = new LinkedList<Resource>();
        int poolSize = 5;
        Specification resourceSpec = null;
        for (int i = 0; i < poolSize; i++) {
            resources.add(new MatchAnyResource(resourceSpec, "resourceId-"+i, poolId));
        }
        return resources;
    }

    public void start() {
        // TODO: read from bd
    }

    public void stop() {
        // TODO: delete all resources?
    }

    public String addJob(Job job) {
        Map<String, Task> taskMap = job.getTasks();
        for(Task task : taskMap.values()){
            /*
            this.scheduler.addTask(task);
            */
        }
        job.setJobState(JobState.READY);
        return job.getId();
    }

    public String stopJob(Job job) {
        Map<String, Task> taskMap = job.getTasks();
        for(Task task : taskMap.values()){
         //
        }
        return job.getId();
    }

    public TaskState getTaskState(String taskId) {
        //FIXME:
        return null;
    }
}
