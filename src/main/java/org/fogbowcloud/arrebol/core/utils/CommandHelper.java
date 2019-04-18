package org.fogbowcloud.arrebol.core.utils;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.command.CommandState;
import org.json.JSONObject;

public class CommandHelper {
    public static Command fromJSON(JSONObject commandJSON) {
        Command command = new Command(commandJSON.optString(Command.COMMAND_KEY));
        return command;
    }
}
