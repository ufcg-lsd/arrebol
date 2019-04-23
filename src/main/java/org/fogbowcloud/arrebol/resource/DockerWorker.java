package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerWorker implements Worker {

    private String containerId;

    public DockerWorker(String containerId) {
        this.containerId = containerId;
    }

    @Override
    public TaskExecutionResult execute(Task task) {
        List<Command> commands = task.getCommands();
        int[] commandsResults = new int[commands.size()];
        for(int i = 0; i < commands.size(); i++) {
            Command c = commands.get(i);
            Map<String, String> status = executeCommand(c);
            if(status.containsKey("EXIT_CODE")) {
                commandsResults[i] = Integer.parseInt(status.get("EXIT_CODE"));
            }
        }
        TaskExecutionResult.RESULT result = TaskExecutionResult.RESULT.SUCCESS;
        for(int i : commandsResults) {
            if (i != 0) {
                result = TaskExecutionResult.RESULT.FAILURE;
            }

        }
        Command[] commandsArr = new Command[commands.size()];
        commandsArr = commands.toArray(commandsArr);
        TaskExecutionResult ter = new TaskExecutionResult(result, commandsResults, commandsArr);
        return ter;
    }

    private Map<String, String> executeCommand(String containerId, Command command){
        Map<String, String> output = new HashMap<String, String>();
        try {
            String commandString = command.getCommand();
            String[] cmd = {
                    "/bin/bash",
                    "-c",
                    "sudo docker exec -it " + containerId + " " + commandString
            };

            Process p = Runtime.getRuntime().exec(cmd);

            int exitCode = p.waitFor();

            output.put("EXIT_CODE", Integer.toString(exitCode));
            System.out.println("Result:");
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String result = "";
            String line = "";
            while ((line = reader.readLine()) != null) {
                result += line + System.lineSeparator();
            }
            output.put("RESULT", result);
        } catch(Exception e){
            e.printStackTrace();
        }
        return output;

    }
}
