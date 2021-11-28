package com.ubayKyu.accountingSystem.dto;

public class User {
	public String name;
	public String Password;

	public User(String name, String password) {
		this.name = name;
		this.Password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		this.Password = password;
	}

}
