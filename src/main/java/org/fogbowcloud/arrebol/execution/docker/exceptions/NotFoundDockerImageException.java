package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class NotFoundDockerImageException extends RuntimeException {

    public NotFoundDockerImageException(String message) {
        super(message);
    }
}
