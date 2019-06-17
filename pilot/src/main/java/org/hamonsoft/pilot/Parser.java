package org.hamonsoft.pilot;

import org.hamonsoft.pilot.module.EndModule;
import org.hamonsoft.pilot.module.LoginModule;
import org.hamonsoft.pilot.module.NormalModule;

import com.google.gson.Gson;

class Parser {
	Gson gson;
	
	public Parser() {
		
		gson = new Gson();
	}
	
	public String getModuleResult(String str) {
		
		Header header = gson.fromJson(str, Header.class);
		
		System.out.println("RECIEVED DATA : "+ header);
		
		String command = header.getCommand();
		String sessionKey = header.getSessionKey();
		
		
		CommandModule commandModule = null;
	
		if(command.equals("login")) {
			
			commandModule = new LoginModule();
		} else if(command.equals("normal")) {
		
			commandModule = new NormalModule(sessionKey);
		} else if(command.equals("bye")) {
			
			commandModule = new EndModule();
		}	

		Header result = commandModule.getResult();
		result.setHostName(ClusteredServer.name);
		
		return gson.toJson(result);
	}
}
