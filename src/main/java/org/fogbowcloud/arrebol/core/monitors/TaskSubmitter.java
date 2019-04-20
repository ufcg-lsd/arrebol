package org.fogbowcloud.arrebol.core.monitors;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.resource.models.Resource;

public interface TaskSubmitter {
    void runTask(Task task, final Resource resource);

    void stopTask(Task task);
}
