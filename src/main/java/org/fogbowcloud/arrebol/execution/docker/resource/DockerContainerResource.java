package org.fogbowcloud.arrebol.execution.docker.resource;


public interface DockerContainerResource {

    void start(ContainerSpecification containerSpecification) throws Exception;
    void stop() throws Exception;
    String getId();
    String getApiAddress();

}
