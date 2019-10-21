package org.fogbowcloud.arrebol.queue;

import java.util.Collection;
import java.util.Map;
import org.fogbowcloud.arrebol.models.task.Task;

public class QueueManager {

    private Map<String, Queue> queues;

    public QueueManager(Map<String, Queue> queues) {
        this.queues = queues;
    }

    public void addTaskToQueue(String queue, Task task) {
        queues.get(queue).addTaskToQueue(task);
    }

    public void addQueue(Queue queue){
        this.queues.put(queue.getId(), queue);
    }

    public Collection<String> getQueuesNames() {
        return queues.keySet();
    }

    public void startQueue(String queueId){
        this.queues.get(queueId).startSchedulerThread();
    }
}
