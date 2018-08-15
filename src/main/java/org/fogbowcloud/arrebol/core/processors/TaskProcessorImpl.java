package org.fogbowcloud.arrebol.core.processors;

import org.fogbowcloud.arrebol.core.models.Command;
import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.Specification;
import org.fogbowcloud.arrebol.core.models.TaskState;

import java.util.List;

public class TaskProcessorImpl implements TaskProcessor {
    @Override
    public String getProcessId() {
        return null;
    }

    @Override
    public String getTaskId() {
        return null;
    }

    @Override
    public List<Command> getCommands() {
        return null;
    }

    @Override
    public void executeTask(Resource resource) {

    }

    @Override
    public TaskState getStatus() {
        return null;
    }

    @Override
    public Specification getSpecification() {
        return null;
    }

    @Override
    public Resource getResource() {
        return null;
    }

    @Override
    public void setStatus(TaskState taskState) {

    }
}
