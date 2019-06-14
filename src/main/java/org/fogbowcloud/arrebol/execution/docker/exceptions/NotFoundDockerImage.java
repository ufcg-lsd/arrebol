package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class NotFoundDockerImage extends RuntimeException {

    public NotFoundDockerImage(String message) {
        super(message);
    }
}
