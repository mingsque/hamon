package org.hamonsoft.pilot;

import java.util.HashSet;

public class SessionManager {
	
	private HashSet<String> sessionSet = new HashSet<String>();
	
	static private SessionManager instance;
	
	//singleton pattern to safe thread about one space
	private SessionManager() {}
	
	public static synchronized SessionManager getInstance() {
		if(instance == null) {
			instance = new SessionManager();
		
		}
		return instance;
	}

	public boolean existSession(String session) {
		
		boolean result = false;
		
		if(sessionSet.contains(session)) {
			result = true;
		}
		return result;
	}
	
	public synchronized void addSession(String session) {

		sessionSet.add(session);
	}
	
	public synchronized void delSession(String session) {
		
		sessionSet.remove(session);
	}
	
	public HashSet<String> getSessionSet() {
		
		return sessionSet;
	}
	
}
