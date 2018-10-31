package org.fogbowcloud.arrebol.core.scheduler.task_queue_processor;

import org.fogbowcloud.arrebol.core.resource.models.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;

import java.util.List;

public class SimpleTaskQueueProcessor implements TaskQueueProcessor {

    /**
     * Get the first task in the queue that matches with a resource (resource accomplish task requirements)
     * @param pendingTasks, list of peding tasks
     * @param freeResources, list of free resources
     * @return an instance of MatchedTask, to save a match between a task and a resource.
     */
    @Override
    public MatchedTask pickTaskToRun(List<Task> pendingTasks, List<AbstractResource> freeResources) {
        for (Task task : pendingTasks) {
            for (AbstractResource resource : freeResources) {
                boolean isSameSpecification = resource.getRequestedSpecification().equals(task.getSpecification());
                if (!task.isFinished() && isSameSpecification) {
                    pendingTasks.remove(task); // TODO: test if is removed
                    freeResources.remove(resource);
                    return new MatchedTask(task, resource);
                }
            }
        }
        return getEmptyMatchedTask();
    }

    private MatchedTask getEmptyMatchedTask() {
        return new MatchedTask(null, null);
    }
}
