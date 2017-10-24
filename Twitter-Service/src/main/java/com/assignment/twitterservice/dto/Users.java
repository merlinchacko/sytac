package com.assignment.twitterservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Merlin
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {
	
	private String id;
	private String name;
	@JsonProperty("screen_name")
	private String screenName;
	@JsonProperty("created_at")
	private Date createdAt;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", name=" + name + ", screenName=" + screenName + ", createdAt=" + createdAt + "]";
	}
}
