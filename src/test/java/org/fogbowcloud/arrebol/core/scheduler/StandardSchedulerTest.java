package org.fogbowcloud.arrebol.core.scheduler;

import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;
import org.fogbowcloud.arrebol.core.monitors.TasksMonitor;
import org.fogbowcloud.arrebol.core.resource.models.Resource;
import org.junit.Assert;
import org.junit.Test;


public class StandardSchedulerTest {

    private final String FAKE_TASK_ID = "fake-task-id";
    private final String FAKE_TASK_UUID = "fake-task-uuid";
    private final String FAKE_RESOURCE_ID = "fake-resource-id";
/**
    @Test
    public void addTaskWhenNoResources() {
        Scheduler scheduler = getNewScheduler();

        Task task = createDefaultTask();
        scheduler.addTask(task);

        Assert.assertEquals(1, scheduler.getPendingTasks().size());
        Assert.assertEquals(scheduler.getPendingTasks().get(0), task);
    }

    @Test
    public void addTaskWhenMatchedResources() {
        Scheduler scheduler = getNewScheduler();

        Task task = createDefaultTask();
        Resource resource = createDefaultResource();

        scheduler.addTask(task);
        Assert.assertEquals(1, scheduler.getPendingTasks().size());

        addNewResource((ResourceObserver) scheduler, resource);
        Assert.assertEquals(0, scheduler.getPendingTasks().size());
    }

    @Test
    public void addTaskWhenThereIsNoMatchedResources() {
        Scheduler scheduler = getNewScheduler();

        Task task = createDefaultTask();
        Specification differentSpec = new Specification("spec-image", "spec-username", "spec-publicKey",
                "spec-privateKeyFilePath", "spec-userDataFile", "spec-userDataType");
        Resource resource = new FogbowResource(FAKE_RESOURCE_ID, differentSpec);;

        scheduler.addTask(task);
        Assert.assertEquals(1, scheduler.getPendingTasks().size());

        addNewResource((ResourceObserver) scheduler, resource);
        Assert.assertEquals(1, scheduler.getPendingTasks().size());
    }

    @Test
    public void stopTask() {
        Scheduler scheduler = getNewScheduler();

        Task task = createDefaultTask();
        Resource resource = createDefaultResource();

        scheduler.addTask(task);
        Assert.assertEquals(TaskState.PENDING, scheduler.getPendingTasks().get(0).getState());

        scheduler.stopTask(task);
        Assert.assertEquals(TaskState.CLOSED, scheduler.getPendingTasks().get(0).getState());
    }

    private Scheduler getNewScheduler() {
        ResourceStateTransitioner resourceStateTransitioner = new ResourceManager();
        TasksMonitor tasksMonitor = new TasksMonitor(resourceStateTransitioner);
        Scheduler scheduler = new StandardScheduler(tasksMonitor);
        return scheduler;
    }

    private void addNewResource(ResourceObserver scheduler, AbstractResource newResource) {
        scheduler.update(newResource);
    }

    private Task createDefaultTask() {
        return new Task(FAKE_TASK_ID, createDefaultSpecification(), FAKE_TASK_UUID);
    }

    private AbstractResource createDefaultResource() {
        return new FogbowResource(FAKE_RESOURCE_ID, createDefaultSpecification());
    }

    private Specification createDefaultSpecification() {
        return new Specification("fake-image", "fake-username", "fake-publicKey",
                "fake-privateKeyFilePath", "fake-userDataFile", "fake-userDataType");
    }*/

}
