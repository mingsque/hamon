package org.hamonsoft.pilot.module;

import org.hamonsoft.pilot.CommandModule;
import org.hamonsoft.pilot.Header;

public class NormalModule extends CommandModule {

	public NormalModule(String session) {
		
		this.session = session;
	}
	
	public void dbProcess(Boolean authResult) {
		if(authResult) {
			
			redisConnector.expire(session);
		} else {
			
			
		}
		
	}

	public void memoryProcess(Boolean authResult) {
		if(authResult) {
			
			
		} else {
			
			
		}
	} 
	
	public boolean sessionAuth() {
		boolean result = true;
		
		if(sessionManager.existSession(session)) {
			result = true;
		} else {
			result = false;
		}
		
		return result;
	}
	
	public Header getResult() {
		Boolean sessionAuth = sessionAuth();
		
		dbProcess(sessionAuth);
		memoryProcess(sessionAuth);
		
		if(sessionAuth) {
			header.setCommand("normal");
		} else {
			
			header.setCommand("abnormal");
		}
		
		return header;
	}
}
