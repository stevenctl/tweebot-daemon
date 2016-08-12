package com.sugarware.tweebot.daemon.util;

import java.util.ArrayList;
import java.util.List;

public class TaskQueueUtil {

	// Singleton instance
	private static TaskQueueUtil INSTANCE;

	// Singleton accessor
	public static TaskQueueUtil getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TaskQueueUtil();
		return INSTANCE;
	}

	private List<Runnable> tasks;

	private TaskQueueUtil() {
		tasks = new ArrayList<>();
	}

	public synchronized void addTask(Runnable task) {
		tasks.add(task);
	}

	public synchronized Runnable remove(){
		return tasks.remove(0);
	}

	public Runnable peek(){
		return tasks.get(0);
	}
	
	public int size(){
		return tasks.size();
	}
	
}