/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.resource;

/**
 * This interface represents a Docker container resource. It controls container initiation and
 * stopping. Contains an id to container and an API address from docker. The container start with a
 * {@link ContainerSpecification}, which specifies properties such as imageId, memory, and more for
 * the container.
 */
public interface DockerContainerResource {

  void start(ContainerSpecification containerSpecification) throws Exception;

  void stop() throws Exception;

  String getId();

  String getApiAddress();

  boolean isStarted();
}
