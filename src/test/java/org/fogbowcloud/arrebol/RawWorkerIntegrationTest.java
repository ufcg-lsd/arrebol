package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.Job;
import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.resource.MatchAnyResource;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.resource.StaticPool;
import org.fogbowcloud.arrebol.resource.ResourcePool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;
import org.junit.Test;
import org.apache.log4j.Logger;

import java.util.*;

public class RawWorkerIntegrationTest {

    private final Logger LOGGER = Logger.getLogger(RawWorkerIntegrationTest.class);

    @Test
    public void addTaskWhenNoResources() throws InterruptedException {

        //setup

        //create the queue
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

        //submit job1 (one task, /usr/bin/true), job2 (two tasks /usr/bin/true), job3 (three tasks /usr/bin/true)
        //job1
        Job job1 = createJob(new String[]{"true"});
        Job job2 = createJob(new String[]{"true", "true"});
        Job job3 = createJob(new String[]{"true", "true", "true"});

        Collection<Job> jobs = new LinkedList<Job>();
        jobs.add(job1);
        jobs.add(job2);
        jobs.add(job3);

        for (Job job : jobs) {
            for (Task task: job.tasks()) {
                queue.addTask(task);
            }
        }

        //exercise
        new Thread(scheduler, "scheduler-thread").start();

        //verify
        //busy wait, all jobs should finished, eventually
        //assert the exitValues
        while(!queue.queue().isEmpty()) {
            Thread.sleep(10000);
            LOGGER.info("waiting queue to become empty");

            LOGGER.info("job1: " + job1);
            LOGGER.info("tasks job1: " + job1.tasks());

            LOGGER.info("job2: " + job2);
            LOGGER.info("tasks job2: " + job2.tasks());

            LOGGER.info("job3: " + job3);
            LOGGER.info("tasks job3: " + job3.tasks());
        }
    }

    private Job createJob(String[] cmdsStr) {

        Set<Task> tasks = new HashSet<Task>();
        for(String cmd : cmdsStr) {
            Task task = createTask(cmd);
            tasks.add(task);
        }

        Job job = new Job(1, tasks);
        return job;
    }

    private int idCount;

    private Task createTask(String cmd) {

        List<Command> cmds = new LinkedList<Command>();
        cmds.add(new Command((cmd)));

        String taskId = "taskId-"+ idCount++;
        Task task = new Task(taskId, new DummySpec(), cmds);

        return task;
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

    private class DummySpec extends Specification {

        public DummySpec() {
            this(null, null, null, null, null);
        }
        public DummySpec(String image, String username, String publicKey, String privateKeyFilePath, Map<String, String> requirements) {
            super(image, username, publicKey, privateKeyFilePath, requirements);
        }

        public DummySpec(String image, String username, String publicKey, String privateKeyFilePath, Map<String, String> requirements, String cloudName) {
            super(image, username, publicKey, privateKeyFilePath, requirements, cloudName);
        }
    }
}
