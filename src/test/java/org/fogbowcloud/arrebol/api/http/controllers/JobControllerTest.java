package org.fogbowcloud.arrebol.api.http.controllers;

import org.fogbowcloud.arrebol.api.http.services.JobService;
import org.fogbowcloud.arrebol.models.job.JobSpec;
import org.fogbowcloud.arrebol.models.task.TaskSpec;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JobControllerTest {
    @Test(expected = Exception.class)
    public void testFailMaxSizeJobSpec(){
        JobService jobService = Mockito.mock(JobService.class);
        JobController jobController = new JobController(jobService);

        TaskSpec taskSpec = new TaskSpec();
        List<TaskSpec> taskSpecs = Collections.nCopies(10001, taskSpec);
        JobSpec jobSpec = new JobSpec("label", taskSpecs);

        ResponseEntity<String> responseEntity = jobController.addJob(jobSpec);
        assertEquals(404, responseEntity.getStatusCode());
    }
}