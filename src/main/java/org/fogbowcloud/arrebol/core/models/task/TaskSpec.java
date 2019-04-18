package org.fogbowcloud.arrebol.core.models.task;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.specification.Specification;

import java.io.Serializable;
import java.util.List;

public class TaskSpec implements Serializable {

    private static final long serialVersionUID = -6111900503456749695L;

    private Specification spec;
    private List<Command> commands;

    public TaskSpec(Specification spec, List<Command> commands){
        this.spec = spec;
        this.commands = commands;
    }

    public TaskSpec(){}


    public List<Command> getCommands(){
        return this.commands;
    }

    public Specification getSpec(){
        return this.spec;
    }

    @Override
    public String toString() {
        /*
        return "TaskSpec{" +
                "spec=" + spec.toString() +
                ", commands=" + commandsToString() +
                '}';
                */
        return "";
    }
}
