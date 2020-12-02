/* (C)2020 */
package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class DockerStartException extends RuntimeException {

  public DockerStartException(String message) {
    super(message);
  }
}
