package org.fogbowcloud.arrebol.execution.docker.resource;

import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_ADDRESS;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_CONTAINER_NAME;
import static org.fogbowcloud.arrebol.execution.docker.DockerUnitTestUtil.MOCK_IMAGE_ID;
import static org.mockito.Matchers.eq;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerCreateContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerImageNotFoundException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerRemoveContainerException;
import org.fogbowcloud.arrebol.execution.docker.exceptions.DockerStartException;
import org.fogbowcloud.arrebol.execution.docker.request.ContainerRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.request.HttpWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpMethod;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpWrapper.class})
public class DefaultDockerContainerResourceTest {

    private ContainerRequestHelper containerRequestHelper;
    private DefaultDockerContainerResource defaultDockerContainerResource;

    @Before
    public void setUp() throws Exception {
        containerRequestHelper = Mockito.mock(ContainerRequestHelper.class);
        Mockito.when(containerRequestHelper.createContainer(eq(MOCK_IMAGE_ID), Mockito.any(Map.class)))
            .thenReturn(MOCK_CONTAINER_NAME);

        PowerMockito.mockStatic(HttpWrapper.class);
        String expectedEndpoint = "mockAddress/images/create?fromImage=mockImageId:latest";
        Mockito.when(HttpWrapper.doRequest(HttpMethod.POST.name(), expectedEndpoint))
            .thenReturn("Pulled");

        defaultDockerContainerResource =
            new DefaultDockerContainerResource(
                MOCK_CONTAINER_NAME, MOCK_ADDRESS, containerRequestHelper);
    }

    @Test
    public void testSuccessStart() throws Exception {
        ContainerSpecification containerSpecification = new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
        defaultDockerContainerResource.start(containerSpecification);
    }

    @Test(expected = DockerImageNotFoundException.class)
    public void testStartWithNullImageId() throws UnsupportedEncodingException {
        ContainerSpecification containerSpecification = new ContainerSpecification(null, new HashMap<>());
        defaultDockerContainerResource.start(containerSpecification);
    }

    @Test
    public void testStartWithNullRequirements() throws UnsupportedEncodingException {
        ContainerSpecification containerSpecification = new ContainerSpecification(MOCK_IMAGE_ID, null);
        defaultDockerContainerResource.start(containerSpecification);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStartWithNullContainerSpecification() throws UnsupportedEncodingException {
        defaultDockerContainerResource.start(null);
    }

    @Test(expected = DockerCreateContainerException.class)
    public void testFailCreateDockerContainer() throws UnsupportedEncodingException {
        Mockito.when(containerRequestHelper.createContainer(eq(MOCK_IMAGE_ID), Mockito.any(Map.class)))
            .thenThrow(new DockerCreateContainerException("Error while create docker container"));
        defaultDockerContainerResource =
            new DefaultDockerContainerResource(
                MOCK_CONTAINER_NAME, MOCK_ADDRESS, containerRequestHelper);
        ContainerSpecification containerSpecification = new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
        defaultDockerContainerResource.start(containerSpecification);
    }

    @Test(expected = DockerStartException.class)
    public void testFailStartDockerContainer() throws UnsupportedEncodingException {
        Mockito.doThrow(new DockerStartException("Error while start docker container"))
            .when(containerRequestHelper).startContainer();
        defaultDockerContainerResource =
            new DefaultDockerContainerResource(
                MOCK_CONTAINER_NAME, MOCK_ADDRESS, containerRequestHelper);
        ContainerSpecification containerSpecification = new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
        defaultDockerContainerResource.start(containerSpecification);
    }

    @Test
    public void testSuccessStop() throws Exception {
        testSuccessStart();
        defaultDockerContainerResource.stop();
    }

    @Test(expected = DockerRemoveContainerException.class)
    public void testFailStop() throws Exception {
        Mockito.doThrow(new DockerRemoveContainerException("Error while remove docker container"))
            .when(containerRequestHelper).removeContainer();
        testSuccessStop();
    }
}