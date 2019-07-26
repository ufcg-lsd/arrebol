package org.fogbowcloud.arrebol.execution.docker;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.getFileContent;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.loadTaskScriptExecutor;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockCommand;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockEcArray;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockEcFileContent;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockEcFilePath;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockExecInstanceId;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockFailExecInstanceResult;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockSuccessExecInstanceResult;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTask;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockTsFilePath;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.mockWorkerDockerRequestHelper;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.taskScriptExecutorName;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.fogbowcloud.arrebol.execution.docker.request.WorkerDockerRequestHelper;
import org.fogbowcloud.arrebol.models.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

public class DockerExecutorHelperTest {

    private static String badFormattedSameLineContent;
    private static String emptyContent;
    private static String badFormattedContent;
    private static String wellFormattedContent;
    private static String taskScriptExecutorContent;
    private static String expectedWriteTsExecutorCommand;

    private Task task;

    @BeforeClass
    public static void setup() throws IOException {
        final String TASK_SCRIPT_EXAMPLES_DIRECTORY = "task-script-examples/";
        final String EXIT_CODES_SAME_LINE_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "ec-in-same-line.ts.ec";
        final String EMPTY_EXAMPLE_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "empty-example.ts.ec";
        final String GAP_IN_EXIT_CODES_FILE_PATH =
                TASK_SCRIPT_EXAMPLES_DIRECTORY + "gap-in-ecs.ts.ec";
        final String WELL_FORMED_FILE_PATH = TASK_SCRIPT_EXAMPLES_DIRECTORY + "well-formed.ts.ec";

        badFormattedSameLineContent =
                getFileContent(new ClassPathResource(EXIT_CODES_SAME_LINE_FILE_PATH));
        emptyContent = getFileContent(new ClassPathResource(EMPTY_EXAMPLE_FILE_PATH));
        badFormattedContent = getFileContent(new ClassPathResource(GAP_IN_EXIT_CODES_FILE_PATH));
        wellFormattedContent = getFileContent(new ClassPathResource(WELL_FORMED_FILE_PATH));
        taskScriptExecutorContent = loadTaskScriptExecutor();
        expectedWriteTsExecutorCommand =
                "echo '" + taskScriptExecutorContent + "' > /tmp/" + taskScriptExecutorName;
    }

    @Before
    public void init() {
        this.task = mockTask();
    }

    @Test
    public void testNotEmptyContent() {
        Assert.assertFalse(wellFormattedContent.isEmpty());
        Assert.assertFalse(badFormattedContent.isEmpty());
        Assert.assertFalse(badFormattedSameLineContent.isEmpty());
    }

    @Test
    public void testEmptyContent() {
        Assert.assertTrue(emptyContent.isEmpty());
    }

    @Test
    public void testSendTaskScriptExecutor() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);

        Mockito.when(
                        dockerCommandExecutor.executeCommand(
                                Mockito.eq(expectedWriteTsExecutorCommand)))
                .thenReturn(mockSuccessExecInstanceResult);

        DockerExecutorHelper dockerExecutorHelper =
                mockSpyDockerExecutorHelper(dockerCommandExecutor);
        dockerExecutorHelper.sendTaskExecutorScript();
    }

    @Test(expected = Exception.class)
    public void testFailCommandSendTaskScriptExecutor() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);

        Mockito.when(
                        dockerCommandExecutor.executeCommand(
                                Mockito.eq(expectedWriteTsExecutorCommand)))
                .thenReturn(mockFailExecInstanceResult);

        DockerExecutorHelper dockerExecutorHelper =
                mockSpyDockerExecutorHelper(dockerCommandExecutor);
        dockerExecutorHelper.sendTaskExecutorScript();
    }

    @Test
    public void testSendTaskScript() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
        String expectedWriteCommand = "echo '" + mockCommand + "' >> " + mockTsFilePath;
        Mockito.when(dockerCommandExecutor.executeCommand(Mockito.eq(expectedWriteCommand)))
                .thenReturn(mockSuccessExecInstanceResult);

        DockerExecutorHelper dockerExecutorHelper =
                mockSpyDockerExecutorHelper(dockerCommandExecutor);
        dockerExecutorHelper.writeTaskScript(task.getTaskSpec().getCommands(), mockTsFilePath);
    }

    @Test(expected = Exception.class)
    public void testFailSendTaskScript() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
        String expectedWriteCommand = "echo '" + mockCommand + "' >> " + mockTsFilePath;
        Mockito.when(dockerCommandExecutor.executeCommand(Mockito.eq(expectedWriteCommand)))
                .thenReturn(mockFailExecInstanceResult);

        DockerExecutorHelper dockerExecutorHelper =
                mockSpyDockerExecutorHelper(dockerCommandExecutor);
        dockerExecutorHelper.writeTaskScript(task.getTaskSpec().getCommands(), mockTsFilePath);
    }

    @Test
    public void testGetEcFile() throws Exception {
        WorkerDockerRequestHelper workerDockerRequestHelper = mockWorkerDockerRequestHelper();
        String commandToGetFile = String.format("cat %s", mockEcFilePath);
        Mockito.when(workerDockerRequestHelper.createExecInstance(commandToGetFile, true, true))
                .thenReturn(mockExecInstanceId);
        Mockito.when(workerDockerRequestHelper.startExecInstance(mockExecInstanceId))
                .thenReturn(mockEcFileContent);
        Mockito.when(workerDockerRequestHelper.inspectExecInstance(mockExecInstanceId))
                .thenReturn(mockSuccessExecInstanceResult);

        DockerExecutorHelper dockerExecutorHelper =
                Mockito.spy(
                        new DockerExecutorHelper(
                                taskScriptExecutorContent, workerDockerRequestHelper));
        Assert.assertEquals(mockEcFileContent, dockerExecutorHelper.getEcFile(mockEcFilePath));
    }

    @Test
    public void testParseEcContentToArray() throws UnsupportedEncodingException {
        DockerExecutorHelper dockerExecutorHelper = mockSpyDockerExecutorHelper();
        int[] result =
                dockerExecutorHelper.parseEcContentToArray(
                        mockEcFileContent, task.getTaskSpec().getCommands().size());
        Assert.assertArrayEquals(mockEcArray, result);
    }

    private DockerExecutorHelper mockSpyDockerExecutorHelper() throws UnsupportedEncodingException {
        return this.mockSpyDockerExecutorHelper(null);
    }

    private DockerExecutorHelper mockSpyDockerExecutorHelper(
            DockerCommandExecutor dockerCommandExecutor) throws UnsupportedEncodingException {
        WorkerDockerRequestHelper workerDockerRequestHelper = mockWorkerDockerRequestHelper();
        DockerExecutorHelper dockerExecutorHelper =
                Mockito.spy(
                        new DockerExecutorHelper(
                                taskScriptExecutorContent, workerDockerRequestHelper));
        dockerExecutorHelper.setDockerCommandExecutor(dockerCommandExecutor);
        return dockerExecutorHelper;
    }
}
