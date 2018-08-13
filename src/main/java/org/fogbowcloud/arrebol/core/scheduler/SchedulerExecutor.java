package org.fogbowcloud.arrebol.core.scheduler;


import org.fogbowcloud.arrebol.core.models.Task;
import org.fogbowcloud.arrebol.core.models.TaskStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchedulerExecutor implements  Runnable {

    private Scheduler scheduler;
    private Map<Task, TaskStatus> taskPool = new ConcurrentHashMap<Task, TaskStatus>();

    public SchedulerExecutor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void run() {

    }
}
