package org.fogbowcloud.arrebol.execution.docker;

import java.io.IOException;
import java.io.InputStream;
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

    public static final String TASK_SCRIPT_EXECUTOR_NAME = "task-script-executor.sh";
    public static final String MOCK_COMMAND = "echo Hello World";
    public static final String MOCK_TASK_ID = "mockTaskId";
    public static final String MOCK_TS_FILE_PATH = "/tmp/" + MOCK_TASK_ID + ".ts";
    public static final String MOCK_EC_FILE_PATH = "/tmp/" + MOCK_TASK_ID + ".ts.ec";
    public static final String MOCK_EXEC_INSTANCE_ID = "mockExecId";
    public static final String MOCK_IMAGE_ID = "mockImageId";
    public static final String MOCK_CONTAINER_NAME = "mockContainerName";
    public static final String MOCK_ADDRESS = "mockAddress";
    public static final String MOCK_EC_FILE_CONTENT = "0\r\n0\r\n0\r\n0\r\n0";
    public static final int[] MOCK_EC_ARRAY = {0, 0, 0, 0, 0};
    public static final Integer MOCK_SUCCESS_STATUS_CODE = 0;
    public static final Integer MOCK_FAIL_STATUS_CODE = 1;
    public static final ExecInstanceResult MOCK_SUCCESS_EXEC_INSTANCE_RESULT =
            new ExecInstanceResult(MOCK_EXEC_INSTANCE_ID, MOCK_SUCCESS_STATUS_CODE, false);
    public static final ExecInstanceResult MOCK_FAIL_EXEC_INSTANCE_RESULT =
            new ExecInstanceResult(MOCK_EXEC_INSTANCE_ID, MOCK_FAIL_STATUS_CODE, false);
    public static final TaskExecutionResult MOCK_SUCCESS_TASK_EXECUTION_RESULT = new TaskExecutionResult(
        RESULT.SUCCESS, MOCK_EC_ARRAY);

    public static Task mockTask() {
        Command command = new Command(MOCK_COMMAND);
        List<Command> commands = Collections.nCopies(5, command);

        Map<String, String> requirements = new HashMap<>();
        Specification specification = new Specification(null, requirements);
        TaskSpec taskSpec = Mockito.mock(TaskSpec.class);

        Mockito.when(taskSpec.getCommands()).thenReturn(commands);
        Mockito.when(taskSpec.getSpec()).thenReturn(specification);

        Task task = new Task(MOCK_TASK_ID, taskSpec);

        return task;
    }

    public static String getFileContent(Resource file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String content = IOUtils.toString(is, "UTF-8");
            return content;
        }
    }

    public static String loadTaskScriptExecutor() throws IOException {
        Resource resource = new ClassPathResource(TASK_SCRIPT_EXECUTOR_NAME);
        return getFileContent(resource);
    }

    protected static DockerContainerResource mockDockerContainerResource() throws Exception {
        DockerContainerResource dockerContainerResource = Mockito.mock(
            DefaultDockerContainerResource.class);
        Mockito.when(dockerContainerResource.getId()).thenReturn(MOCK_CONTAINER_NAME);
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
