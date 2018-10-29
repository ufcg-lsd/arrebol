package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.resource.Resource;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.pools.resource.ResourceObserver;
import org.fogbowcloud.arrebol.pools.resource.ResourcePoolManager;
import org.fogbowcloud.arrebol.pools.resource.ResourceStateTransitioner;
import org.junit.Assert;
import org.junit.Test;


public class StandardSchedulerTest {

    private final String FAKE_TASK_ID = "fake-task-id";
    private final String FAKE_TASK_UUID = "fake-task-uuid";


    @Test
    public void addTaskWhenNoResources() {
        Scheduler scheduler = getNewScheduler();

        Task task = createDefaultTask();
        scheduler.addTask(task);

        Assert.assertEquals(scheduler.getPendingTasks().size(), 1);
        Assert.assertEquals(scheduler.getPendingTasks().get(0), task);
    }

    @Test
    public void addTaskWhenMatchedResources() {
        // TODO
    }

    @Test
    public void stopTask() {
        // TODO
    }

    @Test
    public void getPendingTasks() {
        // TODO
    }

    private Scheduler getNewScheduler() {
        ResourceStateTransitioner resourceStateTransitioner = new ResourcePoolManager();
        TasksMonitor tasksMonitor = new TasksMonitor(resourceStateTransitioner);
        Scheduler scheduler = new StandardScheduler(tasksMonitor);
        return scheduler;
    }

    private void addNewResource(ResourceObserver scheduler, Resource newResource) {
        scheduler.update(newResource);
    }

    private Task createDefaultTask() {
        return new Task(FAKE_TASK_ID, createDefaultSpecification(), FAKE_TASK_UUID);
    }

    private Specification createDefaultSpecification() {
        return new Specification("fake-image", "fake-username", "fake-publicKey",
                "fake-privateKeyFilePath", "fake-userDataFile", "fake-userDataType");
    }


}
