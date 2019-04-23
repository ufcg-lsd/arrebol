package org.fogbowcloud.arrebol.scheduler;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.resource.ResourcePool;
import org.fogbowcloud.arrebol.resource.ResourceState;

import java.util.Collection;
import java.util.LinkedList;

public class FifoSchedulerPolicy implements SchedulerPolicy {

    private final Logger logger = Logger.getLogger(SchedulerPolicy.class);

    @Override
    public Collection<AllocationPlan> schedule(TaskQueue queue, ResourcePool pool) {

        logger.info("queue={" + queue + "} resourcePool={" + pool + "}");

        Collection<Resource> availableResources = filterAvailable(pool);
        Collection<Resource> copyOfAvailableResources = new LinkedList<Resource>(availableResources);

        Collection<AllocationPlan> queueAllocation = new LinkedList<AllocationPlan>();

        for(Task task: queue.queue()) {
            if (TaskState.PENDING.equals(task.getState())) {
                AllocationPlan taskAllocation = scheduleTask(task, copyOfAvailableResources);
                if (taskAllocation != null) {
                    copyOfAvailableResources.remove(taskAllocation.getResource());
                    queueAllocation.add(taskAllocation);
                }
            }
        }

        return queueAllocation;
    }

    private AllocationPlan scheduleTask(Task task, Collection<Resource> availableResources) {

        for (Resource resource: availableResources) {
            if (resource.match(task.getSpecification())) {
                logger.info("allocation made for task={" + task + "} using resource={" + resource + "}");
                return new AllocationPlan(task, resource, AllocationPlan.Type.RUN);
            }
        }

        return null;
    }

    private Collection<Resource> filterAvailable(ResourcePool toFilter) {
        Collection<Resource> availableResources = new LinkedList<Resource>();
        for(Resource resource : toFilter.getResources()) {
            if (resource.getState().equals(ResourceState.IDLE)) {
                availableResources.add(resource);
            }
        }
        return availableResources;
    }
}
