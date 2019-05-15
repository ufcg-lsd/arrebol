package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.remoteWorker.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.remoteWorker.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;

import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class RemoteDockerTaskExecutor implements TaskExecutor {

    private String imageId;
    private String containerName;
    private String address;
    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final Logger LOGGER = Logger.getLogger(RemoteDockerTaskExecutor.class);

    public RemoteDockerTaskExecutor(String imageId, String containerName, String address) {
        this.imageId = imageId;
        this.containerName = containerName;
        this.address = address;
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName, imageId);
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        this.start();
        TaskExecutionResult taskExecutionResult;
        LOGGER.info("Successful started container " + this.containerName);
        Command[] commands = getCommands(task);
        int[] commandsResults = new int[commands.length];
        Arrays.fill(commandsResults, TaskExecutionResult.UNDETERMINED_RESULT);

        LOGGER.info("Starting to execute commands of task " + task.getId());
        for (int i = 0; i < commands.length; i++) {
            Command c = commands[i];
            c.setState(CommandState.RUNNING);
            try {
                Integer exitCode = executeCommand(c);
                commandsResults[i] = exitCode;
                c.setState(CommandState.FINISHED);
            } catch (Throwable t) {
                c.setState(CommandState.FAILED);
            }

        }

        this.stop();

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (Command cmd : commands) {
            if (cmd.getState().equals(CommandState.FAILED)) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }

        LOGGER.info("Result of task" + task.getId() + ": " + result.toString());

        taskExecutionResult = new TaskExecutionResult(result, commandsResults, commands);
        return taskExecutionResult;
    }

    private String start(){
        return this.workerDockerRequestHelper.start();
    }

    private String stop(){
        return this.workerDockerRequestHelper.stop();
    }

    private Integer executeCommand(Command command){
        String execId = this.workerDockerRequestHelper.createExecInstance(command.getCommand());
        this.workerDockerRequestHelper.startExecInstance(execId);
        ExecInstanceResult execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
        while(execInstanceResult.getExitCode() == null){
            execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return execInstanceResult.getExitCode();
    }

    private Command[] getCommands(Task task){
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        int commandsSize = commandsList.size();
        Command[] commands = commandsList.toArray(new Command[commandsSize]);
        return commands;
    }


}
