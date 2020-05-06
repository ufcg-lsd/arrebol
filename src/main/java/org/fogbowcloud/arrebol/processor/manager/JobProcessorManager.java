package org.fogbowcloud.arrebol.processor.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
import org.fogbowcloud.arrebol.execution.Worker;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;
import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;
import org.fogbowcloud.arrebol.processor.JobProcessor;
import org.fogbowcloud.arrebol.processor.dto.DefaultJobProcessorDTO;
import org.fogbowcloud.arrebol.utils.Messages.Exceptions;
import org.fogbowcloud.arrebol.processor.exceptions.QueueNotFoundException;

public class JobProcessorManager {

    private static final int COMMIT_PERIOD_MILLIS = 1000 * 10;
    private static final int UPDATE_PERIOD_MILLIS = 1000 * 10;
    private final Logger LOGGER = Logger.getLogger(JobProcessorManager.class);
    private Map<String, JobProcessor> queues;
    private final Timer jobProcessorDatabaseCommitter;
    private final Timer jobStateMonitor;

    public JobProcessorManager(Map<String, JobProcessor> queues) {
        this.jobProcessorDatabaseCommitter = new Timer(true);
        this.jobStateMonitor = new Timer(true);
        this.queues = queues;
    }

    public void addJobProcessor(JobProcessor jobProcessor){
        this.queues.put(jobProcessor.getId(), jobProcessor);
        QueueDBManager.getInstance().save((DefaultJobProcessor) jobProcessor);
        LOGGER.info("Added new queue [" + jobProcessor.getId() + "]");
    }

    public List<DefaultJobProcessorDTO> getJobProcessors() {
        List<DefaultJobProcessorDTO> list = new ArrayList<>();
        for(JobProcessor jobProcessor : queues.values()) {
            DefaultJobProcessorDTO defaultJobProcessorDTO = new DefaultJobProcessorDTO((DefaultJobProcessor) jobProcessor);
            list.add(defaultJobProcessorDTO);
        }
        return list;
    }

    public DefaultJobProcessorDTO getJobProcessor(String queueId) {
        DefaultJobProcessor d = QueueDBManager.getInstance().findOne(queueId);
        if(Objects.isNull(d)) {
            throw new QueueNotFoundException(String.format(Exceptions.QUEUE_NOT_FOUND_PATTERN, queueId));
        }
        DefaultJobProcessorDTO defaultJobProcessorDTO = new DefaultJobProcessorDTO(d);
        return defaultJobProcessorDTO;
    }

    public void startJobProcessor(String queueId){
        LOGGER.info("Starting queue [" + queueId + "]...");
        this.queues.get(queueId).start();
        this.jobProcessorDatabaseCommitter.schedule(new TimerTask() {
                                                 public void run() {
                                                     LOGGER.info("Commit job pool from queue [" + queueId + "] to the database");
                                                     try {
                                                         QueueDBManager.getInstance().save((DefaultJobProcessor) queues.get(queueId));
                                                     } catch (Exception e) {
                                                         LOGGER.error("Error while saving job processor [" + queueId + "]: " + e.getMessage());
                                                     }
                                                 }
                                             }, COMMIT_PERIOD_MILLIS, COMMIT_PERIOD_MILLIS
        );

        this.jobStateMonitor.schedule(new TimerTask() {
                                          public void run() {
                                              LOGGER.info("Updating job states from queue [" + queueId + "]");
                                              try {
                                                  for (Job job : queues.get(queueId).getJobs().values()) {
                                                      updateJobState(job);
                                                  }
                                              } catch (Exception e) {
                                                  LOGGER.error("Error while updating job states from queue [" + queueId + "]: " + e.getMessage());
                                              }
                                          }
                                      }, UPDATE_PERIOD_MILLIS, UPDATE_PERIOD_MILLIS
        );
    }

    public void addWorkers(String queueId, Collection<Worker> workers) {
        if(!queues.containsKey(queueId)) {
            throw new QueueNotFoundException(String.format(Exceptions.QUEUE_NOT_FOUND_PATTERN, queueId));
        }
        LOGGER.info("Adding workers [" + workers.size() + "] to queue [" + queueId + "]");
        this.queues.get(queueId).addWorkers(workers);
    }

    public void addJob(String queueId, Job job) {
        if(!queues.containsKey(queueId)) {
            throw new QueueNotFoundException(String.format(Exceptions.QUEUE_NOT_FOUND_PATTERN, queueId));
        }

        DefaultJobProcessor defaultJobProcessor = QueueDBManager.getInstance().findOne(queueId);
        defaultJobProcessor.getJobs().put(job.getId(), job);

        QueueDBManager.getInstance().save(defaultJobProcessor);
        queues.get(queueId).addJob(job);
    }

    public Job getJob(String queueId, String jobId) {
        if(!queues.containsKey(queueId)) {
            throw new QueueNotFoundException(String.format(Exceptions.QUEUE_NOT_FOUND_PATTERN, queueId));
        }
        return QueueDBManager.getInstance().findOneJob(queueId, jobId);
    }

    public List<Job> getJobsByLabel(String queueId, String label) {
        if(!queues.containsKey(queueId)) {
            throw new QueueNotFoundException(String.format(Exceptions.QUEUE_NOT_FOUND_PATTERN, queueId));
        }
        return QueueDBManager.getInstance().getJobsByLabel(queueId, label);
    }

    //The arrebol does not change job state internally, so we need this workaround
    private void updateJobState(Job job) {
        JobState jobState = job.getJobState();
        if (!(jobState.equals(JobState.FAILED) || jobState.equals(JobState.FINISHED))) {
            if (all(job.getTasks(), TaskState.FAILED.getValue())) {
                job.setJobState(JobState.FAILED);
            } else if (all(job.getTasks(),
                TaskState.FINISHED.getValue() + TaskState.FAILED.getValue())) {
                job.setJobState(JobState.FINISHED);
            } else if (all(job.getTasks(), TaskState.PENDING.getValue())) {
                job.setJobState(JobState.QUEUED);
            } else {
                job.setJobState(JobState.RUNNING);
            }
        }
    }

    /**
     * Checks whether all tasks in the collection have only states that the mask represents.
     */
    private boolean all(Collection<Task> tasks, int mask) {
        for (Task t : tasks) {
            if ((t.getState().getValue() & mask) == 0) {
                return false;
            }
        }
        return true;
    }

}
