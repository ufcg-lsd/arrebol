package org.fogbowcloud.arrebol.execution;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.resource.ResourceState;
import org.fogbowcloud.arrebol.resource.RawWorker;
import org.fogbowcloud.arrebol.resource.ResourceObserver;
import org.fogbowcloud.arrebol.resource.TaskExecutionResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ExecutionBroker {

//    Logger logger = LogManager.getLogger(ExecutionBroker.class);

    private final Map<String, Execution> runningExecutions = new HashMap<String, Execution>();
    private final ResourceObserver resourceObserver;

    public ExecutionBroker(ResourceObserver resourceObserver) {
        this.resourceObserver = resourceObserver;
    }

    private void onFinish(String execId) {
        //critical region
//        logger.info("execId={}", execId);

        Execution exec = runningExecutions.remove(execId);

        Task task = exec.task;
        Resource resource = exec.resource;

        task.setState(TaskState.FINISHED);
        resource.setState(ResourceState.IDLE);

        resourceObserver.notifyAvailableResource(resource.getId(), resource.getPoolId());
    }

    private void onFailure(String execId) {
        //
    }

    public void execute(final Task task, final Resource resource) {

//        logger.info("task={} resource={}", task, resource);

        //spawn a new thread to execute the task. it reuses the Execution id
        final Execution exec = new Execution(task, resource);

        final String newExecThreadID = exec.executionId.toString();
        this.runningExecutions.put(newExecThreadID, exec);

        new Thread(new Runnable() {

            @Override
            public void run() {

                TaskExecutionResult result = null;
                try {
                    //FIXME: we should not know the specific implementation here
                    result = new RawWorker().execute(task);
                    switch (result.getResult()) {
                        case FAILURE: {
                            onFailure(newExecThreadID);
                            break;
                        }
                        case SUCCESS: {
                            onFinish(newExecThreadID);
                            break;
                        }
                        default: {
//                            logger.error("inconsistent results for execution={" + exec + "}");
                            //TODO: assert we have a proper result object
                            //TODO: I guess the task should be rolled be to its previous (before run) state
                        }
                    }
                } catch (Throwable t) {
//                    logger.error("Execution={" + exec + "} error={}", t);
                }
            }
        },
        "worker-tid-"+newExecThreadID).start();
    }

    private class Execution {
        //yep, it is just an struct

        private final Task task;
        private final Resource resource;
        private final UUID executionId;

        private Execution(Task task, Resource resource) {
            this.task = task;
            this.resource = resource;
            this.executionId = UUID.randomUUID();
        }

        @Override
        public String toString() {
            return "ExecId={" + executionId + "} task={" + task + "} resource={" + resource + "}";
        }
    }
}
