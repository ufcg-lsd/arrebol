package org.fogbowcloud.arrebol.core.models.command;

import org.json.JSONException;
import org.json.JSONObject;


public class Command {

    public static final String COMMAND_KEY = "command";
    public static final String COMMAND_TYPE_KEY = "type";
    public static final String COMMAND_STATE_KEY = "state";

    private String command;
    private final CommandType type;
    private CommandState state;

    public Command(String command, CommandType type) {
        this.command = command;
        this.type = type;
        this.state = CommandState.UNSTARTED;
    }

    public CommandType getType() {
        return type;
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
        return null;
    }

    public JSONObject toJSON() {
        try {
            JSONObject command = new JSONObject();
            command.put(COMMAND_KEY, this.getCommand());
            command.put(COMMAND_TYPE_KEY, this.getType().toString());
            command.put(COMMAND_STATE_KEY, this.getState().toString());
            return command;
        } catch (JSONException e) {
            return null;
        }
    }
}
