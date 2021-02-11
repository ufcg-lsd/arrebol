/* (C)2020 */
package org.fogbowcloud.arrebol.models.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;

@Entity
public class Task implements Serializable {

  private static final long serialVersionUID = -6111900503456749695L;

  @Id private String id;

  @Enumerated(EnumType.STRING)
  private TaskState state;

  @OneToOne(cascade = CascadeType.ALL)
  @JsonProperty("tasks_specs")
  private TaskSpec taskSpec;

  public Task(String id, TaskSpec taskSpec) {
    this.id = id;
    this.taskSpec = taskSpec;
    this.state = TaskState.PENDING;
  }

  Task() {
    // Default constructor
  }

  public String getId() {
    return this.id;
  }

  public TaskState getState() {
    return this.state;
  }

  public void setState(TaskState newState) {
    this.state = newState;
  }

  public TaskSpec getTaskSpec() {
    return taskSpec;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(id, task.id)
        && state == task.state
        && Objects.equals(taskSpec, task.taskSpec);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, state, taskSpec);
  }

  @Override
  public String toString() {
    return "id={" + getId() + "}  state={" + getState() + "} taskSpec={" + getTaskSpec() + "}";
  }
}
