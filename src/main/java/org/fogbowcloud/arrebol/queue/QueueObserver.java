package org.fogbowcloud.arrebol.queue;

public interface QueueObserver {

    void jobAdded(int jobId, int queueId);

    //still not convinced we need that
    //jobStopped(int jobId, int queueId)
}
