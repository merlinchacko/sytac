package com.assignment.twitterservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Merlin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Messages {

	private long id;
	private String text;
	@JsonProperty("created_at")
	private Date createdAt;
	private Users user;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Users getUser() {
		return user;
	}
	public void setUser(Users user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "Messages [id=" + id + ", text=" + text + ", createdAt=" + createdAt + ", user=" + user.toString() + "]";
	}
}
