package org.fogbowcloud.arrebol.core.scheduler;

import java.util.List;
import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.Resource;

public interface Scheduler {

    void addTask(Task task);

    Task pickTaskForRun();

    List<Task> getOpenTasks();
}
