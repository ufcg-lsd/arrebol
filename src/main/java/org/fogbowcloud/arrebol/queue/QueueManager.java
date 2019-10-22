package org.fogbowcloud.arrebol.queue;

import java.util.Collection;
import java.util.Map;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.queue.Queue;

public class QueueManager {

    private Map<String, Queue> queues;

    public QueueManager(Map<String, Queue> queues) {
        this.queues = queues;
    }

    public void addJob(String queue, Job job) {
        queues.get(queue).addJob(job);
    }

    public void addQueue(Queue queue){
        this.queues.put(queue.getId(), queue);
    }

    public Collection<String> getQueuesNames() {
        return queues.keySet();
    }

    public void startQueue(String queueId){
        this.queues.get(queueId).start();
    }

}
