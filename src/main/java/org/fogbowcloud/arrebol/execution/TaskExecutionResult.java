package org.fogbowcloud.arrebol.execution;

import org.fogbowcloud.arrebol.models.command.Command;

import java.util.Arrays;

public class TaskExecutionResult {

    //this class is more like an struct, not very much OO but it ok

    //A task results is SUCCESS when it was able to execute
    //all the command. Failure otherwise
    public enum RESULT {SUCCESS, FAILURE};

    public static final int UNDETERMINED_RESULT = Integer.MAX_VALUE;

    private final RESULT taskResult;
    private final Command[] commands;
    private final int[] commandsResults;

    public TaskExecutionResult(RESULT result, int[] results, Command[] commands) {

        //TODO: check arrays have the same size

        this.taskResult = result;
        this.commands = commands;
        this.commandsResults = results;
    }

    public RESULT getResult() {
        return this.taskResult;
    }

    /**
     * It returns the exit codes for the execution of the commands.
     * When the @{link TaskExecutor} was not able to execute the command, the exit code is
     * is {@link TaskExecutionResult#UNDETERMINED_RESULT}.
     *
     * @return
     */
    public int[] getCommandResults() {
        return Arrays.copyOf(this.commandsResults, this.commandsResults.length);
    }

    public Command[] commands() {
        return Arrays.copyOf(this.commands, this.commands.length);
    }

}
