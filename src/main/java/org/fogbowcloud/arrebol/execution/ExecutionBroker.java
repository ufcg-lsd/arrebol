package org.fogbowcloud.arrebol.execution;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.resource.TaskExecutionResult;
import org.fogbowcloud.arrebol.resource.Worker;

import java.util.*;

public class ExecutionBroker {

    private final Map<String, Execution> runningExecutions = new HashMap<String, Execution>();

    private void onFinish(String execId) {
        //TODO: change task state
        //TODO: change resource state
    }

    private void onFailure(String execId) {
        //
    }

    public void execute(final Task task, final Worker worker) {

        //spawn a new thread to execute the task. it reuses the Execution id
        Execution exec = new Execution(task, worker);
        final String newExecThreadID = exec.executionId.toString();

        this.runningExecutions.put(newExecThreadID, exec);

        new Thread(new Runnable() {

            @Override
            public void run() {

                TaskExecutionResult result = null;
                try {
                    result = worker.execute(task);
                } catch (Throwable t) {

                    switch (result.getResult()) {
                        case FAILURE: {
                            onFailure(newExecThreadID);
                        }
                        case SUCCESS: {
                            onFinish(newExecThreadID);
                        }
                        default: {
                            //TODO: assert we have a proper result object
                            //TODO: I guess the task should be rolled be to its previous (before run) state
                        }
                    }
                }
            }
        },
        newExecThreadID);
    }

    private class Execution {
        //yep, it is just an struct

        private final Task task;
        private final Worker worker;
        private final UUID executionId;

        private Execution(Task task, Worker worker) {
            this.task = task;
            this.worker = worker;
            this.executionId = UUID.randomUUID();
        }
    }
}
