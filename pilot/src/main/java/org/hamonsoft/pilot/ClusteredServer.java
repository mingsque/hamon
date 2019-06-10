package org.hamonsoft.pilot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;


class DataSocket implements Runnable {
	SessionManager sessionManager;
	Socket dsock;
	public DataSocket(Socket dsock, SessionManager sessionManager) {
		this.sessionManager = sessionManager;
		this.dsock = dsock;
	}
	
	public void run() {
		try {
			//create stream
			BufferedReader br = new BufferedReader(new InputStreamReader(dsock.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dsock.getOutputStream()));
		
			Gson gson = new Gson();
			
			String str;
			Header wHeader = new Header();
			
			str = br.readLine();
			//parse to class
			Header rHeader = gson.fromJson(str, Header.class);
			String command = rHeader.getCommand(); 
			if (command.equals("login")) {
				sessionManager.makeKey();
				wHeader.setCommand("newSession");
				wHeader.setSessionKey(sessionManager.getSessionKey());
				// add session to redis
				sessionManager.addRedisSession(sessionManager.getSessionKey());
				// add session to memory
				sessionManager.addMemorySession();
			} else if (command.equals("normal")) {
				wHeader.setCommand("ok");
				// session key exists and collect
				if (sessionManager.existMemorySession(rHeader.getSessionKey())) {
					// set to expire reload
					wHeader.setCommand("normal");
					sessionManager.addRedisSession(rHeader.getSessionKey());
					//sessionManager.setRedisSession();
					System.out.println("collect session");
					// session key exist and incollect
				} else {
					wHeader.setCommand("abnormal");
					System.out.println("abnormal sessionKey");
				}
			}
			System.out.println(rHeader.toString());
			
			wHeader.setHostName(new Integer(ClusteredServer.portAdd).toString());
			
			bw.write(gson.toJson(wHeader) + "\n");
			bw.flush();

			dsock.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
		
	}	
}

public class ClusteredServer  {
	
	static int portAdd = 0;
	 
    public static void main( String[] args ) {
    	
        System.out.println( "Server start" );
        
        int portNumber = 10400;
        
        if(args.length == 0) {
        	portAdd = 0;
        } else {
        	portAdd = Integer.parseInt(args[0]);
        }
        portNumber = portNumber + portAdd;
        
        System.out.println(portNumber);
        
        SessionManager sessionManager = new SessionManager();
        sessionManager.init();
        
		try {
			System.out.println("make accept socket");
			//create socket to accept 
			ServerSocket asock = new ServerSocket(portNumber); 
			
			while(true) {
				System.out.println("beforeAccept");
				Socket dsock = asock.accept();
				System.out.println("accept");
				//create socket to transfer
				Runnable r = new DataSocket(dsock, sessionManager);
			
				Thread t = new Thread(r);
				t.start();
			}
			
			
		} catch (IOException e) {

			e.printStackTrace();
		}
    }
}

