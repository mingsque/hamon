package org.hamonsoft.pilot.module;

import org.hamonsoft.pilot.CommandModule;
import org.hamonsoft.pilot.Header;

public class EndModule extends CommandModule {

	public void dbProcess() {

		redisConnector.del(session);
	}

	public void memoryProcess() {

		sessionManager.delSession(session);
	}
	
	public Header getResult() {
		
		dbProcess();
		memoryProcess();
		
		return header;
	}
}
