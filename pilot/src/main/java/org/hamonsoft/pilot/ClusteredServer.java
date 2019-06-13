package org.hamonsoft.pilot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClusteredServer {

	static int portAdd = 0;

	public static void main(String[] args) {

		int portNumber = 10400;

		if (args.length == 0) {
			portAdd = 0;
		} else {
			portAdd = Integer.parseInt(args[0]);
		}
		portNumber = portNumber + portAdd;

		System.out.println("Server On Port [" + portNumber + "]");
		System.out.println("MAKE ACCEPT SOCKET");

		ServerSocket serverSocket = null;

		RedisConnector.getInstance();
		try {
			
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		Socket dataSocket = null;

		while (true) {

			try {
				
				dataSocket = serverSocket.accept();
			} catch (IOException e) {
				
				e.printStackTrace();
			} 
			System.out.println("ACCEPT");

			Runnable r = new DataSocket(dataSocket);
			Thread t = new Thread(r);
			t.start();
		}
	}
}
