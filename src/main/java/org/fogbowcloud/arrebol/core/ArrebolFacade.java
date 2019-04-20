package org.fogbowcloud.arrebol.core;

import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.job.JobSpec;
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

    public String addJob(Job job){
        return arrebolController.addJob(job);
    }

    public void stopJob(Job job){
        arrebolController.stopJob(job);
    }

    public Job getJob(String id) {return arrebolController.getJob(id);}

    public TaskState getTaskState(String taskId){
        return arrebolController.getTaskState(taskId);
    }

}
