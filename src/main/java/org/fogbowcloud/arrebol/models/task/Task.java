package org.fogbowcloud.arrebol.models.task;

import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.specification.Specification;

import javax.persistence.*;
import java.util.*;

@Entity
public class Task {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private TaskState state;

    @OneToOne(cascade = CascadeType.ALL)
    private TaskSpec taskSpec;

    public Task(String id, TaskSpec taskSpec) {
        this.id = id;
        this.taskSpec = taskSpec;
        this.state = TaskState.PENDING;
    }

    Task(){
        //Default constructor
    }

    public String getId() {
        return this.id;
    }

    public Specification getSpecification() {
        return this.taskSpec.getSpec();
    }

    public TaskState getState() {
        return this.state;
    }

    public void setState(TaskState newState) {
        this.state = newState;
    }

    public List<Command> getCommands() {
        List<Command> commandsClone = new ArrayList<>(this.taskSpec.getCommands());
        return commandsClone;
    }

    public Map<String, String> getMetadata() {
        Map<String, String> metadataClone = new HashMap<>(this.taskSpec.getMetadata());
        return metadataClone;
    }

    @Override
    public String toString() {
        return "id={" + getId() + "}  state={" + getState() + "}";
    }

    //FIXME: humm ... maybe this equals/hash should consider the other instance variable
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
