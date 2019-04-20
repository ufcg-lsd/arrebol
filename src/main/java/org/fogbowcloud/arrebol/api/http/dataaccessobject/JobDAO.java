package org.fogbowcloud.arrebol.api.http.dataaccessobject;

import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobDAO {

    @Autowired
    private JobRepository jobRepository;

    public Job getJobById(String id){
        Job job = jobRepository.findOne(id);
        return job;
    }

    public void addJob(Job job){
        jobRepository.save(job);
    }

    public String deleteJobById(String id){
        return jobRepository.deleteById(id);
    }

}
