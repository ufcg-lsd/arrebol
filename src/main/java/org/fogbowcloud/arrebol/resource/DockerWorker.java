package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.task.Task;

public class DockerWorker implements Worker {

    private String containerId;

    public DockerWorker(String containerId) {
        this.containerId = containerId;
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        int commandsSize = task.getCommands().size();
        Command[] commands = task.getCommands().toArray(new Command[commandsSize]);
        int[] commandsResults = new int[commandsSize];
        for(int i = 0; i < commandsSize; i++) {
            Command c = commands[i];
            Integer exitCode = executeCommand(this.containerId, c);
            commandsResults[i] = exitCode;
        }

        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for(int i : commandsResults) {
            if (i != 0) {
                result = TaskExecutionResult.RESULT.FAILURE;
                break;
            }
        }
        
        TaskExecutionResult taskExecutionResult = new TaskExecutionResult(result, commandsResults, commands);
        return taskExecutionResult;
    }

    private Integer executeCommand(String containerId, Command command){
        Integer exitCode = new Integer(111);
        try {
            String commandString = command.getCommand();
            String[] cmd = {
                    "/bin/bash",
                    "-c",
                    "sudo docker exec " + containerId + " " + commandString
            };

            Process p = Runtime.getRuntime().exec(cmd);

            exitCode = p.waitFor();

        } catch(Exception e){
            e.printStackTrace();
        }
        return exitCode;

    }

    public String getContainerId(){
        return this.containerId;
    }
}
