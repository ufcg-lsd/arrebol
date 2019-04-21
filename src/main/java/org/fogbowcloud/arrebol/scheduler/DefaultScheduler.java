package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.queue.QueueObserver;
import org.fogbowcloud.arrebol.resource.ResourceObserver;
import org.fogbowcloud.arrebol.resource.ResourcePool;

import java.util.Collection;

public class DefaultScheduler implements ResourceObserver, QueueObserver {
    //TODO: to pick a better name (maybe silly-scheduler? :))

    private final JobQueue queue;
    private final ResourcePool pool;
    private final SchedulerPolicy policy;
    private final PlanExecutor planExecutor;

    public DefaultScheduler(JobQueue queue, ResourcePool pool, SchedulerPolicy policy) {

        //this implementation is super-naive: we create a new allocation plan whenever
        //a notification is received (jobAdded or resourceAvailable). Failures are just ignored.
        //There are plenty of possible improvements: e.g waiting a batch of notifications before deciding ...
        //to improve utilization, we also add the notification to a queues that is consume by a planner thread
        //we might also want to "offer" resources based on queue priority

        //also, note that the ResourceObserver and QueueObserver are already more generic than we need now
        //(since we receive queueId and poolId but are associated with a single queue and pool).

        this.queue = queue;
        this.pool = pool;
        this.policy = policy;
        this.planExecutor = new DefaultPlanExecutor();
    }

    @Override
    public void jobAdded(int jobId, int queueId) {
        act();
    }

    @Override
    public void resourceAvailable(int resourceId, int poolId) {
        act();
    }

    @Override
    public void resourceFailed(int resourceId, int poolId) {
        //just ignore this for a while
    }

    private void act() {
        synchronized (this) {
            Collection<AllocationPlan> plan = this.policy.schedule(this.queue, this.pool);
            this.planExecutor.execute(plan);
        }
    }

    //I've created this PlanExecutor abstraction (it was a public interface before).
    //Not sure we need this or the good design is to just have a function to be used by all the possible schedulers.
    //For now, I'll suspend the judgment and create a private implementation here.
    private interface PlanExecutor {

        void execute (Collection<AllocationPlan> plans);
    }

    private class DefaultPlanExecutor implements PlanExecutor {

        @Override
        public void execute(Collection<AllocationPlan> plans) {

            for(AllocationPlan plan : plans) {

                switch (plan.getType()) {
                    case RUN: {
                        //job state should change
                        //task state should change
                        //resource state should change
                        //a worker should be associated with the resource
                        //someone should take care of execution
                        //some should take care of monitoring the execution
                    }
                    case STOP: {

                    }
                    default: {

                    }
                }
            }
        }
    }
}
