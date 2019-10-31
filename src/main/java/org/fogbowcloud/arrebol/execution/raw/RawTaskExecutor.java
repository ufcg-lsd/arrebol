package org.fogbowcloud.arrebol.execution.raw;

import java.util.Map;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.fogbowcloud.arrebol.scheduler.SchedulerPolicy;
import java.io.IOException;
import java.util.List;

public class RawTaskExecutor implements TaskExecutor {

    private final Logger logger = Logger.getLogger(SchedulerPolicy.class);

    @Override
    public TaskExecutionResult execute(Task task) {

        logger.info("taskId={" + task.getId() + "}");

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;

        // converting to array to make bellow code simple?
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        for (Command cmd : commandsList) {
            try {
                cmd.setState(CommandState.RUNNING);
                int exitCode = executeCommand(cmd);
                cmd.setState(CommandState.FINISHED);
                cmd.setExitcode(exitCode);
                logger.debug("taskId={" + task.getId() + "} cmd={" + cmd + "} result={" + exitCode
                        + "}");
            } catch (Throwable t) {
                logger.error("taskId={" + task.getId() + "} cmd={" + cmd, t);
                cmd.setState(CommandState.FAILED);
                cmd.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
                result = TaskExecutionResult.RESULT.FAILURE;
            }
        }

        return new TaskExecutionResult(result, getExitCodes(commandsList));
    }

    //TODO Implement this method
    @Override
    public Map<String, String> getMetadata() {
        return null;
    }

    private int[] getExitCodes(List<Command> commands) {
        int[] exitcodes = new int[commands.size()];
        for (int i = 0; i < commands.size(); i++) {
            exitcodes[i] = commands.get(i).getExitcode();
        }
        return exitcodes;
    }

    private int executeCommand(Command command) throws IOException, InterruptedException {
        // TODO: there are plenty of room to improve this code: working directories, stdout/stderr
        // gathering, env vars
        String cmdStr = command.getCommand();
        ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmdStr);
        Process process = builder.start();
        int exitValue = process.waitFor();
        return exitValue;
    }

}
