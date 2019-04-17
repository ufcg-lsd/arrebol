package org.fogbowcloud.arrebol.api.http.services;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.ArrebolFacade;
import org.fogbowcloud.arrebol.core.models.job.JDFJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class JobService {

    @Lazy
    @Autowired
    private ArrebolFacade arrebolFacade;

    private final Logger LOGGER = Logger.getLogger(JobService.class);

    public String addJob(JDFJob job){
        return this.arrebolFacade.addJob(job);
    }

}
