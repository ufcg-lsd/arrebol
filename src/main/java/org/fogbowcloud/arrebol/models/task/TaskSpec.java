package org.fogbowcloud.arrebol.models.task;

import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.specification.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TaskSpec implements Serializable {

    private static final long serialVersionUID = -6111900503456749695L;

    private Specification spec;
    private List<Command> commands;
    private Map<String, String> metadata;

    public TaskSpec(Specification spec, List<Command> commands, Map<String, String> metadata){
        this.spec = spec;
        this.commands = commands;
        this.metadata = metadata;
    }

    public TaskSpec(){
        //Default constructor.
    }

    public List<Command> getCommands(){
        return this.commands;
    }

    public Specification getSpec(){
        return this.spec;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public String toString() {
        return "TaskSpec{" +
                "spec=" + spec +
                ", commands=" + commands +
                ", metadata=" + metadata +
                '}';
    }
}
