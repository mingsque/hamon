package org.hamonsoft.pilot.module;

import org.hamonsoft.pilot.CommandModule;
import org.hamonsoft.pilot.Header;

public class NormalModule extends CommandModule {

	public NormalModule(String session) {
		
		this.session = session;
	}
	
	public void dbProcess() {
		
		redisConnector.connect();		
	}

	public void memoryProcess() {
		
		if(sessionManager.existSession(session)) {
			
			header.setCommand("normal");
			//redisConnector.expire(session);
		} else {
			
			header.setCommand("abnormal");
		}
		
		System.out.println(sessionManager.getSessionSet());
	}
	
	public Header getResult() {
		
		//dbProcess();
		memoryProcess();
		
		return super.header;
	}
}
