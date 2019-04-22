package org.fogbowcloud.arrebol.queue;

public interface QueueObserver {

    void notifyAddedJob(int jobId, int queueId);

    //still not convinced we need that
    //jobStopped(int jobId, int queueId)
}
