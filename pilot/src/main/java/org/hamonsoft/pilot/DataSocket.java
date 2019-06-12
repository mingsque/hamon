package org.hamonsoft.pilot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

class DataSocket implements Runnable {
	Socket dataSocket;
	
	public DataSocket(Socket dataSocket) {
		this.dataSocket = dataSocket;
	}
	
	public void run() {
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
		
			String str = br.readLine();
			
			Parser parser = new Parser();
			
			String result = parser.getModuleResult(str);
				
			bw.write(result + "\n");
			bw.flush();

			dataSocket.close();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		} 
	}	
}


