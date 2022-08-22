package org.fogbowcloud.arrebol.models.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import org.fogbowcloud.arrebol.models.command.Command;

@Entity
public class TaskSpec implements Serializable {

    private static final long serialVersionUID = -6111900503456749695L;

    private String label;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> requirements;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(
            min = 1,
            max = 10000,
            message = "Commands list may not be smaller than one and greater than 10000")
    private List<Command> commands;

    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, String> metadata;

    public TaskSpec(
            Long id, Map<String, String> requirements, List<Command> commands, Map<String, String> metadata) {
        this.id = id;
        this.requirements = requirements;
        this.commands = commands;
        this.metadata = metadata;
    }

    public TaskSpec() {
        // Default constructor.
    }

    public String getLabel() {
        return label;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public Map<String, String> getMetadata() {
        return this.metadata;
    }

    @Override
    public String toString() {
        return "TaskSpec{" + "id='" + id + '\'' + ", requirements=" + requirements + ", commands="
            + commands + ", metadata=" + metadata + '}';
    }
}
