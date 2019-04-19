package org.fogbowcloud.arrebol.core.models.task;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.specification.Specification;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Task {

    @Id
    private String id;

    @OneToOne(cascade = CascadeType.ALL)
    private Specification specification;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "task")
    private List<Command> commands;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    @ElementCollection
    private Map<String, String> metadata = new HashMap<String, String>();

    public Task(String id, Specification spec, List<Command> commands) {
        this.id = id;
        this.specification = spec;
        this.state = TaskState.PENDING;
        this.commands = commands;;
    }

    public Task(){}


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

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands){
        this.commands = commands;
    }

    public String getId() {
        return this.id;
    }

    @ManyToOne
    private Job job;

    public void setJob(Job job){
        this.job = job;
    }
}
