package org.fogbowcloud.arrebol.core;

import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskState;

public class ArrebolFacade {

    private ArrebolController arrebolController;

    public ArrebolFacade(ArrebolController arrebolController){
        this.arrebolController = arrebolController;
    }

    public void start(){
        arrebolController.start();
    }

    public  void stop(){
        arrebolController.stop();
    }

    public void addTask(Task task){
        arrebolController.addTask(task);
    }

    public void stopTask(Task task){
        arrebolController.stopTask(task);
    }

    public TaskState getTaskState(String taskId){
        return arrebolController.getTaskState(taskId);
    }

}
