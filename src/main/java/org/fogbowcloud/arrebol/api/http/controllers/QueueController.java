package org.fogbowcloud.arrebol.api.http.controllers;

import javax.validation.Valid;
import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.constants.ApiDocumentation.ApiEndpoints;
import org.fogbowcloud.arrebol.api.constants.Messages;
import org.fogbowcloud.arrebol.api.http.services.QueueService;
import org.fogbowcloud.arrebol.models.job.Job;
import org.fogbowcloud.arrebol.models.job.JobSpec;
import org.fogbowcloud.arrebol.queue.spec.QueueSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiEndpoints.QUEUE_ENDPOINT)
public class QueueController {

    private final Logger LOGGER = Logger.getLogger(QueueController.class);

    @Autowired
    private QueueService queueService;

    @Autowired
    public QueueController(QueueService queueService) {
        this.queueService = queueService;
    }

    @RequestMapping(value = ApiEndpoints.JOB_PATH, method = RequestMethod.POST)
    public ResponseEntity<String> addJobToQueue(@PathVariable String queueId,
        @Valid @RequestBody JobSpec jobSpec) {
        LOGGER.info("Adding new Job: " + jobSpec + ".");

        try {
            String jobId = queueService.addJobToQueue(queueId, jobSpec);
            JobResponse jobResponse = new JobResponse(jobId);

            LOGGER.info("Added " + jobSpec.getLabel() + " with id " + jobId + ".");
            return new ResponseEntity(jobResponse, HttpStatus.CREATED);
        } catch (Throwable t) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, t.getMessage()), t);
            throw t;
        }
    }

    @RequestMapping(value = ApiEndpoints.JOB_BY_ID, method = RequestMethod.GET)
    public ResponseEntity<String> getJobFromQueue(@PathVariable String queueId,
        @PathVariable String jobId) {
        LOGGER.info("Getting an job with id: " + jobId);

        try {
            Job job = queueService.getJobByIdFromQueue(queueId, jobId);
            return new ResponseEntity(job, HttpStatus.CREATED);
        } catch (Throwable t) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, t.getMessage()), t);
            throw t;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addQueue(@RequestBody QueueSpec queueSpec) {
        LOGGER.info("Adding an new queue [" + queueSpec.getName() + "]");

        try {
            String queueId = queueService.createQueue(queueSpec);
            return new ResponseEntity(queueId, HttpStatus.CREATED);
        } catch (Throwable t) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, t.getMessage()), t);
            throw t;
        }
    }

    private class JobResponse {

        private String id;

        public JobResponse(String id) {
            this.id = id;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
