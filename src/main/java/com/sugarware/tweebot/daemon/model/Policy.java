package com.sugarware.tweebot.daemon.model;

public class Policy {

	private int policyId;
	private String hashtag;
	private long userId;
	private int number;
	
	public Policy() {
		super();
	}

	public Policy(int policyId, String hashtag, long userId, int number) {
		super();
		this.policyId = policyId;
		this.hashtag = hashtag;
		this.userId = userId;
		this.number = number;
	}

	public int getPolicyId() {
		return policyId;
	}

	public void setPolicyId(int policyId) {
		this.policyId = policyId;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
