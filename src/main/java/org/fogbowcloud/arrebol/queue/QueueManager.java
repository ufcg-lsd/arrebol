package org.fogbowcloud.arrebol.queue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.ArrebolController;
import org.fogbowcloud.arrebol.datastore.managers.QueueDBManager;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobState;
import org.fogbowcloud.arrebol.models.task.Task;
import org.fogbowcloud.arrebol.models.task.TaskState;

public class QueueManager {

    private static final int COMMIT_PERIOD_MILLIS = 1000 * 10;
    private static final int UPDATE_PERIOD_MILLIS = 1000 * 10;
    private final Logger LOGGER = Logger.getLogger(QueueManager.class);
    private Map<String, Queue> queues;
    private final Timer queueDatabaseCommitter;
    private final Timer jobStateMonitor;

    public QueueManager(Map<String, Queue> queues) {
        this.queueDatabaseCommitter = new Timer(true);
        this.jobStateMonitor = new Timer(true);
        this.queues = queues;
    }

    public void addQueue(Queue queue){
        this.queues.put(queue.getId(), queue);
        QueueDBManager.getInstance().save((DefaultQueue) queue);
        LOGGER.info("Added new queue [" + queue.getId() + "]");
    }

    public Map<String, String> getQueuesNames() {
        Map<String, String> queuesNames = new HashMap<>();
        for(Entry<String, Queue> e : this.queues.entrySet()){
            queuesNames.put(e.getKey(), e.getValue().getName());
        }
        return queuesNames;
    }

    public void startQueue(String queueId){
        LOGGER.info("Starting queue [" + queueId + "]...");
        this.queues.get(queueId).start();
        this.queueDatabaseCommitter.schedule(new TimerTask() {
                                                 public void run() {
                                                     LOGGER.info("Commit job pool from queue [" + queueId + "] to the database");
                                                     QueueDBManager.getInstance().save((DefaultQueue) queues.get(queueId));
                                                 }
                                             }, COMMIT_PERIOD_MILLIS, COMMIT_PERIOD_MILLIS
        );

        this.jobStateMonitor.schedule(new TimerTask() {
                                          public void run() {
                                              LOGGER.info("Updating job states from queue [" + queueId + "]");
                                              for (Job job : queues.get(queueId).getJobs().values()) {
                                                  updateJobState(job);
                                              }
                                          }
                                      }, UPDATE_PERIOD_MILLIS, UPDATE_PERIOD_MILLIS
        );
    }

    public void addJob(String queue, Job job) {
        queues.get(queue).addJob(job);
    }

    public Job getJob(String queueId, String jobId){
        return QueueDBManager.getInstance().findOneJob(queueId, jobId);
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
