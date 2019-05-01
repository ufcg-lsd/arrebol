package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.scheduler.SchedulerPolicy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class RawTaskExecutor implements TaskExecutor {

    private final Logger logger = Logger.getLogger(SchedulerPolicy.class);

    @Override
    public TaskExecutionResult execute(Task task) {

        logger.info("taskId={" + task.getId() + "}");

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;

        //converting to array to make bellow code simple?
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        Command[] cmds = new Command[commandsList.size()];
        commandsList.toArray(cmds);

        int [] exitValues = new int[cmds.length];
        Arrays.fill(exitValues, TaskExecutionResult.UNDETERMINED_RESULT);

        for(int i = 0; i < cmds.length; i++) {
            Command commandToExec = cmds[i];
            try {
                commandToExec.setState(CommandState.RUNNING);
                exitValues[i] = executeCommand(commandToExec);
                commandToExec.setState(CommandState.FINISHED);
                logger.debug("taskId={" + task.getId() + "} cmd_index={" + i + "}" +
                        " cmd={" + cmds[i] + "} result={" + exitValues[i] + "}");
            } catch (Throwable t) {
                logger.error("taskId={" + task.getId() + "} cmd_index={" + i + "}" +
                        " cmd={" + cmds[i], t);
                commandToExec.setState(CommandState.FAILED);
                result = TaskExecutionResult.RESULT.FAILURE;
            }
        }

        return new TaskExecutionResult(result, exitValues, cmds);
    }

    private int executeCommand(Command command) throws IOException, InterruptedException {
        //TODO: there are plenty of room to improve this code: working directories, stdout/stderr gathering, env vars
        String cmdStr = command.getCommand();
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmdStr);
        Process process = builder.start();
        int exitValue = process.waitFor();
        return exitValue;
    }

}
