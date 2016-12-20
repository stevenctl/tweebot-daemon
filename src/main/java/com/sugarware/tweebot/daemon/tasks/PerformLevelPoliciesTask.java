package com.sugarware.tweebot.daemon.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sugarware.tweebot.daemon.util.ActionCounterUtil;
import com.sugarware.tweebot.daemon.util.DatabaseUtil;

/**
 * Task that will set up the daily actions in the database.
 * 
 * @author Steven
 *
 */
public class PerformLevelPoliciesTask implements Runnable {

	private static final String COUNT_SUB_AT_LEVEL = "SELECT COUNT(*) FROM Subscription WHERE subscription = ?";
	private static final String GET_SUB_AT_LEVEL = "SELECT * FROM Subscription WHERE subscription = ?";

	// Run up to 8 users' tasks at a time
	private static final int MAX_THREADS = 12;

	// Processes all users policies who are at this subscription tier
	private int level;

	// Multithread so we can have multiple async tasks running at once
	private ExecutorService executor;

	public PerformLevelPoliciesTask(int level) {
		this.level = level;
		executor = Executors.newFixedThreadPool(MAX_THREADS);
	}

	@Override
	public void run() {
		int taskCount = 0;
		Connection conn = null;
		long startTime = System.currentTimeMillis();
		try {
			System.out.println("Preparing to perform events for users at Tier " + level);
			conn = DatabaseUtil.getConnection();
			PreparedStatement stmt = conn.prepareStatement(COUNT_SUB_AT_LEVEL);
			stmt.setInt(1, level);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			int count = rs.getInt(1);

			System.out.println("There are " + count + " users at Tier " + level);

			if (count <= 0)
				return;

			stmt = conn.prepareStatement(GET_SUB_AT_LEVEL);
			stmt.setInt(1, level);

			rs = stmt.executeQuery();

			while (rs.next()) {
				long userId = rs.getLong("userId");
				executor.submit(new PerformUserPoliciesTask(userId));
				taskCount++;

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		long maxTime = taskCount * 90;
		executor.shutdown();
		
		try {
			executor.awaitTermination(maxTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long time = System.currentTimeMillis() - startTime;
		System.out.println("Finished actions for Tier " + level + " in " + getTimeString(time));
		System.out.println("Did " + ActionCounterUtil.getLikes() + " likes.");
		System.out.println("Did " + ActionCounterUtil.getRetweets() + " retweets.");
		System.out.println("Did " + ActionCounterUtil.getFollows() + " follows.");
		ActionCounterUtil.reset();
	}

	private static String getTimeString(long millis) {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
