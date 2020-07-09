package org.fogbowcloud.arrebol.execution.k8s;

import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.DockerTaskExecutor;
import org.fogbowcloud.arrebol.models.task.Task;

@Entity
public class K8sTaskExecutor implements TaskExecutor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	@Transient
    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);
	
	@Override
	//TODO implement this method
	public TaskExecutionResult execute(Task task) {
		TaskExecutionResult taskExecutionResult = null;
		
		return taskExecutionResult;
	}

	@Override
	//TODO implement this method
	public Map<String, String> getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

}
