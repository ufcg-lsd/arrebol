package org.fogbowcloud.arrebol.core.scheduler;

import java.util.List;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.pool.ResourceObserver;

public interface Scheduler {

    void addTask(Task task);

    Task pickTaskToRun();

    List<Task> getPendingTasks();
}
