package org.fogbowcloud.arrebol.core.utils;

import org.fogbowcloud.arrebol.core.models.Command;
import org.fogbowcloud.arrebol.core.models.CommandState;
import org.fogbowcloud.arrebol.core.models.CommandType;
import org.json.JSONObject;

public class CommandHelper {
    public static Command fromJSON(JSONObject commandJSON) {
        Command command = new Command(commandJSON.optString(Command.COMMAND_KEY),
                CommandType.valueOf(commandJSON.optString(Command.COMMAND_TYPE_KEY)));
        command.setState(CommandState.valueOf(commandJSON.optString(Command.COMMAND_TYPE_KEY)));
        return command;
    }
}
