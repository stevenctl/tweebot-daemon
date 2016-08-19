package com.sugarware.tweebot.daemon.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.sugarware.tweebot.daemon.util.ParamMapper;
import com.sugarware.tweebot.daemon.util.RequestSigner;

public class TwitterService {

	private RestTemplate restTemplate;
	private RequestSigner requestSigner;

	public TwitterService() {
		restTemplate = new RestTemplate();
		requestSigner = new RequestSigner();
	}

	public JSONObject tweetQuery(String query, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/search/tweets.json";

		Map<String, String> params = new HashMap<>();
		query = query.replace("#", "%23");
		params.put("q", query);
		params.put("result_type", "recent");
		params.put("count", "100");

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.GET, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		System.out.println("?params  =  " + ParamMapper.mapToParamsUnencoded(params));

		ResponseEntity<String> response = restTemplate.exchange(url + "?" + ParamMapper.mapToParamsUnencoded(params),
				HttpMethod.GET, request, String.class);

		return new JSONObject(response.getBody());
	}

	public JSONObject tweetQuery(String query, String geocode, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/search/tweets.json";

		Map<String, String> params = new HashMap<>();
		query = query.replace("#", "%23");
		params.put("q", query);
		params.put("geocode", geocode);
		params.put("result_type", "recent");
		params.put("count", "100");

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.GET, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);

		System.out.println("?params  =  " + ParamMapper.mapToParamsUnencoded(params));

		ResponseEntity<String> response = restTemplate.exchange(url + "?" + ParamMapper.mapToParamsUnencoded(params),
				HttpMethod.GET, request, String.class);

		return new JSONObject(response.getBody());
	}

	public JSONObject likeTweet(long tweetId, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/favorites/create.json";

		Map<String, String> params = new HashMap<>();

		params.put("id", String.valueOf(tweetId));

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url + "?" + ParamMapper.mapToParamsUnencoded(params),
				HttpMethod.POST, request, String.class);

		return new JSONObject(response.getBody());
	}

	public JSONObject retweetTweet(long tweetId, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/statuses/retweet/" + tweetId + ".json";

		Map<String, String> params = new HashMap<>();

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

		return new JSONObject(response.getBody());
	}

	public JSONObject followUser(long userId, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/friendships/create.json";

		Map<String, String> params = new HashMap<>();

		params.put("user_id", String.valueOf(userId));
		params.put("follow", "false");

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url + "?" + ParamMapper.mapToParamsUnencoded(params),
				HttpMethod.POST, request, String.class);

		return new JSONObject(response.getBody());
	}

	public JSONObject muteUser(long userId, String oauthToken, String oauthTokenSecret) {
		String url = "https://api.twitter.com/1.1/mutes/users/create.json";

		Map<String, String> params = new HashMap<>();

		params.put("user_id", String.valueOf(userId));

		String authHeader = requestSigner.getAuthorizationHeader(url, HttpMethod.POST, params, oauthToken,
				oauthTokenSecret);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", authHeader);
		headers.add(HttpHeaders.ACCEPT, "application/json");
		HttpEntity<?> request = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url + "?" + ParamMapper.mapToParamsUnencoded(params),
				HttpMethod.POST, request, String.class);

		return new JSONObject(response.getBody());
	}
}
