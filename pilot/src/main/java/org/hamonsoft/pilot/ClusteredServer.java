package org.hamonsoft.pilot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClusteredServer  {
	
	static int portAdd = 0;
	 
    public static void main( String[] args ) {  	
  
        int portNumber = 10400;
        
        if(args.length == 0) {
        	portAdd = 0;
        } else {
        	portAdd = Integer.parseInt(args[0]);
        }
        portNumber = portNumber + portAdd;
        
        System.out.println("Server On Port ["+portNumber+"]");
        //sessionManager.listenRedisSub();
		System.out.println("MAKE ACCEPT SOCKET");        
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber); 
			
			while(true) {
				Socket dataSocket = serverSocket.accept();
				
				System.out.println("ACCEPT");
				
				Runnable r = new DataSocket(dataSocket);
				Thread t = new Thread(r);
				t.start();
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
}

