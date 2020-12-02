/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.resource;

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
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerContainerRequestHelper;
import org.fogbowcloud.arrebol.execution.docker.helpers.DockerImageRequestHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DefaultDockerContainerResourceTest {

  private DockerContainerRequestHelper containerRequestHelper;
  private DockerImageRequestHelper imageRequestHelper;
  private DefaultDockerContainerResource defaultDockerContainerResource;

  @Before
  public void setUp() throws Exception {
    containerRequestHelper = Mockito.mock(DockerContainerRequestHelper.class);
    Mockito.when(containerRequestHelper.createContainer(eq(MOCK_IMAGE_ID), Mockito.any(Map.class)))
        .thenReturn(MOCK_CONTAINER_NAME);

    imageRequestHelper = Mockito.mock(DockerImageRequestHelper.class);

    defaultDockerContainerResource =
        new DefaultDockerContainerResource(
            MOCK_CONTAINER_NAME, containerRequestHelper, imageRequestHelper);
  }

  @Test
  public void testSuccessStart() throws Exception {
    ContainerSpecification containerSpecification =
        new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
    defaultDockerContainerResource.start(containerSpecification);
  }

  @Test(expected = DockerStartException.class)
  public void testTwiceStart() throws UnsupportedEncodingException {
    ContainerSpecification containerSpecification =
        new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
    defaultDockerContainerResource.start(containerSpecification);
    defaultDockerContainerResource.start(containerSpecification);
  }

  @Test(expected = DockerImageNotFoundException.class)
  public void testStartWithNullImageId() throws UnsupportedEncodingException {
    ContainerSpecification containerSpecification =
        new ContainerSpecification(null, new HashMap<>());
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
            MOCK_CONTAINER_NAME, containerRequestHelper, imageRequestHelper);
    ContainerSpecification containerSpecification =
        new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
    defaultDockerContainerResource.start(containerSpecification);
  }

  @Test(expected = DockerStartException.class)
  public void testFailStartDockerContainer() throws UnsupportedEncodingException {
    Mockito.doThrow(new DockerStartException("Error while start docker container"))
        .when(containerRequestHelper)
        .startContainer();
    defaultDockerContainerResource =
        new DefaultDockerContainerResource(
            MOCK_CONTAINER_NAME, containerRequestHelper, imageRequestHelper);
    ContainerSpecification containerSpecification =
        new ContainerSpecification(MOCK_IMAGE_ID, new HashMap<>());
    defaultDockerContainerResource.start(containerSpecification);
  }

  @Test
  public void testSuccessStop() throws Exception {
    testSuccessStart();
    defaultDockerContainerResource.stop();
  }

  @Test(expected = DockerRemoveContainerException.class)
  public void testStopInNotStartedResource() {
    defaultDockerContainerResource.stop();
  }

  @Test(expected = DockerRemoveContainerException.class)
  public void testTwiceStop() throws Exception {
    testSuccessStart();
    defaultDockerContainerResource.stop();
    defaultDockerContainerResource.stop();
  }

  @Test(expected = DockerRemoveContainerException.class)
  public void testFailStop() throws Exception {
    Mockito.doThrow(new DockerRemoveContainerException("Error while remove docker container"))
        .when(containerRequestHelper)
        .removeContainer();
    testSuccessStop();
  }
}
