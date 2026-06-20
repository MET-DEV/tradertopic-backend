package com.tradertopic.metsoft.entity.event;

public class UserCreatedEvent {
	private String mail;
	private String username;
	
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

}
