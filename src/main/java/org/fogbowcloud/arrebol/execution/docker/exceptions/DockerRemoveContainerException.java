/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class DockerRemoveContainerException extends RuntimeException {

  public DockerRemoveContainerException(String message) {
    super(message);
  }
}
