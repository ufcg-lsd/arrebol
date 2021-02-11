/* (C)2020 */
package org.fogbowcloud.arrebol.execution;

import java.util.Map;
import org.fogbowcloud.arrebol.models.task.Task;

public interface TaskExecutor {

  // see, now, the result is immutable and delivered after the
  // execution was finished. Then, as a result, we cannot track intermediate progress
  // we can improve this design later
  TaskExecutionResult execute(Task task);

  Map<String, String> getMetadata();
}
