package org.fogbowcloud.arrebol.execution;

import java.util.Arrays;

public class TaskExecutionResult {

    // this class is more like an struct, not very much OO but it ok

    public static final int UNDETERMINED_RESULT = Integer.MAX_VALUE;
    private final RESULT taskResult;
    private final int[] exitcodes;

    public TaskExecutionResult(RESULT result, int[] exitcodes) {
        this.taskResult = result;
        this.exitcodes = exitcodes;
    }

    public RESULT getResult() {
        return this.taskResult;
    }

    /**
     * It returns the exit codes for the execution of the commands. When the @{link TaskExecutor}
     * was not able to execute the command, the exit code is {@link TaskExecutionResult#UNDETERMINED_RESULT}.
     */
    public int[] getExitcodes() {
        return Arrays.copyOf(this.exitcodes, this.exitcodes.length);
    }

    // A task results is SUCCESS when it was able to execute
    // all the commands. Failure otherwise
    public enum RESULT {
        SUCCESS, FAILURE
    }

}
