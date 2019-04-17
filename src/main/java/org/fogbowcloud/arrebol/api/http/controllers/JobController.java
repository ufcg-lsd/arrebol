package org.fogbowcloud.arrebol.api.http.controllers;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.constants.ApiDocumentation;
import org.fogbowcloud.arrebol.api.http.services.JobService;
import org.fogbowcloud.arrebol.core.models.job.JDFJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiDocumentation.ApiEndpoints.JOB_ENDPOINT)
public class JobController {
    private final Logger LOGGER = Logger.getLogger(JobController.class);

    @Lazy
    private JobService jobService;

    @Autowired
    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addJob(@RequestBody JDFJob jdfJob){
        LOGGER.info("Saving new Job.");

        String idJob = jobService.addJob(jdfJob);
        return new ResponseEntity<String>(idJob, HttpStatus.CREATED);
    }
}
