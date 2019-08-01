package org.fogbowcloud.arrebol.execution.docker;

import static org.mockito.Matchers.any;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.resource.ContainerSpecification;
import org.fogbowcloud.arrebol.execution.docker.resource.DefaultDockerContainerResource;
import org.fogbowcloud.arrebol.execution.docker.resource.DockerContainerResource;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.specification.Specification;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class DockerUnitTestUtil {

    protected static final String taskScriptExecutorName = "task-script-executor.sh";
    protected static final String mockCommand = "echo Hello World";
    protected static final String mockTaskId = "mockTaskId";
    protected static final String mockTsFilePath = "/tmp/" + mockTaskId + ".ts";
    protected static final String mockEcFilePath = "/tmp/" + mockTaskId + ".ts.ec";
    protected static final String mockExecInstanceId = "mockExecId";
    protected static final String mockImageId = "mockImageId";
    public static final String mockContainerName = "mockContainerName";
    public static final String mockAddress = "mockAddress";
    public static final String mockEcFileContent = "0\r\n0\r\n0\r\n0\r\n0";
    public static final int[] mockEcArray = {0, 0, 0, 0, 0};
    protected static final Integer mockSuccessStatusCode = 0;
    protected static final Integer mockFailStatusCode = 1;
    protected static final ExecInstanceResult mockSuccessExecInstanceResult =
            new ExecInstanceResult(mockExecInstanceId, mockSuccessStatusCode, false);
    protected static final ExecInstanceResult mockFailExecInstanceResult =
            new ExecInstanceResult(mockExecInstanceId, mockFailStatusCode, false);
    protected static final TaskExecutionResult mockSuccessTaskExecutionResult = new TaskExecutionResult(
        RESULT.SUCCESS, mockEcArray);

    public static Task mockTask() {
        Command command = new Command(mockCommand);
        List<Command> commands = Collections.nCopies(5, command);

        Map<String, String> requirements = new HashMap<>();
        Specification specification = new Specification(null, requirements);
        TaskSpec taskSpec = Mockito.mock(TaskSpec.class);

        Mockito.when(taskSpec.getCommands()).thenReturn(commands);
        Mockito.when(taskSpec.getSpec()).thenReturn(specification);

        Task task = new Task(mockTaskId, taskSpec);

        return task;
    }

    protected static String getFileContent(Resource file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String content = IOUtils.toString(is, "UTF-8");
            return content;
        }
    }

    public static String loadTaskScriptExecutor() throws IOException {
        Resource resource = new ClassPathResource(taskScriptExecutorName);
        return getFileContent(resource);
    }

    protected static DockerContainerResource mockDockerContainerResource() throws Exception {
        DockerContainerResource dockerContainerResource = Mockito.mock(
            DefaultDockerContainerResource.class);
        Mockito.when(dockerContainerResource.getId()).thenReturn(mockContainerName);
        Mockito.doNothing().when(dockerContainerResource).start(Mockito.any(ContainerSpecification.class));
        Mockito.doNothing().when(dockerContainerResource).stop();
        return dockerContainerResource;
    }

    public static boolean isAll(List<Command> commands, CommandState commandState) {
        boolean isAll = true;
        for (Command c : commands) {
            if (!c.getState().equals(commandState)) {
                isAll = false;
                break;
            }
        }
        return isAll;
    }

}
