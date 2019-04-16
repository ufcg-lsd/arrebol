package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;

import java.util.List;

public interface TaskQueueProcessor {
    MatchedTask pickTaskToRun(List<Task> pendingTasks, List<AbstractResource> freeResources);
}