package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.queue.JobQueue;
import org.fogbowcloud.arrebol.resource.ResourcePool;

public interface Scheduler {

    void bind(ResourcePool pool);

    //yet another option is to have comparable queues, instead of a priority number (which is simpler, indeed)
    void bind(JobQueue jobQueue, int priority);

    //I guess we need some method to uncover the inner state of the scheduler (which queues and pools were bound ...)
}
