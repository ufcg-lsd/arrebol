package org.fogbowcloud.arrebol.scheduler;

public interface Scheduler {

    /**
     * I'll ignore these features (to support multiple queues and pools by now
    void bind(ResourcePool pool);
    void bind(JobQueue jobQueue, int priority);
     */

    //I guess we need some method to uncover the inner state of the scheduler (which queues and pools were bound ...)
}
