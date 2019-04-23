package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.resource.RawWorker;
import org.fogbowcloud.arrebol.resource.Resource;
import org.fogbowcloud.arrebol.resource.ResourceState;
import org.fogbowcloud.arrebol.resource.TaskExecutionResult;

import java.util.UUID;

public class ExecutionBroker {

    private final Logger logger = Logger.getLogger(ExecutionBroker.class);

    //Now, this broker is vey simple, e.g we do not keep state. In the future we may want to monitor execution

    public void execute(final Task task, final Resource resource) {

        logger.info("task={" + task + "} resource={" + resource + "}");

        //spawn a new thread to exec the task.
        String workerThreadId = "worker-tid-" + UUID.randomUUID().toString();

        new Thread(new Runnable() {

            @Override
            public void run() {

                TaskExecutionResult result = null;
                try {

                    //FIXME: we should not know the specific implementation here
                    result = new RawWorker().execute(task);

                    switch (result.getResult()) {
                        case FAILURE: {
                            onFailure();
                            break;
                        }
                        case SUCCESS: {
                            onFinish();
                            break;
                        }
                        default: {
                            logger.error("inconsistent results for task={" + task + "} resource={" + resource + "}");
                            //TODO: I guess the task should be rolled be to its previous (before run) state
                        }
                    }
                } catch (Throwable t) {
                    logger.error("task={" + task + "} resource={" + resource + "}", t);
                }
            }

            private void onFinish() {
                logger.info("task={" + task + "} resource={" + resource + "}");
                task.setState(TaskState.FINISHED);
                resource.setState(ResourceState.IDLE);
            }

            private void onFailure() {
                //
            }
        },
        workerThreadId).start();
    }
}
