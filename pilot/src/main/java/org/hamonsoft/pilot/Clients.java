package org.hamonsoft.pilot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

public class Clients {

	public static void main(String[] args) {
		
		System.out.println("client start");
		
		//int portNumber = 10400;
		int portAdd = 0;
		if (args.length == 0) {

			portAdd = 0;
		} else {

			portAdd = Integer.parseInt(args[0]);
		}
		//portNumber = portNumber + portAdd;
		
		try {
			Socket dsock = new Socket("192.168.252.128", 10399);
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dsock.getOutputStream()));
			BufferedReader br = new BufferedReader(new InputStreamReader(dsock.getInputStream()));
			
			Header header = new Header();
			header.setCommand("login");
			
			Gson gson = new Gson();
			System.out.println(gson.toJson(header));
			
			while(true) {
				bw.write(gson.toJson(header)+"\n");
				bw.flush();
				
				Header rHeader = gson.fromJson(br.readLine(), Header.class);
				header.setSessionKey(rHeader.getSessionKey());
				header.setCommand("normal");
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}


