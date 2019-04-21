package org.fogbowcloud.arrebol.resource;

public interface TaskExecutionResult {

    //A task finishes successfully when all commands finish successfully,
    //otherwise it is a failure
    enum RESULT {SUCCESS, FAILURE};

    RESULT getResult();

    //TODO: command list state
}
