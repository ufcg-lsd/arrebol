package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.core.models.Job;
import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.command.CommandType;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.resource.models.MatchAnyResource;
import org.fogbowcloud.arrebol.core.resource.models.Resource;
import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.resource.DefaultPool;
import org.fogbowcloud.arrebol.resource.ResourcePool;
import org.fogbowcloud.arrebol.scheduler.DefaultScheduler;
import org.fogbowcloud.arrebol.scheduler.FifoSchedulerPolicy;
import org.junit.Test;

import java.util.*;

public class RawWorkerIntegrationTest {

    @Test
    public void addTaskWhenNoResources() {

        //setup

        //create the queue
        int queueId = 1;
        String queueName = "defaultQueue";
        JobQueue queue = new JobQueue(queueId, queueName);

        //create the pool
        //FIXME: we are missing something related to worker/resource func
        int poolId = 1;
        Collection<Resource> resources = createPool(5);
        ResourcePool pool = new DefaultPool(poolId, resources);

        //create the scheduler
        //bind the pieces together
        FifoSchedulerPolicy policy = new FifoSchedulerPolicy();
        DefaultScheduler scheduler = new DefaultScheduler(queue, pool, policy);

        //submit job1 (one task, exit 1), job2 (two tasks exit 2), job3 (three tasks exit3)
        //job1
        Job job1 = createJob(new String[]{"exit 1"});
        Job job2 = createJob(new String[]{"exit 2", "exit 2"});
        Job job3 = createJob(new String[]{"exit 3", "exit 3", "exit 3"});

        //exercise

        //verify
        //busy wait, all jobs should finished, eventually
        //assert the exitValues

        /**
        Assert.assertEquals(1, scheduler.getPendingTasks().size());
        Assert.assertEquals(scheduler.getPendingTasks().get(0), task);
         */
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

    private Task createTask(String cmd) {
        UUID taskUUID = UUID.randomUUID();
        Task task = new Task(taskUUID.toString(), new DummySpec(), taskUUID.toString());
        Command command = new Command(cmd, CommandType.LOCAL);
        task.addCommand(command);
        return task;
    }

    private Collection<Resource> createPool(int size){
        Collection<Resource> resources = new LinkedList<Resource>();
        int poolSize = 5;
        Specification resourceSpec = null;
        for (int i = 0; i < poolSize; i++) {
            resources.add(new MatchAnyResource(resourceSpec, "resourceId-"+i));
        }
        return resources;
    }

    private class DummySpec implements Specification {

    }
}
