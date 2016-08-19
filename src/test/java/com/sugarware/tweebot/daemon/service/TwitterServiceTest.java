package com.sugarware.tweebot.daemon.service;

import org.junit.Test;

public class TwitterServiceTest {

	@Test
	public void testQueryWithLocation() {
		TwitterService twitter = new TwitterService();
		System.out.println(twitter.tweetQuery("#tbt","38.74505,-90.72477,25mi" ,"765228368245686272-yMuTpgiGZhZ7xW55BuQqVuXWoyLqKQJ",
				"fvCZVzDA20Jn5zkTtKxU06pbLqK9bbt2Q9tmW2NtItKUB"));
	}

}
