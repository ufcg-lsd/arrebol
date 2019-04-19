package org.fogbowcloud.arrebol.core.models.job;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.task.Task;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class Job implements Serializable {

	private static final long serialVersionUID = -6111900503095749695L;
	private static final Logger LOGGER = Logger.getLogger(Job.class);

	@Id
	private String id;
	private String label;

	@Enumerated(EnumType.STRING)
	private JobState jobState;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@MapKey(name = "id")
	private Map<String, Task> tasks;

	public Job(String label, Map<String, Task> tasks){
		this.id = UUID.randomUUID().toString();
		this.jobState = JobState.SUBMITTED;
		this.label = label;
		this.tasks = tasks;
	}

	public Job(){
	}

	public void addTask(Task task){
		tasks.put(task.getId(), task);
	}

	public Map<String, Task> getTasks(){
		return this.tasks;
	}

	public String getId(){
		return this.id;
	}

	public void setJobState(JobState jobState){
		this.jobState = jobState;
	}
}
