package org.fogbowcloud.arrebol.resource;

import org.fogbowcloud.arrebol.core.models.task.Task;

public interface Worker {

    //see, now, the result is immutable and delivered after the
    //execution was finished. Then, as a result, we cannot track intermediate progress
    //we can improve this design later
    TaskExecutionResult execute(Task task);
}
