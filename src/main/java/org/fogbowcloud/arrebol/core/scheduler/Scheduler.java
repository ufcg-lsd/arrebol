package org.fogbowcloud.arrebol.core.scheduler;

import java.util.List;
import org.fogbowcloud.arrebol.core.models.Task;

public interface Scheduler {
    void addTask(Task task);

    List<Task> getPendingTasks();

    void runTask(Task task);

    void stopTask(Task task);

}
