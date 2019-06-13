package org.hamonsoft.pilot;

public abstract class CommandModule {
		
	public RedisConnector redisConnector;
	public SessionManager sessionManager;
	public Header header;
	public String session;
	
	public CommandModule() {
		
		redisConnector = RedisConnector.getInstance();
		sessionManager = SessionManager.getInstance();
		header = new Header();
	}
	
	public void dbProcess() {};

	public void memoryProccess() {};
	
	public Header getResult() {
		
		dbProcess();
		memoryProccess();
		
		System.out.println("There is no Module");
		
		return header;
	}
}
