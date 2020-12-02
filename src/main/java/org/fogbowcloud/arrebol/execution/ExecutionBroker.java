/* (C)2020 */
package org.fogbowcloud.arrebol.execution;

import java.util.UUID;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.resource.ResourceState;

public class ExecutionBroker {

  private final Logger logger = Logger.getLogger(ExecutionBroker.class);

  // Now, this broker is vey simple, e.g we do not keep state. In the future we may want to monitor
  // execution

  public void execute(final Task task, final Worker worker) {

    logger.info("task={" + task + "} worker={" + worker + "}");

    // spawn a new thread to exec the task.
    String workerThreadId = "worker-tid-" + UUID.randomUUID().toString();

    new Thread(
            new Runnable() {

              @Override
              public void run() {

                TaskExecutionResult result = null;
                try {

                  result = worker.execute(task);

                  switch (result.getResult()) {
                    case FAILURE:
                      {
                        onFailure();
                        break;
                      }
                    case SUCCESS:
                      {
                        onFinish();
                        break;
                      }
                    default:
                      {
                        logger.error(
                            "inconsistent results for task={" + task + "} worker={" + worker + "}");
                        // TODO: I guess the task should be rolled be to its previous (before run)
                        // state
                      }
                  }
                } catch (Throwable t) {
                  logger.error("task={" + task + "} worker={" + worker + "}", t);
                }
              }

              private void onFinish() {
                logger.info(
                    "task={" + task + "} worker={" + worker + "} has finished successfully");
                task.setState(TaskState.FINISHED);
                worker.setState(ResourceState.IDLE);
              }

              private void onFailure() {
                logger.info("task={" + task + "} worker={" + worker + "} has failed");
                task.setState(TaskState.FAILED);
                worker.setState(ResourceState.IDLE);
              }
            },
            workerThreadId)
        .start();
  }
}
