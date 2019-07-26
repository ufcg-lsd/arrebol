package org.fogbowcloud.arrebol.execution.docker;

import static org.mockito.Matchers.any;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.fogbowcloud.arrebol.execution.docker.request.ExecInstanceResult;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
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
    protected static final String mockContainerName = "mockContainerName";
    protected static final String mockAddress = "mockAddress";
    protected static final String mockEcFileContent = "0\r\n0\r\n0\r\n0\r\n0";
    protected static final int[] mockEcArray = {0, 0, 0, 0, 0};
    protected static final Integer mockSuccessStatusCode = 0;
    protected static final Integer mockFailStatusCode = 1;
    protected static final ExecInstanceResult mockSuccessExecInstanceResult =
            new ExecInstanceResult(mockExecInstanceId, mockSuccessStatusCode, false);
    protected static final ExecInstanceResult mockFailExecInstanceResult =
            new ExecInstanceResult(mockExecInstanceId, mockFailStatusCode, false);

    protected static Task mockTask() {
        Command command = new Command(mockCommand);
        List<Command> commands = Collections.nCopies(5, command);

        TaskSpec taskSpec = Mockito.mock(TaskSpec.class);
        Mockito.when(taskSpec.getImage()).thenReturn(null);
        Mockito.when(taskSpec.getCommands()).thenReturn(commands);

        Task task = new Task(mockTaskId, taskSpec);
        return task;
    }

    protected static WorkerDockerRequestHelper mockWorkerDockerRequestHelper()
            throws UnsupportedEncodingException {
        WorkerDockerRequestHelper workerDockerRequestHelper =
                Mockito.mock(WorkerDockerRequestHelper.class);
        Mockito.when(workerDockerRequestHelper.start(any(TaskSpec.class)))
                .thenReturn("mockContainerId");
        Mockito.when(workerDockerRequestHelper.getContainerName()).thenReturn("mockContainer");
        Mockito.doNothing().when(workerDockerRequestHelper).stopContainer();

        return workerDockerRequestHelper;
    }

    protected static String getFileContent(Resource file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            String content = IOUtils.toString(is, "UTF-8");
            return content;
        }
    }

    protected static String loadTaskScriptExecutor() throws IOException {
        Resource resource = new ClassPathResource(taskScriptExecutorName);
        return getFileContent(resource);
    }
}
