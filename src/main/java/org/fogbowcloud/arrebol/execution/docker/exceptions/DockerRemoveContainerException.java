package org.fogbowcloud.arrebol.execution.docker.exceptions;

public class DockerRemoveContainerException extends RuntimeException {

    public DockerRemoveContainerException(String msg){
        super(msg);
    }
}
