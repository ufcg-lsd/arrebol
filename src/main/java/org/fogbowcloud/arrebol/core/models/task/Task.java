package org.fogbowcloud.arrebol.core.models.task;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.specification.Specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Task {

    private String id;
    private Specification specification;
    private List<Command> commands = new ArrayList<Command>();
    private TaskState state;
    private String uuid;
    private boolean isFinished;
    private boolean isFailed;
    private int retries;
    private Map<String, String> metadata = new HashMap<String, String>();

    public Task(String id, Specification spec, String uuid) {
        this.id = id;
        this.specification = spec;
        this.uuid = uuid;
        this.isFinished = false;
        this.isFailed = false;
        this.state = TaskState.PENDING;
        this.retries = -1;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void finish(){
        this.isFinished = true;
    }

    protected void fail() {
        this.isFailed = true;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void putMetadata(String attributeName, String value) {
        this.metadata.put(attributeName, value);
    }

    public String getMetadata(String attributeName) {
        return this.metadata.get(attributeName);
    }

    public Map<String, String> getAllMetadata() {
        return metadata;
    }

    public Specification getSpecification() {
        return this.specification;
    }

    public TaskState getState() {
        return this.state;
    }

    public void setState(TaskState newState) {
        this.state = newState;
    }

    public List<Command> getAllCommands() {
        return commands;
    }

    public String getId() {
        return this.id;
    }

    public String getUUID() {
        return this.uuid;
    }

    public int getRetries() {
        return this.retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
