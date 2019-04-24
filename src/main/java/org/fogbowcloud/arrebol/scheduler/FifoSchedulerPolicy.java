package org.fogbowcloud.arrebol.scheduler;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.queue.TaskQueue;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.resource.WorkerPool;
import org.fogbowcloud.arrebol.resource.ResourceState;

import java.util.Collection;
import java.util.LinkedList;

public class FifoSchedulerPolicy implements SchedulerPolicy {

    private final Logger logger = Logger.getLogger(SchedulerPolicy.class);

    @Override
    public Collection<AllocationPlan> schedule(TaskQueue queue, WorkerPool pool) {

        logger.info("queue={" + queue + "} resourcePool={" + pool + "}");

        Collection<Worker> availableWorkers = filterAvailable(pool);
        Collection<Worker> copyOfAvailableWorkers = new LinkedList<Worker>(availableWorkers);

        logger.info("queue={" + queue + "} resourcePool={" + pool + "} " +
                "availableWorkers={" + availableWorkers.size() + "}");

        Collection<AllocationPlan> queueAllocation = new LinkedList<AllocationPlan>();

        for(Task task: queue.queue()) {
            if (TaskState.PENDING.equals(task.getState())) {
                AllocationPlan taskAllocation = scheduleTask(task, copyOfAvailableWorkers);
                if (taskAllocation != null) {
                    copyOfAvailableWorkers.remove(taskAllocation.getWorker());
                    queueAllocation.add(taskAllocation);
                }
            }
        }

        logger.info("queue={" + queue + "} resourcePool={" + pool + "} " +
                "allocationPlan={" + queueAllocation + "}");

        return queueAllocation;
    }

    private AllocationPlan scheduleTask(Task task, Collection<Worker> availableWorkers) {

        for (Worker worker : availableWorkers) {
            if (worker.match(task.getSpecification())) {
                logger.info("allocation made for task={" + task + "} using worker={" + worker + "}");
                return new AllocationPlan(task, worker, AllocationPlan.Type.RUN);
            }
        }

        return null;
    }

    private Collection<Worker> filterAvailable(WorkerPool toFilter) {
        Collection<Worker> availableWorkers = new LinkedList<Worker>();
        for(Worker worker : toFilter.getWorkers()) {
            if (worker.getState().equals(ResourceState.IDLE)) {
                availableWorkers.add(worker);
            }
        }
        return availableWorkers;
    }
}
