package org.fogbowcloud.arrebol.models.command;

import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;
import javax.persistence.*;

@Entity
public class Command implements Serializable {

    private static final long serialVersionUID = -5555900503095L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 10240)
    private String command;
    @Enumerated(EnumType.STRING)
    private CommandState state;

    private Integer exitcode;

    Command() {
        //default constructor
    }

    public Command(String command) {
        this.command = command;
        this.state = CommandState.UNSTARTED;
        this.exitcode = Integer.MAX_VALUE;
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

    public Integer getExitcode() {
        return exitcode;
    }

    public void setExitcode(Integer exitcode) {
        this.exitcode = exitcode;
    }

    @Override
    public String toString() {
        return "CommandStr={" + this.command + "} state={" + this.state + "}";
    }

}
