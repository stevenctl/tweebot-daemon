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

public class Follows {

	private TwitterService twitterService;
	private GeocodeService geoService;
	private long userId;
	private String oauthToken;
	private String oauthTokenSecret;

	private Follows() {
		super();
		twitterService = new TwitterService();
		geoService = new GeocodeService();
	}

	public Follows(long userId, String oauthToken, String oauthTokenSecret) {
		this();
		this.userId = userId;
		this.oauthToken = oauthToken;
		this.oauthTokenSecret = oauthTokenSecret;
	}

	public int doFollows(Connection conn) {
		Properties props = PropertiesUtil.getInstance();
		int follows = Integer.parseInt(props.getProperty("followsPerEvent"));
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM followpolicy WHERE userId = ?");
			stmt.setLong(1, userId);

			Map<Policy, Integer> policies = new HashMap<>();

			// Fetch policies
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Policy p = new Policy();
				p.setHashtag(rs.getString("hashtag"));
				p.setPolicyId(rs.getInt("policyId"));
				p.setNumber(rs.getInt("follows"));
				p.setUserId(userId);
				policies.put(p, 0);
			}

			// Try to fetch geopolicy
			String location = null;

			stmt = conn.prepareStatement("SELECT * FROM geopolicy WHERE userId = ?");
			rs = stmt.executeQuery();

			if (rs.next()) {
				int zip = rs.getInt("zip");
				int radius = rs.getInt("radius");
				location = geoService.getGeocode(zip, radius);
			}

			// Distribute follows amongst policies
			while (follows > 0) {
				for (Policy p : policies.keySet()) {
					policies.put(p, policies.get(p) + 1);
					follows--;
					if (follows <= 0)
						break;
				}
			}

			// Perform the follows
			for (Policy p : policies.keySet()) {

				JSONArray tweets = location != null
						? twitterService.tweetQuery("#" + p.getHashtag(), location, oauthToken, oauthTokenSecret)
								.getJSONArray("statuses")
						: twitterService.tweetQuery("#" + p.getHashtag(), oauthToken, oauthTokenSecret)
								.getJSONArray("statuses");

				int successfulFollows = 0;
				int n = policies.get(p);
				// Do n follows
				for (int i = 0; i < n; i++) {
					try {
						Random rand = new Random();
						int index = rand.nextInt(tweets.length());
						long tweetOwnerId = Long
								.parseLong(tweets.getJSONObject(index).getJSONObject("user").getString("id_str"));
						twitterService.followUser(tweetOwnerId, oauthToken, oauthTokenSecret);
						try {
							twitterService.muteUser(tweetOwnerId, oauthToken, oauthTokenSecret);
						} catch (Exception e) {
							System.err.println("Couldn't mute user...");
						}
						successfulFollows++;
					} catch (Exception e) {
						System.err.println(e.getMessage() + " " + "(" + e.getStackTrace()[0].getClassName() + ":"
								+ e.getStackTrace()[0].getLineNumber() + ")");
					}
				}

				// update the number of follows we've done for this policy
				// overall
				stmt = conn.prepareStatement("UPDATE followpolicy set follows = ? where policyId = ?");
				stmt.setInt(1, successfulFollows + p.getNumber());
				stmt.setInt(2, p.getPolicyId());
				stmt.executeUpdate();

				return successfulFollows;
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
