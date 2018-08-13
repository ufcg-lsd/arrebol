package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.Task;

public interface TaskProcessor {
    void start();

    void runTask(Task task);

    void stopTask(Task task);
}
