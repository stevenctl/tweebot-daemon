package com.sugarware.tweebot.daemon.cli.commands;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.sugarware.tweebot.daemon.Scheduler;
import com.sugarware.tweebot.daemon.cli.Command;

public class Nextdaily implements Command {

	@Override
	public void execute(String... args) {
		long timeuntil = Scheduler.dailySchedule.getDelay(TimeUnit.MILLISECONDS);
		Date then = new Date(System.currentTimeMillis() + timeuntil);
		System.out.println("The daily scheduling will occur in " + getTimeString(timeuntil) + " at " + then.toString());
	}

	private static String getTimeString(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}
	
}
