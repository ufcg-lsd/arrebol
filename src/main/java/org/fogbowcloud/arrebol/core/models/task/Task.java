package org.fogbowcloud.arrebol.core.models.task;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.specification.Specification;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Task {

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    private Specification specification;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Command> commands;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    @ElementCollection
    private Map<String, String> metadata;

    public Task(String id, Specification spec, List<Command> commands) {
        this(id, spec, commands, new HashMap<String, String>());
    }

    public Task(String id, Specification spec, List<Command> commands, Map<String, String> metadata) {
        this.id = id;
        this.specification = spec;
        this.state = TaskState.PENDING;
        this.commands = commands;
        this.metadata = metadata;
    }

    Task(){
        //Default constructor
    }

    public String getId() {
        return this.id;
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

    public List<Command> getCommands() {
        List<Command> commandsClone = new ArrayList<>(this.commands);
        return commandsClone;
    }

    public Map<String, String> getMetadata() {
        Map<String, String> metadataClone = new HashMap<>(this.metadata);
        return metadataClone;
    }

    @Override
    public String toString() {
        return "id={" + getId() + "}";
    }
}
