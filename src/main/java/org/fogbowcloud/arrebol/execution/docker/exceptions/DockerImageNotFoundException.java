/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class DockerImageNotFoundException extends RuntimeException {

  public DockerImageNotFoundException(String message) {
    super(message);
  }
}
