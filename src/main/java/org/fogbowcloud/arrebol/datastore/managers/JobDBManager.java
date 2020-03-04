package org.fogbowcloud.arrebol.datastore.managers;

import org.fogbowcloud.arrebol.datastore.repositories.JobRepository;
import org.fogbowcloud.arrebol.models.job.Job;

public class JobDBManager {
    private static JobDBManager instance;

    private JobRepository jobRepository;

    private JobDBManager() { }

    public synchronized static JobDBManager getInstance() {
        if (instance == null) {
            instance = new JobDBManager();
        }
        return instance;
    }

    public void save(Job job) {
        this.jobRepository.save(job);
    }

    public Job findOne(String id) {
        return this.jobRepository.findOne(id);
    }

    public void setJobRepository(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
}
