package org.fogbowcloud.arrebol.api.http.dataaccessobject;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.repositories.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class JobDAO {

    private final Logger LOGGER = Logger.getLogger(JobDAO.class);

    @Autowired
    private JobRepository jobRepository;

    public Job getJobById(String id){
        LOGGER.debug("Acessing database to getting a job");
        Job job = jobRepository.findOne(id);
        return job;
    }

    public void addJob(Job job){
        LOGGER.debug("Adding job to the database");
        jobRepository.save(job);
    }

    public String deleteJobById(String id){
        LOGGER.debug("Removing job from the database");
        return jobRepository.deleteById(id);
    }

}
