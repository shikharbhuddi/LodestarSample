package com.codebrat.lodestarsample.model;

/**
 * Created by Shikhar on 05-08-2017.
 */

public class User {
	private String username;
	private String token;

	public User(){
		// Empty Constructor
	}

	public User(String username, String token){
		this.username = username;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
