package org.fogbowcloud.arrebol.core.processors;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.resource.Resource;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.TaskState;

import java.util.List;
import java.util.UUID;

public class TaskProcessor {

    private String processId;
    private String taskId;
    private List<Command> commands;
    private Specification specification;
    private String userId;
    private TaskState status;
    private Resource resource;

    public TaskProcessor(String taskId, List<Command> commands, Specification specification, String userId) {
        this.processId = UUID.randomUUID().toString();
        this.taskId = taskId;
        this.commands = commands;
        this.specification = specification;
        this.userId = userId;
    }

    public String getProcessId() {
        return this.processId;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public void executeTask(Resource resource) {

    }

    public Specification getSpecification() {
        return this.specification;
    }

    public Resource getResource() {
        return this.resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public TaskState getStatus() {
        return this.status;
    }

    public void setStatus(TaskState taskState) {
        this.status = taskState;
    }
}
