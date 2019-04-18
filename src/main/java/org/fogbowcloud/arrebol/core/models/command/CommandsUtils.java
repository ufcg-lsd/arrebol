package org.fogbowcloud.arrebol.core.models.command;

import org.json.JSONException;
import org.json.JSONObject;

public class CommandsUtils {

    public static final String JSON_KEY_COMMAND = "command";
    public static final String JSON_KEY_STATE = "state";

    public static Command fromJSON(JSONObject commandJSON) {
        String commandStr = commandJSON.optString(JSON_KEY_COMMAND);
        CommandState commandState = CommandState.valueOf(commandJSON.optString(JSON_KEY_STATE));

        Command command = new Command(commandStr);
        command.setState(commandState);
        return command;
    }

}
