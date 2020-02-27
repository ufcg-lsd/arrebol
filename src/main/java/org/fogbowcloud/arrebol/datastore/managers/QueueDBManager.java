package org.fogbowcloud.arrebol.datastore.managers;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.datastore.repositories.DefaultQueueRepository;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.processor.DefaultJobProcessor;

public class QueueDBManager {

    private final Logger LOGGER = Logger.getLogger(QueueDBManager.class);
    private static QueueDBManager instance;

    private DefaultQueueRepository defaultQueueRepository;

    private QueueDBManager() {
    }

    public synchronized static QueueDBManager getInstance() {
        if (instance == null) {
            instance = new QueueDBManager();
        }
        return instance;
    }

    public synchronized void save(DefaultJobProcessor queue) {
        String s = String.format("Searching queue %s", queue.getId());
        LOGGER.debug(s);
        this.defaultQueueRepository.save(queue);
    }

    public DefaultJobProcessor findOne(String queueId){
        return this.defaultQueueRepository.findOne(queueId);
    }

    public Job findOneJob(String queueId, String jobId) {
        String s = String.format("Searching job %s in queue %s", queueId, jobId);
        LOGGER.debug(s);
        return this.defaultQueueRepository.findOne(queueId).getJob(jobId);
    }

    public boolean containsJob(String queueId, String jobId){
        return this.defaultQueueRepository.findOne(queueId).containsJob(jobId);
    }

    public void setDefaultQueueRepository(DefaultQueueRepository defaultQueueRepository) {
        this.defaultQueueRepository = defaultQueueRepository;
    }
}
