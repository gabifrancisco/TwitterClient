package com.gabisdev.twitterclient.model;

import com.google.gson.annotations.SerializedName;

public class Tweet {

	@SerializedName("text")
	private String text;
	@SerializedName("user")
	private User User;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public User getUser() {
		return User;
	}

	public void setUser(User user) {
		User = user;
	}
}
