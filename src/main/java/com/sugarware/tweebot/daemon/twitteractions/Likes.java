package com.sugarware.tweebot.daemon.twitteractions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.json.JSONArray;

import com.sugarware.tweebot.daemon.model.Policy;
import com.sugarware.tweebot.daemon.service.TwitterService;
import com.sugarware.tweebot.daemon.util.PropertiesUtil;

public class Likes {

	private TwitterService twitterService;
	private long userId;
	private String oauthToken;
	private String oauthTokenSecret;

	public Likes() {
		super();
		twitterService = new TwitterService();
	}

	public Likes(long userId, String oauthToken, String oauthTokenSecret) {
		this();
		this.userId = userId;
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;
	}

	public int doLikes(Connection conn) {
		Properties props = PropertiesUtil.getInstance();
		int likes = Integer.parseInt(props.getProperty("likesPerEvent"));
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM likepolicy WHERE userId = ?");
			stmt.setLong(1, userId);

			Map<Policy, Integer> policies = new HashMap<>();

			// Fetch policies
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Policy p = new Policy();
				p.setHashtag(rs.getString("hashtag"));
				p.setPolicyId(rs.getInt("policyId"));
				p.setNumber(rs.getInt("likes"));
				p.setUserId(userId);
				policies.put(p, 0);
			}

			// Distribute likes amongst policies
			while (likes > 0) {
				for (Policy p : policies.keySet()) {
					policies.put(p, policies.get(p) + 1);
					likes--;
					if (likes <= 0)
						break;
				}
			}
			// Perform the likes
			for (Policy p : policies.keySet()) {
				JSONArray tweets = twitterService.tweetQuery("#" + p.getHashtag(), oauthToken, oauthTokenSecret)
						.getJSONArray("statuses");
				int successfulLikes = 0;
				int n = policies.get(p);
				// Do n likes
				for (int i = 0; i < n; i++) {
					try {
						Random rand = new Random();
						int index = rand.nextInt(tweets.length());
						long tweetId = Long.parseLong(tweets.getJSONObject(index).getString("id_str"));
						twitterService.likeTweet(tweetId, oauthToken, oauthTokenSecret);
						successfulLikes++;
					} catch (Exception e) {
						System.err.println(e.getMessage() + " " + "(" + e.getStackTrace()[0].getClassName() + ":"
								+ e.getStackTrace()[0].getLineNumber() + ")");
					}
				}

				// update the number of likes we've done for this policy overall
				stmt = conn.prepareStatement("UPDATE likepolicy set likes = ? where policyId = ?");
				stmt.setInt(1, successfulLikes + p.getNumber());
				stmt.setInt(2, p.getPolicyId());
				stmt.executeUpdate();

				return successfulLikes;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public TwitterService getTwitterService() {
		return twitterService;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}

	public void setOauthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}

}
