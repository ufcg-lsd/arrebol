package org.fogbowcloud.arrebol.core.models;

import java.util.List;

public interface Task {
    String getId();

    void setState(TaskState newState);

    List<Command> getAllCommands();

    Specification getSpecification();

    String getUUID();

    void finish();
}
