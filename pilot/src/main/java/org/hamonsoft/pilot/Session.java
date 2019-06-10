package org.hamonsoft.pilot;

import java.util.Date;
import java.util.UUID;

public class Session {
	private String key;
	private String creationTime;
	private int expireTime;
	
	public Session() {
		this.key			= UUID.randomUUID().toString();
		this.creationTime	= new Date().toString();
		this.expireTime		= 5; //second;
	}

	public String getKey() {
		return key;
	}

	public String getCreationTime() {
		return creationTime;
	}

	public int getExpireTime() {
		return expireTime;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}

	public void setExpireTime(int expireTime) {
		this.expireTime = expireTime;
	}

	@Override
	public String toString() {
		return "Session [key=" + key + ", creationTime=" + creationTime + ", expireTime=" + expireTime + "]";
	}
	
}


