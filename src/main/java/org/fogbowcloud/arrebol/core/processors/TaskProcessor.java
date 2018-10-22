package org.fogbowcloud.arrebol.core.processors;

import org.fogbowcloud.arrebol.core.models.Command;
import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.TaskState;

import java.util.List;

public interface TaskProcessor {
    String getProcessId();

    String getTaskId();

    List<Command> getCommands();

    void executeTask(Resource resource);

    Specification getSpecification();

    Resource getResource();

    void setResource(Resource resource);

    TaskState getStatus();

    void setStatus(TaskState taskState);
}
