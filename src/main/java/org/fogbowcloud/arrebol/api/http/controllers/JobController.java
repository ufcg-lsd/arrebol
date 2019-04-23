package org.fogbowcloud.arrebol.api.http.controllers;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.constants.ApiDocumentation;
import org.fogbowcloud.arrebol.api.http.services.JobService;
import org.fogbowcloud.arrebol.core.models.job.Job;
import org.fogbowcloud.arrebol.core.models.job.JobSpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiDocumentation.ApiEndpoints.JOB_ENDPOINT)
public class JobController {

    private final Logger LOGGER = Logger.getLogger(JobController.class);

    @Autowired
    private JobService jobService;

    @Autowired
    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addJob(@RequestBody JobSpec jobSpec){
        LOGGER.info("Adding new Job: " + jobSpec.getLabel() + ".");

        String jobId = jobService.addJob(jobSpec);
        JobResponse jobResponse = new JobResponse(jobId);

        LOGGER.info("Added " + jobSpec.getLabel() + " with id " + jobId + ".");
        return new ResponseEntity(jobResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value = ApiDocumentation.ApiEndpoints.JOB_PATH, method = RequestMethod.GET)
    public ResponseEntity<String> getJob(@PathVariable String id){
        LOGGER.info("Getting an job with id: " + id);

        Job job = jobService.getJobById(id);
        return new ResponseEntity(job, HttpStatus.CREATED);
    }

    public class JobResponse {
        private String id;
        public JobResponse(String id) { this.id = id; }
        public String getId() {
            return this.id;
        }
        public void setIt(String id) { this.id = id;}
    }

}

