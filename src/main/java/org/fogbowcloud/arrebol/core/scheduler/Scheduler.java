package org.fogbowcloud.arrebol.core.scheduler;

import java.util.List;

import org.fogbowcloud.arrebol.core.models.task.Task;

public interface Scheduler {
    void addTask(Task task);

    List<Task> getPendingTasks();

    void stopTask(Task task);
}
