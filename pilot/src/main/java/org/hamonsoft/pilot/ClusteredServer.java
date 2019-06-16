package org.hamonsoft.pilot;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClusteredServer {
	
	public static String name;
	
	public static void main(String[] args) {

		int portNumber = 10400;
		
		if (args.length != 0) {
			portNumber = portNumber + Integer.parseInt(args[0]);
		}
		
		if(args.length == 2) {
			name = args[1];
		}
		init();
		
		System.out.println("SERVER ON PORT [" + portNumber + "]");

		ServerSocket serverSocket = null;
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
				break;
			} 
			System.out.println("ACCEPT!");

			Runnable r = new DataSocket(dataSocket);
			Thread t = new Thread(r);
			t.start();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("오류로 서버 종료");
		}
	}
	
	public static void init() {
		SessionManager.getInstance();
		RedisConnector.getInstance().connect();
		RedisConnector.getInstance().runSubscriber();
		RedisConnector.getInstance().getAll();
	}
}
