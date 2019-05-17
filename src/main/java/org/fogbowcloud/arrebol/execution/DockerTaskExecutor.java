package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.dockerworker.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.dockerworker.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;


import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class DockerTaskExecutor implements TaskExecutor {

    private String imageId;
    private String containerName;


    private WorkerDockerRequestHelper workerDockerRequestHelper;
    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);

    public DockerTaskExecutor(String imageId, String containerName, String address) {
        this.imageId = imageId;
        this.containerName = containerName;
        this.workerDockerRequestHelper = new WorkerDockerRequestHelper(address, containerName, imageId);
    }

    @Override
    public TaskExecutionResult execute(Task task){
        //FIXME: We should catch the errors when starting/finishing the container and move the task to its FAILURE state
        //FIXME: also, follow the SAME log format we used in the RawTaskExecutor
        TaskExecutionResult taskExecutionResult;
        Integer startStatus = this.start();

        if(startStatus != 0){
            LOGGER.error("Exit code from container start: " + startStatus);
            throw new DockerStartException("Could not start container " + getContainerName());
        }

        LOGGER.info("Successful started container " + getContainerName());
        Command[] commands = getCommands(task);
        LOGGER.info("Starting to execute commands [" + commands.length + "] of task " + task.getId());
        int[] commandsResults = executeCommands(commands);

        Integer stopStatus = this.stop();
        if(stopStatus != 0){
            LOGGER.error("Exit code from container stop: " + stopStatus);
        }

        taskExecutionResult = getTaskResult(commands, commandsResults);

        LOGGER.info("Result of task [" + task.getId() + "]: " + taskExecutionResult.getResult().toString());
        return taskExecutionResult;
    }

    protected Command[] getCommands(Task task){
        TaskSpec taskSpec = task.getTaskSpec();
        List<Command> commandsList = taskSpec.getCommands();

        int commandsSize = commandsList.size();
        Command[] commands = commandsList.toArray(new Command[commandsSize]);
        return commands;
    }

    protected int[] executeCommands(Command[] commands){
        int[] commandsResults = new int[commands.length];
        Arrays.fill(commandsResults, TaskExecutionResult.UNDETERMINED_RESULT);
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
        return commandsResults;
    }

    protected TaskExecutionResult getTaskResult(Command[] commands, int[] commandsResults){
        TaskExecutionResult taskExecutionResult;
        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for (Command cmd : commands) {
            if (cmd.getState().equals(CommandState.FAILED)) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }
        taskExecutionResult = new TaskExecutionResult(result, commandsResults, commands);
        return taskExecutionResult;
    }

    protected Integer start(){
        try {
            LOGGER.info("Starting DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.start();
            return new Integer(0);
        } catch (Exception e) {
            return new Integer(127);
        }
    }

    protected Integer stop(){
        try {
            LOGGER.info("Stopping DockerTaskExecutor " + this.getContainerName());
            this.workerDockerRequestHelper.stop();
            return new Integer(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new Integer(127);
        }
    }

    protected Integer executeCommand(Command command){
        try {
            LOGGER.info("Executing command [" + command.getCommand() + "] in worker [" + this.getContainerName() + "].");
            String execId = this.workerDockerRequestHelper.createExecInstance(command.getCommand());
            this.workerDockerRequestHelper.startExecInstance(execId);
            ExecInstanceResult execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
            while(execInstanceResult.getExitCode() == null){
                execInstanceResult = this.workerDockerRequestHelper.inspectExecInstance(execId);
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LOGGER.info("Executed command [" + command.getCommand() + "] with exitcode=[" + execInstanceResult.getExitCode() + "] in worker [" + this.getContainerName() + "].");
            return execInstanceResult.getExitCode();
        } catch(Exception e){
            e.printStackTrace();
            return new Integer(127);
        }
    }

    @Override
    public String toString() {
        return "DockerTaskExecutor imageId={" + getImageId() + "} containerName={" + getContainerName() + "}";
    }

    public String getContainerName(){
        return this.containerName;
    }

    public String getImageId(){
        return this.imageId;
    }


}
