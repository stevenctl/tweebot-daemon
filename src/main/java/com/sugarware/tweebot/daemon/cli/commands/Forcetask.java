package com.sugarware.tweebot.daemon.cli.commands;

import com.sugarware.tweebot.daemon.cli.Command;
import com.sugarware.tweebot.daemon.util.TaskQueueUtil;

public class Forcetask implements Command {

	@Override
	public void execute(String... args) {
		if (TaskQueueUtil.getInstance().size() == 0) {
			System.out.println("Task queue is empty! Nothing to execute.");
			return;
		}
		Runnable r = TaskQueueUtil.getInstance().remove();
		System.out.println("Executing " + r.getClass().getSimpleName());
		new Thread(r).start();
		new Nextqueued().execute();
	}

}
