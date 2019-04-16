package org.fogbowcloud.arrebol.api;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.api.constants.ApiDocumentation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = ApiDocumentation.ApiEndpoints.JOB_ENDPOINT)
public class JobController {
    private final Logger LOGGER = Logger.getLogger(JobController.class);

}
