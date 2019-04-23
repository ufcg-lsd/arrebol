package org.fogbowcloud.arrebol.models.job;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.models.task.Task;

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

	public Job(String label, Collection<Task> tasks){
		this.id = UUID.randomUUID().toString();
		this.jobState = JobState.SUBMITTED;
		this.label = label;

		this.tasks = new HashMap<>();

		for(Task task: tasks) {
			this.tasks.put(task.getId(), task);
		}
	}

	Job(){
		//Default constructor
	}

	public String getId(){
		return this.id;
	}

	public String getLabel(){
		return this.label;
	}

	public JobState getJobState(){
		return this.jobState;
	}

	public void setJobState(JobState jobState){
		this.jobState = jobState;
	}

	public Map<String, Task> getTasks(){
		Map<String, Task> mapTasks = new HashMap<>(this.tasks);
		return mapTasks;
	}
}
