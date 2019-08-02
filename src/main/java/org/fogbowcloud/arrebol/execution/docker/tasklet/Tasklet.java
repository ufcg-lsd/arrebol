package org.fogbowcloud.arrebol.execution.docker.tasklet;

import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.models.task.Task;


public interface Tasklet {

    TaskExecutionResult execute(Task task);
}
