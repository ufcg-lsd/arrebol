package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;

import java.util.List;

public interface TaskQueueProcessor {
    MatchedTask pickTaskToRun(List<Task> pendingTasks, List<Resource> freeResources);
}
