package org.fogbowcloud.arrebol.execution;

import org.fogbowcloud.arrebol.models.command.Command;
import java.util.Arrays;

public class TaskExecutionResult {

    // this class is more like an struct, not very much OO but it ok

    // A task results is SUCCESS when it was able to execute
    // all the commands. Failure otherwise
    public enum RESULT {
        SUCCESS, FAILURE
    }

    public static final int UNDETERMINED_RESULT = Integer.MAX_VALUE;

    private final RESULT taskResult;
    private final Command[] commands;

    public TaskExecutionResult(RESULT result, Command[] commands) {
        this.taskResult = result;
        this.commands = commands;
    }

    public RESULT getResult() {
        return this.taskResult;
    }

    /**
     * It returns the exit codes for the execution of the commands. When the @{link TaskExecutor}
     * was not able to execute the command, the exit code is
     * {@link TaskExecutionResult#UNDETERMINED_RESULT}.
     *
     * @return
     */
    public Command[] commands() {
        return Arrays.copyOf(this.commands, this.commands.length);
    }

}
