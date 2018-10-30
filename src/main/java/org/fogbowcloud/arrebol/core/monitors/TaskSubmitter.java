package org.fogbowcloud.arrebol.core.monitors;

import org.fogbowcloud.arrebol.core.models.resource.AbstractResource;
import org.fogbowcloud.arrebol.core.models.task.Task;

public interface TaskSubmitter {
    void runTask(Task task, final AbstractResource resource);

    void stopTask(Task task);
}
