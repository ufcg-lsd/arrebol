package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.TaskState;

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

    public String addJob(String queue, Job job){
        return arrebolController.addJob(queue, job);
    }

    public void stopJob(Job job){
        arrebolController.stopJob(job);
    }

    //TODO: do we really need this?
    public TaskState getTaskState(String taskId){
        return arrebolController.getTaskState(taskId);
    }

}
