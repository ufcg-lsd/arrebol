package org.fogbowcloud.arrebol.core;

import org.fogbowcloud.arrebol.core.models.job.JDFJob;
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

    public String addJob(JDFJob job){
        return arrebolController.addJob(job);
    }

    public void stopJob(JDFJob job){
        arrebolController.stopJob(job);
    }

    public TaskState getTaskState(String taskId){
        return arrebolController.getTaskState(taskId);
    }

}
