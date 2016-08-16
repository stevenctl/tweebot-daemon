package com.sugarware.tweebot.daemon;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.sugarware.tweebot.daemon.cli.CommandLineReader;
import com.sugarware.tweebot.daemon.cli.commands.Nextqueued;
import com.sugarware.tweebot.daemon.tasks.ScheduleDaily;
import com.sugarware.tweebot.daemon.util.PropertiesUtil;
import com.sugarware.tweebot.daemon.util.TaskQueueUtil;

public class Scheduler {

	private static ScheduledExecutorService sch = Executors.newScheduledThreadPool(1);
	private static final int minutesPerDay = 60 * 24;

	public static ScheduledFuture<?> periodicSchedule;
	public static ScheduledFuture<?> dailySchedule;

	public static void main(String[] args) {
		// schedule a daily task that will add to the task queue
		dailySchedule = sch.scheduleAtFixedRate(new ScheduleDaily(), 0, 1, TimeUnit.DAYS);

		int totalPerDay = PropertiesUtil.getDailyTaskCount();
		System.out.println(totalPerDay + "/day");

		// run the top of the task queue every (24/totalTasks) hours
		periodicSchedule = sch.scheduleAtFixedRate(() -> {
			if (TaskQueueUtil.getInstance().size() > 0) {
				TaskQueueUtil.getInstance().remove().run();
				new Nextqueued().execute();
			} else {
				System.out.println("The task queue is empty! Nothing to execute.");
			}
		}, 1, minutesPerDay / totalPerDay, TimeUnit.MINUTES);

		System.out.println("Tasks will be run from Queue every " + (minutesPerDay / totalPerDay) + " minutes.");

		// Start the command line reader
		new CommandLineReader().startReading();
	}
}
