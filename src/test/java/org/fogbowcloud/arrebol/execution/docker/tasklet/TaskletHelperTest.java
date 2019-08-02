package org.fogbowcloud.arrebol.execution.docker.tasklet;

import org.fogbowcloud.arrebol.execution.docker.DockerCommandExecutor;
import org.fogbowcloud.arrebol.models.task.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.*;
import static org.junit.Assert.*;

public class TaskletHelperTest {

    private Task task;
    private static String badFormattedSameLineContent;
    private static String emptyContent;
    private static String badFormattedContent;
    private static String wellFormattedContent;
    private static String taskScriptExecutorContent;
    private static String expectedWriteTsExecutorCommand;

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
    public void beforeEachTestMethod() {
        task = mockTask();
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
                        Mockito.eq(mockAddress),
                        Mockito.eq(mockContainerName),
                        Mockito.eq(expectedWriteTsExecutorCommand)))
                .thenReturn(mockSuccessExecInstanceResult);

        TaskletHelper taskletHelper = new TaskletHelper(mockAddress, mockContainerName);
        taskletHelper.setDockerCommandExecutor(dockerCommandExecutor);
        taskletHelper.sendTaskScriptExecutor(taskScriptExecutorContent);
    }

    @Test(expected = Exception.class)
    public void testFailCommandSendTaskScriptExecutor() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);

        Mockito.when(
                dockerCommandExecutor.executeCommand(
                        Mockito.eq(mockAddress),
                        Mockito.eq(mockContainerName),
                        Mockito.eq(expectedWriteTsExecutorCommand)))
                .thenReturn(mockFailExecInstanceResult);

        TaskletHelper taskletHelper = new TaskletHelper(mockAddress, mockContainerName);
        taskletHelper.setDockerCommandExecutor(dockerCommandExecutor);
        taskletHelper.sendTaskScriptExecutor(loadTaskScriptExecutor());
    }

    @Test
    public void testSendTaskScript() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
        String expectedWriteCommand = "echo '" + mockCommand + "' >> " + mockTsFilePath;
        Mockito.when(dockerCommandExecutor.executeCommand(
                    Mockito.eq(mockAddress),
                    Mockito.eq(mockContainerName),
                    Mockito.eq(expectedWriteCommand)))
                .thenReturn(mockSuccessExecInstanceResult);

        TaskletHelper taskletHelper = new TaskletHelper(mockAddress, mockContainerName);
        taskletHelper.setDockerCommandExecutor(dockerCommandExecutor);
        taskletHelper.sendTaskScript(task.getId(), task.getTaskSpec().getCommands());
    }

    @Test(expected = Exception.class)
    public void testFailSendTaskScript() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
        String expectedWriteCommand = "echo '" + mockCommand + "' >> " + mockTsFilePath;
        Mockito.when(dockerCommandExecutor.executeCommand(
                Mockito.eq(mockAddress),
                Mockito.eq(mockContainerName),
                Mockito.eq(expectedWriteCommand)))
                .thenReturn(mockFailExecInstanceResult);

        TaskletHelper taskletHelper = new TaskletHelper(mockAddress, mockContainerName);
        taskletHelper.setDockerCommandExecutor(dockerCommandExecutor);
        taskletHelper.sendTaskScript(task.getId(), task.getTaskSpec().getCommands());
    }

    @Test
    public void testGetExitCodes() throws Exception {
        DockerCommandExecutor dockerCommandExecutor = Mockito.mock(DockerCommandExecutor.class);
        String expectedCommand = "cat " + mockEcFilePath;
        Mockito.when(dockerCommandExecutor.executeCommandWithStout(
                Mockito.eq(mockAddress),
                Mockito.eq(mockContainerName),
                Mockito.eq(expectedCommand)))
                .thenReturn(mockEcFileContent);

        TaskletHelper taskletHelper = new TaskletHelper(mockAddress, mockContainerName);
        taskletHelper.setDockerCommandExecutor(dockerCommandExecutor);
        assertArrayEquals(mockEcArray, taskletHelper.getExitCodes(task.getId(), task.getTaskSpec().getCommands().size()));
    }
}