package org.fogbowcloud.arrebol.core.models.command;

import org.json.JSONException;
import org.json.JSONObject;
import javax.persistence.*;

@Entity
public class Command {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    private String command;
    @Enumerated(EnumType.STRING)
    private CommandState state;

    Command() {
        //default constructor
    }

    public Command(String command) {
        this.command = command;
        this.state = CommandState.UNSTARTED;
    }

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
        return new Command(this.command);
    }

    public JSONObject toJSON() {
        try {
            JSONObject command = new JSONObject();
            return command;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "CommandStr={" + this.command + "} state={" + this.state + "}";
    }

}
