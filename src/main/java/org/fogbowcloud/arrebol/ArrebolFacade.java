package org.fogbowcloud.arrebol;

import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.queue.Queue;
import org.fogbowcloud.arrebol.queue.spec.QueueSpec;

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

    public String createQueue(QueueSpec queueSpec){
        return arrebolController.createQueue(queueSpec);
    }

    public String addJob(String queue, Job job){
        return arrebolController.addJob(queue, job);
    }

    public Job getJob(String queueId, String jobId){
        return arrebolController.getJob(queueId, jobId);
    }

    public void stopJob(Job job){
        arrebolController.stopJob(job);
    }

    //TODO: do we really need this?
    public TaskState getTaskState(String taskId){
        return arrebolController.getTaskState(taskId);
    }

}
