package org.fogbowcloud.arrebol.core.processors;

import org.fogbowcloud.arrebol.core.models.Command;
import org.fogbowcloud.arrebol.core.models.Resource;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.TaskState;

import java.util.List;
import java.util.UUID;

public class TaskProcessorImpl implements TaskProcessor {

    private String processId;
    private String taskId;
    private List<Command> commands;
    private Specification specification;
    private String userId;
    private TaskState status;
    private Resource resource;

    public TaskProcessorImpl(String taskId, List<Command> commands, Specification specification, String userId) {
        this.processId = UUID.randomUUID().toString();
        this.taskId = taskId;
        this.commands = commands;
        this.specification = specification;
        this.userId = userId;
    }

    @Override
    public String getProcessId() {
        return this.processId;
    }

    @Override
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    public List<Command> getCommands() {
        return this.commands;
    }

    @Override
    public void executeTask(Resource resource) {

    }

    @Override
    public Specification getSpecification() {
        return this.specification;
    }

    @Override
    public Resource getResource() {
        return this.resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public TaskState getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(TaskState taskState) {
        this.status = taskState;
    }
}
