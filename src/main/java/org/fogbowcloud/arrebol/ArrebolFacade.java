package org.fogbowcloud.arrebol;

import java.util.List;
import java.util.Map;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.processor.dto.DefaultJobProcessorDTO;
import org.fogbowcloud.arrebol.processor.spec.JobProcessorSpec;
import org.fogbowcloud.arrebol.processor.spec.WorkerNode;

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

    public String createQueue(JobProcessorSpec jobProcessorSpec){
        return arrebolController.createQueue(jobProcessorSpec);
    }

    public List<DefaultJobProcessorDTO> getQueues(){
        return arrebolController.getQueues();
    }

    public DefaultJobProcessorDTO getQueue(String queueId) {
        return arrebolController.getQueue(queueId);
    }

    public void addWorkers(String queueId, WorkerNode workerNode) {
        arrebolController.addWorkers(queueId, workerNode);
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
