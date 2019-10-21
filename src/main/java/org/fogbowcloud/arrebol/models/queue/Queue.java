package org.fogbowcloud.arrebol.models.queue;

import org.fogbowcloud.arrebol.models.task.Task;

public interface Queue {

    String getId();

    boolean addTaskToQueue(Task task);

    void startSchedulerThread();

}
