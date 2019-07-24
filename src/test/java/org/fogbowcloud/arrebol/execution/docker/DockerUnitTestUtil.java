package org.fogbowcloud.arrebol.execution.docker;

import static org.mockito.Matchers.any;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.mockito.Mockito;

public class DockerUnitTestUtil {

    protected static final String defaultMockCommand = "echo Hello World";
    protected static final String defaultMockTaskId = "mockTaskId";
    protected static final String mockTsFilePath = "/tmp/" + defaultMockTaskId + ".ts";
    protected static final String mockEcFilePath = "/tmp/" + defaultMockTaskId + ".ts.ec";
    protected static final String mockExecInstanceId = "mockExecId";
    protected static final String mockEcFileContent = "0\r\n0\r\n0\r\n0\r\n0";
    protected static final Integer mockSuccessStatusCode = 0;
    protected static final Integer mockFailStatusCode = 1;

    protected static Task mockTask() {
        Command command = new Command(defaultMockCommand);
        List<Command> commands = Collections.nCopies(5, command);

        TaskSpec taskSpec = Mockito.mock(TaskSpec.class);
        Mockito.when(taskSpec.getImage()).thenReturn(null);
        Mockito.when(taskSpec.getCommands()).thenReturn(commands);

        Task task = new Task(defaultMockTaskId, taskSpec);
        return task;
    }

    protected static WorkerDockerRequestHelper mockWorkerDockerRequestHelper()
        throws UnsupportedEncodingException {
        WorkerDockerRequestHelper workerDockerRequestHelper = Mockito
            .mock(WorkerDockerRequestHelper.class);
        Mockito.when(workerDockerRequestHelper.start(any(TaskSpec.class)))
            .thenReturn("MockedContainerId");
        Mockito.when(workerDockerRequestHelper.getContainerName()).thenReturn("mockContainer");
        Mockito.doNothing().when(workerDockerRequestHelper).stopContainer();

        return workerDockerRequestHelper;
    }
}
