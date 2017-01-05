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
import com.sugarware.tweebot.daemon.service.GeocodeService;
import com.sugarware.tweebot.daemon.service.TwitterService;
import com.sugarware.tweebot.daemon.util.PropertiesUtil;

public class Retweets {

	private TwitterService twitterService;
	private GeocodeService geoService;
	private long userId;
	private String oauthToken;
	private String oauthTokenSecret;

	private Retweets() {
		super();
		twitterService = new TwitterService();
		geoService = new GeocodeService();
	}

	public Retweets(long userId, String oauthToken, String oauthTokenSecret) {
		this();
		this.userId = userId;
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;
	}

	public int doRetweets(Connection conn) {
		Properties props = PropertiesUtil.getInstance();
		int retweets = Integer.parseInt(props.getProperty("retweetsPerEvent"));
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM RetweetPolicy WHERE userId = ?");
			stmt.setLong(1, userId);

			Map<Policy, Integer> policies = new HashMap<>();

			// Fetch policies
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Policy p = new Policy();
				p.setHashtag(rs.getString("hashtag"));
				p.setPolicyId(rs.getInt("policyId"));
				p.setNumber(rs.getInt("retweets"));
				p.setUserId(userId);
				policies.put(p, 0);
			}

			// Try to fetch GeoPolicy
			String location = null;

			stmt = conn.prepareStatement("SELECT * FROM GeoPolicy WHERE userId = ?");
			stmt.setLong(1, userId);
			rs = stmt.executeQuery();

			if (rs.next()) {
				int zip = rs.getInt("zipCode");
				int radius = rs.getInt("radius");
				location = geoService.getGeocode(zip, radius);
			}

			// Distribute retweets amongst policies
			while (retweets > 0) {
				for (Policy p : policies.keySet()) {
					policies.put(p, policies.get(p) + 1);
					retweets--;
					if (retweets <= 0)
						break;
				}
			}

			// Perform the retweets
			for (Policy p : policies.keySet()) {

				JSONArray tweets = location != null
						? twitterService.tweetQuery("#" + p.getHashtag(), location, oauthToken, oauthTokenSecret)
								.getJSONArray("statuses")
						: twitterService.tweetQuery("#" + p.getHashtag(), oauthToken, oauthTokenSecret)
								.getJSONArray("statuses");

				int successfulRetweets = 0;
				int n = policies.get(p);
				// Do n retweets
				for (int i = 0; i < n; i++) {
					try {
						Random rand = new Random();
						int index = rand.nextInt(tweets.length());
						long tweetId = Long.parseLong(tweets.getJSONObject(index).getString("id_str"));
						twitterService.retweetTweet(tweetId, oauthToken, oauthTokenSecret);
						successfulRetweets++;
					} catch (Exception e) {
						System.err.println(e.getMessage() + " " + "(" + e.getStackTrace()[0].getClassName() + ":"
								+ e.getStackTrace()[0].getLineNumber() + ")");
					}
				}

				// update the number of retweets we've done for this policy
				// overall
				stmt = conn.prepareStatement("UPDATE RetweetPolicy set retweets = ? where policyId = ?");
				stmt.setInt(1, successfulRetweets + p.getNumber());
				stmt.setInt(2, p.getPolicyId());
				stmt.executeUpdate();

				return successfulRetweets;
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
