package org.fogbowcloud.arrebol.queue;

import org.fogbowcloud.arrebol.models.task.Task;

public interface Queue {

    String getId();

    boolean addTaskToQueue(Task task);

    void startSchedulerThread();

}
