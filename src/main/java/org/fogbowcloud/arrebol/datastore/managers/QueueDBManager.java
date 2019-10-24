package org.fogbowcloud.arrebol.datastore.managers;

import org.fogbowcloud.arrebol.datastore.repositories.DefaultQueueRepository;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.queue.DefaultQueue;

public class QueueDBManager {

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

    public void save(DefaultQueue queue) {
        this.defaultQueueRepository.save(queue);
    }

    public DefaultQueue findOne(String queueId){
        return this.defaultQueueRepository.findOne(queueId);
    }

    public Job findOneJob(String queueId, String jobId) {
        return this.defaultQueueRepository.findOne(queueId).getJob(jobId);
    }

    public boolean containsJob(String queueId, String jobId){
        return this.defaultQueueRepository.findOne(queueId).containsJob(jobId);
    }

    public void setDefaultQueueRepository(DefaultQueueRepository defaultQueueRepository) {
        this.defaultQueueRepository = defaultQueueRepository;
    }
}
