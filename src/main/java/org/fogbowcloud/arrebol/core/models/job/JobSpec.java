package org.fogbowcloud.arrebol.core.models.job;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.command.Command;
import org.fogbowcloud.arrebol.core.models.specification.Specification;
import org.fogbowcloud.arrebol.core.models.task.Task;
import org.fogbowcloud.arrebol.core.models.task.TaskSpec;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JobSpec implements Serializable {

    private static final long serialVersionUID = -6111900503095749695L;
    private static final Logger LOGGER = Logger.getLogger(JobSpec.class);

    private String label;
    private List<TaskSpec> tasksSpecs;

    JobSpec(){}

    public JobSpec(String label, List<TaskSpec> taskSpecs){
        this.label = label;
        this.tasksSpecs = taskSpecs;
    }


    public String getLabel(){
        return this.label;
    }

    public List<TaskSpec> getTasksSpecs(){
        return this.tasksSpecs;
    }

}
