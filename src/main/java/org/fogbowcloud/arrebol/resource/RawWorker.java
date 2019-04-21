package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.task.Task;

import java.io.IOException;
import java.util.Arrays;

public class RawWorker implements Worker {

    @Override
    public TaskExecutionResult execute(Task task) {

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;

        Command[] cmds = (Command[]) task.getAllCommands().toArray();

        int [] exitValues = new int[cmds.length];
        Arrays.fill(exitValues, TaskExecutionResult.UNDETERMINED_RESULT);

        for(int i = 0; i < cmds.length; i++) {
            try {
                exitValues[i] = executeCommand(cmds[i]);
            } catch (Throwable t) {
                result = TaskExecutionResult.RESULT.FAILURE;
            }
        }

        return new TaskExecutionResult(result, exitValues, cmds);
    }

    private int executeCommand(Command command) throws IOException, InterruptedException {

        //TODO: there are plenty of room to improve this code: working directories, stdout/stderr gathering, env vars
        String cmdStr = command.getCommand();
        Process process = Runtime.getRuntime().exec(cmdStr);
        int exitValue = process.waitFor();
        return exitValue;
    }
}
