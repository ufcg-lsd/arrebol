package org.fogbowcloud.arrebol.core.models.command;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;

@Entity
public class Command {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String command;
    //@Enumerated(EnumType.STRING)
    private CommandState state;

    public Command(String command) {
        this.command = command;
        this.state = CommandState.UNSTARTED;
    }

    public Command(){}

    public String getCommand() {
        return command;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    public CommandState getState() {
        return this.state;
    }

    public Command clone() {
        return null;
    }

    public JSONObject toJSON() {
        try {
            JSONObject command = new JSONObject();
            return command;
        } catch (JSONException e) {
            return null;
        }
    }

    public String toString(){
        return "Command: " + this.command + System.lineSeparator() + "State: " + this.state.toString();
    }

    @ManyToOne
    @JoinColumn(name="id_task")
    private Task task;
}
