package com.sugarware.tweebot.daemon.util;

public class ActionCounterUtil {

	private static int likes;
	private static int retweets;
	private static int follows;

	public static synchronized void reset() {
		likes = 0;
		retweets = 0;
		follows = 0;
	}

	public static synchronized void addLikes(int n) {
		likes += n;
	}

	public static synchronized void addRetweets(int n) {
		retweets += n;
	}

	public static synchronized void addFolows(int n) {
		follows += n;
	}

	public static int getLikes() {
		return likes;
	}

	public static int getRetweets() {
		return retweets;
	}

	public static int getFollows() {
		return follows;
	}

}
