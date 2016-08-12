package com.sugarware.tweebot.daemon.cli.commands;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.sugarware.tweebot.daemon.Scheduler;
import com.sugarware.tweebot.daemon.cli.Command;
import com.sugarware.tweebot.daemon.util.TaskQueueUtil;

public class Nextqueued implements Command {

	@Override
	public void execute(String... args) {
		long timeuntil = Scheduler.periodicSchedule.getDelay(TimeUnit.MILLISECONDS);
		Date then = new Date(System.currentTimeMillis() + timeuntil);
		if(TaskQueueUtil.getInstance().size() == 0){
			System.out.println("There are no tasks queued.");
			return;
		}
		System.out.println("The queued task will execute in " + getTimeString(timeuntil) + " at " + then.toString());
		System.out.println("The next queued task is a " + TaskQueueUtil.getInstance().peek().getClass().getSimpleName());
	}

	private static String getTimeString(long millis) {
		return String.format("%02dh%02dm%02ds", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	
}
