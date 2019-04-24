package org.fogbowcloud.arrebol.execution;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.resource.ResourceState;

import java.util.UUID;

public class ExecutionBroker {

    private final Logger logger = Logger.getLogger(ExecutionBroker.class);

    //Now, this broker is vey simple, e.g we do not keep state. In the future we may want to monitor execution

    public void execute(final Task task, final Worker worker) {

        logger.info("task={" + task + "} worker={" + worker + "}");

        //spawn a new thread to exec the task.
        String workerThreadId = "worker-tid-" + UUID.randomUUID().toString();

        new Thread(new Runnable() {

            @Override
            public void run() {

                TaskExecutionResult result = null;
                try {

                    //FIXME: we should not know the specific implementation here
                    result = new RawTaskExecutor().execute(task);

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
                            logger.error("inconsistent results for task={" + task + "} worker={" + worker + "}");
                            //TODO: I guess the task should be rolled be to its previous (before run) state
                        }
                    }
                } catch (Throwable t) {
                    logger.error("task={" + task + "} worker={" + worker + "}", t);
                }
            }

            private void onFinish() {
                logger.info("task={" + task + "} worker={" + worker + "}");
                task.setState(TaskState.FINISHED);
                worker.setState(ResourceState.IDLE);
            }

            private void onFailure() {
                //
            }
        },
        workerThreadId).start();
    }
}
