package org.fogbowcloud.arrebol.core.models.command;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandsUtils {

    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_COMMAND = "command";
    public static final String JSON_KEY_STATE = "state";

    public static Command fromJSON(JSONObject commandJSON) {
        String commandStr = commandJSON.optString(JSON_KEY_COMMAND);
        CommandType commandType = CommandType.valueOf(commandJSON.optString(JSON_KEY_TYPE));
        CommandState commandState = CommandState.valueOf(commandJSON.optString(JSON_KEY_STATE));

        Command command = new Command(commandStr, commandType);
        command.setState(commandState);
        return command;
    }

    public JSONObject toJSON(Command command) {
        try {
            JSONObject commandJSON = new JSONObject();
            commandJSON.put(JSON_KEY_COMMAND, command.getCommand());
            commandJSON.put(JSON_KEY_TYPE, command.getType().toString());
            commandJSON.put(JSON_KEY_STATE, command.getState().toString());
            return commandJSON;
        } catch (JSONException e) {
            return null;
        }
    }
}
