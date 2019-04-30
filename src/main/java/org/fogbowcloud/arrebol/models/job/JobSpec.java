package org.fogbowcloud.arrebol.models.job;

import org.fogbowcloud.arrebol.models.task.TaskSpec;

import java.io.Serializable;
import java.util.List;

public class JobSpec implements Serializable {

    private static final long serialVersionUID = -6111900503095749695L;

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
