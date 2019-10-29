package org.fogbowcloud.arrebol.processor;

import java.util.Collection;
import java.util.Map;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.job.Job;

public interface JobProcessor {

    String getId();

    String getName();

    void addJob(Job job);

    void start();

    Job getJob(String id);

    Map<String, Job> getJobs();

    boolean containsJob(String id);

    void addWorkers(Collection<Worker> workers);

}
