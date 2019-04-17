package org.fogbowcloud.arrebol.core.models.job;

import org.apache.log4j.Logger;
import org.fogbowcloud.arrebol.core.models.task.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class Job implements Serializable {

	private static final long serialVersionUID = -6111900503095749695L;
	private static final Logger LOGGER = Logger.getLogger(Job.class);

	private Map<String, Task> taskList;
	private final ReentrantReadWriteLock taskReadyLock;
	private boolean isCreated;

	public Job(List<Task> tasks) {
		this.isCreated = true;
		this.taskList = new HashMap<String, Task>();
		this.taskReadyLock = new ReentrantReadWriteLock();
		addTasks(tasks);
	}

	//TODO: not sure that we need to guarantee thread safety at the job level
	public void addTask(Task task) {
		LOGGER.debug("Adding task " + task.getId());
		taskReadyLock.writeLock().lock();
		try {
			getTaskList().put(task.getId(), task);
		} finally {
			taskReadyLock.writeLock().unlock();
		}
	}

	private void addTasks(List<Task> tasks) {
		for(Task task : tasks){
			addTask(task);
		}
	}
	
	public List<Task> getTasks(){
		return new ArrayList<Task>(taskList.values());
	}
	
	public abstract void finish(Task task);

	public abstract void fail(Task task);

	public abstract String getId();

	//TODO: it seems this *created* and restart methods help the Scheduler class to its job. I'm not sure
	//if we should keep them.
	public boolean isCreated() {
		return this.isCreated;
	}
	
	public void setCreated() { this.isCreated = true; }

	public void restart() {
		this.isCreated = false;
	}

	public Map<String, Task> getTaskList() {
		return taskList;
	}

	//FIXME: why do we need this method? (serialization?)
	public void setTaskList(Map<String, Task> taskList) {
		this.taskList = taskList;
	}
}
