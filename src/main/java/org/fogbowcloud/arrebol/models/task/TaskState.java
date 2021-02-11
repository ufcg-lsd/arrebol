/* (C)2020 */
package org.fogbowcloud.arrebol.models.task;

public enum TaskState {
  PENDING(1),
  RUNNING(2),
  FINISHED(4),
  FAILED(8);

  private int value;

  TaskState(int id) {
    this.value = id;
  }

  public int getValue() {
    return this.value;
  }
}
