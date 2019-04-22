package org.fogbowcloud.arrebol.queue;

import org.fogbowcloud.arrebol.core.models.Job;

import java.util.LinkedList;
import java.util.Queue;

public class JobQueue {

    //FIXME: we should unify how we use IDs. (a place uses int, other string, other UUID)
    private final int id;
    private final String name;
    private final Queue<Job> jobQueue;

    public JobQueue(int id, String name) {
        this.id = id;
        this.name = name;
        this.jobQueue = new LinkedList<Job>();
    }

    public boolean addJob(Job job) {
        return this.jobQueue.add(job);
    }

    public Queue<Job> queue() {
        return new LinkedList<Job>(this.jobQueue);
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
