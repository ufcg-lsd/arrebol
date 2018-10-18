package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Task;

import java.util.List;

public class SimpleTaskQueueProcessor implements TaskQueueProcessor {
    @Override
    public MatchedTask pickTaskToRun(List<Task> pendingTasks, List<Resource> freeResources) {
        // TODO
        // get the first task in the queue that matches with a resource (resource accomplish task requirements)
        return null;
    }
}
