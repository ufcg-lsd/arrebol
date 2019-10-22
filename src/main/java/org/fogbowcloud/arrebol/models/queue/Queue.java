package org.fogbowcloud.arrebol.models.queue;

import org.fogbowcloud.arrebol.models.job.Job;

public interface Queue {

    String getId();

    void addJob(Job job);

    void start();

}
