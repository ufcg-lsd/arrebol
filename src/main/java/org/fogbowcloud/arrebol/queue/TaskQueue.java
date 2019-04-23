package org.fogbowcloud.arrebol.queue;

import org.fogbowcloud.arrebol.models.task.Task;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class TaskQueue {

    private final String id;
    private final String name;
    private final Collection<Task> pendingTasks;

    public TaskQueue(String id, String name) {
        this.id = id;
        this.name = name;
        this.pendingTasks = Collections.synchronizedCollection(new LinkedList<Task>());
    }

    public boolean addTask(Task task) {
        return this.pendingTasks.add(task);
    }

    public boolean removeTask(Task task) {
        return this.pendingTasks.remove(task);
    }

    public Collection<Task> queue() {
        return new LinkedList<Task>(this.pendingTasks);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "id={" + getId() + "} name={" + getName() + "}";
    }
}
