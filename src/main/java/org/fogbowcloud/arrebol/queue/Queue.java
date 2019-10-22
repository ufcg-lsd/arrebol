package org.fogbowcloud.arrebol.queue;

import org.fogbowcloud.arrebol.models.job.Job;

public interface Queue {

    String getId();

    void addJob(Job job);

    void start();

    boolean containsJob(String id);

}
