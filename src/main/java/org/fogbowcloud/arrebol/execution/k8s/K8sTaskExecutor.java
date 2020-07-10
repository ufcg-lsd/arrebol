package org.fogbowcloud.arrebol.execution.k8s;

import static java.lang.Thread.sleep;
import static org.fogbowcloud.arrebol.execution.docker.constants.DockerConstants.ADDRESS_METADATA_KEY;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult;
import org.fogbowcloud.arrebol.execution.TaskExecutor;
import org.fogbowcloud.arrebol.execution.TaskExecutionResult.RESULT;
import org.fogbowcloud.arrebol.execution.k8s.client.K8sClient;
import org.fogbowcloud.arrebol.execution.k8s.resource.K8sClusterResource;
import org.fogbowcloud.arrebol.models.command.Command;
import org.fogbowcloud.arrebol.models.command.CommandState;
import org.fogbowcloud.arrebol.models.task.RequirementsContants;
import org.fogbowcloud.arrebol.models.task.Task;

import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Job;

@Entity
public class K8sTaskExecutor implements TaskExecutor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Transient
	private final Logger LOGGER = Logger.getLogger(K8sTaskExecutor.class);
	@Transient
	private K8sClusterResource k8sClusterResource;
	@Transient
	private K8sClient k8sClient;
	@Transient
	private static final long POOLING_PERIOD_TIME_MS = 5 * 1000;

	public K8sTaskExecutor(K8sClusterResource k8sClusterResource, K8sClient k8sClient) {
		this.k8sClusterResource = k8sClusterResource;
		this.k8sClient = k8sClient;
	}

	public K8sTaskExecutor() {
	}

	@Override
	public TaskExecutionResult execute(Task task) {
		TaskExecutionResult taskExecutionResult = null;

		String jobName = task.getId();

		List<Command> commands = task.getTaskSpec().getCommands();
		String command = joinCommands(commands);

		Map<String, String> requirements = task.getTaskSpec().getRequirements();
		String imageId = getImageId(requirements);

		LOGGER.debug("Image id: " + imageId);
		LOGGER.debug("Command: " + command);

		int tasksListSize = task.getTaskSpec().getCommands().size();

		try {
			V1Job job = k8sClient.createJob(jobName, imageId, command);
			boolean jobIsRunning = true;

			while (jobIsRunning) {
				try {
					sleep(POOLING_PERIOD_TIME_MS);
				} catch (InterruptedException e) {
					LOGGER.error(e.getMessage(), e);
				}

				job = k8sClient.getJob(jobName);
				jobIsRunning = job.getStatus().getActive() != null;
			}

			if (wasSuccessful(job)) {
				successTask(task);
				taskExecutionResult = getSuccessResultInstance(tasksListSize);
			} else {
				failTask(task);
				taskExecutionResult = getFailResultInstance(tasksListSize);
			}
		} catch (ApiException e) {
			LOGGER.error("Error while call K8s client API. " + e.getMessage(), e);
			failTask(task);
			taskExecutionResult = getFailResultInstance(tasksListSize);
			return taskExecutionResult;
		}

		return taskExecutionResult;
	}

	@Override
	public Map<String, String> getMetadata() {
		Map<String, String> metadata = new HashMap<>();
		String address = this.k8sClusterResource.getApiAddress();
		metadata.put(ADDRESS_METADATA_KEY, address);
		return metadata;
	}

	private String joinCommands(List<Command> commands) {
		String result = "";
		for (Command command : commands)
			result += command.getCommand() + " && ";
		result = result.substring(0, result.length() - 4);

		return result;
	}

	private String getImageId(Map<String, String> requirements) throws IllegalArgumentException {
		String imageId = null;
		if (Objects.nonNull(requirements))
			imageId = requirements.get(RequirementsContants.IMAGE_KEY);

		return imageId;
	}

	private boolean wasSuccessful(V1Job job) {
		Integer successedQuant = job.getStatus().getSucceeded();
		return successedQuant != null && successedQuant > 0;
	}

	private void successTask(Task task) {
		for (Command c : task.getTaskSpec().getCommands()) {
			c.setState(CommandState.FINISHED);
			c.setExitcode(TaskExecutionResult.SUCCESS_RESULT);
		}
	}

	private void failTask(Task task) {
		for (Command c : task.getTaskSpec().getCommands()) {
			c.setState(CommandState.FAILED);
			c.setExitcode(TaskExecutionResult.UNDETERMINED_RESULT);
		}
	}

	private TaskExecutionResult getFailResultInstance(int size) {
		int[] exitCodes = new int[size];
		Arrays.fill(exitCodes, TaskExecutionResult.UNDETERMINED_RESULT);
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(RESULT.FAILURE, exitCodes);
		return taskExecutionResult;
	}

	private TaskExecutionResult getSuccessResultInstance(int size) {
		int[] exitCodes = new int[size];
		Arrays.fill(exitCodes, TaskExecutionResult.SUCCESS_RESULT);
		TaskExecutionResult taskExecutionResult = new TaskExecutionResult(RESULT.SUCCESS, exitCodes);
		return taskExecutionResult;
	}

}
