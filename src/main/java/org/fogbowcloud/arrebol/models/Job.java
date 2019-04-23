package org.fogbowcloud.arrebol.models;

import org.fogbowcloud.arrebol.models.task.Task;

import java.util.HashSet;
import java.util.Set;

public class Job {

    private final int jobId;
    private final Set<Task> task;

    public Job(int jobId, Set<Task> task) {
        this.jobId = jobId;
        this.task = task;
    }

    public int getJobId() {
        return this.jobId;
    }

    public Set<Task> tasks() {
        return new HashSet<Task>(task);
    }

    @Override
    public String toString() {
        return "id={" + getJobId() + "}";
    }
}
