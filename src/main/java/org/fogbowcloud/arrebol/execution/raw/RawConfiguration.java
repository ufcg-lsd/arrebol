package org.fogbowcloud.arrebol.execution.raw;

import org.fogbowcloud.arrebol.models.configuration.Configuration;
import org.fogbowcloud.arrebol.models.configuration.Property;
import org.fogbowcloud.arrebol.execution.docker.constans.DockerConstants;

public class RawConfiguration {

    public Integer workerPoolSize;

    public RawConfiguration(Configuration configuration) throws Exception {
        checkRawConfigurationProperties(configuration);
        Property<Integer> workerPoolSize = configuration.getProperty(DockerConstants.JSON_WORKER_PULL_SIZE_KEY);
        this.workerPoolSize = workerPoolSize.getValue();
    }

    private void checkRawConfigurationProperties(Configuration configuration) throws Exception {
        String verifyMsg = " Please, verify your configuration file.";
        String workerPoolSizeMsg = "Worker pool size configuration property wrong or missing." + verifyMsg;

        Property<Integer> workerPoolSize = configuration.getProperty(DockerConstants.JSON_WORKER_PULL_SIZE_KEY);

        if (workerPoolSize.getValue() == null || workerPoolSize.getValue() == 0) {
            throw new Exception(workerPoolSizeMsg);
        }
    }

    public Integer getWorkerPoolSize() {
        return workerPoolSize;
    }
}
