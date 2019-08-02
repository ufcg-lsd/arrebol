package org.fogbowcloud.arrebol.models.task;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.specification.Specification;

@Entity
public class TaskSpec implements Serializable {

    private static final long serialVersionUID = -6111900503456749695L;

    @Id private String id;

    @OneToOne(cascade = CascadeType.ALL)
    private Specification spec;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
//    @NotNull(message = "Commands list may not be null")
    @Size(
            min = 1,
            max = 10000,
            message = "Commands list may not be smaller than one and greater than 10000")
    private List<Command> commands;

    @ElementCollection private Map<String, String> metadata;

    public TaskSpec(
            String id, Specification spec, List<Command> commands, Map<String, String> metadata) {
        this.id = id;
        this.spec = spec;
        this.commands = commands;
        this.metadata = metadata;
    }

    public TaskSpec() {
        // Default constructor.
    }

    public String getId() {
        return this.id;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public Specification getSpec() {
        return this.spec;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public String toString() {
        return "TaskSpec{"
                + "spec="
                + spec
                + ", commands="
                + commands
                + ", metadata="
                + metadata
                + '}';
    }
}
