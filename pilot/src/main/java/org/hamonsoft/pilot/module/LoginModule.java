package org.hamonsoft.pilot.module;
import java.util.UUID;

import org.hamonsoft.pilot.CommandModule;
import org.hamonsoft.pilot.Header;

public class LoginModule extends CommandModule {

	public LoginModule() {
		
		session = UUID.randomUUID().toString();
	}
	
	public void dbProcess() {
		
		redisConnector.connect();
		redisConnector.set(session);
	}

	public void memoryProcess() {

		sessionManager.addSession(session);
		header.setCommand("newSession");
		header.setSessionKey(session);
	}
	
	public Header getResult() {
		
		//dbProcess();
		memoryProcess();
		
		return header;
	}
}
