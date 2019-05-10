package org.fogbowcloud.arrebol.api.http.controllers;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.constants.ApiDocumentation;
import org.fogbowcloud.arrebol.api.constants.Messages;
import org.fogbowcloud.arrebol.api.http.services.JobService;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobSpec;

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
        LOGGER.info(String.format(Messages.Info.JobController.ADDING_NEW_JOB, jobSpec));

        try{
            String jobId = jobService.addJob(jobSpec);
            JobResponse jobResponse = new JobResponse(jobId);

            LOGGER.info(String.format(Messages.Info.JobController.ADDED_JOB, jobSpec.getLabel(), jobId));
            return new ResponseEntity(jobResponse, HttpStatus.CREATED);
        } catch (Throwable t){
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, t.getMessage()), t);
            throw t;
        }
    }

    @RequestMapping(value = ApiDocumentation.ApiEndpoints.JOB_PATH, method = RequestMethod.GET)
    public ResponseEntity<String> getJob(@PathVariable String id){
        LOGGER.info(String.format(Messages.Info.JobController.GETTING_JOB, id));

        try {
            Job job = jobService.getJobById(id);
            return new ResponseEntity(job, HttpStatus.CREATED);
        } catch(Throwable t){
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, t.getMessage()), t);
            throw t;
        }
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

