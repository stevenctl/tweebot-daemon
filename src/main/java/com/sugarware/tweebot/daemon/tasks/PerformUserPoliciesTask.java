package com.sugarware.tweebot.daemon.tasks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sugarware.tweebot.daemon.twitteractions.Follows;
import com.sugarware.tweebot.daemon.twitteractions.Likes;
import com.sugarware.tweebot.daemon.twitteractions.Retweets;
import com.sugarware.tweebot.daemon.util.ActionCounterUtil;
import com.sugarware.tweebot.daemon.util.DatabaseUtil;

public class PerformUserPoliciesTask implements Runnable {

	private long userId;
	private String oauthToken;
	private String oauthTokenSecret;

	public PerformUserPoliciesTask(long userId) {
		this.userId = userId;
	}

	@Override
	public void run() {
		System.out.println(userId + " thingheryer");

		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection();

			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM AccessToken WHERE userId = ?");
			stmt.setLong(1, userId);
			ResultSet rs = stmt.executeQuery();
			rs.next();

			this.oauthToken = rs.getString("oauth_token");
			this.oauthTokenSecret = rs.getString("oauth_token_secret");

			int likes = new Likes(userId, oauthToken, oauthTokenSecret).doLikes(conn);
			ActionCounterUtil.addLikes(likes);
			int retweets = new Retweets(userId, oauthToken, oauthTokenSecret).doRetweets(conn);
			ActionCounterUtil.addRetweets(retweets);
			int follows = new Follows(userId, oauthToken, oauthTokenSecret).doFollows(conn);
			ActionCounterUtil.addFolows(follows);

			System.out.println(userId + " thingheryer");
			
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
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
