package org.fogbowcloud.arrebol.queue;

import java.util.Map;
import org.fogbowcloud.arrebol.models.job.Job;

public interface Queue {

    String getId();

    void addJob(Job job);

    void start();

    Job getJob(String id);

    Map<String, Job> getJobs();

    boolean containsJob(String id);

}
