package org.fogbowcloud.arrebol.core.models;

public interface Task {
    String getId();

    void setState(TaskState newState);
}
