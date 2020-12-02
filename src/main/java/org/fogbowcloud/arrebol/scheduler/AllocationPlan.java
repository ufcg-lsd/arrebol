/* (C)2020 */
package org.fogbowcloud.arrebol.scheduler;

import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.task.Task;

public class AllocationPlan {

  // TODO: I do not quite remember what the STOP type mean (is it related to failures?!)
  public enum Type {
    RUN,
    STOP
  };

  private Task task;
  private Worker worker;
  private Type type;

  public AllocationPlan(Task t, Worker r, Type type) {
    this.task = t;
    this.worker = r;
    this.type = type;
  }

  public Task getTask() {
    return this.task;
  }

  public Worker getWorker() {
    return this.worker;
  }

  public Type getType() {
    return this.type;
  }

  @Override
  public String toString() {
    return "type={" + getType() + "}" + "task={" + getTask() + "} worker={" + getWorker() + "}";
  }
}
