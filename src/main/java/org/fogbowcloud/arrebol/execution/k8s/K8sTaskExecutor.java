package org.fogbowcloud.arrebol.execution.k8s;

import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.docker.DockerTaskExecutor;
import org.fogbowcloud.arrebol.execution.k8s.client.DefaultK8sClient;
import org.fogbowcloud.arrebol.execution.k8s.client.K8sClient;
import org.fogbowcloud.arrebol.execution.k8s.resource.DefaultK8sClusterResource;
import org.fogbowcloud.arrebol.execution.k8s.resource.K8sClusterResource;
import org.fogbowcloud.arrebol.models.task.Task;

@Entity
public class K8sTaskExecutor implements TaskExecutor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
	@Transient
    private final Logger LOGGER = Logger.getLogger(DockerTaskExecutor.class);
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = DefaultK8sClusterResource.class)
	private K8sClusterResource k8sClusterResource;
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = DefaultK8sClient.class)
	private K8sClient k8sClient;
	
	public K8sTaskExecutor(K8sClusterResource k8sClusterResource, K8sClient k8sClient) {
		this.k8sClusterResource = k8sClusterResource;
		this.k8sClient = k8sClient;
	}
	
	public K8sTaskExecutor(){}
	
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
